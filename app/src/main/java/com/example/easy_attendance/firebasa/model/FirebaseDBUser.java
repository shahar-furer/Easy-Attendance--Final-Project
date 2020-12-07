package com.example.easy_attendance.firebasa.model;

import com.example.easy_attendance.firebase.model.dataObject.UserObj;

public class FirebaseDBUser extends FirebaseBaseModel
{
    public void addUserToDB(int userNumber, String fName, String lName, String email, String password)
    {
        writeNewUser(userNumber, fName, lName, email, password);
    }

    private void writeNewUser(int userNumber, String fName, String lName, String email, String password)
    {
        UserObj useRej = new UserObj(userNumber, fName, lName, email, password);
        //myRef.child("users").child(userNumber).setValue(useRej);
    }

    /*public DatabaseReference getUserFromDB (String userNumber)
    {
        return myRef.getRef().child("users").child(userNumber);
    }
    */
}
