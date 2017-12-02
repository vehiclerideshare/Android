package com.cycliq;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cycliq.Adapter.MyTripsAdapter;
import com.cycliq.Application.CycliqApplication;
import com.cycliq.CommonClasses.Constants;
import com.cycliq.model.LocationListModel;
import com.cycliq.model.TripSummaryModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import static com.cycliq.R.id.btnBack;

//implementing onclicklistener
public class MyTripsActivity extends AppCompatActivity implements View.OnClickListener {

    //View Objects
    private ImageButton btnBack;
    private Button btnSubmit, btnVerifyPin, btnCancel;
    private EditText editPhoneNumber, editOtp;
    private LinearLayout layoutLogin;
    private LinearLayout layoutMyWallet, layoutSettings;

    private ListView listMyTrips;

    private MyTripsAdapter myTripsAdapter;

    private ArrayList<TripSummaryModel> arrTripSummary = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_trips);

        getSupportActionBar().hide();

        setViews();

        setListener();

        getMyTripsDetails();


    }

    private void setListener() {

//        layoutMyWallet.setOnClickListener(this);
//
        btnBack.setOnClickListener(this);

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
        btnBack = (ImageButton) findViewById(R.id.btnBack);

//        layoutMyWallet = (LinearLayout) findViewById(R.id.layoutMyWallet);
//
//        layoutSettings = (LinearLayout) findViewById(R.id.layoutSettings);

        listMyTrips = (ListView) findViewById(R.id.listMyTrips);

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

        if (view == layoutSettings) {

            Intent intent = new Intent(this, MyWalletActivity.class);

            startActivity(intent);

        }

        if (view == btnBack) {

            finish();

        }

        if(id == R.id.linearMain)
        {

            Intent intent = new Intent(this, TripDetailsActivity.class);

            int pos = (int) view.getTag();

            TripSummaryModel tripSummaryModel = arrTripSummary.get(pos);

            intent.putExtra("tripDetails", (Serializable) tripSummaryModel);

            startActivity(intent);

        }
    }

    private void assignAdapter() {

        myTripsAdapter = new MyTripsAdapter(this, this, arrTripSummary);
        listMyTrips.setAdapter(myTripsAdapter);

    }


    private void getMyTripsDetails() {

        if (Constants.isNetworkAvailable(this)) {

            Constants.showProgressDialog(this);

            JSONObject params = new JSONObject();
            try {

                params.put("userRegistrationId", Constants.USER_REGISTERATION_ID);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            String url = Constants.DEMO_BASE_URL + Constants.KEY_TRIP_SUMMARY;

            RequestQueue queue = CycliqApplication.getInstance().getRequestQueue();
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            System.out.println("" + Constants.KEY_TRIP_SUMMARY + "==" + response);

                            try {

                                JSONArray jsonArray = response.getJSONArray("tripSummary");

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                                    TripSummaryModel tripSummaryModel = new TripSummaryModel(jsonObject.getString("tripDateTime").toString(), jsonObject.getString("tripID").toString(), jsonObject.getString("bikeID").toString(), jsonObject.getString("tripAmount").toString());

                                    arrTripSummary.add(tripSummaryModel);

                                }

                                assignAdapter();

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

                            Constants.showToast(MyTripsActivity.this, Constants.serverErrorMsg);

                        }
                    });

            queue.add(jsObjRequest);

        } else {


            Constants.showToast(this, Constants.internetAlert);

        }

    }


}