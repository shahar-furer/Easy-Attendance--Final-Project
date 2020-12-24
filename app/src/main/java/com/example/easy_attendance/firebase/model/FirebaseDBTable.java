package com.example.easy_attendance.firebase.model;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.easy_attendance.firebase.model.FirebaseBaseModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
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

    public void addEntryToDB(Date d, String s)
    {

        String year = yearFormat.format(d);
        String month = monthFormat.format(d);
        String day = dayFormat.format(d);
        String hour= hourFormat.format(d);
        myRef.child("Attendance").child(keyId).child(year).child(month).addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot snapshot) {
            if (!(snapshot.hasChild(day))) {
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
    }

    private void addExitAttendance(String year ,String month,String day,String hour)
    {
        myRef.child("Attendance").child(keyId).child(year).child(month).child(day).child("entry").setValue(hour); //if we build the DB as hashMap, then will change the Entry/Exit
    }

    public void addExitToAttendance(Date d)
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
                                }
                            }
                            else {
                                myRef.child("Attendance").child(keyId).child(year).child(month).child(day).child("entry").setValue("09:00:00"); //if we build the DB as hashMap, then will change the Entry/Exit
                                myRef.child("Attendance").child(keyId).child(year).child(month).child(day).child("exit").setValue(hour); //if we build the DB as hashMap, then will change the Entry/Exit
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

   public DatabaseReference getAttendanceFromDB (String month) //not finished. need loop for printing all the dates
   {
       return myRef.getRef().child("Attendance").child(uid).child(month);
    }
}
