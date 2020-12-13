package com.example.easy_attendance;

import androidx.appcompat.app.AppCompatActivity;
import java.util.Date;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import android.widget.Toast;


import com.example.easy_attendance.firebase.model.FBAuth;
import com.example.easy_attendance.firebase.model.FirebaseDBTable;


public class HomePage extends AppCompatActivity  implements View.OnClickListener  {

    private Button start , end , logout;
    Boolean entryPressed;
    FirebaseDBTable newAttendance = new FirebaseDBTable();
    FBAuth auth = new FBAuth();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        start = findViewById(R.id.startBtn);
        end = findViewById(R.id.endBtn);
        logout = findViewById(R.id.logoutBtn);

        start.setOnClickListener(this);
        end.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v== start) {
            entryPressed = true;
            newAttendance.addEntryToDB(new Date() ,"entry");
        }

        if(v == end) {
            if (entryPressed == true) {
                newAttendance.addExitToAttendance(new Date());
            }
            else {
                newAttendance.addEntryToDB(new Date() , "entry");
                newAttendance.addExitToAttendance(new Date());
                Toast.makeText(getApplicationContext(), "Entry was not registered , need to change manually", Toast.LENGTH_LONG).show();
            }
        }
        if( v== logout) {
            Toast.makeText(getApplicationContext(), "logout button was pressed", Toast.LENGTH_LONG).show();

            auth.signOut();

        }

    }
}