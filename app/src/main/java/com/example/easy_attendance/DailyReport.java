package com.example.easy_attendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


import android.widget.Toast;


import com.example.easy_attendance.firebase.model.FBAuth;
import com.example.easy_attendance.firebase.model.FirebaseDBTable;


public class DailyReport extends AppCompatActivity  implements View.OnClickListener {

    private Button start, end, logout;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bottom_nav_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_daily_report:
                Toast.makeText(this, "Clicked Menu 1", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(DailyReport.this, DailyReport.class);
                startActivity(intent);
                break;
            case R.id.menu_monthly_report:
                Toast.makeText(this, "Clicked Menu 2", Toast.LENGTH_SHORT).show();
                Intent mintent = new Intent(DailyReport.this, MonthlyReport.class);
                startActivity(mintent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
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