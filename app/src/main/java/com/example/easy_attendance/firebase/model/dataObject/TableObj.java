package com.example.easy_attendance.firebase.model.dataObject;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.concurrent.TimeUnit;



public class TableObj {

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date ;
        String isEntryExit;

        public TableObj(Date d, String s)
        {
            this.date=d;
            this.isEntryExit=s;

        }


        // param entry is current time on pressing start button
    void setDate(Date entry) { date = entry; }

    void setIsEntryExit(String s) { isEntryExit = s;}

    public Date getDate() { return this.date; }

    public String getIsEntryExit(){ return this.isEntryExit ; }




}
