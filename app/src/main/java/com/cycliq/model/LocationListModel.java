package com.cycliq.model;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cycliq.MapsActivity;
import com.cycliq.R;

//implementing onclicklistener
public class LocationListModel  {

    String vehicleRegnNumber;
    String lat;
    String lng;
    String name;
    String deviceid;

    public String getVehicleRegnNumber() {
        return vehicleRegnNumber;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    public String getName() {
        return name;
    }

    public String getDeviceid() {
        return deviceid;
    }

    // Empty constructor
    public LocationListModel(){

    }
    // constructor
    public LocationListModel(String vehicleRegnNumber, String lat, String lng){
        this.vehicleRegnNumber = vehicleRegnNumber;
        this.lat = lat;
        this.lng = lng;

    }

}