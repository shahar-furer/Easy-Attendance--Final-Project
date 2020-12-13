package com.example.easy_attendance.firebase.model.dataObject;

import java.text.SimpleDateFormat;
import java.util.Date;


public class TableObj {

        SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
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

    public Date getEnDate() { return this.entryDate; }

    public String getEnMonth() { return monthFormat.format(entryDate);}

    public String getEnDay() { return dayFormat.format(entryDate);}

    public String getEnYear() { return yearFormat.format(entryDate); }

    public String getEnHour() { return hourFormat.format(entryDate);}

    public Date geExtDate() { return this.exitDate; }

    public String getExMonth() { return monthFormat.format(exitDate);}

    public String getExDay() { return dayFormat.format(exitDate);}

    public String getExYear() { return yearFormat.format(exitDate); }

    public String getExHour() { return hourFormat.format(exitDate);}

    public String getIsEntryExit(){ return this.isEntryExit; }






}
