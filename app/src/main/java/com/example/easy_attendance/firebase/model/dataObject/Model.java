package com.example.easy_attendance.firebase.model.dataObject;

public class Model {

    private String FBid;
    private String ID;
    private String Name;
    private double Salary;


    public Model(String FBid, String ID, String Name, double salary) {  //String uid, first
        this.FBid = FBid;
        this.ID = ID;
        this.Name = Name;
        this.Salary = salary;
    }
    public double getSalary() { return Salary; }

    public String getFBid() { return FBid; }

    public String getID() {
        return ID;
    }

    public String getName() { return Name; }

}