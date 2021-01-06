package com.example.easy_attendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.view.View;
import android.widget.Toast;




import com.example.easy_attendance.firebase.model.FBAuth;
import com.example.easy_attendance.firebase.model.FirebaseDBUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class RegistrationPage extends AppCompatActivity {

    private EditText fname ,lname ,emailReg ,passwordReg, id ,orgKey;
    private Button register;
    private Switch isManager;
    FBAuth auth;
    FirebaseDBUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_page);
        auth= new FBAuth();
        user = new FirebaseDBUser();
        register = findViewById(R.id.registerBtn);
        fname = findViewById(R.id.editTextTextFirstName);
        lname = (EditText) findViewById(R.id.editTextTextLastName);
        emailReg = (EditText) findViewById(R.id.editTextTextEmailReg);
        passwordReg = (EditText) findViewById(R.id.editTextTextPasswordReg);
        id = (EditText) findViewById(R.id.editTextTextId);
        orgKey = (EditText) findViewById(R.id.editTextTextOrgKey);
        isManager = (Switch) findViewById(R.id.isManager);


        orgKey.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                String orKeyt = orgKey.getText().toString().trim();
                DatabaseReference userRef = user.getOrganization(orKeyt);

                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child("Manager").exists()){
                            Toast.makeText(getApplicationContext(), "manager already exists in this organization , registration denied", Toast.LENGTH_SHORT).show();
                            isManager.setChecked(false);
                            isManager.setEnabled(false);
                        }
                        else {
                            isManager.setEnabled(true);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }

                });
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });



        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String fNt = fname.getText().toString().trim();
                String lNt = lname.getText().toString().trim();
                String orKeyt = orgKey.getText().toString().trim();
                String idt = id.getText().toString().trim();
                String et = emailReg.getText().toString().trim();
                String pt = passwordReg.getText().toString().trim();
                Boolean isMan = isManager.isChecked();



                if (fNt.isEmpty()) {
                    fname.setError("First Name is required");
                }
                if (lNt.isEmpty()) {
                    lname.setError("Last Name is required");
                }

                if (et.isEmpty()) {
                    emailReg.setError("Email is required");
                }
                if (pt.isEmpty()) {
                    passwordReg.setError("Password is required");
                }

                if (orKeyt.isEmpty()) {
                    orgKey.setError("organization id is required");
                }

                if (idt.isEmpty()) {
                    id.setError("id is required");
                }


                if (et == null || !Patterns.EMAIL_ADDRESS.matcher(et).matches()) {
                    Toast.makeText(getApplicationContext(), "Invalid email", Toast.LENGTH_LONG).show();
                    return;
                }
                if (pt == null || pt.trim().length() <= 5) {
                    Toast.makeText(getApplicationContext(), "Password length must be at least 6", Toast.LENGTH_LONG).show();
                    return;
                }


                DatabaseReference userRef = user.getAllUsers();
                final boolean[] isOK = {true};
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (snapshot.child("ID").getValue().equals(idt)) {
                                isOK[0] = false;
                                id.setError("id is already in use");
                                return;
                            }
                        }
                        if(isOK[0])
                            auth.registerUserToDB(orKeyt, idt, fNt, lNt, et, pt, isMan, RegistrationPage.this);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                    //danielle test



                });

            }
        });
    }

}