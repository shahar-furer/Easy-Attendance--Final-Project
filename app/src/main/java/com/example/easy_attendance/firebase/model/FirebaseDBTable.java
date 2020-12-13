package com.example.easy_attendance.firebase.model;
import com.example.easy_attendance.firebase.model.FirebaseBaseModel;
import com.example.easy_attendance.firebase.model.dataObject.TableObj;

import com.example.easy_attendance.firebase.model.dataObject.UserObj;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.Date;
import java.util.HashMap;


public class FirebaseDBTable extends FirebaseBaseModel {
    TableObj table;
    HashMap <String ,Object> entryAndExit;
    FBAuth mAuth = new FBAuth();
    String keyID = "33512";
    String uid = mAuth.getUserID();
            //mAuth.getCurrentUser().getUid();

    public FirebaseDBTable() {
        entryAndExit = new HashMap<>();
    }

    public void addEntryToDB(Date d, String s)
    {
        table = new TableObj(d, s);
        entryAndExit.put("entry" , table.getEnHour());
        writeNewAttendance();
    }

    private void writeNewAttendance()
    {
        String month = table.getEnMonth();
        String year = table.getEnYear(); //casting month to string- not finished
        myRef.child("Attendance").child(uid).child(year).child(month).child(table.getEnDay()).setValue(entryAndExit); //if we build the DB as hashMap, then will change the Entry/Exit
    }

    public void addExitToAttendance(Date d)
    {
        table.setExitDate(d);
        entryAndExit.put("exit" , table.getExHour());
        writeNewAttendance();
    }

   public DatabaseReference getAttendanceFromDB (String month) //not finished. need loop for printing all the dates
   {
       return myRef.getRef().child("Attendance").child(uid).child(month).getRef();
    }
}
