package com.cycliq;

import android.*;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceView;
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
import com.google.android.gms.vision.barcode.Barcode;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import github.nisrulz.qreader.QRDataListener;
import github.nisrulz.qreader.QREader;
import info.androidhive.barcode.BarcodeReader;
import info.androidhive.barcode.ScannerOverlay;


//implementing onclicklistener
public class QRScanActivity extends AppCompatActivity implements BarcodeReader.BarcodeReaderListener  {


    private static final String cameraPerm = android.Manifest.permission.CAMERA;


    //View Objects
    private TextView txtQrResult, txtLoading;

    int loadingPercentage = 0;

    Timer timer = null;

    ProgressDialog progressDialog;


    ScannerOverlay overlay_view;

    //qr code scanner object
   private IntentIntegrator qrScan;

    private boolean isOPenScanAlready = false;

    String rideId = "";
    String rideStatus = "";

    Boolean isStart = true;
    Boolean webCalling = false;
    Boolean unLocking = false;
    Boolean cancelProcess = false;

    private SurfaceView mySurfaceView;
    private QREader qrEader;
    boolean hasCameraPermission = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_screen);
        hasCameraPermission = RuntimePermissionUtil.checkPermissonGranted(this, cameraPerm);

//        getSupportActionBar().hide();

        //View objects
        txtQrResult = (TextView) findViewById(R.id.txtQrResult);

        txtLoading = (TextView) findViewById(R.id.txtLoading);


        CycliqBluetoothComm.getInstance().setCurrentActivity(this);
        CycliqBluetoothComm.getInstance().init();

//        update("testid");

        // Setup SurfaceView
        // -----------------
        mySurfaceView = (SurfaceView) findViewById(R.id.camera_view);
        overlay_view = (ScannerOverlay) findViewById(R.id.overlay_view);


        if (hasCameraPermission) {
            // Setup QREader
            setupQREader();
        } else {
            RuntimePermissionUtil.requestPermission(QRScanActivity.this, cameraPerm, 100);
        }


    }
    private BarcodeReader barcodeReader;

    void setupQREader() {


     //   barcodeReader = (BarcodeReader) getSupportFragmentManager().findFragmentById(R.id.barcode_fragment);

        // Init QREader
        // ------------
        qrEader = new QREader.Builder(this, mySurfaceView, new QRDataListener() {
            @Override
            public void onDetected(final String data) {
                Log.d("QREader", "Value : " + data);
                txtQrResult.post(new Runnable() {
                    @Override
                    public void run() {
                        update(data);
                        qrEader.stop();
                        mySurfaceView.setVisibility(View.GONE);
                        overlay_view.setVisibility(View.GONE);
                    }
                });
            }
        }).facing(QREader.BACK_CAM)
                .enableAutofocus(true)
                .height(mySurfaceView.getHeight())
                .width(mySurfaceView.getWidth())
                .build();
    }


    @Override
    protected void onPause() {
        super.onPause();

        if (hasCameraPermission) {

            // Cleanup in onPause()
            // --------------------
            qrEader.releaseAndCleanup();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (hasCameraPermission) {

            // Init and Start with SurfaceView
            // -------------------------------
            qrEader.initAndStart(mySurfaceView);
        }
    }


    //Getting the scan results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
//                    JSONObject obj = new JSONObject(result.getContents());
//                    //setting values to textviews
//                  //  txtQrResult.setText(obj.getString("name"));
//
//                    update(obj.getString("name"));

                    update("testid");

                } catch (NullPointerException e) {

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

        }, 0, 500);


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

                                            CycliqBluetoothComm.getInstance().bgOperation(1006);


                                        }
                                    });
                                } else {
                                    if (strRideStatus.equalsIgnoreCase("Authorized")) {

                                        String strRideId = response.getString(Constants.KEY_RIDE_ID);

                                        rideId = strRideId;
                                        rideStatus = strRideStatus;


                                        unLocking = true;

                                        CycliqBluetoothComm.getInstance().bgOperation(1006);


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

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        if(requestCode==2){
//            // 请求定位权限
//            if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
//                //
//            }else{
//                showDialog(getResources().getString(R.string.main_permissions_location));
//            }
//        }
//    }

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

                Intent intent = new Intent(QRScanActivity.this, TripRunningActivity.class);

                startActivity(intent);

                //CycliqBluetoothComm.getInstance().getMapsActivity().sendLockOpenStatus();
                finish();


            }
        });

    }


    @Override
    public void onBackPressed() {
        if (unLocking) {

        } else {
            cancelProcess = true;
            Intent intent = new Intent(QRScanActivity.this, MapsActivity.class);

            startActivity(intent);

        }


    }



    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final String FRAGMENT_DIALOG = "dialog";

    private void requestCameraPermission() {
        //if (this.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
        //    new ConfirmationDialog().show(this.getFragmentManager(), FRAGMENT_DIALOG);
        //} else {
//        this.requestPermissions(new String[]{Manifest.permission.CAMERA},
//                REQUEST_CAMERA_PERMISSION);

        //}
        //Log.e(TAG, "DOESNT HAVE CAMERA PERMISSION");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions,
                                           @NonNull final int[] grantResults) {
        if (requestCode == 100) {
            RuntimePermissionUtil.onRequestPermissionsResult(grantResults, new RPResultListener() {
                @Override
                public void onPermissionGranted() {
                    if ( RuntimePermissionUtil.checkPermissonGranted(QRScanActivity.this, cameraPerm)) {
                        restartActivity();
                    }
                }

                @Override
                public void onPermissionDenied() {
                    // do nothing
                }
            });
        }
    }

    void restartActivity() {
        startActivity(new Intent(QRScanActivity.this, QRScanActivity.class));
    }

    @Override
    public void onScanned(Barcode barcode) {
        // play beep sound
        barcodeReader.playBeep();
    }

    @Override
    public void onScannedMultiple(List<Barcode> list) {

    }

    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {

    }

    @Override
    public void onScanError(String s) {

    }

    @Override
    public void onCameraPermissionDenied() {
        Toast.makeText(getApplicationContext(), "Camera permission denied!", Toast.LENGTH_LONG).show();
    }


}
