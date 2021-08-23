package com.example.easy_attendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.easy_attendance.firebase.model.FirebaseDBUser;
import com.example.easy_attendance.firebase.model.dataObject.UserObj;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class EditEmployeeDataActivity extends Menu {

    private EditText etSalary;
    private EditText etSickDays;
    private EditText etVacationDays;
    private Button btnUpdateData;

    private FirebaseDBUser fdbu;

    private String userID;
    private String userFbUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_employee_data);

        fdbu = new FirebaseDBUser();

        Bundle args = getIntent().getExtras();
        if(args != null) {
            userID = args.getString("userId");
            userFbUid = args.getString("userFbUid");
        }


        etSalary = findViewById(R.id.etSalary);
        etSickDays = findViewById(R.id.etSickDays);
        etVacationDays = findViewById(R.id.etVacationDays);
        btnUpdateData = findViewById(R.id.btnUpdateData);

        fdbu.getUserFromDB(userFbUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserObj user = snapshot.getValue(UserObj.class);

                etSalary.setText(""+user.hourlyPay);
                etSickDays.setText(""+user.SickDays);
                etVacationDays.setText(""+user.daysOff);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        btnUpdateData.setOnClickListener(this::updateData);
    }

    private void updateData(View view) {
        if(!isFormValid()){
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
        }

        double salary = Double.parseDouble(etSalary.getText().toString());
        int sickDays = Integer.parseInt(etSickDays.getText().toString());
        int vacationDays = Integer.parseInt(etVacationDays.getText().toString());



        fdbu.writeNewEmployeeData(userFbUid, salary, sickDays, vacationDays);
    }

    private boolean isFormValid() {
        return !etSalary.getText().toString().isEmpty() &&
                !etSickDays.getText().toString().isEmpty() &&
                !etVacationDays.getText().toString().isEmpty();
    }
}