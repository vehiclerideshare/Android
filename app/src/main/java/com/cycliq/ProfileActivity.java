package com.cycliq;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cycliq.Application.CycliqApplication;
import com.cycliq.CommonClasses.Constants;

import org.json.JSONException;
import org.json.JSONObject;

//implementing onclicklistener
public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    //View Objects
    private Button btnSubmit, btnVerifyPin, btnCancel;
    private EditText editPhoneNumber, editOtp;
    private LinearLayout layoutLogin;
    private LinearLayout layoutMyWallet;
    private ImageButton btnBack;

    String strCreditScore = "";
    String strDuration = "";
    String strCarbon = "";
    String strCalories = "";

    private TextView tvPhoneNumber, tvCarbon, tvKcal, tvDuration, tvCreditScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        getSupportActionBar().hide();

        setViews();

        setListener();


        getUserProfileDetails();

        String strNumber = Constants.getSavedData(this, Constants.KEY_USER_PHONE);

        tvPhoneNumber.setText(strNumber);

    }

    private void assignValues() {

        tvCreditScore.setText("My Credits: " + strCreditScore);
        tvCarbon.setText(strCarbon);
        tvDuration.setText(strDuration);
        tvKcal.setText(strCalories);

    }

    private void setListener() {

        layoutMyWallet.setOnClickListener(this);



        btnBack.setOnClickListener(this);

    }

    private void setViews() {

        tvPhoneNumber = (TextView) findViewById(R.id.tvNumber);
        tvCarbon = (TextView) findViewById(R.id.tvCarbon);
        tvDuration = (TextView) findViewById(R.id.tvDuration);
        tvKcal = (TextView) findViewById(R.id.tvKcal);
        tvCreditScore = (TextView) findViewById(R.id.tvCreditScore);

        layoutMyWallet = (LinearLayout) findViewById(R.id.layoutMyWallet);

        btnBack = (ImageButton) findViewById(R.id.btnBack);

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

//        if(view == layoutMyPayments)
//        {
//
//            Intent intent = new Intent(this, MyPaymentsActivity.class);
//
//            startActivity(intent);
//
//        }
//
//        if (view == layoutSettings) {
//
//            Intent intent = new Intent(this, SettingsActivity.class);
//
//            startActivity(intent);
//
//        }
//
//        if (view == layoutMyTrips) {
//
//            Intent intent = new Intent(this, MyTripsActivity.class);
//
//            startActivity(intent);
//
//        }

        if (view == btnBack) {

            finish();

        }

    }

    private void getUserProfileDetails() {

        if (Constants.isNetworkAvailable(this)) {

            Constants.showProgressDialog(this);

            JSONObject params = new JSONObject();
            try {

                params.put("userRegistrationId", Constants.USER_REGISTERATION_ID);

//                params.put("rideId", Constants.getSavedData(this, Constants.KEY_RIDE_ID));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            String url = Constants.DEMO_BASE_URL + Constants.KEY_USER_PROFILE;

            RequestQueue queue = CycliqApplication.getInstance().getRequestQueue();
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            System.out.println(""+Constants.KEY_USER_PROFILE + "==" + response);

                            try {

                                strCreditScore = response.getString("creditScore");
                                strDuration = response.getString("duration");
                                strCarbon = response.getString("carbon");
                                strCalories = response.getString("calories");

                                assignValues();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Constants.hideProgressDialog();

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Constants.hideProgressDialog();

                            Constants.showToast(ProfileActivity.this, Constants.serverErrorMsg);

                        }
                    });

            queue.add(jsObjRequest);

        } else {


            Constants.showToast(this, Constants.internetAlert);

        }

    }

}