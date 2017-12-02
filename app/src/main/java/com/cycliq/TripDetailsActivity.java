package com.cycliq;

import android.*;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cycliq.model.TripSummaryModel;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;

import java.util.ArrayList;

//implementing onclicklistener
public class TripDetailsActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {

    //View Objects
    private Button btnDetails, btnVerifyPin, btnCancel;
    private EditText editPhoneNumber, editOtp;
    private LinearLayout layoutLogin;
    private LinearLayout layoutMyWallet;
    private ImageButton btnBack;

    TripSummaryModel tripSummaryModel;

    private TextView txtTripId, txtBikeId, txtTripStart, txtTripEnd, txtDuration, txtAmount;
    private GoogleMap mMap;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trip_details);

        getSupportActionBar().hide();

        setViews();

        setListener();

        assignValues();


    }

    private void assignValues() {


        tripSummaryModel = (TripSummaryModel) getIntent().getSerializableExtra("tripDetails");

        txtBikeId.setText("Vehicle No: " + tripSummaryModel.getBikeID());

        txtAmount.setText(tripSummaryModel.getTripAmount());

        txtTripId.setText("TRIP ID:" + tripSummaryModel.getTripID());

        txtTripStart.setText(tripSummaryModel.getTripDateTime());

        txtTripEnd.setText(tripSummaryModel.getTripDateTime());

        // txtDuration.setText(tripSummaryModel.getTripDateTime());


    }

    private void setListener() {

        btnDetails.setOnClickListener(this);

        btnBack.setOnClickListener(this);

    }

    private void setViews() {

        txtTripId = (TextView) findViewById(R.id.txtTripId);

        txtBikeId = (TextView) findViewById(R.id.txtBikeId);

        txtTripStart = (TextView) findViewById(R.id.txtTripStart);

        txtTripEnd = (TextView) findViewById(R.id.txtTripEnd);

        txtDuration = (TextView) findViewById(R.id.txtDuration);

        txtAmount = (TextView) findViewById(R.id.txtAmount);


        btnDetails = (Button) findViewById(R.id.btnDetails);

        btnBack = (ImageButton) findViewById(R.id.btnBack);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    private void showToast(String msg) {

        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

    }


    @Override
    public void onClick(View view) {

//        Intent intent = new Intent(this, QRScanActivity.class);

        int id = view.getId();

        if (view == btnDetails) {

            Intent intent = new Intent(this, DetailsActivity.class);

            startActivity(intent);

        }

        if (view == btnBack) {

            finish();

        }


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        try {

            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

//            let sourceLocation = CLLocationCoordinate2D(latitude: 40.759011, longitude: -73.984472)
//            let destinationLocation = CLLocationCoordinate2D(latitude: 40.748441, longitude: -73.985564)

            // Add a marker in Sydney and move the camera
            LatLng latLngSource = new LatLng(Double.parseDouble("40.759011"), Double.parseDouble("-73.984472"));

            MarkerOptions markerSource = new MarkerOptions().position(latLngSource).title("Times Square");

            // Changing marker icon
            markerSource.icon(BitmapDescriptorFactory.fromResource(R.mipmap.pin));

            // adding marker
            mMap.addMarker(markerSource);

            // Add a marker in Sydney and move the camera
            LatLng latLngDest = new LatLng(Double.parseDouble("40.748441"), Double.parseDouble("-73.985564"));

            MarkerOptions markerDest = new MarkerOptions().position(latLngDest).title("Empire State Building");

            // Changing marker icon
            markerDest.icon(BitmapDescriptorFactory.fromResource(R.mipmap.pin));


//========================

            PolylineOptions rectOptions = new PolylineOptions();
            LatLng polLatLng = null;


            LatLng start = latLngSource;
            LatLng end = latLngDest;


            ArrayList<LatLng> pathPoint = new ArrayList<LatLng>();


            pathPoint.add(new LatLng(start.latitude, start.longitude));
            pathPoint.add(new LatLng(end.latitude, end.longitude));

            polLatLng = new LatLng(start.latitude, start.longitude);


//            mMap.addMarker(new MarkerOptions().position(start).title("start"));
//            mMap.addMarker(new MarkerOptions().position(end).title("end"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(polLatLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
            rectOptions.addAll(pathPoint);
            rectOptions.width(5);
            rectOptions.color(Color.RED);
            mMap.addPolyline(rectOptions);


            // adding marker
            mMap.addMarker(markerDest);


            LatLngBounds.Builder builder1 = new LatLngBounds.Builder();
            builder1.include(latLngSource).include(latLngDest);

//Animate to the bounds
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(builder1.build(), 100);
            mMap.moveCamera(cameraUpdate);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}