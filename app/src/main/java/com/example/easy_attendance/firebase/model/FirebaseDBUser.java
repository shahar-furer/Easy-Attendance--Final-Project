package com.example.easy_attendance.firebase.model;

import com.example.easy_attendance.firebase.model.FirebaseBaseModel;
import com.example.easy_attendance.firebase.model.dataObject.UserObj;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;

public class FirebaseDBUser extends FirebaseBaseModel
{
    public void addUserToDB(String orgKey, String keyID, String fName, String lName, String email, String password, boolean isManager)
    {
        writeNewUser(orgKey, keyID, fName, lName, email, password, isManager);
    }

    private void writeNewUser(String orgKey, String keyID, String fName, String lName, String email, String password, boolean isManager)
    {
        UserObj useRej = new UserObj(orgKey ,keyID, email, fName, lName, password, isManager);
        myRef.child("Users").setValue(keyID);
        myRef.child("Users").child(keyID).setValue(useRej);
    }

    public DatabaseReference getUserFromDB (String keyID)
    {
        return myRef.getRef().child("Users").child(keyID);
    }



}
