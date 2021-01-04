package com.example.easy_attendance;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Spinner;

import com.example.easy_attendance.R;
import com.example.easy_attendance.firebase.model.FBAuth;
import com.example.easy_attendance.firebase.model.FirebaseDBTable;
import com.example.easy_attendance.firebase.model.FirebaseDBUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


public class CalculateSallary extends AppCompatActivity {
    private Button calc;
    private Spinner month , year;
    private double hourlyPay;
    String [] months;
    String [] Years;
    FBAuth auth = new FBAuth();
    String uid= auth.getUserID();
    FirebaseDBUser fdbu ;
    FirebaseDBTable fdbt;
    DatabaseReference userRef;
    DatabaseReference tableRef;
    String userID;
    String orgKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calculate_sallary);
        month = (Spinner) findViewById(R.id.spinnerMonthC);
        calc= findViewById(R.id.calculateButton);
        userRef=fdbu.getUserFromDB(uid);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userID =dataSnapshot.child("ID").getValue(String.class);
                String hourlyP = dataSnapshot.child("hourlyPay").getValue(String.class);
                hourlyPay = Double.parseDouble(hourlyP);
                orgKey = dataSnapshot.child("orgKey").getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }
}