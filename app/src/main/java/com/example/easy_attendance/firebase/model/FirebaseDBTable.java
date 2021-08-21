package com.example.easy_attendance.firebase.model;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.easy_attendance.DailyReport;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import android.widget.Chronometer;
import android.os.SystemClock;
import java.util.HashMap;


public class FirebaseDBTable extends FirebaseBaseModel {
    SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
    SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
    SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
    SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm:ss");
    SimpleDateFormat hourOnlyFormat = new SimpleDateFormat("HH");

    FBAuth mAuth = new FBAuth();
    String uid = mAuth.getUserID();
    FirebaseDBUser userDB = new FirebaseDBUser();
    DatabaseReference userRef = userDB.getUserFromDB(uid);
    String keyId;
    Chronometer chronomoter;

    public FirebaseDBTable()
    {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                keyId =dataSnapshot.child("ID").getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void addEntryToDB(Date d, Chronometer c , LinearLayout ll)
    {

        String year = yearFormat.format(d);
        String month = monthFormat.format(d);
        String day = dayFormat.format(d);
        String hour= hourFormat.format(d);
        chronomoter=c;
        int prevDay = Integer.parseInt(day)-1;
        myRef.child("Attendance").child(keyId).child(year).child(month).child(String.valueOf(prevDay)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild("entry")&&(!(snapshot.hasChild("exit")))) {
                    myRef.child("Attendance").child(keyId).child(year).child(month).child(String.valueOf(prevDay)).child("exit").setValue("18:00:00"); //if we build the DB as hashMap, then will change the Entry/Exit987
                    Snackbar.make(ll, "Exit did not registered  yesterday , default exit registered.",
                            Snackbar.LENGTH_SHORT)
                            .show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        myRef.child("Attendance").child(keyId).child(year).child(month).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!(snapshot.hasChild(day))) {
                    startChrom();
                    writeNewAttendance( year,month, day ,hour);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void writeNewAttendance(String year ,String month,String day,String hour)
    {
        myRef.child("Attendance").child(keyId).child(year).child(month).child(day).child("entry").setValue(hour); //if we build the DB as hashMap, then will change the Entry/Exit
        myRef.child("Attendance").child(keyId).child(year).child(month).child(day).child("total").setValue("00:00");
    }

    public void writeAttendance(String year ,String month,String day,String entryHour, String exitHour)
    {
        myRef.child("Attendance").child(keyId).child(year).child(month).child(day).child("entry").setValue(entryHour); //if we build the DB as hashMap, then will change the Entry/Exit
        myRef.child("Attendance").child(keyId).child(year).child(month).child(day).child("exit").setValue(exitHour);

        int t1 = getTotalMinutes(entryHour);
        int t2 = getTotalMinutes(exitHour);
        int result = Math.abs(t1 - t2);
        String total = getResult(result);

        myRef.child("Attendance").child(keyId).child(year).child(month).child(day).child("total").setValue(total);
    }

    public void writeSpecialDayAttendance(String year ,String month,String day, int dayType){
        String dayTypeStr = "";
        switch (dayType){
            case 1:
                dayTypeStr = "vacation";
                break;

            case 2:
                dayTypeStr = "sick";
                break;
        }
        myRef.child("Attendance").child(keyId).child(year).child(month).child(day).child("type").setValue(dayTypeStr);
    }

    public void reportAs(Date d, String dayType){
        String year = yearFormat.format(d);
        String month = monthFormat.format(d);
        String day = dayFormat.format(d);

        myRef.child("Attendance").child(keyId).child(year).child(month).child(day).setValue(dayType);

    }

    public void addExitToAttendance(Date d, LinearLayout ll)
    {
        String year = yearFormat.format(d);
        String month = monthFormat.format(d);
        String day = dayFormat.format(d);
        String hour= hourFormat.format(d);
        myRef.child("Attendance").child(keyId).child(year).child(month).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild(day)) {
                    if ((snapshot.child(day).hasChild("entry"))&&(!(snapshot.child(day).hasChild("exit")))) {
                        myRef.child("Attendance").child(keyId).child(year).child(month).child(day).child("exit").setValue(hour); //if we build the DB as hashMap, then will change the Entry/Exit
                        if(DailyReport.isRunning) stopChrom();
                        String entryHour =snapshot.child(day).child("entry").getValue(String.class);
                        String exitHour =hour;
                        int t1 = getTotalMinutes(entryHour);
                        int t2 = getTotalMinutes(exitHour);
                        int result = Math.abs(t1 - t2);
                        String total = getResult(result);
                        myRef.child("Attendance").child(keyId).child(year).child(month).child(day).child("total").setValue(total);
                        Snackbar.make(ll, "Your total hours is " +total,
                                Snackbar.LENGTH_SHORT)
                                .show();

                    }
                }
                else {

                    int prevDay = Integer.parseInt(day)-1;
                    myRef.child("Attendance").child(keyId).child(year).child(month).child(Integer.toString(prevDay)).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if ((snapshot.hasChild("entry"))&&Integer.parseInt(hourOnlyFormat.format(d))<10) {
                                if (!(snapshot.hasChild("exit"))) {
                                    myRef.child("Attendance").child(keyId).child(year).child(month).child(Integer.toString(prevDay)).child("exit").setValue(hour); //if we build the DB as hashMap, then will change the Entry/Exit
                                    String entryHour =snapshot.child("entry").getValue(String.class);
                                    String exitHour =hour;
                                    int t1 = getTotalMinutes(entryHour);
                                    int t2 = getTotalMinutes(exitHour);
                                    int result = Math.abs(t1 - t2);
                                    String total = getResult(result);
                                    myRef.child("Attendance").child(keyId).child(year).child(month).child(Integer.toString(prevDay)).child("total").setValue(total); //if we build the DB as hashMap, then will change the Entry/Exit
                                    Snackbar.make(ll, "Your total hours is " +total,
                                            Snackbar.LENGTH_SHORT)
                                            .show();

                                }
                            }
                            else {
                                myRef.child("Attendance").child(keyId).child(year).child(month).child(day).child("entry").setValue("09:00:00"); //if we build the DB as hashMap, then will change the Entry/Exit
                                myRef.child("Attendance").child(keyId).child(year).child(month).child(day).child("exit").setValue(hour);
                                myRef.child("Attendance").child(keyId).child(year).child(month).child(day).child("total").setValue("00:00");//if we build the DB as hashMap, then will change the Entry/Exit
                                Snackbar.make(ll, "Your total hours is 0 ,no entry was registered" ,
                                        Snackbar.LENGTH_SHORT)
                                        .show();

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    //TODO: not finished. need loop for printing all the dates
    public DatabaseReference getAttendanceFromDB (String UserID,String year,String month)
    {
        return myRef.getRef().child("Attendance").child(UserID).child(year).child(month);
    }

    public DatabaseReference getAttendanceFromDBInDay (String UserID,String year,String month, String day)
    {
        return myRef.getRef().child("Attendance").child(UserID).child(year).child(month).child(day);
    }

    //TODO: not finished. need loop for printing all the dates
    public DatabaseReference getUserAttendanceFromDB (String UserID)
    {
        return myRef.getRef().child("Attendance").child(UserID);
    }


    public void startChrom(){

        chronomoter.setBase(SystemClock.elapsedRealtime());
        chronomoter.setText("00:00:00");
        chronomoter.start();
        DailyReport.isRunning=true;

    }

    public void stopChrom(){
        chronomoter.stop();
        DailyReport.isRunning=false;
    }


    public static int getTotalMinutes(String time) {
        String[] t = time.split(":");
        return Integer.valueOf(t[0]) * 60 + Integer.valueOf(t[1]);
    }

    public static String getResult(int total) {
        int minutes = total % 60;
        int hours = ((total - minutes) / 60) % 24;
        return String.format("%02d:%02d", hours, minutes);
    }




}
