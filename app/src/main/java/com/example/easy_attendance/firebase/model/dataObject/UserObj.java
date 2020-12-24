package com.example.easy_attendance.firebase.model.dataObject;

public class UserObj
{
    public String orgKey;
    public String ID;
    public String email;
    public String fName;
    public String lName;
    public String password;
    public boolean isManager;
    public double hourlyPay =29.12;
    public UserObj()
    {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public UserObj ( String orgKey, String ID, String fName, String lName, String email, String password, boolean isManager)
    {
        this.orgKey = orgKey;
        this.ID = ID;
        this.email = email;
        this.fName = fName;
        this.lName = lName;
        this.password = password;
        this.isManager = isManager;
    }

//    public void setPassword (String newPassword)
//    {
//        password = newPassword;
//    }
//
//    public String getPassword ()
//    {
//        return password;
//    }
//
//    public void setEmail (String newEmail)
//    {
//        email = newEmail;
//    }
//
//    public String getEmail()
//    {
//        return email;
//    }

//    public void setIsManager (boolean isManager) {this.isManager = isManager;}

    public void setHourlyPay (double pay) {this.hourlyPay =pay;}

  //  public Boolean getIsManager () {return isManager;}

  //  public String getFirstName () {return fName;}

//    public String getUserId () {return ID;}

    public double getHourlyPay () {return hourlyPay;}




}
