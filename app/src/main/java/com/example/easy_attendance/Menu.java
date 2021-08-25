package com.example.easy_attendance;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.easy_attendance.firebase.model.FirebaseDBUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class Menu extends AppCompatActivity {
    Intent intent;
    FirebaseAuth fba = FirebaseAuth.getInstance();
    FirebaseDBUser userDB = new FirebaseDBUser();
    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.bottom_nav_menu, menu);
        android.view.Menu optionsMenu = menu;

        DatabaseReference userRef =userDB.getUserFromDB(fba.getUid());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boolean isManager=dataSnapshot.child("isManager").getValue(Boolean.class);
                if (!(isManager)) {
                    optionsMenu.getItem(4).setVisible(false);
                    optionsMenu.getItem(5).setVisible(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_retroactive_report:
                intent = new Intent(this, RetroactiveReportActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_daily_report:
                intent = new Intent(this, DailyReport.class);
                startActivity(intent);
                break;
            case R.id.menu_monthly_report:
                intent = new Intent(this, MonthlyReport.class);
                startActivity(intent);
                break;

            case R.id.menu_calc_sallary:
                intent = new Intent(this, CalculateSallary.class);
                startActivity(intent);
                break;

            case R.id.menu_workers_list:
                intent = new Intent(this, WorkersList.class);
                startActivity(intent);
                break;

            case R.id.menu_org_location:
                intent = new Intent(this, OrgLocation.class);
                startActivity(intent);
                break;

            case R.id.menu_logout:
                FirebaseAuth.getInstance().signOut();
                intent = new Intent(this, LoginPage.class);
                startActivity(intent);


            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
