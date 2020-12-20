package com.example.easy_attendance.firebase.model.dataObject;

public class UserObj
{
    public String orgKey;
    public String keyID;
    public String userID;
    public String email;
    public String fName;
    public String lName;
    public String password;
    public boolean isManager;

    public UserObj()
    {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public UserObj (String userID, String orgKey, String keyID, String email, String fName, String lName, String password, boolean isManager)
    {
        this.orgKey = orgKey;
        this.keyID = keyID;
        this.userID=userID;
        this.email = email;
        this.fName = fName;
        this.lName = lName;
        this.password = password;
        this.isManager = isManager;
    }

    public void setPassword (String newPassword)
    {
        password = newPassword;
    }

    public String getPassword ()
    {
        return password;
    }

    public void setEmail (String newEmail)
    {
        email = newEmail;
    }

    public String getEmail()
    {
        return email;
    }

    public void setIsManager (boolean isManager) {this.isManager = isManager;}

    public Boolean getIsManager () {return isManager;}


}
