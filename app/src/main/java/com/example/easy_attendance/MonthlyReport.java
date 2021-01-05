package com.example.easy_attendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.easy_attendance.firebase.model.FBAuth;
import com.example.easy_attendance.firebase.model.FirebaseDBTable;
import com.example.easy_attendance.firebase.model.FirebaseDBUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MonthlyReport extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Spinner spinnerYear, spinnerMonth, spinnerWorker;
    private TextView textWorker;
    String [] workers;
    String [] years;
    String [][] daysTimes;
    String selectedWorker = "";
    String selectedMonth = "";
    String selectedYear = "";
    FBAuth mAuth = new FBAuth();
    String uid = mAuth.getUserID();
    FirebaseDBUser userDB = new FirebaseDBUser();
    DatabaseReference userRef = userDB.getUserFromDB(uid);
    FirebaseDBTable tableDB = new FirebaseDBTable();
    DatabaseReference orgRef;
    Boolean isManager;
    String keyId;
    String orgKey;
    Bitmap bmp, scaledbmp;
    int pageWidth = 1200;
    String EmployeeName ="";
    String EmployeeId = "";
    Button createButton;
    Date dataObj;
    DateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monthly_report);
        spinnerWorker = (Spinner) findViewById(R.id.spinnerWorker);
        spinnerYear = (Spinner) findViewById(R.id.spinnerYear);
        spinnerMonth = (Spinner) findViewById(R.id.spinnerMonth);
        textWorker = (TextView) findViewById(R.id.txtWorker);
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        scaledbmp = Bitmap.createScaledBitmap(bmp, 400, 400 , false);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                isManager = dataSnapshot.child("isManager").getValue(Boolean.class);
                keyId = dataSnapshot.child("ID").getValue(String.class);
                orgKey = dataSnapshot.child("orgKey").getValue(String.class);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        if(!(isManager))
        {
            spinnerWorker.setVisibility(View.GONE);
            textWorker.setVisibility(View.GONE);
        }
        else
        {
            orgRef = userDB.getOrganization(orgKey);
            orgRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int count = 0;
                    long numOfEmployees = (dataSnapshot.getChildrenCount())-1;
                    workers = new String[(int)numOfEmployees] ;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot.getKey() == "Manager")
                            continue;
                        workers[count]= snapshot.getKey();  //Option to provide names with "(String)snapshot.getValue();"
                        count++;
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }

            });
            ArrayAdapter<String> adapterWorkers = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, workers);
            adapterWorkers.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinnerWorker.setAdapter(adapterWorkers);
            spinnerWorker.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int position,long id) {
                    selectedWorker = workers[position];
                    DatabaseReference attendanceRef = tableDB.getUserAttendanceFromDB(selectedWorker);
                    attendanceRef.addListenerForSingleValueEvent(new ValueEventListener(){
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            int count = 0;
                            long numOfYear = (snapshot.getChildrenCount())-1;
                            years = new String[(int)numOfYear] ;
                            for (DataSnapshot snap : snapshot.getChildren())
                            {
                                years[count] = snap.getKey();
                                count++;
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    Toast.makeText(getApplicationContext(), "Please Chose Employee" ,Toast.LENGTH_SHORT).show();
                }
            });
        }

        ArrayAdapter<String> adapterYear = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, years);
        adapterYear.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<String> adapterMonth = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.month));
        adapterMonth.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerYear.setAdapter(adapterYear);
        spinnerYear.setOnItemSelectedListener(new OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "Selected Year: "+years[position] ,Toast.LENGTH_SHORT).show();
                selectedYear = years[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerMonth.setAdapter(adapterMonth);
        spinnerMonth.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedMonth = getResources().getStringArray(R.array.month)[position];
                DatabaseReference attendanceRefOfDays = tableDB.getAttendanceFromDB(selectedWorker, selectedYear, selectedMonth);
                attendanceRefOfDays.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        int countRow = 0;
                        long numOfDays = (snapshot.getChildrenCount())-1;
                        long numOfentrys = (snapshot.getChildrenCount())-1;
                        daysTimes = new String[(int)numOfDays][4];
                        for (DataSnapshot snap : snapshot.getChildren())
                        {
                            daysTimes[countRow][0] = snap.getKey();
                            if (snapshot.child(daysTimes[countRow][0]).hasChild("entry")){
                                daysTimes[countRow][1] = snapshot.child(daysTimes[countRow][0]).child("entry").getValue(String.class);
                            }
                            else
                                daysTimes[countRow][1] = "";
                            if (snapshot.child(daysTimes[countRow][0]).hasChild("exit")){
                                daysTimes[countRow][2] = snapshot.child(daysTimes[countRow][0]).child("exit").getValue(String.class);
                            }
                            else
                                daysTimes[countRow][2] = "";
                            if (snapshot.child(daysTimes[countRow][0]).hasChild("total")){
                                daysTimes[countRow][3] = snapshot.child(daysTimes[countRow][0]).child("total").getValue(String.class);
                            }
                            else
                                daysTimes[countRow][3] = "";
                            countRow++;
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




        createPDF();

    }
    public void createPDF(){
        if (selectedMonth !="" && selectedWorker !="" && selectedYear != ""){
        createButton.setOnClickListener(new View.OnClickListener() {
        public void onClick(View view) {
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
            canvas.drawText("Hours Report", pageWidth / 2, 270, titlePaint);
            //nees to add employeName

            titlePaint.setTextAlign(Paint.Align.LEFT);
            myPaint.setTextSize(50f);
            myPaint.setColor(Color.BLACK);
            canvas.drawText("Employee Name: " + EmployeeName, 30, 310, myPaint);
            canvas.drawText("Employee ID: " + EmployeeId, 30, 310, myPaint);

            dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
            canvas.drawText("Date: " + dateFormat.format(dataObj), pageWidth-20, 640 , myPaint);

            myPaint.setStyle(Paint.Style.STROKE);
            myPaint.setStrokeWidth(2);
            canvas.drawRect(20, 780,pageWidth-20,860, myPaint);

            myPaint.setTextAlign(Paint.Align.LEFT);
            myPaint.setStyle(Paint.Style.FILL);
            canvas.drawText("Date", 40, 830, myPaint);
            canvas.drawText("Entry Time", 300, 830, myPaint);
            canvas.drawText("Exit Time", 600, 830, myPaint);
            canvas.drawText("Total", 900, 830, myPaint);

            canvas.drawLine(280, 790, 280,840,myPaint);
            canvas.drawLine(580, 790, 580,840,myPaint);
            canvas.drawLine(880, 790, 880,840,myPaint);



            //for loop of the days in the manth
            int countY = 950;
            for (int i=0;i<daysTimes[0].length;i++){
                String fullDate = daysTimes[i][0]+"/"+selectedMonth+"/"+selectedYear;
                canvas.drawText(fullDate, 40, countY, myPaint);
                canvas.drawText(daysTimes[i][1], 300, countY, myPaint);
                canvas.drawText(daysTimes[i][2], 600, countY, myPaint);
                canvas.drawText(daysTimes[i][3], 900, countY, myPaint);
                canvas.drawLine(20, countY+50, pageWidth-20,countY+50,myPaint);
                countY = countY+100;
            }

            myPaint.setTextAlign(Paint.Align.RIGHT);
            pdfWorkHours.finishPage(myPage);
            File file  = new File(Environment.getExternalStorageDirectory(), "/Monthly Report "+selectedWorker+".pdf");

            try {
                pdfWorkHours.writeTo(new FileOutputStream(file));
                Toast.makeText(getApplicationContext(), "The PFD saved: " ,Toast.LENGTH_SHORT).show();
            } catch (IOException e){
                e.printStackTrace();
            }
            pdfWorkHours.close();
        }
        });
        }
        else{
            //popup massage that selected not fill
        }
    }
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position,long id) {
        //Toast.makeText(getApplicationContext(), "Selected Worker: "+workers[position] ,Toast.LENGTH_SHORT).show();
        //Toast.makeText(getApplicationContext(), "Selected Month: "+getResources().getStringArray(R.array.month)[position] ,Toast.LENGTH_SHORT).show();
        //Toast.makeText(getApplicationContext(), "Selected Year: "+years[position] ,Toast.LENGTH_SHORT).show();
        //selectedWorker = workers[position];
        //selectedMonth = getResources().getStringArray(R.array.month)[position];
        selectedYear = years[position];


    }
    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO - Custom Code
    }
}