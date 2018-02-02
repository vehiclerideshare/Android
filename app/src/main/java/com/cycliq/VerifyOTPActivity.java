package com.cycliq;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.cycliq.CommonClasses.Constants;

//implementing onclicklistener
public class VerifyOTPActivity extends AppCompatActivity implements View.OnClickListener {

    //View Objects
    private Button btnSubmit, btnVerifyPin, btnCancel;
    private EditText editPhoneNumber, editOtp;
    private LinearLayout layoutLogin;
    private LinearLayout layoutVerifyOtp;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verify_otp);

        getSupportActionBar().hide();

      // setViews();

       // setListener();

        Constants.saveData(VerifyOTPActivity.this, Constants.KEY_RIDE_ID, "");
        Constants.saveData(VerifyOTPActivity.this, Constants.KEY_RIDE_STATUS, Constants.NONE);
//        Constants.saveData(LoginActivity.this, Constants.KEY_RIDE_STATUS, "");

    }




    private void setListener() {

        btnSubmit.setOnClickListener(this);

        btnVerifyPin.setOnClickListener(this);

        btnCancel.setOnClickListener(this);

        btnBack.setOnClickListener(this);


    }

    private void setViews() {

        editPhoneNumber = (EditText) findViewById(R.id.txtPhoneNumber);

        editOtp = (EditText) findViewById(R.id.txtOtp);

        btnSubmit = (Button) findViewById(R.id.btnSubmit);

        btnVerifyPin = (Button) findViewById(R.id.btnVerifyPin);

        btnCancel = (Button) findViewById(R.id.btnCancel);

        layoutLogin = (LinearLayout) findViewById(R.id.layoutLogin);

        layoutVerifyOtp = (LinearLayout) findViewById(R.id.layoutVerifyOtp);

        btnBack = (ImageButton) findViewById(R.id.btnBack);

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

                Constants.saveData(VerifyOTPActivity.this, Constants.KEY_USER_PHONE, editPhoneNumber.getText().toString());

                layoutLogin.setVisibility(View.GONE);

                layoutVerifyOtp.setVisibility(View.VISIBLE);

                editOtp.requestFocus();

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
        else if (view == btnBack) {

            finish();

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        System.exit(0);

    }


}