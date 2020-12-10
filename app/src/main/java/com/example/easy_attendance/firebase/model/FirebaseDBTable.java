package com.example.easy_attendance.firebase.model;
import com.example.easy_attendance.firebase.model.FirebaseBaseModel;
import com.example.easy_attendance.firebase.model.dataObject.TableObj;

import com.example.easy_attendance.firebase.model.dataObject.UserObj;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;

import java.util.Date;


public class FirebaseDBTable extends FirebaseBaseModel {
    public void addEntryToDB(UserObj user, Date d, String s)
    {
        writeNewAttendance(user, d, s);
    }

    private void writeNewAttendance(UserObj user, Date d, String s)
    {
        TableObj table = new TableObj(d, s);
        String month = table.getMonth(); //casting month to string- not finished
        myRef.child("Months").child(user.keyID).child(month).child(table.getIsEntryExit()).setValue(table.getDate().getTime()); //if we build the DB as hashMap, then will change the Entry/Exit
    }

   public DatabaseReference getAttendanceFromDB (UserObj user, String month) //not finished. need loop for printing all the dates
   {
       return myRef.getRef().child("Months").child(user.keyID).child(month).getRoot();
    }
}
