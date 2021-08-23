package com.example.easy_attendance;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.easy_attendance.firebase.model.FBAuth;
import com.example.easy_attendance.firebase.model.FirebaseDBTable;
import com.example.easy_attendance.firebase.model.FirebaseDBUser;
import com.example.easy_attendance.firebase.model.dataObject.Model;
import com.example.easy_attendance.firebase.model.dataObject.UserObj;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class MonthlyReport extends Menu implements OnItemSelectedListener {
    private Spinner spinnerYear, spinnerMonth, spinnerWorker;
    private TextView textWorker;
    String[][] daysTimes;
    HashMap<String, UserObj> workersList = new HashMap<>();
    String selectedWorker = "";
    String selectedMonth = "";
    String selectedYear = "";
    FBAuth mAuth = new FBAuth();
    String uid = mAuth.getUserID();
    FirebaseDBUser userDB;//= new FirebaseDBUser();
    DatabaseReference userRef;//= userDB.getUserFromDB(uid);
    FirebaseDBTable tableDB;//= new FirebaseDBTable();
    DatabaseReference orgRef;
    DatabaseReference tableRef;
    Boolean isManager;
    String keyId;
    String EmployeeName;      // ADDED
    String orgKey;
    Bitmap bmp, scaledbmp;
    int pageWidth = 1200;
    String EmployeeId = "";
    Button createPdfButton;
    Button openPdfButton;
    Date dataObj;
    DateFormat dateFormat;
    ArrayAdapter<String> adapterYear;
    ArrayAdapter<String> adapterMonth;
    ArrayAdapter<String> adapterWorkers;
    public static final int REQUEST_PERM_READ_STORAGE = 103;
    PDFView pdfview;

    private int sickDaysLeft;
    private int vacationDaysLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(MonthlyReport.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
        setContentView(R.layout.monthly_report);

        spinnerWorker = (Spinner) findViewById(R.id.spinnerWorker);
        spinnerYear = (Spinner) findViewById(R.id.spinnerYear);
        spinnerMonth = (Spinner) findViewById(R.id.spinnerMonth);
        textWorker = (TextView) findViewById(R.id.txtDay);

        createPdfButton = (Button) findViewById(R.id.downloadPdf);
        createPdfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPdf();
            }
        });

        openPdfButton = (Button) findViewById(R.id.openPdf);
        openPdfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MonthlyReport.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERM_READ_STORAGE);
                }
                openPdf();
            }
        });

        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        scaledbmp = Bitmap.createScaledBitmap(bmp, 250, 250, false);
        userDB = new FirebaseDBUser();
        tableDB = new FirebaseDBTable();
        userRef = userDB.getUserFromDB(uid);
        adapterWorkers = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
        adapterWorkers.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWorker.setAdapter(adapterWorkers);
        spinnerWorker.setOnItemSelectedListener(this);

        adapterYear = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
        adapterYear.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(adapterYear);
        spinnerYear.setOnItemSelectedListener(this);

        adapterMonth = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
        adapterMonth.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(adapterMonth);
        spinnerMonth.setOnItemSelectedListener(this);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                updateUserDetails(dataSnapshot);
                if (!(isManager)) {
                    spinnerWorker.setVisibility(View.GONE);
                    textWorker.setVisibility(View.GONE);
                    selectedWorker = keyId;
                    updateYearSpinner();
                } else {
                    orgRef = userDB.getOrganization(orgKey);
                    orgRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                if (snapshot.getKey() == "Manager")
                                    continue;
                                findWorkerName(snapshot.getKey().toString());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //Toast.makeText(getApplicationContext(), "Selected Worker: "+workers[position] ,Toast.LENGTH_SHORT).show();
        //Toast.makeText(getApplicationContext(), "Selected Month: "+getResources().getStringArray(R.array.month)[position] ,Toast.LENGTH_SHORT).show();
        //Toast.makeText(getApplicationContext(), "Selected Year: "+years[position] ,Toast.LENGTH_SHORT).show();
        //selectedWorker = workers[position];
        //selectedMonth = getResources().getStringArray(R.array.month)[position];
        //selectedYear = years[position];
        switch (parent.getId()) {
            case R.id.spinnerWorker:
                findWorkerId(parent.getSelectedItem().toString());
                adapterYear.clear();
                adapterMonth.clear();
                updateYearSpinner();
                break;

            case R.id.spinnerYear:
                selectedYear = parent.getSelectedItem().toString();
                adapterMonth.clear();
                updateMonthSpinner();
                break;

            case R.id.spinnerMonth:
                wordsToNumbers(parent.getSelectedItem().toString());
                createArrayPdf();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO - Custom Code
    }

    private void updateUserDetails(DataSnapshot dataSnapshot) {
        isManager = dataSnapshot.child("isManager").getValue(Boolean.class);
        keyId = dataSnapshot.child("ID").getValue(String.class);
        EmployeeName = dataSnapshot.child("fName").getValue(String.class) + " " + dataSnapshot.child("lName").getValue(String.class);
        orgKey = dataSnapshot.child("orgKey").getValue(String.class);

        sickDaysLeft = !dataSnapshot.hasChild("SickDays") ? 0 : dataSnapshot.child("SickDays").getValue(Integer.class);
        vacationDaysLeft = !dataSnapshot.hasChild("daysOff") ? 0 : dataSnapshot.child("daysOff").getValue(Integer.class);



        calcLeftSickDays();
    }

    private void calcLeftSickDays() {

    }

    private void updateYearSpinner() {
        tableRef = tableDB.getUserAttendanceFromDB(selectedWorker);
        tableRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    adapterYear.add(snapshot.getKey());
                    adapterYear.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void updateMonthSpinner() {
        tableRef = tableDB.getUserAttendanceFromDB(selectedWorker).child(selectedYear);
        tableRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    adapterMonth.add(numbersToWords(snapshot.getKey().toString()));
                    adapterMonth.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }

    private void createArrayPdf() {
        DatabaseReference attendanceRefOfDays = tableDB.getAttendanceFromDB(selectedWorker, selectedYear, selectedMonth);
        attendanceRefOfDays.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                int countRow = 0;
                long numOfDays = (snapshot.getChildrenCount());
                daysTimes = new String[(int) numOfDays][5];
                for (DataSnapshot snap : snapshot.getChildren()) {
                    daysTimes[countRow][0] = snap.getKey();


                    if (snapshot.child(daysTimes[countRow][0]).hasChild("entry")) {
                        daysTimes[countRow][1] = snapshot.child(daysTimes[countRow][0]).child("entry").getValue(String.class);
                    } else
                        daysTimes[countRow][1] = "";


                    if (snapshot.child(daysTimes[countRow][0]).hasChild("exit")) {
                        daysTimes[countRow][2] = snapshot.child(daysTimes[countRow][0]).child("exit").getValue(String.class);
                    } else
                        daysTimes[countRow][2] = "";

                    if (snapshot.child(daysTimes[countRow][0]).hasChild("total")) {
                        daysTimes[countRow][3] = snapshot.child(daysTimes[countRow][0]).child("total").getValue(String.class);
                    } else
                        daysTimes[countRow][3] = "";
//                    }

                    if (snapshot.child(daysTimes[countRow][0]).hasChild("type")) {
                        String t= snapshot.child(daysTimes[countRow][0]).child("type").getValue(String.class);
                        if(t.equals("sick")) {
                            daysTimes[countRow][2] = "( " + t + " )";
                            sickDaysLeft--;
                        }
                        if(t.equals("vacation")) {
                            daysTimes[countRow][2] = "( " + t + " )";
                            vacationDaysLeft--;
                        }
                    }
                    else {
                        daysTimes[countRow][2] = "";
                    }

                    countRow++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    //@Override
    public void createPdf() {
        if (selectedMonth != "" && selectedWorker != "" && selectedYear != "" && isManager || selectedMonth != "" && selectedYear != "" && !isManager) {
            dataObj = new Date();
            PdfDocument pdfWorkHours = new PdfDocument();
            Paint myPaint = new Paint();
            Paint titlePaint = new Paint();

            PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(1200, 2010, 1).create();
            PdfDocument.Page myPage = pdfWorkHours.startPage(myPageInfo);
            Canvas canvas = myPage.getCanvas();

            canvas.drawBitmap(scaledbmp, 0, 0, myPaint);
            titlePaint.setTextAlign(Paint.Align.CENTER);
            titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            titlePaint.setTextSize(70);
            canvas.drawText("Attendance Report", pageWidth / 2, 270, titlePaint);


            titlePaint.setTextAlign(Paint.Align.LEFT);
            myPaint.setTextSize(50f);
            myPaint.setColor(Color.BLACK);
            canvas.drawText("Employee Name: " + EmployeeName, 30, 400, myPaint);
            canvas.drawText("Employee ID: " + selectedWorker, 30, 470, myPaint);


            dateFormat = new SimpleDateFormat("dd/MM/yy");
            canvas.drawText("Date: " + dateFormat.format(dataObj), pageWidth - 400, 400, myPaint);

            dateFormat = new SimpleDateFormat("HH:mm:ss");
            canvas.drawText("Time: " + dateFormat.format(dataObj), pageWidth - 400, 470, myPaint);

            canvas.drawText("Days Offs Left: " + vacationDaysLeft, 30, 540, myPaint);
            canvas.drawText("Sick Days Left: " + sickDaysLeft, 30, 610, myPaint);


            myPaint.setStyle(Paint.Style.STROKE);
            myPaint.setStrokeWidth(2);
            canvas.drawRect(20, 600 + 100, pageWidth - 20, 680+ 100, myPaint);

            myPaint.setTextAlign(Paint.Align.LEFT);
            myPaint.setStyle(Paint.Style.FILL);
            canvas.drawText("Date", 40, 650+ 100, myPaint);
            canvas.drawText("Entry Time", 350, 650+ 100, myPaint);
            canvas.drawText("Exit Time", 650, 650+ 100, myPaint);
            canvas.drawText("Total", 950, 650+ 100, myPaint);

            canvas.drawLine(330, 610+ 100, 330, 660+ 100, myPaint);
            canvas.drawLine(630, 610+ 100, 630, 660+ 100, myPaint);
            canvas.drawLine(930, 610+ 100, 930, 660+ 100, myPaint);

            //for loop of the days in the manth
            int countY = 770 + 100;
            for (int i = 0; i < daysTimes.length; i++) {
                String fullDate = daysTimes[i][0] + "/" + selectedMonth + "/" + selectedYear;
                canvas.drawText(fullDate, 40, countY, myPaint);
                canvas.drawText(daysTimes[i][1], 350, countY, myPaint);
                canvas.drawText(daysTimes[i][2], 650, countY, myPaint);
                canvas.drawText(daysTimes[i][3], 950, countY, myPaint);
                canvas.drawLine(20, countY + 20, pageWidth - 20, countY + 20, myPaint);
                countY = countY + 100;
            }

            myPaint.setTextAlign(Paint.Align.RIGHT);
            pdfWorkHours.finishPage(myPage);
            //File file  = new File(Environment.getExternalStorageDirectory(), "/Monthly Report "+selectedWorker+".pdf");
            File outDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "/Monthly Report " + selectedWorker + " " + selectedMonth + ".pdf");

            try {
                pdfWorkHours.writeTo(new FileOutputStream(outDir));
                Toast.makeText(getApplicationContext(), "The PFD saved: ", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
            pdfWorkHours.close();
        } else {
            //popup massage that selected not fill
        }
    }

    public void openPdf() {
        createPdf();

        setContentView(R.layout.pdfview);
        pdfview = findViewById(R.id.pdfView);

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Monthly Report " + selectedWorker + " " + selectedMonth + ".pdf");
        pdfview.fromFile(file).load();
    }


    private void wordsToNumbers(String monthToConvert) {
        switch (monthToConvert) {

            case "January":
                selectedMonth = "01";
                break;
            case "February":
                selectedMonth = "02";
                break;
            case "March":
                selectedMonth = "03";
                break;
            case "April":
                selectedMonth = "04";
                break;
            case "May":
                selectedMonth = "05";
                break;
            case "June":
                selectedMonth = "06";
                break;
            case "July":
                selectedMonth = "07";
                break;
            case "August":
                selectedMonth = "08";
                break;
            case "September":
                selectedMonth = "09";
                break;
            case "October":
                selectedMonth = "10";
                break;
            case "November":
                selectedMonth = "11";
                break;
            case "December":
                selectedMonth = "12";
                break;


        }


    }

    private String numbersToWords(String monthToConvert) {
        String monthInWords = "";
        switch (monthToConvert) {

            case "01":
                monthInWords = "January";
                break;
            case "02":
                monthInWords = "February";
                break;
            case "03":
                monthInWords = "March";
                break;
            case "04":
                monthInWords = "April";
                break;
            case "05":
                monthInWords = "May";
                break;
            case "06":
                monthInWords = "June";
                break;
            case "07":
                monthInWords = "July";
                break;
            case "08":
                monthInWords = "August";
                break;
            case "09":
                monthInWords = "September";
                break;
            case "10":
                monthInWords = "October";
                break;
            case "11":
                monthInWords = "November";
                break;
            case "12":
                monthInWords = "December";
                break;

        }

        return monthInWords;


    }


    private void findWorkerName(String id) {
        if (id == "Manager") return;
        DatabaseReference users = userDB.getAllUsers();
        users.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot.child("ID").getValue(String.class).equals(id)) {
//                        String fName = dataSnapshot.child("fName").getValue(String.class);
//                        String lName = dataSnapshot.child("lName").getValue(String.class);
//                        String lName = dataSnapshot.child("lName").getValue(String.class);
//                        String lName = dataSnapshot.child("lName").getValue(String.class);

                        UserObj obj = dataSnapshot.getValue(UserObj.class);

                        //Map<String, Object> data = new HashMap<>();

                        workersList.put(id, obj);
                        adapterWorkers.add(obj.fName + " " + obj.lName);
                        adapterWorkers.notifyDataSetChanged();
                        break;
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void findWorkerId (String name) {
        for (String i : workersList.keySet()) {
            String workerName = workersList.get(i).fName + " " + workersList.get(i).lName;
            if(workerName.equals(name)) {
                selectedWorker = i;
                sickDaysLeft = workersList.get(i).SickDays;
                vacationDaysLeft = workersList.get(i).daysOff;
                EmployeeName = workerName;
            }
        }

    }

}
