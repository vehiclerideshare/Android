package com.cycliq;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

//implementing onclicklistener
public class DetailsActivity extends AppCompatActivity implements View.OnClickListener {

    //View Objects
    private Button btnSubmit, btnVerifyPin, btnCancel;
    private EditText editPhoneNumber, editOtp;
    private LinearLayout layoutLogin;
    private LinearLayout layoutMyWallet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);

        getSupportActionBar().hide();

        setViews();

        setListener();

    }

    private void setListener() {

//        layoutMyWallet.setOnClickListener(this);


    }

    private void setViews() {

//        editPhoneNumber = (EditText) findViewById(R.id.txtPhoneNumber);
//
//        editOtp = (EditText) findViewById(R.id.txtOtp);
//
//        btnSubmit = (Button) findViewById(R.id.btnSubmit);
//
//        btnVerifyPin = (Button) findViewById(R.id.btnVerifyPin);
//
//        btnCancel = (Button) findViewById(R.id.btnCancel);

      //  layoutMyWallet = (LinearLayout) findViewById(R.id.layoutMyWallet);


    }

    private void showToast(String msg) {

        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onClick(View view) {

//        Intent intent = new Intent(this, QRScanActivity.class);

        int id = view.getId();

        if (view == layoutMyWallet) {

            Intent intent = new Intent(this, MyWalletActivity.class);

            startActivity(intent);

        }



    }

}