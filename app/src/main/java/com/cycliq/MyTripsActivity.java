package com.cycliq;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cycliq.Adapter.ExpandableTripsListAdapter;
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
import java.util.HashMap;
import java.util.List;
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
    private TextView txtTripId, txtBikeId, txtTripStart, txtTripEnd, txtDuration, txtAmount;

    private ListView listMyTrips;

    private MyTripsAdapter myTripsAdapter;

    private ArrayList<TripSummaryModel> arrTripSummary = new ArrayList<>();

    ExpandableTripsListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_trips);

//        getSupportActionBar().hide();

        setViews();

        setExpandList();

        setListener();

        getMyTripsDetails();

    }

    private void setExpandList() {

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);

        // preparing list data
        prepareListData();

        listAdapter = new ExpandableTripsListAdapter(this, listDataHeader, listDataChild, this);

        // setting list adapter
        expListView.setAdapter(listAdapter);

        // Listview Group click listener
        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                // Toast.makeText(getApplicationContext(),
                // "Group Clicked " + listDataHeader.get(groupPosition),
                // Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        // Listview Group expanded listener
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
//				Toast.makeText(getApplicationContext(),
//						listDataHeader.get(groupPosition) + " Expanded",
//						Toast.LENGTH_SHORT).show();


            }
        });

        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
//				Toast.makeText(getApplicationContext(),
//						listDataHeader.get(groupPosition) + " Collapsed",
//						Toast.LENGTH_SHORT).show();

            }
        });

        // Listview on child click listener
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                // TODO Auto-generated method stub
//				Toast.makeText(
//						getApplicationContext(),
//						listDataHeader.get(groupPosition)
//								+ " : "
//								+ listDataChild.get(
//										listDataHeader.get(groupPosition)).get(
//										childPosition), Toast.LENGTH_SHORT)
//						.show();
                return true;
            }
        });


    }

    /*
     * Preparing the list data
	 */
    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add("Top 250");
        listDataHeader.add("Now Showing");
        listDataHeader.add("Coming Soon..");

        // Adding child data
        List<String> top250 = new ArrayList<String>();
        top250.add("The Shawshank Redemption");
        top250.add("The Godfather");
        top250.add("The Godfather: Part II");
        top250.add("Pulp Fiction");
        top250.add("The Good, the Bad and the Ugly");
        top250.add("The Dark Knight");
        top250.add("12 Angry Men");

        List<String> nowShowing = new ArrayList<String>();
        nowShowing.add("The Conjuring");
        nowShowing.add("Despicable Me 2");
        nowShowing.add("Turbo");
        nowShowing.add("Grown Ups 2");
        nowShowing.add("Red 2");
        nowShowing.add("The Wolverine");

        List<String> comingSoon = new ArrayList<String>();
        comingSoon.add("2 Guns");
        comingSoon.add("The Smurfs 2");
        comingSoon.add("The Spectacular Now");
        comingSoon.add("The Canyons");
        comingSoon.add("Europa Report");

        listDataChild.put(listDataHeader.get(0), top250); // Header, Child data
        listDataChild.put(listDataHeader.get(1), nowShowing);
        listDataChild.put(listDataHeader.get(2), comingSoon);
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

        if (id == R.id.layoutGroupMain) {

            // custom dialog
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_trip_details1);
            dialog.setCancelable(true);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            txtTripId = (TextView) dialog.findViewById(R.id.txtTripId);

            txtBikeId = (TextView) dialog.findViewById(R.id.txtBikeId);

            txtTripStart = (TextView) dialog.findViewById(R.id.txtTripStart);

            txtTripEnd = (TextView) dialog.findViewById(R.id.txtTripEnd);

            txtDuration = (TextView) dialog.findViewById(R.id.txtDuration);

            txtAmount = (TextView) dialog.findViewById(R.id.txtAmount);


            Button btnTripClose = (Button) dialog.findViewById(R.id.btnTripClose);
            // if button is clicked, close the custom dialog
            btnTripClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();

        }

        if (id == R.id.ivExpand) {

            int pos = (int) view.getTag();

            if (expListView.isGroupExpanded(pos)) {

                expListView.collapseGroup(pos);

            } else {

                expListView.expandGroup(pos);

            }
        }

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

        if (id == R.id.linearMain) {

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