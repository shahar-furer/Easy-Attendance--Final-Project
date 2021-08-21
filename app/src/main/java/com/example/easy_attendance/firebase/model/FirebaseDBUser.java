package com.example.easy_attendance.firebase.model;

import android.util.Log;

import com.example.easy_attendance.firebase.model.dataObject.UserObj;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class FirebaseDBUser extends FirebaseBaseModel
{

    public void addUserToDB(String userID,String orgKey, String ID, String fName, String lName, String email,  boolean isManager)
    {
        writeNewUser(userID , orgKey, ID, fName, lName, email, isManager);
    }

    private void writeNewUser(String userID ,String orgKey, String ID, String fName, String lName, String email, boolean isManager)
    {
        UserObj userRej = new UserObj(orgKey ,ID,  fName, lName ,email, isManager);
        myRef.child("Users").child(userID).setValue(userRej);
        writeUserToOrg(orgKey , ID ,fName ,lName,isManager);
    }

    private void writeUserToOrg(String orgKey , String ID,String fName,String lName , boolean isManager)
    {

        if(isManager) {
            myRef.child("organization").child(orgKey).child("Manager").setValue(ID);
        }
        myRef.child("organization").child(orgKey).child(ID).setValue(fName+"-"+lName);


    }

    public DatabaseReference getUserFromDB (String userID)
    {
        return myRef.child("Users").child(userID);
    }

    public DatabaseReference getOrganization(String orgKey)
    {
        return myRef.getRef().child("organization").child(orgKey);
    }

    public DatabaseReference getAllUsers()
    {
        return myRef.getRef().child("Users");
    }

    public DatabaseReference getAllMessages()
    {
        return myRef.getRef().child("Messages");
    }

    public void deleteMessages(String ID)
    {
        myRef.getRef().child("Messages").child(ID).removeValue();
    }

//
//    public void getCurrentEmployeeData(String userID) {
//        myRef.child("Users").child(userID);
//    }

    public void writeNewEmployeeData(String userID, double salary, int sickDays, int vacationDays)
    {

        Map<String, Object> children = new HashMap<>();
        children.put("hourlyPay", salary);
        children.put("SickDays", sickDays);
        children.put("daysOff", vacationDays);
        myRef.child("Users").child(userID).updateChildren(children);

//        myRef.child("Attendance").child(keyId).child(year).child(month).child(day).child("entry").setValue(hour); //if we build the DB as hashMap, then will change the Entry/Exit
//        myRef.child("Attendance").child(keyId).child(year).child(month).child(day).child("total").setValue("00:00");
    }



}
