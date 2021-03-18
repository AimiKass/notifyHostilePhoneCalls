package com.example.notifyhostilephonecalls.models;

import java.util.ArrayList;

public class PhoneNumber
{

    int _id;
    public String phoneNumber;
    public String rating;

    public PhoneNumber() {
    }

    public PhoneNumber(String phoneNumber, String rating) {
        this.phoneNumber = phoneNumber;
        this.rating = rating;
    }

    public PhoneNumber(int id, String phoneNumber, String rating)
    {
        this._id = id;
        this.phoneNumber = phoneNumber;
        this.rating = rating;
    }

    public int getID(){
        return this._id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getRating() {
        return rating;
    }

    public void setID(int _id) {
        this._id = _id;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}