package com.example.easy_attendance;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;


public class MonthlyReport extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    String[] workers ={"test", "test1"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monthly_report);

        Spinner spinnerMonth = (Spinner) findViewById(R.id.spinnerMonth);
        Spinner spinnerWorker = (Spinner) findViewById(R.id.spinnerWorker);

        ArrayAdapter<String> adapterMonth = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.month));
        adapterMonth.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<String> adapterWorkers = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, workers);
        adapterMonth.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerMonth.setAdapter(adapterMonth);
        spinnerMonth.setOnItemSelectedListener(this);

        spinnerWorker.setAdapter(adapterWorkers);
        spinnerWorker.setOnItemSelectedListener(this);
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