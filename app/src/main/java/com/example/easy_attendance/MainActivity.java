package com.example.easy_attendance;


import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.easy_attendance.R;
import com.example.easy_attendance.firebase.model.FBAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText emailEditText, passwordEditText;
    private Button register, login;
    FBAuth auth = new FBAuth();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        register = findViewById(R.id.registerBtn);
        login = findViewById(R.id.loginBtn);
        emailEditText = (EditText) findViewById(R.id.editTextTextEmail);
        passwordEditText = (EditText) findViewById(R.id.editTextTextPassword);

        register.setOnClickListener(this);
        login.setOnClickListener(this);
    }

        @Override
    public void onClick(View v) {
        if (v == login) {
            Log.d("login btn was pressed" , "lets see if it work");
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (TextUtils.isEmpty(email)) {
                emailEditText.setError("Email is required");

            }
            if (TextUtils.isEmpty(password)) {
                passwordEditText.setError("Password is required");
            } else {

                auth.validationUser(email, password,MainActivity.this);
            }

        }
        if (v == register) {
            Intent intent = new Intent(MainActivity.this, RegistrationPage.class);
            startActivity(intent);
        }


    }

}