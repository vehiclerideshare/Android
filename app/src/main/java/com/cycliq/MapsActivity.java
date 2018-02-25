package com.cycliq;

import android.*;
import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.RequestResult;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.model.Step;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cycliq.Application.CycliqApplication;
import com.cycliq.CommonClasses.Constants;
import com.cycliq.ble.CycliqBluetoothComm;
import com.cycliq.model.LocationListModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.data.Subscription;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.nearby.messages.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.cycliq.CommonClasses.Constants.KEY_RIDE_ID;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, LocationListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;

    private Button btnUnlock, btnReserve, btnSearchCancel, btnMakeReservation, btnTripClose, btnBikeReport;

    private ImageButton btnRefresh, btnLocation, btnMenu, btnSearch, btnReport, btnCompose, btnBack;

    LinearLayout layoutRideStatus, layoutSearch, layoutClock, layoutBottom;

    TextView txtRideStatus, txtRideId, txtClock, txtCancelReservation, btnCloseBottomView, txtBottomVehicleNo, txtAvailable, txtAddress;

    LatLngBounds.Builder builder = new LatLngBounds.Builder();

    Location currentLocation = null;

    LocationManager locationManager;

    Timer timer = new Timer();

    Timer timerTopClock = new Timer();

    Handler handler = new Handler();

    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    LocationListModel clickedMarkerDetails;

    String vehReserveStatus = "Available";

    private static final String FORMAT = "%02d:%02d";

    CountDownTimer countDownTimer;

    ProgressBar progress;

    long millisFinishedUntil = 300000;


    private TextView txtTripId, txtBikeId, txtTripStart, txtTripEnd, txtTripTimer, txtBikeIdTop;


    String stringLat = "0.0";
    String stringLng = "0.0";

    Timer tripTimer = null;

    Integer tripCountDown = 0;

    Polyline polylineFinal = null;


    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_PHOTOS = "photos";
    private static final String TAG_MOVIES = "movies";
    private static final String TAG_NOTIFICATIONS = "notifications";
    private static final String TAG_SETTINGS = "settings";
    public static String CURRENT_TAG = TAG_HOME;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private android.os.Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().hide();

//        createLocationRequest();
//
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addApi(LocationServices.API)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .build();


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        setViews();

        setListener();

        setUpNavigationView();

//        timerTopClock.scheduleAtFixedRate(new TimerTask() {
//
//                @Override
//                public void run() {
//
//                    checkStatus();
//
//                }
//
//            }, 0, 100000);


        resetValues();

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("", "Place: " + place.getName());

                System.out.println("place" + place.getLatLng().latitude);
                System.out.println("place" + place.getLatLng().longitude);

                stringLat = "" + place.getLatLng().latitude;
                stringLng = "" + place.getLatLng().longitude;

                readDataFromJson(false);

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("", "An error occurred: " + status);
            }
        });


//        if(getVehReserveStatus()) {
//            self.vehNumber = (UserDefaults.standard.value(forKey: KEY_RESERVE_VEHNUMBER) as? String)!
//                    self.vehReserveStatus = "Reserved"
//        } else {
//            self.vehReserveStatus = "Available"
//        }

        layoutRideStatus.setVisibility(View.GONE);

        CycliqBluetoothComm.getInstance().setMapsActivity(this);

//        startCounDownTimer();
//
//        countDownTimer.start();
//
//        layoutClock.setVisibility(View.VISIBLE);

    }

    private void startCounDownTimer() {

        progress.setMax((int) millisFinishedUntil);

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

                resetValues();
            }

        };

    }

    private boolean getVehReserveStatus() {


        return false;

    }

//    private void getAssetsValues() {
//
//        try {
//
//            JSONObject obj = new JSONObject(loadJSONFromAsset());
//
//            JSONArray m_jArry = obj.getJSONArray("locationlists");
//
//            Log.d("Array values-->", String.valueOf(m_jArry));
//
//            ArrayList<LocationListModel> arrLocationList = new ArrayList<>();
//
//            builder = new LatLngBounds.Builder();
//
//            for (int i = 0; i < m_jArry.length(); i++) {
//                JSONObject jo_inside = m_jArry.getJSONObject(i);
//
//                Log.d("Details-->", jo_inside.getString("locaddress"));
//                String locAddress = jo_inside.getString("locaddress");
//                String lat = jo_inside.getString("lat");
//                String lon = jo_inside.getString("lng");
//
//
//                LocationListModel locationListModel = new LocationListModel(locAddress, lat, lon);
//
//                loadMapMarker(locationListModel);
//
//                arrLocationList.add(locationListModel);
//
//            }
//
//            LatLngBounds bounds = builder.build();
//
//            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10));
//
//            Log.d("arrLocationList-->", arrLocationList.toString());
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    public String loadJSONFromAsset() {
//        String json = null;
//        try {
//            InputStream is = this.getAssets().open("location_lists.json");
//            int size = is.available();
//            byte[] buffer = new byte[size];
//            is.read(buffer);
//            is.close();
//            json = new String(buffer, "UTF-8");
//        } catch (IOException ex) {
//            ex.printStackTrace();
//            return null;
//        }
//        return json;
//    }

    private void loadMapMarker(LocationListModel locationListModel) {

        // Add a marker in Sydney and move the camera
        LatLng latLng = new LatLng(Double.parseDouble(locationListModel.getLat()), Double.parseDouble(locationListModel.getLng()));

        MarkerOptions marker = new MarkerOptions().position(latLng).title(locationListModel.getVehicleRegnNumber());

        // Changing marker icon
        marker.icon(BitmapDescriptorFactory.fromResource(R.mipmap.pin));


        // adding marker
        Marker markerNew = mMap.addMarker(marker);


        markerNew.setTag(locationListModel);

//        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        builder.include(latLng);

        for (int i = 0; i < navigationView.getMenu().size(); i++) {
            setTextColorForMenuItem(navigationView.getMenu().getItem(i), android.R.color.black);

        }

        setUpNavigationView();

        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

//        Marker marker =  mMap.addMarker(new MarkerOptions()
//                .position(latLng)
//                .title(locationListModel.getLocaddress()
//                ).icon(BitmapDescriptorFactory.fromResource(R.mipmap.pin)));
//
////      //  MarkerOptions marker = new MarkerOptions().position(latLng).title(locationListModel.getLocaddress());
////
////        // Changing marker icon
////        marker.icon(BitmapDescriptorFactory.fromResource(R.mipmap.pin));
//
//
//        // adding marker
//        mMap.addMarker(marker);

    }


    private void setTextColorForMenuItem(MenuItem menuItem, @ColorRes int color) {

        SpannableString spanString = new SpannableString(menuItem.getTitle().toString());
        spanString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, color)), 0, spanString.length(), 0);
        menuItem.setTitle(spanString);

        Drawable drawable = menuItem.getIcon();
        if (drawable != null) {
            drawable.mutate();
            drawable.setColorFilter(getResources().getColor(android.R.color.black), PorterDuff.Mode.SRC_ATOP);
        }

    }

    private void setUpNavigationView() {


        for (int i = 0; i < navigationView.getMenu().size(); i++) {
            setTextColorForMenuItem(navigationView.getMenu().getItem(i), android.R.color.black);

            Drawable drawable = navigationView.getMenu().getItem(i).getIcon();
            if(drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(android.R.color.black), PorterDuff.Mode.SRC_ATOP);
            }

        }

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.home:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                        break;
                    case R.id.nav_wallet:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_PHOTOS;

                        startActivity(new Intent(MapsActivity.this, MyWalletActivity.class));
                        drawer.closeDrawers();
                        break;
                    case R.id.nav_payments:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_MOVIES;

                        startActivity(new Intent(MapsActivity.this, MyPaymentsActivity.class));
                        drawer.closeDrawers();
                        break;
                    case R.id.nav_trips:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_NOTIFICATIONS;
                        startActivity(new Intent(MapsActivity.this, MyTripsActivity.class));
                        drawer.closeDrawers();
                        break;
                    case R.id.nav_messages:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_NOTIFICATIONS;
                        drawer.closeDrawers();
                        break;
                    case R.id.nav_invite_friends:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_NOTIFICATIONS;

                        drawer.closeDrawers();
                        break;
                    case R.id.nav_user_guide:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_NOTIFICATIONS;

                        drawer.closeDrawers();
                        break;
                    case R.id.nav_settings:
                        navItemIndex = 4;
                        CURRENT_TAG = TAG_SETTINGS;

                        startActivity(new Intent(MapsActivity.this, SettingsActivity.class));
                        drawer.closeDrawers();

                        break;
//                    case R.id.nav_about_us:
//                        // launch new intent instead of loading fragment
//                        startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
//                        drawer.closeDrawers();
//                        return true;
//                    case R.id.nav_privacy_policy:
//                        // launch new intent instead of loading fragment
//                        startActivity(new Intent(MainActivity.this, PrivacyPolicyActivity.class));
//                        drawer.closeDrawers();
//                        return true;
                    default:
                        navItemIndex = 0;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                //  loadHomeFragment();

                return true;
            }
        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }


    private void setListener() {

        btnUnlock.setOnClickListener(this);

        btnRefresh.setOnClickListener(this);

        btnLocation.setOnClickListener(this);

        btnMenu.setOnClickListener(this);

        btnSearch.setOnClickListener(this);

        btnReport.setOnClickListener(this);

        btnCompose.setOnClickListener(this);

        btnSearchCancel.setOnClickListener(this);

        txtCancelReservation.setOnClickListener(this);

        btnMakeReservation.setOnClickListener(this);

        btnCloseBottomView.setOnClickListener(this);

        btnBikeReport.setOnClickListener(this);

        btnTripClose.setOnClickListener(this);

        btnBack.setOnClickListener(this);

        btnReserve.setOnClickListener(this);

    }

    private void setViews() {

        progress = (ProgressBar) findViewById(R.id.circle_progress_bar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        navigationView = (NavigationView) findViewById(R.id.nav_view);

        btnUnlock = (Button) findViewById(R.id.btnUnlock);

        btnReserve = (Button) findViewById(R.id.btnReserve);

        btnRefresh = (ImageButton) findViewById(R.id.btnRefresh);

        btnLocation = (ImageButton) findViewById(R.id.btnLocation);

        btnMenu = (ImageButton) findViewById(R.id.btnMenu);

        layoutRideStatus = (LinearLayout) findViewById(R.id.layoutRideStatus);

        layoutRideStatus.setVisibility(View.GONE);

//        txtRideId = (TextView) findViewById(R.id.txtRideId);
//
        // txtRideStatus = (TextView) findViewById(R.id.txtRideStatus);

        btnCompose = (ImageButton) findViewById(R.id.btnCompose);

        btnSearch = (ImageButton) findViewById(R.id.btnSearch);

        btnReport = (ImageButton) findViewById(R.id.btnReport);

        layoutSearch = (LinearLayout) findViewById(R.id.layoutSearch);

        btnSearchCancel = (Button) findViewById(R.id.btnSearchCancel);

        txtClock = (TextView) findViewById(R.id.txtClock);

        txtCancelReservation = (TextView) findViewById(R.id.txtCancelReservation);

        layoutClock = (LinearLayout) findViewById(R.id.layoutClock);

        btnMakeReservation = (Button) findViewById(R.id.btnMakeReservation);

        layoutBottom = (LinearLayout) findViewById(R.id.layoutBottom);

        btnCloseBottomView = (TextView) findViewById(R.id.btnCloseBottomView);

        txtBottomVehicleNo = (TextView) findViewById(R.id.txtBottomVehicleNo);

        txtAvailable = (TextView) findViewById(R.id.txtAvailable);

        txtBikeId = (TextView) findViewById(R.id.txtBikeId);
        txtBikeIdTop = (TextView) findViewById(R.id.txtBikeIdTop);

        txtTripId = (TextView) findViewById(R.id.txtTripId);
        txtTripStart = (TextView) findViewById(R.id.txtTripStart);
        txtTripEnd = (TextView) findViewById(R.id.txtTripEnd);
        txtTripTimer = (TextView) findViewById(R.id.txtTripTimer);
        btnBikeReport = (Button) findViewById(R.id.btnReportBike);
        btnTripClose = (Button) findViewById(R.id.btnTripClose);
        txtAddress = (TextView) findViewById(R.id.txtBottomAddress);
        progress = (ProgressBar) findViewById(R.id.progressbar);



        txtAddress.setVisibility(View.GONE);
        navHeader = navigationView.getHeaderView(0);
        btnBack = (ImageButton) navHeader.findViewById(R.id.btnBack);

    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        if (view == btnUnlock) {

            Intent intent = new Intent(this, QRScanActivity.class);

            startActivity(intent);

        }
        else if (view == btnReport) {

            Intent intent = new Intent(this, ReportActivity.class);

            startActivity(intent);

        }
        else if (view == btnMenu) {

//            Intent intent = new Intent(this, ProfileActivity.class);
//
//            startActivity(intent);

            drawer.openDrawer(Gravity.LEFT);

        } else if (view == btnRefresh) {

            readDataFromJson(false);

        } else if (view == btnSearchCancel) {

            layoutSearch.setVisibility(View.GONE);

        } else if (view == btnCompose) {
//
//            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
//            builder1.setMessage("Report option coming here");
//            builder1.setCancelable(true);
//
////            builder1.setPositiveButton(
////                    "OK",
////                    new DialogInterface.OnClickListener() {
////                        public void onClick(DialogInterface dialog, int id) {
////                            dialog.cancel();
////                        }
////                    });
//
//            builder1.setNegativeButton(
//                    "OK",
//                    new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            dialog.cancel();
//                        }
//                    });
//
//            AlertDialog alert11 = builder1.create();
//            alert11.show();


            Intent intent = new Intent(this, ReportFormActivity.class);

            startActivity(intent);

        } else if (view == btnSearch) {

            layoutSearch.setVisibility(View.VISIBLE);

//            try {
//                Intent intent =
//                        new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
//                                .build(this);
//                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
//            } catch (GooglePlayServicesRepairableException e) {
//                // TODO: Handle the error.
//            } catch (GooglePlayServicesNotAvailableException e) {
//                // TODO: Handle the error.
//            }

        } else if (view == btnLocation) {

            Log.d("location button", "location button pressed.");

            getMyLocation();

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            if (locationManager != null) {

                currentLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

                if (currentLocation != null) {

                    Log.d("location button", "location button pressed with not null.");

//                    LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
//
//                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                }

            }
        } else if (view == txtCancelReservation) {

            cancelReservation();

        } else if (view == btnMakeReservation) {

            makeReservation();


        } else if (view == btnReserve) {

            makeReservation();


        } else if (view == btnCloseBottomView) {

            btnReserve.setVisibility(View.GONE);
            btnUnlock.setVisibility(View.VISIBLE);

            layoutBottom.setVisibility(View.GONE);
            if (polylineFinal != null) {
                polylineFinal.remove();
                polylineFinal = null;
            }

        } else if (view == txtAvailable) {

            layoutClock.setVisibility(View.VISIBLE);

        } else if (view == btnBikeReport) {


        } else if (view == btnTripClose) {

            layoutRideStatus.setVisibility(View.GONE);
            btnUnlock.setVisibility(View.VISIBLE);

        } else if (view == btnBack) {

            drawer.closeDrawers();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i("", "Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i("", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        for(int i = 0; i < menu.size(); i++){
            Drawable drawable = menu.getItem(i).getIcon();
            if(drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
            }
        }

        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setOnMapClickListener(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(false);

            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            currentLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            if (currentLocation != null) {
                stringLat = Double.toString(currentLocation.getLatitude());
                stringLng = Double.toString(currentLocation.getLongitude());

                CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
                CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
                mMap.moveCamera(center);
                mMap.animateCamera(zoom);


            }
            Criteria crit = new Criteria();
            crit.setAccuracy(Criteria.ACCURACY_FINE);
            String best = locationManager.getBestProvider(crit, false);
            locationManager.requestLocationUpdates(best, 0, 1, new android.location.LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    currentLocation = location;

                    if (currentLocation != null) {
                        stringLat = Double.toString(currentLocation.getLatitude());
                        stringLng = Double.toString(currentLocation.getLongitude());


                    }

                    CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
                    CameraUpdate zoom = CameraUpdateFactory.zoomTo(11);
                    mMap.moveCamera(center);
                    mMap.animateCamera(zoom);

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

        mMap.setOnMarkerClickListener(this);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // currentLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

        readDataFromJson(false);

    }

    @Override
    public void onLocationChanged(Location location) {

        currentLocation = location;

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

       // finish();

      //  System.exit(0);


        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);

    }

    private void readDataFromJson(boolean isLocal) {

        if (Constants.isNetworkAvailable(this)) {

            Constants.showProgressDialog(this);

            JSONObject params = new JSONObject();

            try {

                params.put("userRegistrationId", Constants.USER_REGISTERATION_ID);

                if (isLocal) {

                    params.put("latitude", "80.0");

                    params.put("longitude", "12.2");

                } else {

                    params.put("latitude", stringLat);

                    params.put("longitude", stringLng);

                }

            } catch (JSONException e) {

                e.printStackTrace();

            }

            String url = Constants.DEMO_BASE_URL + Constants.LOCATE_NEAR_BY_VEHICLES;

            RequestQueue queue = CycliqApplication.getInstance().getRequestQueue();

            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            System.out.println(response);

                            try {
                                JSONArray arrVehicleList = response.getJSONArray("vehicleList");

                                ArrayList<LocationListModel> arrLocationList = new ArrayList<>();

                                builder = new LatLngBounds.Builder();

                                for (int i = 0; i < arrVehicleList.length(); i++) {

                                    JSONObject jo_inside = arrVehicleList.getJSONObject(i);

                                    String locAddress = jo_inside.getString("vehicleRegnNumber");

                                    String lat = jo_inside.getString("lat");

                                    String lon = jo_inside.getString("lng");

                                    LocationListModel locationListModel = new LocationListModel(locAddress, lat, lon);

                                    loadMapMarker(locationListModel);

                                    arrLocationList.add(locationListModel);

                                }

                                if (builder != null) {

                                    LatLngBounds bounds = builder.build();

                                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 160));

                                }

                                Log.d("arrLocationList-->", arrLocationList.toString());

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

                            Constants.showToast(MapsActivity.this, Constants.serverErrorMsg);


                        }
                    });
            queue.add(jsObjRequest);

        } else {

            Constants.showToast(this, Constants.internetAlert);

        }

    }


    private void getMyLocation() {
        if (currentLocation != null) {
            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 30);
            mMap.animateCamera(cameraUpdate);


        }


    }


    private void makeReservation() {

        if (Constants.isNetworkAvailable(this)) {

            Constants.showProgressDialog(this);

            JSONObject params = new JSONObject();

            try {

                params.put("userRegistrationId", Constants.USER_REGISTERATION_ID);

                params.put("vehicleRegnNumber", clickedMarkerDetails.getVehicleRegnNumber());

            } catch (JSONException e) {

                e.printStackTrace();

            }

            String url = Constants.DEMO_BASE_URL + Constants.KEY_RESERVE_VEHICLE;

            RequestQueue queue = CycliqApplication.getInstance().getRequestQueue();

            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            System.out.println(response);

                            try {

                                String strStatus = response.getString("status");

                                if (strStatus.equalsIgnoreCase("success")) {

                                    txtAvailable.setText("Reserved");

                                    txtAvailable.setTextColor(getResources().getColor(R.color.colorAccent));

                                    layoutClock.setVisibility(View.VISIBLE);

                                    btnMakeReservation.setBackgroundColor(getResources().getColor(R.color.dark_gray));

                                    System.out.println("millisFinishedUntil == " + millisFinishedUntil);

                                    startCounDownTimer();

                                    countDownTimer.start();

                                }

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

                            Constants.showToast(MapsActivity.this, Constants.serverErrorMsg);

                        }
                    });
            queue.add(jsObjRequest);

        } else {

            Constants.showToast(this, Constants.internetAlert);

        }

    }

    private void cancelReservation() {

        if (Constants.isNetworkAvailable(this)) {

            Constants.showProgressDialog(this);

            JSONObject params = new JSONObject();

            try {

                params.put("userRegistrationId", Constants.USER_REGISTERATION_ID);

                params.put("vehicleRegnNumber", clickedMarkerDetails.getVehicleRegnNumber());

            } catch (JSONException e) {

                e.printStackTrace();

            }

            String url = Constants.DEMO_BASE_URL + Constants.KEY_CANCEL_VEHICLE;

            RequestQueue queue = CycliqApplication.getInstance().getRequestQueue();

            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            System.out.println(response);

                            try {

                                String strStatus = response.getString("status");

                                if (strStatus.equalsIgnoreCase("success")) {

                                    txtAvailable.setText("Avaiable");

                                    txtAvailable.setTextColor(getResources().getColor(R.color.colorGreen));

                                    btnMakeReservation.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                                    countDownTimer.cancel();

                                    resetValues();
                                }


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

                            Constants.showToast(MapsActivity.this, Constants.serverErrorMsg);

                        }
                    });
            queue.add(jsObjRequest);

        } else {

            Constants.showToast(this, Constants.internetAlert);

        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        layoutRideStatus.setVisibility(View.GONE);

        //   Constants.showProgressDialog(this, true);

        layoutSearch.setVisibility(View.GONE);

        // validateCurrentRideStatus();

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
            layoutRideStatus.setVisibility(View.VISIBLE);

            txtTripId.setText("Trip ID:" + rideId);
            txtBikeId.setText("Bike ID:" + rideId);
            txtBikeIdTop.setText("Bike ID:" + rideId);

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

            layoutRideStatus.setVisibility(View.GONE);

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

                                Constants.saveData(MapsActivity.this, Constants.KEY_RIDE_STATUS, strRideStatus);

                                Constants.saveData(MapsActivity.this, KEY_RIDE_ID, strRideId);


                                // for timer un command here.
                                //validateCurrentRideStatus();

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

                        }
                    });

            queue.add(jsObjRequest);

        } else {


            Constants.showToast(this, Constants.internetAlert);

        }

    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        clickedMarkerDetails = (LocationListModel) (marker.getTag());

        txtBottomVehicleNo.setText("Vehicle No:" + clickedMarkerDetails.getVehicleRegnNumber());

        layoutBottom.setVisibility(View.GONE);

        btnUnlock.setVisibility(View.GONE);
        btnReserve.setVisibility(View.VISIBLE);

        makeDirection(clickedMarkerDetails.getLat(), clickedMarkerDetails.getLng());

        return false;
    }

    @Override
    public void onMapClick(LatLng latLng) {


        if (polylineFinal != null) {
            polylineFinal.remove();
            polylineFinal = null;
        }

        resetValues();
    }

    private class Handler {


    }

    private void resetValues() {

        layoutBottom.setVisibility(View.GONE);
        btnUnlock.setVisibility(View.VISIBLE);
        btnReserve.setVisibility(View.GONE);

        layoutClock.setVisibility(View.GONE);



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();

                System.exit(0);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void sendLockOpenStatus() {

        validateCurrentRideStatus();
        layoutRideStatus.setVisibility(View.VISIBLE);
        txtTripStart.setText("Trip Start: " + GetToday());
        txtTripEnd.setText("Trip End: " + "-");
        txtTripTimer.setText("00:00:00");
        btnTripClose.setVisibility(View.GONE);
        btnUnlock.setVisibility(View.GONE);

        JSONObject params = new JSONObject();
        try {
            params.put("userRegistrationId", Constants.USER_REGISTERATION_ID);
            params.put("rideId", Constants.getSavedData(MapsActivity.this, KEY_RIDE_ID));
            params.put("command", "start");
            params.put("appTime", GetToday());
            params.put("latitude", stringLat);
            params.put("longitude", stringLng);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        startTripTimer();


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
                Constants.saveData(MapsActivity.this, Constants.KEY_RIDE_ID, "");
                Constants.saveData(MapsActivity.this, Constants.KEY_RIDE_STATUS, Constants.NONE);


            }
        });


        JSONObject params = new JSONObject();
        try {
            params.put("userRegistrationId", Constants.USER_REGISTERATION_ID);
            params.put("rideId", Constants.getSavedData(MapsActivity.this, KEY_RIDE_ID));
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

    public static String GetToday() {
        Date presentTime_Date = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return dateFormat.format(presentTime_Date);
    }

    public void startTripTimer() {
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

                final String text = String.format("%02d:%02d:%02d", hours, mins, seconds);


//                final String text = String.format(Locale.getDefault(), "%02d:%02d:%02d",
//                        TimeUnit.MILLISECONDS.toHours(tripCountDown) % 60,
//                        TimeUnit.MILLISECONDS.toMinutes(tripCountDown) % 60,
//                        TimeUnit.MILLISECONDS.toSeconds(tripCountDown) % 60);
                runOnUiThread(new Runnable() {
                    public void run() {
                        txtTripTimer.setText(text);


                    }
                });
            }
        }, 0, 1000);

    }

    public void stopTripTimer() {
        if (tripTimer != null) {
            tripTimer.cancel();
            tripTimer = null;
        }

        runOnUiThread(new Runnable() {
            public void run() {
                txtTripEnd.setText("Trip End :" + GetToday());
                btnTripClose.setVisibility(View.VISIBLE);

            }
        });

//        txtTripTimer.setText("00:00:00");


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

                        List<List<HashMap<String, String>>> routes = new ArrayList<>();
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
                                            txtAddress.setVisibility(View.VISIBLE);
                                            txtAddress.setText(current_address);

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


    public void makeDirection(String lat, String lng) {


        if (polylineFinal != null) {
            polylineFinal.remove();
            polylineFinal = null;
        }

        getStreetAddress(lat, lng);

        final LatLng latLngSource = new LatLng(Double.parseDouble(stringLat), Double.parseDouble(stringLng));
        final LatLng latLngDest = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));


        String destloc = "latlng=" + lat + "," + lng;


        LatLng start = latLngSource;
        LatLng end = latLngDest;

        String serverKey = "AIzaSyDLlE_6rqomakJuSnthZLw9AyzOyZdF87U";
        LatLng origin = start;
        LatLng destination = end;
        GoogleDirection.withServerKey(serverKey)
                .from(origin)
                .to(destination)
                .execute(new DirectionCallback() {

                    @Override
                    public void onDirectionSuccess(Direction direction, String s) {
                        String status = direction.getStatus();
                        if (status.equals(RequestResult.OK)) {
                            Route route = direction.getRouteList().get(0);
                            Leg leg = route.getLegList().get(0);
                            ArrayList<LatLng> directionPositionList = leg.getDirectionPoint();
                            PolylineOptions polylineOptions = DirectionConverter.createPolyline(getApplicationContext(), directionPositionList, 5, Color.RED);
                            polylineFinal = mMap.addPolyline(polylineOptions);
                            LatLngBounds.Builder builder1 = new LatLngBounds.Builder();
                            builder1.include(latLngSource).include(latLngDest);

//Animate to the bounds
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(builder1.build(), 160);
                            mMap.moveCamera(cameraUpdate);
                            List<Step> stepList = direction.getRouteList().get(0).getLegList().get(0).getStepList();
                            ArrayList<PolylineOptions> polylineOptionList = DirectionConverter.createTransitPolyline(getApplicationContext(), stepList, 5, Color.RED, 3, Color.BLUE);
                            for (PolylineOptions polylineOption : polylineOptionList) {
                                mMap.addPolyline(polylineOption);
                            }
                            // Do something
                        } else if (status.equals(RequestResult.NOT_FOUND)) {
                            // Do something
                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        // Do something here
                    }
                });

       /* JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("response values == " + response);

                        List<List<HashMap<String, String>>> routes = new ArrayList<>() ;
                        JSONArray jRoutes;
                        JSONArray jLegs;
                        JSONArray jSteps;


                        try {
                            jRoutes = response.getJSONArray("routes");

                            if (jRoutes != null) {
                                if (jRoutes.length() > 0) {
                                    ArrayList<LatLng> points = null;

                                    PolylineOptions polyLineOptions = null;
                                    for (int i = 0; i < routes.size(); i++) {
                                        points = new ArrayList<LatLng>();
                                        polyLineOptions = new PolylineOptions();
                                        List<HashMap<String, String>> path = routes.get(i);

                                        for (int j = 0; j < path.size(); j++) {
                                            HashMap<String, String> point = path.get(j);

                                            double lat = Double.parseDouble(point.get("lat"));
                                            double lng = Double.parseDouble(point.get("lng"));
                                            LatLng position = new LatLng(lat, lng);

                                            points.add(position);
                                        }

                                        polyLineOptions.addAll(points);
                                        polyLineOptions.width(2);
                                        polyLineOptions.color(Color.BLUE);
                                    }

                                    mMap.addPolyline(polyLineOptions);

//                                    JSONObject overview_polyline = jRoutes.getJSONObject(0);
//                                    if (overview_polyline.length() > 0) {
//                                        JSONObject dictPolyline = overview_polyline.getJSONObject("overview_polyline");
//                                        String points = dictPolyline.getString("points");
//
//
//                                    }
                                }


                            }



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


        queue.add(jsObjRequest);*/


      /*  PolylineOptions rectOptions = new PolylineOptions();
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
        polylineFinal = mMap.addPolyline(rectOptions);





        LatLngBounds.Builder builder1 = new LatLngBounds.Builder();
        builder1.include(latLngSource).include(latLngDest);

//Animate to the bounds
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(builder1.build(), 100);
        mMap.moveCamera(cameraUpdate);*/
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // All good!
        } else {
            Toast.makeText(this, "Need your location!", Toast.LENGTH_SHORT).show();
        }


    }


}
