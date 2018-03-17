package com.cycliq;

import android.*;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cycliq.Application.CycliqApplication;
import com.cycliq.CommonClasses.Constants;
import com.cycliq.ble.CycliqBluetoothComm;
import com.cycliq.model.LocationListModel;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static com.cycliq.CommonClasses.Constants.KEY_RIDE_ID;

public class TripRunningActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, LocationListener, GoogleMap.OnMarkerClickListener {

    private TextView txtTripId, txtBikeId, txtTripStart, txtTripEnd, txtTripTimer, txtTripAmount, txtClock, txtRideStatus;

    private GoogleMap mMap;

    private Button btnTripClose, btnBikeReport;
    String stringLat = "0.0";
    String stringLng = "0.0";

    Timer tripTimer = null;
    private static final String FORMAT = "%02d:%02d";

    Integer tripCountDown = 0;

    Location currentLocation = null;

    LocationManager locationManager;
    Timer timer = new Timer();
    CountDownTimer countDownTimer;

    ProgressBar progress;
    Marker markerFinal;
    long millisFinishedUntil = 300000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_running);

        txtBikeId = (TextView) findViewById(R.id.txtBikeId);
        txtTripId = (TextView) findViewById(R.id.txtTripId);
        txtTripStart = (TextView) findViewById(R.id.txtTripStart);
        txtTripEnd = (TextView) findViewById(R.id.txtTripEnd);
        txtRideStatus = (TextView) findViewById(R.id.ridestatus);

//        txtTripTimer = (TextView) findViewById(R.id.txtTripTimer);
        txtTripAmount = (TextView) findViewById(R.id.txtTripAmount);
        txtClock = (TextView) findViewById(R.id.txtClock);
        progress = (ProgressBar) findViewById(R.id.circle_progress_bar);
        progress.setMax((int) 7200);

//        txtTripAmount.setVisibility(View.GONE);
        txtTripAmount.setText("₹ 0.00");

        btnBikeReport = (Button) findViewById(R.id.btnReportBike);
        btnTripClose = (Button) findViewById(R.id.btnTripClose);

        btnBikeReport.setOnClickListener(this);

        btnTripClose.setOnClickListener(this);

        CycliqBluetoothComm.getInstance().setTripRunningActivity(this);

        sendLockOpenStatus();

        txtRideStatus.setText("Ride In Progress");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);


    }


    private void startCounDownTimer() {


        countDownTimer = new CountDownTimer(millisFinishedUntil, 1000) {

            public void onTick(long millisUntilFinished) {

                progress.setProgress((int) millisUntilFinished);

                txtClock.setText("" + String.format(FORMAT, TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));

                millisFinishedUntil = millisUntilFinished;

            }

            public void onFinish() {

                txtClock.setText("00:00");

            }

        };

    }

    @Override
    public void onClick(View view) {

        int id = view.getId();
        if (view == btnBikeReport) {


        } else if (view == btnTripClose) {


            Intent intent = new Intent(this, MapsActivity.class);

            startActivity(intent);

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;




        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);

            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            currentLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            if (currentLocation != null) {
                stringLat = Double.toString(currentLocation.getLatitude());
                stringLng = Double.toString(currentLocation.getLongitude());

                CameraUpdate center=CameraUpdateFactory.newLatLng(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
                CameraUpdate zoom=CameraUpdateFactory.zoomTo(11);
                mMap.moveCamera(center);
                mMap.animateCamera(zoom);


                MarkerOptions markerSource = new MarkerOptions().position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())).title("");

                // Changing marker icon
                markerSource.icon(BitmapDescriptorFactory.fromResource(R.mipmap.pin));

                // adding marker
                markerFinal = mMap.addMarker(markerSource);


            }
            Criteria crit = new Criteria();
            crit.setAccuracy(Criteria.ACCURACY_FINE);
            String best = locationManager.getBestProvider(crit, false);
            locationManager.requestLocationUpdates(best, 0, 1, new android.location.LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    CameraUpdate center=CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
                    CameraUpdate zoom=CameraUpdateFactory.zoomTo(11);
                    mMap.moveCamera(center);
                    mMap.animateCamera(zoom);

                    if (markerFinal != null) {
                        markerFinal.remove();
                    }


                    currentLocation = location;

                    if (currentLocation != null) {
                        stringLat = Double.toString(currentLocation.getLatitude());
                        stringLng = Double.toString(currentLocation.getLongitude());

                        MarkerOptions markerSource = new MarkerOptions().position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())).title("");

                        // Changing marker icon
                        markerSource.icon(BitmapDescriptorFactory.fromResource(R.mipmap.pin));

                        // adding marker
                        markerFinal = mMap.addMarker(markerSource);

                    }

                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            });
        }


        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // currentLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);


    }

    @Override
    public void onLocationChanged(Location location) {

        currentLocation = location;

        if (currentLocation != null) {
            stringLat = Double.toString(currentLocation.getLatitude());
            stringLng = Double.toString(currentLocation.getLongitude());
            getMyLocation();
        }



    }

    private void getMyLocation() {
        if (currentLocation != null) {
            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 100);
            mMap.animateCamera(cameraUpdate);

        }
    }

    public void sendLockOpenStatus() {

//        validateCurrentRideStatus();
        txtTripStart.setText("Trip Start: "+GetToday());
        txtTripEnd.setText("Trip End: "+"-");
//        txtTripTimer.setText("00:00:00");
        btnTripClose.setVisibility(View.GONE);

        JSONObject params = new JSONObject();
        try {
            params.put("userRegistrationId", Constants.USER_REGISTERATION_ID);
            params.put("rideId", Constants.getSavedData(TripRunningActivity.this, KEY_RIDE_ID));
            params.put("command", "start");
            params.put("appTime", GetToday());
            params.put("latitude", stringLat);
            params.put("longitude", stringLng);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        startTripTimer();


        //startCounDownTimer();

        //countDownTimer.start();

        String url = Constants.DEMO_BASE_URL + Constants.UPDATE_RIDE_STATUS;

        RequestQueue queue = CycliqApplication.getInstance().getRequestQueue();

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("response values == " + response);

                        try {

                            String strRideStatus = response.getString(Constants.KEY_RIDE_STATUS);


                        } catch (JSONException e) {

                            e.printStackTrace();

                        }

                        runOnUiThread(new Runnable() {
                            public void run() {
                                // Update UI elements



                            }
                        });

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(final VolleyError error) {

                        runOnUiThread(new Runnable() {
                            public void run() {
                                // Update UI elements
//                                    Toast.makeText(this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();


                            }
                        });
                    }
                });


        queue.add(jsObjRequest);
    }

    public void sendLockClosedStatus() {


        runOnUiThread(new Runnable() {
            public void run() {
                // Update UI elements

//                layoutRideStatus.setVisibility(View.GONE);
//                btnUnlock.setVisibility(View.VISIBLE);
                Constants.saveData(TripRunningActivity.this, Constants.KEY_RIDE_ID, "");
                Constants.saveData(TripRunningActivity.this, Constants.KEY_RIDE_STATUS, Constants.NONE);


            }
        });



        JSONObject params = new JSONObject();
        try {
            params.put("userRegistrationId", Constants.USER_REGISTERATION_ID);
            params.put("rideId", Constants.getSavedData(TripRunningActivity.this, KEY_RIDE_ID));
            params.put("command", "end");
            params.put("appTime", GetToday());
            params.put("latitude", stringLat);
            params.put("longitude", stringLng);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        stopTripTimer();

        String url = Constants.DEMO_BASE_URL + Constants.UPDATE_RIDE_STATUS;

        RequestQueue queue = CycliqApplication.getInstance().getRequestQueue();

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("response values == " + response);

                        try {

                            String strRideStatus = response.getString(Constants.KEY_RIDE_STATUS);


                        } catch (JSONException e) {

                            e.printStackTrace();

                        }

                        runOnUiThread(new Runnable() {
                            public void run() {
                                // Update UI elements


                            }
                        });

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(final VolleyError error) {

                        runOnUiThread(new Runnable() {
                            public void run() {
                                // Update UI elements
//                                    Toast.makeText(this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();


                            }
                        });
                    }
                });


        queue.add(jsObjRequest);


    }

    public static String GetToday(){
        Date presentTime_Date = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return dateFormat.format(presentTime_Date);
    }

    public void startTripTimer () {
        //Every three seconds it will trigger the api call


        if (tripTimer != null) {
            tripTimer.cancel();
            tripTimer = null;
        }

        tripTimer = new Timer();

        tripTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                tripCountDown = tripCountDown + 1;

                Integer hours = (tripCountDown / 3600) % 60;
                Integer mins = (tripCountDown / 60) % 60;
                Integer seconds = tripCountDown % 60;

                final String text =  String.format("%02d:%02d:%02d", hours, mins, seconds);



                progress.setProgress((int) tripCountDown);

                checkStatus();

//                final String text = String.format(Locale.getDefault(), "%02d:%02d:%02d",
//                        TimeUnit.MILLISECONDS.toHours(tripCountDown) % 60,
//                        TimeUnit.MILLISECONDS.toMinutes(tripCountDown) % 60,
//                        TimeUnit.MILLISECONDS.toSeconds(tripCountDown) % 60);
                runOnUiThread(new Runnable() {
                    public void run() {
                        txtClock.setText(text);


                    }
                });
            }
        }, 0, 1000);

    }

    public void stopTripTimer () {
        if (tripTimer != null) {
            tripTimer.cancel();
            tripTimer = null;
        }

        runOnUiThread(new Runnable() {
            public void run() {
                txtTripEnd.setText("Trip End :" + GetToday());
                btnTripClose.setVisibility(View.VISIBLE);
                txtTripAmount.setVisibility(View.VISIBLE);
                txtRideStatus.setText("Ride Completed");

            }
        });



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (timer != null) {

            timer.cancel();

            timer = null;
        }
    }

    private void validateCurrentRideStatus() {

        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        String rideStatus = Constants.getSavedData(this, Constants.KEY_RIDE_STATUS);

        String rideId = Constants.getSavedData(this, KEY_RIDE_ID);

        if (!rideStatus.equalsIgnoreCase(Constants.NONE)) {

            // here change to visible

//            txtTripId.setText("Trip ID:" + rideId);
//            txtBikeId.setText("Bike ID:" + rideId);

            Log.d("TAG", "Tag values rideId == " + rideId);

            // txtRideStatus.setText(rideStatus);

            timer = new Timer();



            checkStatus();

            // for timer un command here.
//            timer.scheduleAtFixedRate(new TimerTask() {
//
//                @Override
//                public void run() {
//
//                    checkStatus();
//
//                }
//
//            }, 0, 100000);

        } else {


        }

    }

    private void checkStatus() {

        if (Constants.isNetworkAvailable(this)) {

            JSONObject params = new JSONObject();
            try {

                params.put("userRegistrationId", Constants.USER_REGISTERATION_ID);
                params.put("rideId", Constants.getSavedData(this, KEY_RIDE_ID));
                params.put("latitude", stringLat);
                params.put("longitude", stringLng);
                params.put("appTime", GetToday());

                Log.d("TAG key ride id == ", Constants.getSavedData(this, KEY_RIDE_ID));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            String url = Constants.DEMO_BASE_URL + Constants.CHECK_STATUS;

            RequestQueue queue = CycliqApplication.getInstance().getRequestQueue();
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            System.out.println(response);

                            try {

                                String strRideStatus = response.getString(Constants.KEY_RIDE_STATUS);

                                String strRideId = response.getString(KEY_RIDE_ID);

                                Constants.saveData(TripRunningActivity.this, Constants.KEY_RIDE_STATUS, strRideStatus);

                                Constants.saveData(TripRunningActivity.this, KEY_RIDE_ID, strRideId);


                                // for timer un command here.
                                //validateCurrentRideStatus();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                           // Constants.hideProgressDialog();

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                           // Constants.hideProgressDialog();

                        }
                    });

            queue.add(jsObjRequest);

        } else {


           // Constants.showToast(this, Constants.internetAlert);

        }

    }

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {




        if (marker.getPosition() != null) {
            LatLng postition = marker.getPosition();

            getStreetAddress(Double.toString(postition.latitude),Double.toString(postition.longitude));
        }


        return false;
    }

    public void getStreetAddress(String lat, String lng) {
        String destloc = "latlng=" + lat + "," + lng;
        String serverKey = "AIzaSyDLlE_6rqomakJuSnthZLw9AyzOyZdF87U";

        String url = "https://maps.googleapis.com/maps/api/geocode/json?" + destloc + "&sensor=true&key=" + serverKey;

        RequestQueue queue = CycliqApplication.getInstance().getRequestQueue();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("response values == " + response);

                        List<List<HashMap<String, String>>> routes = new ArrayList<>() ;
                        JSONArray jRoutes;
                        JSONArray jLegs;
                        JSONArray jSteps;


                        try {
                            jRoutes = response.getJSONArray("results");

                            if (jRoutes != null) {
                                if (jRoutes.length() > 0) {
                                    JSONObject address_comp = jRoutes.getJSONObject(0);
                                    final String current_address = address_comp.getString("formatted_address");

                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            // Update UI elements
                                            if (markerFinal != null) {
                                                markerFinal.setTitle(current_address);
                                            }

                                        }
                                    });
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


                            }
                        });
                    }
                });


        queue.add(jsObjRequest);

    }
}
