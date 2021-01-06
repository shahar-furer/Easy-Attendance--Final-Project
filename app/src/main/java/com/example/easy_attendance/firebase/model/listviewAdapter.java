package com.example.easy_attendance.firebase.model;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.example.easy_attendance.R;
import com.example.easy_attendance.firebase.model.dataObject.Model;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class listviewAdapter extends BaseAdapter {

    public ArrayList<Model> workersList;
    Activity activity;

    FBAuth mAuth = new FBAuth();
    String uid = mAuth.getUserID();
    FirebaseDBUser userDB = new FirebaseDBUser();
    DatabaseReference userRef = userDB.getUserFromDB(uid);

    public listviewAdapter(Activity activity, ArrayList<Model> workersList) {
        super();
        this.activity = activity;
        this.workersList = workersList;
    }

    @Override
    public int getCount() {
        return workersList.size();
    }

    @Override
    public Object getItem(int position) {
        return workersList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    private class ViewHolder {
        TextView mID;
        TextView mName;

        EditText newPass;
        EditText newSalary;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        LayoutInflater inflater = activity.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listview_row, null);
            holder = new ViewHolder();
            holder.mID = (TextView) convertView.findViewById(R.id.id);
            holder.mName = (TextView) convertView.findViewById(R.id.name);
            holder.newPass = (EditText) convertView.findViewById(R.id.editPassword);
            holder.newSalary = (EditText) convertView.findViewById(R.id.changeSalary);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Model item = workersList.get(position);
        holder.mID.setText(item.getID().toString());
        holder.mName.setText(item.getName().toString());

        holder.newPass.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && holder.newPass.getText() !=null)
                {
                    userRef = userDB.getUserFromDB(workersList.get(position).getFBid());
                    userRef.child("password").setValue(holder.newPass.getText().toString());
                }
            }
        });

        holder.newSalary.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && holder.newSalary.getText() !=null)
                {
                    userRef = userDB.getUserFromDB(workersList.get(position).getFBid());
                    userRef.child("hourlyPay").setValue(holder.newSalary.getText().toString());
                }
            }
        });

        {

            return convertView;
        }
    }
}