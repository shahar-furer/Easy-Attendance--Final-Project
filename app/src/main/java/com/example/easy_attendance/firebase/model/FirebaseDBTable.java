package com.example.easy_attendance.firebase.model;
import com.example.easy_attendance.firebase.model.FirebaseBaseModel;
import com.example.easy_attendance.firebase.model.dataObject.TableObj;

import com.example.easy_attendance.firebase.model.dataObject.UserObj;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;

import java.util.Date;
import java.util.HashMap;


public class FirebaseDBTable extends FirebaseBaseModel {
    TableObj table;
    HashMap <String ,Object> entryAndExit;

    public FirebaseDBTable() {
        entryAndExit = new HashMap<>();
    }

    public void addEntryToDB(UserObj user, Date d, String s)
    {
        table = new TableObj(d, s);
        entryAndExit.put(table.getMonth() , table.getDate());
        writeNewAttendance(user, d, s);
    }

    private void writeNewAttendance(UserObj user, Date d, String s)
    {
        String month = table.getMonth(); //casting month to string- not finished
        myRef.child("Attendance").child(user.keyID).child(month).child(table.getIsEntryExit()).setValue(entryAndExit); //if we build the DB as hashMap, then will change the Entry/Exit
    }

    private void addExitToAttendance(Date d)
    {
        table.setExitDate(d);
        entryAndExit.put(table.getMonth() , table);
    }

   public DatabaseReference getAttendanceFromDB (UserObj user, String month) //not finished. need loop for printing all the dates
   {
       return myRef.getRef().child("Attendance").child(user.keyID).child(month).getRoot();
    }
}
