package com.cycliq;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cycliq.Application.CycliqApplication;
import com.cycliq.CommonClasses.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

//implementing onclicklistener
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    //View Objects
    private Button btnSubmit, btnVerifyPin, btnCancel;
    private EditText editPhoneNumber, editOtp;
    private LinearLayout layoutLogin;
    private LinearLayout layoutVerifyOtp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        getSupportActionBar().hide();

        setViews();

        setListener();

        Constants.saveData(LoginActivity.this, Constants.KEY_RIDE_ID, "");
        Constants.saveData(LoginActivity.this, Constants.KEY_RIDE_STATUS, Constants.NONE);
//        Constants.saveData(LoginActivity.this, Constants.KEY_RIDE_STATUS, "");


    }

    private void setListener() {

        btnSubmit.setOnClickListener(this);

        btnVerifyPin.setOnClickListener(this);

        btnCancel.setOnClickListener(this);

    }

    private void setViews() {

        editPhoneNumber = (EditText) findViewById(R.id.txtPhoneNumber);

        editOtp = (EditText) findViewById(R.id.txtOtp);

        btnSubmit = (Button) findViewById(R.id.btnSubmit);

        btnVerifyPin = (Button) findViewById(R.id.btnVerifyPin);

        btnCancel = (Button) findViewById(R.id.btnCancel);

        layoutLogin = (LinearLayout) findViewById(R.id.layoutLogin);

        layoutVerifyOtp = (LinearLayout) findViewById(R.id.layoutVerifyOtp);

    }

    private boolean isValidatedLogin() {

        if (editPhoneNumber.getText().toString().length() == 0) {

            Constants.showToast(this,"Enter mobile number");

            return false;
        }

        if (editPhoneNumber.getText().toString().length() != 10) {

            Constants.showToast(this,"Enter valid mobile number");

            return false;
        }

        return true;
    }

    private boolean isValidatedOtp() {

        if (editOtp.getText().toString().length() == 0) {

            Constants.showToast(this,"Enter PIN");

            return false;
        }

        return true;
    }

    @Override
    public void onClick(View view) {

//        Intent intent = new Intent(this, QRScanActivity.class);

        int id = view.getId();

        if (view == btnSubmit) {

            if (isValidatedLogin()) {

                Constants.saveData(LoginActivity.this, Constants.KEY_USER_PHONE, editPhoneNumber.getText().toString());

                layoutLogin.setVisibility(View.GONE);

                layoutVerifyOtp.setVisibility(View.VISIBLE);

            }
        } else if (view == btnVerifyPin) {

            if (isValidatedOtp()) {

                Intent intent = new Intent(this, MapsActivity.class);

                startActivity(intent);

            }
        } else if (view == btnCancel) {

            if (isValidatedLogin()) {

                layoutLogin.setVisibility(View.VISIBLE);

                layoutVerifyOtp.setVisibility(View.GONE);

            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        System.exit(0);

    }
}