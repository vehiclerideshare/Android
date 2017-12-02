package com.cycliq.model;

import java.io.Serializable;

//implementing onclicklistener
public class TripSummaryModel implements Serializable{

    String tripDateTime;
    String tripID;
    String bikeID;
    String tripAmount;

    public String getTripDateTime() {
        return tripDateTime;
    }

    public String getTripID() {
        return tripID;
    }

    public String getBikeID() {
        return bikeID;
    }

    public String getTripAmount() {
        return tripAmount;
    }

    // Empty constructor
    public TripSummaryModel(){

    }

    // constructor
    public TripSummaryModel(String tripDateTime, String tripID, String bikeID, String tripAmount){
        this.tripDateTime = tripDateTime;
        this.tripID = tripID;
        this.bikeID = bikeID;
        this.tripAmount = tripAmount;

    }

}