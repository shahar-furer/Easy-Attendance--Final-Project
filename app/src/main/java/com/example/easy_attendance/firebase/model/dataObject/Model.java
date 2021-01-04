package com.example.easy_attendance.firebase.model.dataObject;

public class Model {

    private String ID;
    private String Name;
    private String ResetPassword;
    private String price;

    public Model(String ID, String Name, String ResetPassword, String price) {
        this.ID = ID;
        this.Name = Name;
        this.ResetPassword = ResetPassword;
        this.price = price;
    }

    public String getID() {
        return ID;
    }

    public String getName() {
        return Name;
    }

    public String getResetPassword() {
        return ResetPassword;
    }

    public String getPrice() {
        return price;
    }
}