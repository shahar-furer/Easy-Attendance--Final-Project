package com.example.easy_attendance;

import androidx.appcompat.app.AppCompatActivity;
import java.util.Date;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import android.widget.Toast;


import com.example.easy_attendance.firebase.model.FBAuth;
import com.example.easy_attendance.firebase.model.FirebaseDBTable;


public class DailyReport extends AppCompatActivity  implements View.OnClickListener {

    private Button start, end, logout;
    Boolean entryPressed = false;
    Boolean exitPressed = false;
    FirebaseDBTable newAttendance = new FirebaseDBTable();
    FBAuth auth = new FBAuth();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.daily_report);
        start = findViewById(R.id.startBtn);
        end = findViewById(R.id.endBtn);
        logout = findViewById(R.id.logoutBtn);

        start.setOnClickListener(this);
        end.setOnClickListener(this);
        logout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == start) {
            newAttendance.addEntryToDB(new Date(), "entry");
        }

        if (v == end) {
            newAttendance.addExitToAttendance(new Date());
        }

        if (v == logout) {
            Toast.makeText(getApplicationContext(), "logout button was pressed", Toast.LENGTH_LONG).show();

            auth.signOut();
            Intent intent = new Intent(DailyReport.this, LoginPage.class);
            startActivity(intent);

        }

    }
}