package com.example.a19360.secondproject;

import java.io.Serializable;

public class Friends implements Serializable {

    public String getName(){
        return this.Name;
    }

    public void setName(String name){
        this.Name= name;
    }

    public String getPhoneNumber(){return this.PhoneNumber;}

    public void setPhoneNumber(String phoneNumber){this.PhoneNumber=phoneNumber;}

    public String getLatitude(){return this.Latitude;}

    public void setLatitude(String latitude){this.Latitude=latitude;}

    public String getLongitude(){return this.Longitude;}

    public void setLongitude(String Longitude){this.Longitude=Longitude;}

    private String Name;
    private String PhoneNumber;
    private String Latitude;
    private String Longitude;
}