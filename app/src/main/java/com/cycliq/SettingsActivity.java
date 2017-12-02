package com.cycliq;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

//implementing onclicklistener
public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    //View Objects
    private ImageButton btnBack;
    private Button btnLogout, btnCancel;
    private EditText editPhoneNumber, editOtp;
    private LinearLayout layoutLogin;
    private LinearLayout layoutMyWallet, layoutSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        getSupportActionBar().hide();

        setViews();

        setListener();

    }

    private void setListener() {

//        layoutMyWallet.setOnClickListener(this);
//
//        layoutSettings.setOnClickListener(this);

        btnBack.setOnClickListener(this);

        btnLogout.setOnClickListener(this);

    }

    private void setViews() {

//        editPhoneNumber = (EditText) findViewById(R.id.txtPhoneNumber);
//
//        editOtp = (EditText) findViewById(R.id.txtOtp);
//
//        btnSubmit = (Button) findViewById(R.id.btnSubmit);
//
        btnLogout = (Button) findViewById(R.id.btnLogout);

        btnBack = (ImageButton) findViewById(R.id.btnBack);

//        layoutSettings = (LinearLayout) findViewById(R.id.layoutSettings);

    }

    private void showToast(String msg) {

        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onClick(View view) {

//        Intent intent = new Intent(this, QRScanActivity.class);

        int id = view.getId();


        if (view == btnBack) {

          finish();

        }

        if (view == btnLogout) {

            Intent intent = new Intent(this, LoginActivity.class);

            startActivity(intent);
        }
    }

}