package com.example.easy_attendance.firebase.model.dataObject;

import java.text.SimpleDateFormat;
import java.util.Date;


public class TableObj {

        SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
        SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm:ss");
        Date date ;
        String isEntryExit;

        public TableObj(Date d, String s) {
            this.date=d;
            this.isEntryExit=s;

        }


        // param entry is current time on pressing start button
    void setDate(Date entry) { date = entry; }

    void setIsEntryExit(String s) { isEntryExit = s;}

    Date getDate() { return this.date; }

    String getMonth() { return monthFormat.format(date);}

    String getYear() { return yearFormat.format(date); }

    String getHour() { return hourFormat.format(date);}

    String getIsEntryExit(){ return this.isEntryExit ; }




}
