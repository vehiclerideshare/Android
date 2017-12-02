package com.cycliq.model;

//implementing onclicklistener
public class MyPaymentModel {

    String paymentDateTime;
    String paymentAmount;

    public String getPaymentDateTime() {
        return paymentDateTime;
    }

    public String getPaymentAmount() {
        return paymentAmount;
    }

    // Empty constructor
    public MyPaymentModel(){

    }

    // constructor
    public MyPaymentModel(String paymentDateTime, String paymentAmount){
        this.paymentDateTime = paymentDateTime;
        this.paymentAmount = paymentAmount;


    }

}