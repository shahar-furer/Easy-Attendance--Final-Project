package com.example.easy_attendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.provider.ContactsContract;
import android.util.Log;
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

import org.w3c.dom.Document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MonthlyReport extends Menu implements OnItemSelectedListener, View.OnClickListener {
    private Spinner spinnerYear, spinnerMonth, spinnerWorker;
    private TextView textWorker;
    String[][] daysTimes;
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
    String orgKey;
    Bitmap bmp, scaledbmp;
    int pageWidth = 1200;
    String EmployeeName = "";
    String EmployeeId = "";
    Button createButton;
    Date dataObj;
    DateFormat dateFormat;
    ArrayAdapter<String> adapterYear;
    ArrayAdapter<String> adapterMonth;
    ArrayAdapter<String> adapterWorkers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(MonthlyReport.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
        setContentView(R.layout.monthly_report);
        spinnerWorker = (Spinner) findViewById(R.id.spinnerWorker);
        spinnerYear = (Spinner) findViewById(R.id.spinnerYear);
        spinnerMonth = (Spinner) findViewById(R.id.spinnerMonth);
        textWorker = (TextView) findViewById(R.id.txtWorker);
        createButton = findViewById(R.id.downloadPdf);
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
        createButton.setOnClickListener(this);

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
                                adapterWorkers.add(snapshot.getKey().toString());
                                adapterWorkers.notifyDataSetChanged();
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
    //createPDF();

    public void createPDF(){
            createButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (selectedMonth !="" && selectedWorker !="" && selectedYear != "" && isManager || selectedMonth !="" && selectedYear != "" && !isManager ){
                    dataObj = new Date();
                    PdfDocument pdfWorkHours = new PdfDocument();
                    Paint myPaint = new Paint();
                    Paint titlePaint = new Paint();

                    PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(1200, 2010, 1).create();
                    PdfDocument.Page myPage = pdfWorkHours.startPage(myPageInfo);
                    Canvas canvas = myPage.getCanvas();

                    canvas.drawBitmap(scaledbmp, 15, 15, myPaint);
                    titlePaint.setTextAlign(Paint.Align.CENTER);
                    titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                    titlePaint.setTextSize(70);
                    canvas.drawText("Attendance Report", pageWidth / 2, 300, titlePaint);
                    //nees to add employeName

                    titlePaint.setTextAlign(Paint.Align.LEFT);
                    myPaint.setTextSize(50f);
                    myPaint.setColor(Color.BLACK);
                    canvas.drawText("Employee Name: " + EmployeeName, 30, 500, myPaint);
                    canvas.drawText("Employee ID: " + selectedWorker, 30, 600, myPaint);

                    dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
                    canvas.drawText("Date: " + dateFormat.format(dataObj), 30, 720 , myPaint);

                    myPaint.setStyle(Paint.Style.STROKE);
                    myPaint.setStrokeWidth(2);
                    canvas.drawRect(20, 780,pageWidth-20,860, myPaint);

                    myPaint.setTextAlign(Paint.Align.LEFT);
                    myPaint.setStyle(Paint.Style.FILL);
                    canvas.drawText("Date", 40, 830, myPaint);
                    canvas.drawText("Entry Time", 340, 830, myPaint);
                    canvas.drawText("Exit Time", 650, 830, myPaint);
                    canvas.drawText("Total", 950, 830, myPaint);

                    canvas.drawLine(330, 790, 330,840,myPaint);
                    canvas.drawLine(630, 790, 630,840,myPaint);
                    canvas.drawLine(930, 790, 930,840,myPaint);


                    //for loop of the days in the manth
                    int countY = 950;
                    for (int i=0;i<daysTimes[0].length;i++){
                        String fullDate = daysTimes[i][0]+"/"+selectedMonth+"/"+selectedYear;
                        canvas.drawText(fullDate, 40, countY, myPaint);
                        canvas.drawText(daysTimes[i][1], 360, countY, myPaint);
                        canvas.drawText(daysTimes[i][2], 650, countY, myPaint);
                        canvas.drawText(daysTimes[i][3], 950, countY, myPaint);
                        canvas.drawLine(20, countY+50, pageWidth-20,countY+50,myPaint);
                        canvas.drawLine(330, countY, 330,countY+100,myPaint);
                        canvas.drawLine(630, countY, 630,countY+100,myPaint);
                        canvas.drawLine(930, countY, 930,countY+100,myPaint);
                        countY = countY+100;
                    }

                    myPaint.setTextAlign(Paint.Align.RIGHT);
                    pdfWorkHours.finishPage(myPage);
                    String directory_path = Environment.getExternalStorageDirectory().getPath() +"/mypdf/";
                    File file  = new File(directory_path);
                        if (!file.exists()) {
                            file.mkdirs();
                        }
                    String targetPdf = directory_path+ "/Monthly Report "+selectedWorker+".pdf";
                    File filePath = new File(targetPdf);

                    try {
                        pdfWorkHours.writeTo(new FileOutputStream(filePath));
                        Toast.makeText(getApplicationContext(), "The PFD saved: " ,Toast.LENGTH_SHORT).show();
                    } catch (IOException e){
                        Log.e("main", "error "+e.toString());
                        Toast.makeText(getApplicationContext(), "Something wrong: " + e.toString(),  Toast.LENGTH_LONG).show();
                    }
                    pdfWorkHours.close();
                }
                else {
                    //popup massage that selected not fill
                }
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
                selectedWorker = parent.getSelectedItem().toString();
                adapterYear.clear();
                updateYearSpinner();
                break;

            case R.id.spinnerYear:
                selectedYear = parent.getSelectedItem().toString();
                adapterMonth.clear();
                updateMonthSpinner();
                break;

            case R.id.spinnerMonth:
                selectedMonth = parent.getSelectedItem().toString();
                createArrayPdf();
                break;
        }

    }
    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO - Custom Code
    }


    private void updateUserDetails(DataSnapshot dataSnapshot){
        isManager = dataSnapshot.child("isManager").getValue(Boolean.class);
        keyId = dataSnapshot.child("ID").getValue(String.class);
        orgKey = dataSnapshot.child("orgKey").getValue(String.class);
    }


    private void updateYearSpinner(){
        tableRef=tableDB.getUserAttendanceFromDB(selectedWorker);
        tableRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    adapterYear.add(snapshot.getKey().toString());
                    adapterYear.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void updateMonthSpinner(){
        tableRef=tableDB.getUserAttendanceFromDB(selectedWorker).child(selectedYear);
        tableRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    adapterMonth.add(snapshot.getKey().toString());
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
    public void onClick(View v) {
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
                    canvas.drawText("Attendance Report", pageWidth / 2, 270, titlePaint);
                    //nees to add employeName

                    titlePaint.setTextAlign(Paint.Align.LEFT);
                    myPaint.setTextSize(50f);
                    myPaint.setColor(Color.BLACK);
                    canvas.drawText("Employee Name: " + EmployeeName, 30, 310, myPaint);
                    canvas.drawText("Employee ID: " + EmployeeId, 30, 340, myPaint);

                    dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
                    canvas.drawText("Date: " + dateFormat.format(dataObj), pageWidth-70, 450 , myPaint);

                    myPaint.setStyle(Paint.Style.STROKE);
                    myPaint.setStrokeWidth(2);
                    canvas.drawRect(20, 600,pageWidth-20,680, myPaint);

                    myPaint.setTextAlign(Paint.Align.LEFT);
                    myPaint.setStyle(Paint.Style.FILL);
                    canvas.drawText("Date", 40, 650, myPaint);
                    canvas.drawText("Entry Time", 350, 650, myPaint);
                    canvas.drawText("Exit Time", 650, 650, myPaint);
                    canvas.drawText("Total", 950, 650, myPaint);

                    canvas.drawLine(280, 610, 280,660,myPaint);
                    canvas.drawLine(580, 610, 580,660,myPaint);
                    canvas.drawLine(880, 610, 880,660,myPaint);



                    //for loop of the days in the manth
                    int countY = 770;
                    for (int i=0;i<daysTimes.length;i++){
                        String fullDate = daysTimes[i][0]+"/"+selectedMonth+"/"+selectedYear;
                        canvas.drawText(fullDate, 40, countY, myPaint);
                        canvas.drawText(daysTimes[i][1], 300, countY, myPaint);
                        canvas.drawText(daysTimes[i][2], 600, countY, myPaint);
                        canvas.drawText(daysTimes[i][3], 900, countY, myPaint);
                        canvas.drawLine(20, countY+50, pageWidth-20,countY+50,myPaint);
                        canvas.drawLine(280, countY, 280,countY+50,myPaint);
                        canvas.drawLine(580, countY, 580,countY+50,myPaint);
                        canvas.drawLine(880, countY, 880,countY+50,myPaint);
                        countY = countY+100;
                    }

                    myPaint.setTextAlign(Paint.Align.RIGHT);
                    pdfWorkHours.finishPage(myPage);
                    //File file  = new File(Environment.getExternalStorageDirectory(), "/Monthly Report "+selectedWorker+".pdf");
                    File outDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),  "/Monthly Report "+selectedWorker+".pdf");

                    try {
                        pdfWorkHours.writeTo(new FileOutputStream(outDir));
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
}