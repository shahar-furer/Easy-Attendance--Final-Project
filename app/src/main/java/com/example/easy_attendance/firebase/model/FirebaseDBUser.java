package com.example.easy_attendance.firebase.model;

import com.example.easy_attendance.firebase.model.FirebaseBaseModel;
import com.example.easy_attendance.firebase.model.dataObject.UserObj;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;

public class FirebaseDBUser extends FirebaseBaseModel
{
    public void addUserToDB(String userID,String orgKey, String keyID, String fName, String lName, String email, String password, boolean isManager)
    {
        writeNewUser(userID , orgKey, keyID, fName, lName, email, password, isManager);
    }

    private void writeNewUser(String userID ,String orgKey, String keyID, String fName, String lName, String email, String password, boolean isManager)
    {
        UserObj useRej = new UserObj(userID ,orgKey ,keyID, email, fName, lName, password, isManager);
        myRef.child("Users").child(keyID).setValue(useRej);
    }

    public DatabaseReference getUserFromDB (String keyID)
    {
        return myRef.getRef().child("Users").child(keyID);
    }



}
