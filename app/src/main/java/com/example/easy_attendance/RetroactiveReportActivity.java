package com.example.easy_attendance;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.easy_attendance.R;
import com.example.easy_attendance.firebase.model.FirebaseDBTable;

public class RetroactiveReportActivity extends Menu implements DatePickerDialog.OnDateSetListener {

    private TextView txtDate;
    private EditText etHourStart;
    private EditText etHourEnd;
    private LinearLayout layoutHourStart;
    private LinearLayout layoutHourEnd;

    private Button btnUpdate;
    private Spinner spinnerDayType;
    private int selectedDayType;
    private boolean canUpdate = false;  // if the dorm is valid

    private FirebaseDBTable fdbt = new FirebaseDBTable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retroactive_report);

        txtDate = findViewById(R.id.txtDate);
        etHourStart = findViewById(R.id.etStartHour);
        etHourEnd = findViewById(R.id.etEndHour);
        btnUpdate = findViewById(R.id.btnUpdateDay);
        spinnerDayType = findViewById(R.id.spinnerDayType);

        layoutHourStart = findViewById(R.id.layoutStartHour);
        layoutHourEnd = findViewById(R.id.layoutEndHour);

        final ArrayAdapter monthAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, android.R.id.text1,  getResources().getStringArray(R.array.dayTypes));
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerDayType.setAdapter(monthAdapter);
        spinnerDayType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                layoutHourStart.setVisibility(position == 0 ? View.VISIBLE : View.INVISIBLE);
                layoutHourEnd.setVisibility(position == 0 ? View.VISIBLE : View.INVISIBLE);
                selectedDayType = position;
                checkIfFormValid();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedDayType = 0;
            }
        });

        txtDate.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        btnUpdate.setOnClickListener(this::updateDay);

        txtDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkIfFormValid();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etHourStart.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkIfFormValid();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etHourEnd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkIfFormValid();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void checkIfFormValid() {
        if(selectedDayType == 0) {
            canUpdate = !txtDate.getText().toString().isEmpty() && !etHourStart.getText().toString().isEmpty() && !etHourEnd.getText().toString().isEmpty();
        }
        else {
            canUpdate = !txtDate.getText().toString().isEmpty();

        }

        btnUpdate.setEnabled(canUpdate);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showDatePicker(){
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, AlertDialog.THEME_HOLO_DARK, this, year, month, day);
        datePickerDialog.setTitle("Select Date");
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        txtDate.setText(dayOfMonth + "/" + (month+1) + "/" + year);
    }


    /// updates the database with
    private void updateDay(View view){
        String[] dateSplits = txtDate.getText().toString().split("/");
        String dayStr = dateSplits[0];
        String monthStr = Integer.parseInt(dateSplits[1]) >= 10 ? dateSplits[1] : "0" + dateSplits[1];
        String yearStr = dateSplits[2];

        String entryHour = etHourStart.getText().toString();
        String exitHour = etHourEnd.getText().toString();

        if(selectedDayType == 0){
            fdbt.writeAttendance(yearStr, monthStr, dayStr, entryHour, exitHour);
        }
        fdbt.writeSpecialDayAttendance(yearStr, monthStr, dayStr, selectedDayType);
        Toast.makeText(this, "We updated your data!", Toast.LENGTH_SHORT).show();
        clearForm();
    }

    private void clearForm() {
        etHourEnd.setText("");
        etHourStart.setText("");
    }


}