package com.example.easy_attendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.easy_attendance.firebase.model.FBAuth;
import com.example.easy_attendance.firebase.model.FirebaseDBUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


public class MonthlyReport extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Spinner spinnerYear, spinnerMonth, spinnerWorker;
    private TextView textWorker;
    String [] workers;
    String[] years = {"test", "test2"};
    FBAuth mAuth = new FBAuth();
    String uid = mAuth.getUserID();
    FirebaseDBUser userDB = new FirebaseDBUser();
    DatabaseReference userRef = userDB.getUserFromDB(uid);
    DatabaseReference orgRef;
    Boolean isManager;
    String keyId;
    String orgKey;
    int numOfEmployees;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monthly_report);

        spinnerYear = (Spinner) findViewById(R.id.spinnerYear);
        spinnerMonth = (Spinner) findViewById(R.id.spinnerMonth);
        spinnerWorker = (Spinner) findViewById(R.id.spinnerWorker);
        textWorker = (TextView) findViewById(R.id.txtWorker);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                isManager = dataSnapshot.child("isManager").getValue(Boolean.class);
                keyId = dataSnapshot.child("ID").getValue(String.class);
                orgKey = dataSnapshot.child("orgKey").getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        if(!(isManager))
        {
            spinnerWorker.setVisibility(View.GONE);
            textWorker.setVisibility(View.GONE);

        }
        else
        {
            orgRef = userDB.getOrganization(orgKey);
            orgRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int count = 0;
                    long numOfW = (dataSnapshot.getChildrenCount())-1;
                    workers = new String[(int)numOfW] ;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot.getKey() == "Manager")
                            continue;
                        workers[count]= snapshot.getKey();
                        count++;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }



            });
            ArrayAdapter<String> adapterWorkers = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, workers);
            adapterWorkers.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinnerWorker.setAdapter(adapterWorkers);
            spinnerWorker.setOnItemSelectedListener(this);
        }


        ArrayAdapter<String> adapterYear = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, years);
        adapterYear.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<String> adapterMonth = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.month));
        adapterMonth.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        spinnerMonth.setAdapter(adapterMonth);
        spinnerMonth.setOnItemSelectedListener(this);


    }
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position,long id) {
        Toast.makeText(getApplicationContext(), "Selected Month: "+getResources().getStringArray(R.array.month)[position] ,Toast.LENGTH_SHORT).show();
        Toast.makeText(getApplicationContext(), "Selected Worker: "+workers[position] ,Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO - Custom Code
    }
}