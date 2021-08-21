package com.example.easy_attendance.firebase.model;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.easy_attendance.R;
import com.example.easy_attendance.firebase.model.dataObject.Model;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class listviewAdapter extends BaseAdapter {

    public ArrayList<Model> workersList;
    Activity activity;
    LinearLayout lL;
    FBAuth mAuth = new FBAuth();
    String uid = mAuth.getUserID();
    FirebaseDBUser userDB = new FirebaseDBUser();
    DatabaseReference userRef = userDB.getUserFromDB(uid);
    DatabaseReference messageRef=userDB.getAllMessages();

    public listviewAdapter(Activity activity, ArrayList<Model> workersList, LinearLayout lL) {
        super();
        this.activity = activity;
        this.workersList = workersList;
        this.lL = lL;
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
        TextView mSalary;
        //        EditText newSalary;
        Button newPass;
        Button updateDataBtn;

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
            holder.mSalary = (TextView) convertView.findViewById(R.id.salary);
            //   holder.newSalary = (EditText) convertView.findViewById(R.id.changeSalary);
            holder.newPass = (Button) convertView.findViewById(R.id.btnEditPassword);
            holder.updateDataBtn = convertView.findViewById(R.id.btnUpdateData);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Model item = workersList.get(position);
        holder.mID.setText(item.getID());
        holder.mName.setText(item.getName());
        holder.mSalary.setText(item.getSalary()+ "/hr");

        holder.newPass.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN)
                        && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on Enter key press
                    holder.newPass.clearFocus();
                 /*   if (holder.newPass.getText() !=null)
                    {
                        userRef = userDB.getUserFromDB(workersList.get(position).getFBid());
                        userRef.child("password").setValue(holder.newPass.getText().toString());
                        Snackbar.make(lL,workersList.get(position).getID()+  " Password Updated Successfully!", Snackbar.LENGTH_SHORT).show();
                    } */
                    return true;
                }
                return false;
            }
        });

        holder.newPass.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String email = workersList.get(position).getEmail();
                Log.d("email" , email);
                mAuth.resetPassword(email);
                Snackbar.make(lL,  "Reset Password Mail Sent To "+workersList.get(position).getID() +" Successfully!", Snackbar.LENGTH_SHORT).show();
            }

        });

        holder.updateDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(activity, EditEmployeeDataActivity.class);

                intent.putExtra("userFbUid", item.getFBid());
                intent.putExtra("userId", item.getID());

                activity.startActivity(intent);
            }
        });

//        holder.newSalary.setOnKeyListener(new View.OnKeyListener() {
//
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                // If the event is a key-down event on the "enter" button
//                if ((event.getAction() == KeyEvent.ACTION_DOWN)
//                        && (keyCode == KeyEvent.KEYCODE_ENTER)) {
//                    // Perform action on Enter key press
//                    holder.newSalary.clearFocus();
//                 /*   if (holder.newSalary.getText() !=null)
//                    {
//                        userRef = userDB.getUserFromDB(workersList.get(position).getFBid());
//                        userRef.child("hourlyPay").setValue(holder.newSalary.getText().toString());
//                        Snackbar.make(lL,workersList.get(position).getID()+  " Hour Salary Updated Successfully!", Snackbar.LENGTH_SHORT).show();
//                    } */
//                    return true;
//                }
//                return false;
//            }
//        });

//        holder.newSalary.setOnFocusChangeListener(new View.OnFocusChangeListener(){
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (!hasFocus && holder.newSalary.getText().length() !=0)
//                {
//                    userRef = userDB.getUserFromDB(workersList.get(position).getFBid());
//                    userRef.child("hourlyPay").setValue(Double.parseDouble(holder.newSalary.getText().toString().trim()));
//                    messageRef.child(workersList.get(position).getID()).child("Title").setValue("Manager Updated Your Hourly Payment");
//                    messageRef.child(workersList.get(position).getID()).child("Text").setValue("Your Payment Now is " + holder.newSalary.getText().toString().trim()+ "NIS");
//
//
//                    Snackbar.make(lL,workersList.get(position).getID()+  " Hour Salary Updated Successfully!", Snackbar.LENGTH_SHORT).show();
//                }
//            }
//        });



        {

            return convertView;
        }
    }
}