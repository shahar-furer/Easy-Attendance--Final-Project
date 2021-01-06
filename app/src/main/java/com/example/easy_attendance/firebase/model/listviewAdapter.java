package com.example.easy_attendance.firebase.model;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.example.easy_attendance.R;
import com.example.easy_attendance.firebase.model.dataObject.Model;

import java.util.ArrayList;

public class listviewAdapter extends BaseAdapter {

    public ArrayList<Model> workersList;
    Activity activity;

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
        //TextView mResetPassword;
        //TextView mPrice;
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
            //holder.mResetPassword = (TextView) convertView.findViewById(R.id.resetPassword);
            holder.newPass = (EditText) convertView.findViewById(R.id.editPassword);
            holder.newSalary = (EditText) convertView.findViewById(R.id.changeSalary);
            //holder.mPrice = (TextView) convertView.findViewById(R.id.price);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Model item = workersList.get(position);
        holder.mID.setText(item.getID().toString());
        holder.mName.setText(item.getName().toString());
        //holder.mResetPassword.setText(item.getResetPassword().toString());
        //holder.mPrice.setText(item.getPrice().toString());
        //holder.newPass

        holder.newPass.setOnFocusChangeListener(new View.OnFocusChangeListener(){

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && holder.newPass.getText() !=null)
                {
                    Log.d("the new pass", "onFocusChange: "+ holder.newPass.getText().toString() + "  i"+ position);
                }
            }
        });

        {


            return convertView;
        }
    }
}