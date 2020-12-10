package com.example.easy_attendance.firebase.model.dataObject;

import java.text.SimpleDateFormat;
import java.util.Date;


public class TableObj {

        SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
        SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm:ss");
        Date entryDate;
        Date exitDate;
        String isEntryExit; // entry or exit


// we need to add option for only Exit, only Entry, and both together
        public TableObj(Date d, String s) //now we have option only for entry and at the end of the day-Exit
        {
            this.isEntryExit = s;
            if (isEntryExit == "entry")
                this.entryDate = d;
            else
                this.exitDate = d;

        }



        // param entry is current time on pressing start button


    public void setEntryDate(Date entryD) { this.entryDate = entryD; }

    public void setExitDate(Date exitD) { exitDate = exitD; }

    public void setIsEntryExit(String s) { isEntryExit = s;}

    public Date getDate() { return this.entryDate; }

    public String getMonth() { return monthFormat.format(entryDate);}

    public String getYear() { return yearFormat.format(entryDate); }

    public String getHour() { return hourFormat.format(entryDate);}

    public String getIsEntryExit(){ return this.isEntryExit; }






}
