package com.cycliq;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cycliq.Application.CycliqApplication;
import com.cycliq.CommonClasses.Constants;
import com.cycliq.ble.CycliqBluetoothComm;
import com.cycliq.model.LocationListModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

//implementing onclicklistener
public class QRScanActivity extends AppCompatActivity {

    //View Objects
    private TextView txtQrResult, txtLoading;

    int loadingPercentage = 0;

    Timer timer = null;

    ProgressDialog progressDialog;

    //qr code scanner object
    private IntentIntegrator qrScan;

    private boolean isOPenScanAlready = false;

    String rideId = "";
    String rideStatus = "";

    Boolean isStart = true;
    Boolean webCalling = false;
    Boolean unLocking = false;
    Boolean cancelProcess = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_screen);

        getSupportActionBar().hide();

        //View objects
        txtQrResult = (TextView) findViewById(R.id.txtQrResult);

        txtLoading = (TextView) findViewById(R.id.txtLoading);

        //intializing scan object
        qrScan = new IntentIntegrator(this);

        if (!isOPenScanAlready) {

            qrScan.initiateScan();
        }

        CycliqBluetoothComm.getInstance().setCurrentActivity(this);
        CycliqBluetoothComm.getInstance().init();

    }

    //Getting the scan results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {

            isOPenScanAlready = true;

            //if qrcode has nothing in it
            if (result.getContents() == null) {

                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();

                txtQrResult.setText("");

            } else {

                txtQrResult.setVisibility(View.GONE);

                //if qr contains data
                try {
                    //converting the data to json
                    JSONObject obj = new JSONObject(result.getContents());
                    //setting values to textviews
                  //  txtQrResult.setText(obj.getString("name"));

                    update(obj.getString("name"));

                } catch (JSONException e) {

                    e.printStackTrace();

                    //if control comes here
                    //that means the encoded format not matches
                    //in this case you can display whatever data is available on the qrcode
                    //to a toast

                    //txtQrResult.setText(result.getContents());

                    update(result.getContents());

                    //Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (timer != null) {

            timer.cancel();

            timer = null;
        }
    }

    private void update(final String scannedString) {

        progressDialog = new ProgressDialog(QRScanActivity.this);
        progressDialog.setCancelable(false);
        //  dialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(false);
        //  progressDialog.setMax(100);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        // progressDialog.setProgress(0);
        progressDialog.setMax(100);
        // progressDialog.setMessage("Loading ...");
        progressDialog.show();

        loadingPercentage = 0;

        timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {

                loadingPercentage = loadingPercentage + 10;

                runOnUiThread(new Runnable() {
                    public void run() {
                        // Update UI elements

                        txtLoading.setText("Your request is processing! Please wait...");

                        progressDialog.setProgress(loadingPercentage);

                    }
                });

                if (loadingPercentage >= 90) {
                    timer.cancel();

                    timer = null;

                    loadingPercentage = 100;

                    progressDialog.setProgress(loadingPercentage);

                    progressDialog.dismiss();

                    callApi(scannedString);

                }

            }

        }, 0, 1500);


    }

    private void callApi(String scannedString) {

        if (Constants.isNetworkAvailable(this)) {


            runOnUiThread(new Runnable() {
                public void run() {
                    // Update UI elements

                    Constants.showProgressDialog(QRScanActivity.this);

                }
            });


            JSONObject params = new JSONObject();
            try {
                params.put("userRegistrationId", Constants.USER_REGISTERATION_ID);
                params.put("qrCode", scannedString);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            String url = Constants.DEMO_BASE_URL + Constants.UNLOCK_VEHICLE;
            webCalling = true;
            RequestQueue queue = CycliqApplication.getInstance().getRequestQueue();
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            System.out.println("response values == "+response);

                            webCalling = false;

                            if (cancelProcess) {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        // Update UI elements

                                            Constants.hideProgressDialog();



                                    }
                                });
                                return;
                            }


                            try {

                                String strRideStatus = response.getString(Constants.KEY_RIDE_STATUS);

                                if (strRideStatus == "null") {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            // Update UI elements

//                                            Constants.hideProgressDialog();
//                                            Toast.makeText(this, getResources().getString(R.string.error_message_unlock_server), Toast.LENGTH_SHORT).show();

//                                            Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();

                                            rideId = "123426100000150";
                                            rideStatus = "Authorized";


                                            unLocking = true;

                                            CycliqBluetoothComm.getInstance().bgOperation(1002);


                                        }
                                    });
                                } else {
                                    if (strRideStatus.equalsIgnoreCase("Authorized")) {

                                        String strRideId = response.getString(Constants.KEY_RIDE_ID);

                                        rideId = strRideId;
                                        rideStatus = strRideStatus;


                                        unLocking = true;

                                        CycliqBluetoothComm.getInstance().bgOperation(1002);


                                    }
                                }



                            } catch (JSONException e) {

                                e.printStackTrace();

                            }



                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(final VolleyError error) {

                            runOnUiThread(new Runnable() {
                                public void run() {
                                    // Update UI elements
//                                    Toast.makeText(this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                                    Constants.hideProgressDialog();

                                }
                            });
                        }
                    });

            queue.add(jsObjRequest);

        } else {


            Constants.showToast(this, Constants.internetAlert);

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode==2){
            // 请求定位权限
            if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                //
            }else{
                showDialog(getResources().getString(R.string.main_permissions_location));
            }
        }
    }

    private void showDialog(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setTitle(getResources().getString(R.string.main_dialog_tip));
        builder.setPositiveButton(getResources().getString(R.string.main_dialog_submit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public void sendLockOpenStatus() {
        runOnUiThread(new Runnable() {
            public void run() {
                // Update UI elements

                Constants.saveData(QRScanActivity.this, Constants.KEY_RIDE_ID, rideId);

                Constants.saveData(QRScanActivity.this, Constants.KEY_RIDE_STATUS, rideStatus);

                Constants.hideProgressDialog();
                CycliqBluetoothComm.getInstance().getMapsActivity().sendLockOpenStatus();
                finish();


            }
        });

    }


    @Override
    public void onBackPressed() {
        if (unLocking) {

        } else {
            cancelProcess = true;
            super.onBackPressed();
        }


    }
}
