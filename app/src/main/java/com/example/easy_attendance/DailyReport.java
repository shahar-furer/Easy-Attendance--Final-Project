package com.example.easy_attendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.easy_attendance.firebase.model.FBAuth;
import com.example.easy_attendance.firebase.model.FirebaseDBTable;
import com.example.easy_attendance.firebase.model.FirebaseDBUser;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;


public class DailyReport extends AppCompatActivity  implements View.OnClickListener {

    private Button start, end, logout;
    private TextView helloUser;
    public Chronometer chrom;
    public static boolean isRunning;
    FirebaseDBTable newAttendance = new FirebaseDBTable();
    FirebaseDBUser userDB =new FirebaseDBUser();
    FBAuth auth = new FBAuth();
    String uid= auth.getUserID();
    String userName;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.daily_report);
        userDB =new FirebaseDBUser();
        start = findViewById(R.id.startBtn);
        end = findViewById(R.id.endBtn);
        logout = findViewById(R.id.logoutBtn);
        chrom = (Chronometer)findViewById(R.id.chronometerWatch);
        isRunning=false;
        helloUser= findViewById(R.id.textViewHello);

        start.setOnClickListener(this);
        end.setOnClickListener(this);
        logout.setOnClickListener(this);
//        chrom.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
//            @Override
//            public void onChronometerTick(Chronometer chronometerChanged) {
//                chrom = chronometerChanged;
//                long time = SystemClock.elapsedRealtime() - chrom.getBase();
//                int h   = (int)(time /3600000);
//                int m = (int)(time - h*3600000)/60000;
//                int s= (int)(time - h*3600000- m*60000)/1000 ;
//                String t = (h < 10 ? "0"+h: h)+":"+(m < 10 ? "0"+m: m)+":"+ (s < 10 ? "0"+s: s);
//                chrom.setText(t);
//            }
//        });
        findUserName();
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


    protected void onResume() {
        super.onResume();
        if(isRunning) chrom.start();
    }

    protected void onPause() {
        super.onPause();
        if(isRunning) chrom.stop();

    }

    protected void onDestroy() {
        super.onDestroy();
        if(isRunning) chrom.stop();

    }


    @Override
    public void onClick(View v) {
        if (v == start) {
            newAttendance.addEntryToDB(new Date(), chrom);

        }

        if (v == end) {
            LinearLayout ll =findViewById(R.id.totalHours);
            newAttendance.addExitToAttendance(new Date() , ll);



//            Snackbar.make(findViewById(R.id.totalHours), "Your total hours is" +total,
//                    Snackbar.LENGTH_SHORT)
//                    .show();
        }

        if (v == logout) {
            Toast.makeText(getApplicationContext(), "logout button was pressed", Toast.LENGTH_LONG).show();

            auth.signOut();
            Intent intent = new Intent(DailyReport.this, LoginPage.class);
            startActivity(intent);

        }

    }



    private void findUserName() {

       DatabaseReference userRef =userDB.getUserFromDB(uid);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               userName =dataSnapshot.child("fName").getValue(String.class);
                helloUser.append("Hello "+userName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


    }



}