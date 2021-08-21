package com.example.easy_attendance;

import android.Manifest;
import android.app.Notification;
import android.content.Context;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.format.Time;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.easy_attendance.firebase.model.FBAuth;
import com.example.easy_attendance.firebase.model.FirebaseDBTable;
import com.example.easy_attendance.firebase.model.FirebaseDBUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.example.easy_attendance.MyNotification.CHANNEL_Update_HourlyPay;

public class DailyReport extends Menu implements View.OnClickListener {

    SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
    SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
    SimpleDateFormat dayFormat = new SimpleDateFormat("dd");

    private Button btnStart, btnEnd, logout, btnReportDayOff, btnReportSick;
    private TextView helloUser;
    private TextView date;
    public Chronometer chrom;
    public static boolean isRunning;
    DatabaseReference messageRef;
    FirebaseDBTable newAttendance = new FirebaseDBTable();
    FirebaseDBUser userDB =new FirebaseDBUser();
    DatabaseReference orgRef;
    FBAuth auth = new FBAuth();
    String uid= auth.getUserID();
    String userName ,ID, orgKey;
    double workLat, workLong;
    LocationCoords loc;
    String isInWorkLocation = "0";
    LinearLayout ll;

    MutableLiveData<String> listen = new MutableLiveData<>();

    private final int REQUEST_LOCATION_PERMISSION = 1;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(REQUEST_LOCATION_PERMISSION)
    public boolean requestLocationPermission() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
        if(EasyPermissions.hasPermissions(this, perms)) {
            startTimerByOrgLocation();
        }
        else {
            EasyPermissions.requestPermissions(this, "Please grant the location permission", REQUEST_LOCATION_PERMISSION, perms);
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.daily_report);
        requestLocationPermission();
        listen.setValue(isInWorkLocation); //Initilize with a value
        userDB =new FirebaseDBUser();
        btnStart = findViewById(R.id.startBtn);
        btnEnd = findViewById(R.id.endBtn);
        chrom = (Chronometer)findViewById(R.id.chronometerWatch);
        isRunning=false;
        helloUser= findViewById(R.id.textViewHello);
        date = findViewById(R.id.textViewDate);
        ll =findViewById(R.id.totalHours);
        btnReportDayOff = findViewById(R.id.btnReportVaction);
        btnReportSick = findViewById(R.id.btnReportSick);
        findUserName();




        btnStart.setOnClickListener(this);
        btnEnd.setOnClickListener(this);
        btnReportDayOff.setOnClickListener(this);
        btnReportSick.setOnClickListener(this);
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
        listen.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String changedValue) {
                if (changedValue.equals("1")){
                    newAttendance.addEntryToDB(new Date(), chrom ,ll);
                }
            }
        });

    }

    private void updateStartTime() {
        Date currentTime = Calendar.getInstance().getTime();


        String year = yearFormat.format(currentTime);
        String month = monthFormat.format(currentTime);
        String day = dayFormat.format(currentTime);

        newAttendance.getAttendanceFromDBInDay(ID, year, month, day)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String entryString = snapshot.child("entry").getValue(String.class);
                        String endString = snapshot.child("exit").getValue(String.class);

                        if(entryString == null)
                            return;

                        String[] splits = entryString.split(":");


                        Date currentTime = Calendar.getInstance().getTime();

                        Time now = new Time();
                        if(endString == null) {
                            now.hour = currentTime.getHours();
                            now.minute = currentTime.getMinutes();
                            now.second = currentTime.getSeconds();
                        }
                        else {
                            String[] splitsEnd = endString.split(":");

                            now.hour = Integer.parseInt(splitsEnd[0]);
                            now.minute = Integer.parseInt(splitsEnd[1]);
                            now.second = Integer.parseInt(splitsEnd[2]);
                        }

                        Time entry = new Time();
                        entry.hour = Integer.parseInt(splits[0]);
                        entry.minute = Integer.parseInt(splits[1]);
                        entry.second = Integer.parseInt(splits[2]);

                        long millisPassed = now.toMillis(true) - entry.toMillis(true);
                        int minutePassed = (int) ((millisPassed / 1000) / 60);
                        int secondsPassed = (int) ((millisPassed / 1000) % 60);

                        chrom.setBase(SystemClock.elapsedRealtime() - (minutePassed * 60000 + secondsPassed * 1000));

                        if(endString == null) {
                            btnStart.setEnabled(false);
                            btnEnd.setEnabled(true);
                            isRunning = true;
                            chrom.start();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


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
        if (v == btnStart) {
            newAttendance.addEntryToDB(new Date(), chrom ,ll);
            btnStart.setEnabled(false);
            btnEnd.setEnabled(true);
        }

        if (v == btnEnd) {

            newAttendance.addExitToAttendance(new Date() , ll);
            btnStart.setEnabled(true);
            btnEnd.setEnabled(false);



//            Snackbar.make(findViewById(R.id.totalHours), "Your total hours is" +total,
//                    Snackbar.LENGTH_SHORT)
//                    .show();
        }

        if(v == btnReportDayOff){
            newAttendance.reportAs(new Date(), "vacation");
            chrom.stop();
        }

        if(v == btnReportSick){
            newAttendance.reportAs(new Date(), "sick");
            chrom.stop();
        }


    }



    private void findUserName() {

        DatabaseReference userRef = userDB.getUserFromDB(uid);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userName =dataSnapshot.child("fName").getValue(String.class);
                ID=dataSnapshot.child("ID").getValue(String.class);

                updateStartTime();

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

    private void startTimerByOrgLocation() {
        DatabaseReference userRef =userDB.getUserFromDB(uid);
        DailyReport activity = this;
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                orgKey =dataSnapshot.child("orgKey").getValue(String.class);
                orgRef = userDB.getOrganization(orgKey);
                orgRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        workLat = (Double) dataSnapshot.child("LocationLat").getValue();
                        workLong = (Double) dataSnapshot.child("LocationLong").getValue();

                        loc = new LocationCoords(workLong, workLat);
                        final LocationService locationTracker = new LocationService(activity);
                        final LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                        if (locationTracker.getLastLocation(locationManager, loc)){
                            Toast.makeText(getBaseContext(), "You've arrived to your work place. Starting timer", Toast.LENGTH_SHORT).show();
                            isInWorkLocation = "1";
                            listen.setValue(isInWorkLocation); //Initilize with a value

                        }

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }

                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}