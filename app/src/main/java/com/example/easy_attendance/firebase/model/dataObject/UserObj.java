package com.example.easy_attendance.firebase.model.dataObject;

public class UserObj
{
    public String orgKey;
    public String ID;
    public String email;
    public String fName;
    public String lName;
    public boolean isManager;
    public double hourlyPay = 29.12;

    public int SickDays;
    public int daysOff;

    public UserObj()
    {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public UserObj (String orgKey, String ID, String fName, String lName, String email, boolean isManager)//}, int SickDays, int daysOff)
    {
        this.orgKey = orgKey;
        this.ID = ID;
        this.email = email;
        this.fName = fName;
        this.lName = lName;
        this.isManager = isManager;
        this.SickDays = 15;
        this.daysOff = 15;
    }


    public void setHourlyPay (double pay) {this.hourlyPay =pay;}

    public double getHourlyPay () {return hourlyPay;}


//    public void setDaysOff (int daysOff) {this.daysOff =daysOff;}
//
//
//    public int getDaysOff () {return daysOff;}
//
//    public void setSickDays (int SickDays) {this.SickDays =SickDays;}
//
//    public int getSickDays () {return SickDays;}
}
