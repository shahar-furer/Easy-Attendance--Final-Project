package com.example.easy_attendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.easy_attendance.firebase.model.FBAuth;
import com.example.easy_attendance.firebase.model.FirebaseDBUser;
import com.example.easy_attendance.firebase.model.dataObject.Model;
import com.example.easy_attendance.firebase.model.listviewAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class WorkersList extends Menu implements View.OnClickListener{
    String [] idArray;
    FBAuth mAuth = new FBAuth();
    String uid = mAuth.getUserID();
    FirebaseDBUser userDB = new FirebaseDBUser();
    DatabaseReference userRef = userDB.getUserFromDB(uid);
    DatabaseReference orgRef;
    String orgKey;
    String workerName;
    private ArrayList<Model> workersList;
    listviewAdapter adapter;
    LinearLayout lL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workers_list);
        lL =findViewById(R.id.massage);
        workersList = new ArrayList<Model>();
        ListView lview = (ListView) findViewById(R.id.listview);
        adapter = new listviewAdapter(this, workersList, lL);
        lview.setAdapter(adapter);



        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                updateOrgKey(dataSnapshot);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        lview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String ID = ((TextView)view.findViewById(R.id.id)).getText().toString();
                String Name = ((TextView)view.findViewById(R.id.name)).getText().toString();

                Toast.makeText(getApplicationContext(),
                        "ID : " + ID +"\n"
                                +"Name : " + Name +"\n", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateOrgKey(DataSnapshot dataSnapshot) {
        orgKey = dataSnapshot.child("orgKey").getValue(String.class);
        orgRef = userDB.getOrganization(orgKey);

        orgRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long numOfEmployees = (dataSnapshot.getChildrenCount());
                idArray = new String[(int)numOfEmployees] ;
                int count = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if (snapshot.getKey() == "Manager")
                        continue;
                    idArray[count] = snapshot.getKey();
                    count++;
                }

                workerInformation();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


    }

    public void workerInformation() //(String uid)
    {
        userRef = userDB.getAllUsers();  // or: userRef = userDB.getUserFromDB(uid);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    for(int i = 0; i < idArray.length; i++)
                    {
                        if (snapshot.child("ID").getValue(String.class).equals(idArray [i]))
                        {
                            String FBid = snapshot.getKey();
                            Log.d("FBid", "onDataChange: "+ FBid);
                            String fName = snapshot.child("fName").getValue(String.class);
                            String lName = snapshot.child("lName").getValue(String.class);
                            Double Salary = snapshot.child("hourlyPay").getValue(Double.class);
                            workerName = fName+ " "+ lName;
                            Model model = new Model(FBid, idArray[i], workerName, Salary);
                            workersList.add(model);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }


    @Override
    public void onClick(View v) {

    }
}