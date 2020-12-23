package com.example.easy_attendance.firebase.model;

import android.util.Log;

import com.example.easy_attendance.firebase.model.dataObject.UserObj;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

import static android.content.ContentValues.TAG;

public class FirebaseDBUser extends FirebaseBaseModel
{
    static int counter=2;

    public void addUserToDB(String userID,String orgKey, String keyID, String fName, String lName, String email, String password, boolean isManager)
    {
        writeNewUser(userID , orgKey, keyID, fName, lName, email, password, isManager);
    }

    private void writeNewUser(String userID ,String orgKey, String keyID, String fName, String lName, String email, String password, boolean isManager)
    {
        UserObj userRej = new UserObj(orgKey ,keyID,  fName, lName,email, password, isManager);
        myRef.child("Users").child(userID).setValue(userRej);
        writeUserToOrg(orgKey , keyID ,isManager);
    }

    private void writeUserToOrg(String orgKey , String keyID , boolean isManager)
    {
        HashMap hm = new HashMap();
        hm.put("isManager" ,isManager);
        myRef.child("organization").child(orgKey).child(keyID).setValue(hm);


    }

    public DatabaseReference getUserFromDB (String userID)
    {
        return myRef.child("Users").child(userID);
    }

    public DatabaseReference getOrganization(String orgKey)
    {
        return myRef.getRef().child("organization").child(orgKey);
    }




}
