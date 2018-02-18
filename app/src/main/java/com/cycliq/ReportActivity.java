package com.cycliq;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.pm.PackageManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cycliq.Adapter.ExpandableTripsListAdapter;
import com.cycliq.Adapter.MyTripsAdapter;
import com.cycliq.Adapter.ReportRecyclerAdapter;
import com.cycliq.Application.CycliqApplication;
import com.cycliq.CommonClasses.Constants;
import com.cycliq.model.TripSummaryModel;
import com.cycliq.utils.Utility;
import com.payu.magicretry.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//implementing onclicklistener
public class ReportActivity extends AppCompatActivity implements View.OnClickListener {

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

    private RecyclerView rGridView;

    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private String userChoosenTask;

    ReportRecyclerAdapter adapter;
    ArrayList<Bitmap> arrBitmap = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_report);

//        getSupportActionBar().hide();

        setViews();

        setListener();

        arrBitmap = new ArrayList<>();


        rGridView.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false));

        setRecyAdapter();

    }

    private void setRecyAdapter() {

        adapter = new ReportRecyclerAdapter(arrBitmap, this);

        rGridView.setAdapter(adapter);

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

        rGridView = (RecyclerView) findViewById(R.id.recycler_view);


    }

    private void showToast(String msg) {

        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if (userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(ReportActivity.this);

                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    if (result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        ivImage.setImageBitmap(thumbnail);

        arrBitmap.add(thumbnail);

        setRecyAdapter();
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

//        ivImage.setImageBitmap(bm);

        arrBitmap.add(bm);

        setRecyAdapter();


    }

    @Override
    public void onClick(View view) {

//        Intent intent = new Intent(this, QRScanActivity.class);

        int id = view.getId();

        if (id == R.id.btnAdd) {

            selectImage();

        } else if (id == R.id.btnDelete) {

            int pos = (int) view.getTag();

            arrBitmap.remove(pos);

            setRecyAdapter();

        } else if (id == R.id.layoutGroupMain) {

            // custom dialog
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_trip_details);
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

                                // assignAdapter();

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

                            Constants.showToast(ReportActivity.this, Constants.serverErrorMsg);

                        }
                    });

            queue.add(jsObjRequest);

        } else {


            Constants.showToast(this, Constants.internetAlert);

        }

    }


}