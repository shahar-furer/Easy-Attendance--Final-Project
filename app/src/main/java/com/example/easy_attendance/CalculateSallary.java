package com.example.easy_attendance;

import androidx.annotation.NonNull;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.easy_attendance.firebase.model.FBAuth;
import com.example.easy_attendance.firebase.model.FirebaseDBTable;
import com.example.easy_attendance.firebase.model.FirebaseDBUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;


public class CalculateSallary extends Menu implements AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener, View.OnClickListener {
    private Button calculateBtn;
    private Spinner month , year;
    private TextView totalYearTxtview , totalMonthTxtview , totalHoursTxtview ,totalPayTxtView;
    private double hourlyPay ,totalPay,totalHours=0, basicHoursInMins = 9*60;
    private String chosenMonth="", chosenYear="";
    FBAuth auth = new FBAuth();
    String uid= auth.getUserID();
    FirebaseDBUser fdbu  ;
    FirebaseDBTable fdbt;
    DatabaseReference userRef;
    DatabaseReference tableRef;
    String userID;
    String orgKey;
    ArrayAdapter<String> yearAdapter;
    ArrayAdapter<String> monthAdapter;
    DecimalFormat numberFormat = new DecimalFormat("#.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calculate_sallary);
        month = (Spinner) findViewById(R.id.spinnerMonthC);
        year = (Spinner)findViewById(R.id.spinnerYearC);
        calculateBtn = findViewById(R.id.calculateButton);
        totalMonthTxtview = findViewById(R.id.txtTotalMonth);
        totalYearTxtview = findViewById(R.id.txtTotalYear);
        totalHoursTxtview = findViewById(R.id.txtTotalHours);
        totalPayTxtView = findViewById(R.id.txtTotalPay);
        fdbu = new FirebaseDBUser();
        fdbt= new FirebaseDBTable();
        userRef=fdbu.getUserFromDB(uid);
        yearAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        year.setAdapter(yearAdapter);
        year.setOnItemSelectedListener(this);
        monthAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        month.setAdapter(monthAdapter);
        month.setOnItemSelectedListener(this);
        calculateBtn.setOnClickListener(this);



        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                updateUserDetails(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


    }

    private void updateUserDetails(DataSnapshot dataSnapshot) {
        userID=dataSnapshot.child("ID").getValue(String.class);
        hourlyPay = dataSnapshot.child("hourlyPay").getValue(Double.class);
        orgKey = dataSnapshot.child("orgKey").getValue(String.class);
        tableRef=fdbt.getUserAttendanceFromDB(userID);
        tableRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    yearAdapter.add(snapshot.getKey().toString());
                    yearAdapter.notifyDataSetChanged();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }




    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.spinnerYearC:
                chosenYear = parent.getSelectedItem().toString();
                monthAdapter.clear();
                updateMonths(parent.getSelectedItem().toString());

                break;
            case R.id.spinnerMonthC:
                wordsToNumbers(parent.getSelectedItem().toString());
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }


    private void updateMonths(String year) {
        tableRef=fdbt.getUserAttendanceFromDB(userID).child(year);
        tableRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    monthAdapter.add(numbersToWords(snapshot.getKey().toString()));
                    monthAdapter.notifyDataSetChanged();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }



    @Override
    public void onClick(View v) {
        if(chosenYear != "" && chosenMonth != "") {
            totalHours = 0;
            totalPay = 0;
            tableRef = fdbt.getAttendanceFromDB(userID, chosenYear, chosenMonth);
            tableRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if(!snapshot.hasChild("total")){
                            totalPay += hourlyPay * 9;      // add the daily pay
                        }
                        String total = snapshot.child("total").getValue(String.class);
                        calcSallary(getTotalMinutes(total));

                    }
                    totalMonthTxtview.setText(chosenMonth);
                    totalYearTxtview.setText(chosenYear);
                    totalHoursTxtview.setText((int) (totalHours / 60) + ":" + (int) totalHours % 60);
                    totalPayTxtView.setText("Total Pay: "+ numberFormat.format(totalPay) + " NIS");
                    totalPayTxtView.setVisibility(View.VISIBLE);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }

            });
        }

    }

    private void calcSallary(int dailyTotalHours){
        Log.d("total mins " ," "+dailyTotalHours);
        double dailyPay=0;
        totalHours+=dailyTotalHours;
        Double minutePay= hourlyPay/60;
        if(dailyTotalHours  >= basicHoursInMins) {
            dailyPay += minutePay *basicHoursInMins;
            dailyTotalHours -= basicHoursInMins;
            if(dailyTotalHours <= 120) {
                dailyPay += (minutePay*1.25) * dailyTotalHours;
                totalPay +=dailyPay;
                Log.d("daily pay " , " "+dailyPay+ " "+totalPay);
                return;
            }
            dailyPay += (minutePay*1.25) *120;
            dailyTotalHours -= 120;
            dailyPay +=(minutePay*1.5) *dailyTotalHours;
            totalPay += dailyPay;
            Log.d("daily pay " , " "+dailyPay+ " "+totalPay);
            return;
        }

        dailyPay = dailyTotalHours*minutePay;
        totalPay += dailyPay;
        Log.d("daily pay " , " "+dailyPay+ " "+totalPay);
    }

    private int getTotalMinutes(String time) {
        String[] t = time.split(":");
        return Integer.valueOf(t[0]) * 60 + Integer.valueOf(t[1]);

    }

    private void wordsToNumbers(String monthToConvert){
        switch (monthToConvert ){

            case "January" :
                chosenMonth="01";
                break;
            case "February" :
                chosenMonth="02";
                break;
            case "March" :
                chosenMonth="03";
                break;
            case "April" :
                chosenMonth="04";
                break;
            case "May" :
                chosenMonth="05";
                break;
            case "June" :
                chosenMonth="06";
                break;
            case "July" :
                chosenMonth="07";
                break;
            case "August" :
                chosenMonth="08";
                break;
            case "September" :
                chosenMonth="09";
                break;
            case "October" :
                chosenMonth="10";
                break;
            case "November" :
                chosenMonth="11";
                break;
            case "December" :
                chosenMonth="12";
                break;


        }

        Log.d("chosen month: " ,chosenMonth );


    }

    private String numbersToWords(String monthToConvert){
        String monthInWords="";
        switch (monthToConvert ){

            case "01" :
                monthInWords="January";
                break;
            case "02" :
                monthInWords="February";
                break;
            case "03" :
                monthInWords="March";
                break;
            case "04" :
                monthInWords="April";
                break;
            case "05" :
                monthInWords="May";
                break;
            case "06" :
                monthInWords="June";
                break;
            case "07" :
                monthInWords="July";
                break;
            case "08" :
                monthInWords="August";
                break;
            case "09" :
                monthInWords="September";
                break;
            case "10" :
                monthInWords="October";
                break;
            case "11" :
                monthInWords="November";
                break;
            case "12" :
                monthInWords="December";
                break;

        }

        return monthInWords;


    }


}