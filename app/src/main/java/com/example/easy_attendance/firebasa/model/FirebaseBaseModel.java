package com.example.easy_attendance.firebasa.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseBaseModel
{
    FirebaseDatabase mDatabase;
    DatabaseReference dbRootRef;

    public FirebaseBaseModel()
    {
        mDatabase = FirebaseDatabase.getInstance();
        dbRootRef = mDatabase.getReference();

    }
}
