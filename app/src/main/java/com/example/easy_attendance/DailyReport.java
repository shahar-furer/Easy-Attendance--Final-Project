package com.example.easy_attendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
import static com.example.easy_attendance.MyNotification.CHANNEL_Update_HourlyPay;


public class DailyReport extends Menu implements View.OnClickListener {

    private Button start, end, logout;
    private TextView helloUser;
    private TextView date;
    public Chronometer chrom;
    public static boolean isRunning;
    DatabaseReference messageRef;
    FirebaseDBTable newAttendance = new FirebaseDBTable();
    FirebaseDBUser userDB =new FirebaseDBUser();
    FBAuth auth = new FBAuth();
    String uid= auth.getUserID();
    String userName ,ID;
    LinearLayout ll;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.daily_report);
        userDB =new FirebaseDBUser();
        start = findViewById(R.id.startBtn);
        end = findViewById(R.id.endBtn);
        chrom = (Chronometer)findViewById(R.id.chronometerWatch);
        isRunning=false;
        helloUser= findViewById(R.id.textViewHello);
        date = findViewById(R.id.textViewDate);
         ll =findViewById(R.id.totalHours);
        findUserName();

        start.setOnClickListener(this);
        end.setOnClickListener(this);
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

    }




    protected void onResume() {
        super.onResume();
        if(isRunning == true) chrom.start();
    }

    protected void onPause() {
        super.onPause();
        if(isRunning == true) chrom.stop();

    }

    protected void onDestroy() {
        super.onDestroy();
        if(isRunning == true) chrom.stop();

    }


    @Override
    public void onClick(View v) {
        if (v == start) {
            newAttendance.addEntryToDB(new Date(), chrom ,ll);

        }

        if (v == end) {

            newAttendance.addExitToAttendance(new Date() , ll);



//            Snackbar.make(findViewById(R.id.totalHours), "Your total hours is" +total,
//                    Snackbar.LENGTH_SHORT)
//                    .show();
        }


    }



    private void findUserName() {

       DatabaseReference userRef =userDB.getUserFromDB(uid);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               userName =dataSnapshot.child("fName").getValue(String.class);
               ID=dataSnapshot.child("ID").getValue(String.class);
                helloUser.append("Hello "+userName);
                date.append(new Date().toString());
                checkMessege();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


    }

    private void checkMessege()
    {
        messageRef = userDB.getAllMessages();
        messageRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
              if(snapshot.hasChild(ID)) {
                  String Title = snapshot.child(ID).child("Title").getValue().toString();
                  String Text = snapshot.child(ID).child("Text").getValue().toString();
                  userDB.deleteMessages(ID);
                  sendNotification(Title ,Text);

              }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void sendNotification(String Title , String Text){
        NotificationManagerCompat NM = NotificationManagerCompat.from(this);
        Notification notification = new NotificationCompat.Builder(DailyReport.this, CHANNEL_Update_HourlyPay)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(Title)
                .setContentText(Text)
                .build();
        NM.notify(2,notification);
    }



}