package com.example.driverproject.driver_slip;

public class UserInformation
{
    public String fname, contact, age, gender, uid;

    public UserInformation(String fname, String contact, String age, String gender, String uid)

    {
        //hello
        this.fname=fname;
        this.contact=contact;
        this.age=age;
        this.gender=gender;
        this.uid = uid;
    }

    public String getFname() {
        return fname;
    }

    public String getContact() {
        return contact;
    }

    public String getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public String getUid() {
        return uid;
    }
}
