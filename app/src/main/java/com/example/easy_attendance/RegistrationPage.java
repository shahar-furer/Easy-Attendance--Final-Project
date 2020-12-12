package com.example.easy_attendance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.view.View;
import android.widget.Toast;

import com.example.easy_attendance.firebase.model.FBAuth;

public class RegistrationPage extends AppCompatActivity implements View.OnClickListener {

    private EditText fname ,lname ,email ,password, id ,orgKey;
    private Button register;
    private Switch isManager;
    FBAuth auth= new FBAuth();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_page);

        register = findViewById(R.id.registerBtn);
        fname = findViewById(R.id.editTextFirstName);
        lname = (EditText) findViewById(R.id.editTextLastName);
        email = (EditText) findViewById(R.id.editTextEmail);
        password = (EditText) findViewById(R.id.editTextPassword);
        id = (EditText) findViewById(R.id.editTextId);
        orgKey = (EditText) findViewById(R.id.editTextOrgKey);
        isManager = (Switch) findViewById(R.id.isManager);
        register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){

        String fNt=fname.getText().toString().trim();
        String lNt=lname.getText().toString().trim();
        String orKeyt=orgKey.getText().toString().trim();
        String idt=id.getText().toString().trim();
        String et=email.getText().toString().trim();
        String pt=password.getText().toString().trim();
        Boolean isMan= isManager.isChecked();

        if (fNt.isEmpty()) {
            fname.setError("First Name is required");
        }
        if (lNt.isEmpty()) {
            lname.setError("Last Name is required");
        }

        if (et.isEmpty()) {
            email.setError("Email is required");
        }
        if (pt.isEmpty()) {
            password.setError("Password is required");
        }

        if (idt.isEmpty()) {
            password.setError("Password is required");
        }


        if (et == null||!Patterns.EMAIL_ADDRESS.matcher(et).matches()) {
            Toast.makeText(getApplicationContext(),"Invalid email",Toast.LENGTH_LONG).show();
            return;
        }
        if(pt == null || pt.trim().length() <= 3)
        {
            Toast.makeText(getApplicationContext(),"Password length must be at least 4", Toast.LENGTH_LONG).show();
            return;
        }
        auth.registerUserToDB(orKeyt,idt,fNt,lNt,et,pt,isMan, RegistrationPage.this);
        Intent intent = new Intent(RegistrationPage.this, MainActivity.class);
        startActivity(intent);
    }



}