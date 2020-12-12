package com.example.easy_attendance;

import androidx.appcompat.app.AppCompatActivity;

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

public class HomePage extends AppCompatActivity  implements View.OnClickListener  {

    private Button start , end;
    Boolean entryPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        start = findViewById(R.id.startBtn);
        end = findViewById(R.id.endBtn);

        start.setOnClickListener(this);
        end.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v== start) {
            boolean entryPressed = true;

        }

    }
}