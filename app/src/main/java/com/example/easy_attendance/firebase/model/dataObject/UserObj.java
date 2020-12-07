package com.example.easy_attendance.firebase.model.dataObject;

public class UserObj {
    public int userNumber;
    public String email;
    public String fName;
    public String lName;
    public String password;

    public UserObj (int userNumber, String email, String fName, String lName, String password)
    {
        this.userNumber = userNumber;
        this.email = email;
        this.fName = fName;
        this.lName = lName;
        this.password = password;
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
}
