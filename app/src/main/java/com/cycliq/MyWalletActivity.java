package com.cycliq;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
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
import com.cycliq.Payment.AppEnvironment;
import com.cycliq.Payment.AppPreference;
import com.cycliq.R;
import com.payumoney.core.PayUmoneyConfig;
import com.payumoney.core.PayUmoneyConstants;
import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.core.entity.TransactionResponse;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;
import com.payumoney.sdkui.ui.utils.ResultModel;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;

import static com.cycliq.CommonClasses.Constants.KEY_RIDE_ID;

//implementing onclicklistener
public class MyWalletActivity extends AppCompatActivity implements View.OnClickListener, PaymentResultListener {
    public static final String TAG = "MainActivity : ";

    //View Objects
    private Button btnDetails, btnRecharge, btnCancel;
    private EditText editPhoneNumber, editOtp;
    private LinearLayout layoutLogin;
    private LinearLayout layoutMyWallet;
    private ImageButton btnBack;
    private TextView txtAdd1, txtAdd2, txtAdd3, txtAdd4;
    private EditText txtAmount;
    private LinearLayout layoutAdd1, layoutAdd2, layoutAdd3, layoutAdd4;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private SharedPreferences userDetailsPreference;
    private String userMobile, userEmail;
    private AppPreference mAppPreference;
    private PayUmoneySdkInitializer.PaymentParam mPaymentParams;
    private String appEnvironmentFurl, appEnvironmentSurl, email, firstName,  key, merchantHash, merchantId, phone, productName, transactionId;

    private boolean isDebug;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_wallet);

        mAppPreference = new AppPreference();

        Checkout.preload(getApplicationContext());


        getSupportActionBar().hide();

        setViews();

        setListener();

        hideAllColor();
    }

    private void setListener() {

        btnDetails.setOnClickListener(this);

        btnBack.setOnClickListener(this);

        txtAdd1.setOnClickListener(this);

        txtAdd2.setOnClickListener(this);

        txtAdd3.setOnClickListener(this);

        txtAdd4.setOnClickListener(this);

        btnRecharge.setOnClickListener(this);

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
        btnDetails = (Button) findViewById(R.id.btnDetails);

        btnBack = (ImageButton) findViewById(R.id.btnBack);

        txtAmount = (EditText) findViewById(R.id.txtAmount);

        btnRecharge = (Button) findViewById(R.id.btnRecharge);



        layoutAdd1 = (LinearLayout) findViewById(R.id.layoutAdd1);

        layoutAdd2 = (LinearLayout) findViewById(R.id.layoutAdd2);

        layoutAdd3 = (LinearLayout) findViewById(R.id.layoutAdd3);

        layoutAdd4 = (LinearLayout) findViewById(R.id.layoutAdd4);


        txtAdd1 = (TextView) findViewById(R.id.txtAdd1);

        txtAdd2 = (TextView) findViewById(R.id.txtAdd2);

        txtAdd3 = (TextView) findViewById(R.id.txtAdd3);

        txtAdd4 = (TextView) findViewById(R.id.txtAdd4);

//        layoutMyWallet = (LinearLayout) findViewById(R.id.layoutLogin);


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

        if(txtAmount.getText().toString().length() == 0)
        {

            txtAmount.setText("0");
        }

        if (view == txtAdd1) {

            hideAllColor();

            txtAdd1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

            txtAmount.setText(String.valueOf(Integer.parseInt(txtAmount.getText().toString()) + 50));

        }

        if (view == txtAdd2) {

            hideAllColor();

            txtAdd2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

            txtAmount.setText(String.valueOf(Integer.parseInt(txtAmount.getText().toString()) + 100));

        }

        if (view == txtAdd3) {

            hideAllColor();

            txtAdd3.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

            txtAmount.setText(String.valueOf(Integer.parseInt(txtAmount.getText().toString()) + 200));

        }

        if (view == txtAdd4) {

            hideAllColor();

            txtAdd4.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

            txtAmount.setText(String.valueOf(Integer.parseInt(txtAmount.getText().toString()) + 500));

        }


        if(view == btnRecharge)
        {




            setPaymentParameters();
        }

    }

    private void hideAllColor()
    {

        txtAdd1.setBackgroundColor(Color.WHITE);
        txtAdd2.setBackgroundColor(Color.WHITE);
        txtAdd3.setBackgroundColor(Color.WHITE);
        txtAdd4.setBackgroundColor(Color.WHITE);

    }

    /**
     * This function sets the mode to SANDBOX in Shared Preference
     */
    private void selectSandBoxEnv() {
        ((CycliqApplication) getApplication()).setAppEnvironment(AppEnvironment.SANDBOX);
        editor = settings.edit();
        editor.putBoolean("is_prod_env", false);
        editor.apply();

        if (PayUmoneyFlowManager.isUserLoggedIn(getApplicationContext())) {
           // logoutBtn.setVisibility(View.VISIBLE);
        } else {
          //  logoutBtn.setVisibility(View.GONE);

        }
       // setupCitrusConfigs();
    }

    public static String hashCal(String str) {
        byte[] hashseq = str.getBytes();
        StringBuilder hexString = new StringBuilder();
        try {
            MessageDigest algorithm = MessageDigest.getInstance("SHA-512");
            algorithm.reset();
            algorithm.update(hashseq);
            byte messageDigest[] = algorithm.digest();
            for (byte aMessageDigest : messageDigest) {
                String hex = Integer.toHexString(0xFF & aMessageDigest);
                if (hex.length() == 1) {
                    hexString.append("0");
                }
                hexString.append(hex);
            }
        } catch (NoSuchAlgorithmException ignored) {
        }
        return hexString.toString();
    }

    private void setUpUserDetails() {
        userDetailsPreference = getSharedPreferences(AppPreference.USER_DETAILS, MODE_PRIVATE);
        userEmail = userDetailsPreference.getString(AppPreference.USER_EMAIL, mAppPreference.getDummyEmail());
        userMobile = userDetailsPreference.getString(AppPreference.USER_MOBILE, mAppPreference.getDummyMobile());

//        restoreAppPref();
    }

   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result Code is -1 send from Payumoney activity
        Log.d("MainActivity", "request code " + requestCode + " resultcode " + resultCode);
        if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_OK && data !=
                null) {
            TransactionResponse transactionResponse = data.getParcelableExtra(PayUmoneyFlowManager
                    .INTENT_EXTRA_TRANSACTION_RESPONSE);

            ResultModel resultModel = data.getParcelableExtra(PayUmoneyFlowManager.ARG_RESULT);

            // Check which object is non-null
            if (transactionResponse != null && transactionResponse.getPayuResponse() != null) {
                if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.SUCCESSFUL)) {
                    //Success Transaction

                    Log.d("MainActivity", "request code if == " + requestCode + " resultcode " + resultCode);

                } else {
                    //Failure Transaction

                    Log.d("MainActivity", "request code else == " + requestCode + " resultcode " + resultCode);


                }

                // Response from Payumoney
                String payuResponse = transactionResponse.getPayuResponse();

                // Response from SURl and FURL
                String merchantResponse = transactionResponse.getTransactionDetails();

                new AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setMessage("Payu's Data : " + payuResponse + "\n\n\n Merchant's Data: " + merchantResponse)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }).show();

            } else if (resultModel != null && resultModel.getError() != null) {
                Log.d(TAG, "Error response : " + resultModel.getError().getTransactionResponse());
            } else {
                Log.d(TAG, "Both objects are null!");
            }
        }
    }*/




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result Code is -1 send from Payumoney activity
        Log.d("MainActivity", "request code " + requestCode + " resultcode " + resultCode);
        if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_OK && data !=
                null) {
            TransactionResponse transactionResponse = data.getParcelableExtra(PayUmoneyFlowManager
                    .INTENT_EXTRA_TRANSACTION_RESPONSE);

            ResultModel resultModel = data.getParcelableExtra(PayUmoneyFlowManager.ARG_RESULT);

            // Check which object is non-null
            if (transactionResponse != null && transactionResponse.getPayuResponse() != null) {
                if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.SUCCESSFUL)) {
                    //Success Transaction
//                    Toast.makeText(CartActivity.this, "Hi payment Success", Toast.LENGTH_LONG).show();
                } else {
                    //Failure Transaction
//                    Toast.makeText(CartActivity.this, "Hi payment Failure", Toast.LENGTH_LONG).show();

                }

                // Response from Payumoney
                String payuResponse = transactionResponse.getPayuResponse();

                // Response from SURl and FURL
                String merchantResponse = transactionResponse.getTransactionDetails();

                new AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setMessage("Payu's Data : " + payuResponse + "\n\n\n Merchant's Data: " + merchantResponse)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }).show();

            } else if (resultModel != null && resultModel.getError() != null) {
                Log.d(TAG, "Error response : " + resultModel.getError().getTransactionResponse());
            } else {
                Log.d(TAG, "Both objects are null!");
            }
        }

    }
    /**
     * This function prepares the data for payment and launches payumoney plug n play sdk
     */
    private void launchPayUMoneyFlow() {

        PayUmoneyConfig payUmoneyConfig = PayUmoneyConfig.getInstance();

        //Use this to set your custom text on result screen button
        payUmoneyConfig.setDoneButtonText("Pay");

        //Use this to set your custom title for the activity
        payUmoneyConfig.setPayUmoneyActivityTitle("PayUMoney");

        PayUmoneySdkInitializer.PaymentParam.Builder builder = new PayUmoneySdkInitializer.PaymentParam.Builder();

        double amount = 0;
        try {

            amount = Double.parseDouble(txtAmount.getText().toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

//        if(amount > 0)
//        {
//
//            showToast("Please enter the amount");
//        }


        String udf1 = "";
        String udf2 = "";
        String udf3 = "";
        String udf4 = "";
        String udf5 = "";
        String udf6 = "";
        String udf7 = "";
        String udf8 = "";
        String udf9 = "";
        String udf10 = "";

      /*  String txnId = System.currentTimeMillis() + "";
        String phone = "9826723222";
        String productName = mAppPreference.getProductInfo();
        String firstName = mAppPreference.getFirstName();
        String email = "as@gmail.xom";
        String udf1 = "";
        String udf2 = "";
        String udf3 = "";
        String udf4 = "";
        String udf5 = "";
        String udf6 = "";
        String udf7 = "";
        String udf8 = "";
        String udf9 = "";
        String udf10 = "";*/

        AppEnvironment appEnvironment = ((CycliqApplication) getApplication()).getAppEnvironment();

        builder.setAmount(amount)
                .setTxnId(transactionId)
                .setPhone(phone)
                .setProductName(productName)
                .setFirstName(firstName)
                .setEmail(email)
                .setsUrl(appEnvironmentSurl)
                .setfUrl(appEnvironmentFurl)
                .setUdf1(udf1)
                .setUdf2(udf2)
                .setUdf3(udf3)
                .setUdf4(udf4)
                .setUdf5(udf5)
                .setUdf6(udf6)
                .setUdf7(udf7)
                .setUdf8(udf8)
                .setUdf9(udf9)
                .setUdf10(udf10)
                .setIsDebug(false)
                .setKey(key)
                .setMerchantId(merchantId);


        try {
            mPaymentParams = builder.build();

            mPaymentParams.setMerchantHash(merchantHash);

            /*
            * Hash should always be generated from your server side.
            * */
        //    generateHashFromServer(mPaymentParams);

/*            *//**
             * Do not use below code when going live
             * Below code is provided to generate hash from sdk.
             * It is recommended to generate hash from server side only.
             * *//*
            mPaymentParams = calculateServerSideHashAndInitiatePayment1(mPaymentParams);

           if (AppPreference.selectedTheme != -1) {
                PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams,MainActivity.this, AppPreference.selectedTheme,mAppPreference.isOverrideResultScreen());
            } else {
                PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams,MainActivity.this, R.style.AppTheme_default, mAppPreference.isOverrideResultScreen());
            }*/

            PayUmoneyFlowManager.startPayUMoneyFlow(calculateServerSideHashAndInitiatePayment1(mPaymentParams), MyWalletActivity.this, R.style.AppTheme_default, false);

        } catch (Exception e) {
            // some exception occurred
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            btnRecharge.setEnabled(true);
        }
    }

    /**
     * Thus function calculates the hash for transaction
     *
     * @param paymentParam payment params of transaction
     * @return payment params along with calculated merchant hash
     */
    private PayUmoneySdkInitializer.PaymentParam calculateServerSideHashAndInitiatePayment1(final PayUmoneySdkInitializer.PaymentParam paymentParam) {

        StringBuilder stringBuilder = new StringBuilder();
        HashMap<String, String> params = paymentParam.getParams();
        stringBuilder.append(params.get(PayUmoneyConstants.KEY) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.TXNID) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.AMOUNT) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.PRODUCT_INFO) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.FIRSTNAME) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.EMAIL) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF1) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF2) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF3) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF4) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF5) + "||||||");

        AppEnvironment appEnvironment = ((CycliqApplication) getApplication()).getAppEnvironment();
        stringBuilder.append(appEnvironment.salt());

        String hash = hashCal(stringBuilder.toString());
        paymentParam.setMerchantHash(merchantHash);

        return paymentParam;
    }

    /**
     * This method generates hash from server.
     *
     * @param paymentParam payments params used for hash generation
     */
    public void generateHashFromServer(PayUmoneySdkInitializer.PaymentParam paymentParam) {
        //nextButton.setEnabled(false); // lets not allow the user to click the button again and again.

        HashMap<String, String> params = paymentParam.getParams();

        // lets create the post params
        StringBuffer postParamsBuffer = new StringBuffer();
        postParamsBuffer.append(concatParams(PayUmoneyConstants.KEY, params.get(PayUmoneyConstants.KEY)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.AMOUNT, params.get(PayUmoneyConstants.AMOUNT)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.TXNID, params.get(PayUmoneyConstants.TXNID)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.EMAIL, params.get(PayUmoneyConstants.EMAIL)));
        postParamsBuffer.append(concatParams("productinfo", params.get(PayUmoneyConstants.PRODUCT_INFO)));
        postParamsBuffer.append(concatParams("firstname", params.get(PayUmoneyConstants.FIRSTNAME)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF1, params.get(PayUmoneyConstants.UDF1)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF2, params.get(PayUmoneyConstants.UDF2)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF3, params.get(PayUmoneyConstants.UDF3)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF4, params.get(PayUmoneyConstants.UDF4)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF5, params.get(PayUmoneyConstants.UDF5)));

        String postParams = postParamsBuffer.charAt(postParamsBuffer.length() - 1) == '&' ? postParamsBuffer.substring(0, postParamsBuffer.length() - 1).toString() : postParamsBuffer.toString();

        // lets make an api call
//        GetHashesFromServerTask getHashesFromServerTask = new GetHashesFromServerTask();
//        getHashesFromServerTask.execute(postParams);

        mPaymentParams.setMerchantHash(merchantHash);


        PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams, MyWalletActivity.this, R.style.AppTheme_default, false);

    }


    protected String concatParams(String key, String value) {
        return key + "=" + value + "&";
    }

    /**
     * This AsyncTask generates hash from server.
     */
    private class GetHashesFromServerTask extends AsyncTask<String, String, String> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MyWalletActivity.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... postParams) {

            String merchantHash = "";
            try {
                //TODO Below url is just for testing purpose, merchant needs to replace this with their server side hash generation url
                URL url = new URL("https://payu.herokuapp.com/get_hash");

                String postParam = postParams[0];

                byte[] postParamsByte = postParam.getBytes("UTF-8");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Content-Length", String.valueOf(postParamsByte.length));
                conn.setDoOutput(true);
                conn.getOutputStream().write(postParamsByte);

                InputStream responseInputStream = conn.getInputStream();
                StringBuffer responseStringBuffer = new StringBuffer();
                byte[] byteContainer = new byte[1024];
                for (int i; (i = responseInputStream.read(byteContainer)) != -1; ) {
                    responseStringBuffer.append(new String(byteContainer, 0, i));
                }

                JSONObject response = new JSONObject(responseStringBuffer.toString());

                Iterator<String> payuHashIterator = response.keys();
                while (payuHashIterator.hasNext()) {
                    String key = payuHashIterator.next();
                    switch (key) {
                        /**
                         * This hash is mandatory and needs to be generated from merchant's server side
                         *
                         */
                        case "payment_hash":
                            merchantHash = response.getString(key);
                            break;
                        default:
                            break;
                    }
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return merchantHash;
        }

        @Override
        protected void onPostExecute(String merchantHash) {
            super.onPostExecute(merchantHash);

            progressDialog.dismiss();
            btnRecharge.setEnabled(true);

            if (merchantHash.isEmpty() || merchantHash.equals("")) {
                Toast.makeText(MyWalletActivity.this, "Could not generate hash", Toast.LENGTH_SHORT).show();
            } else {
                mPaymentParams.setMerchantHash(merchantHash);

//                if (AppPreference.selectedTheme != -1) {
//                    PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams, MyWalletActivity.this, AppPreference.selectedTheme, mAppPreference.isOverrideResultScreen());
//                } else {
//                    PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams, MyWalletActivity.this, R.style.AppTheme_default, mAppPreference.isOverrideResultScreen());
//                }

                PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams, MyWalletActivity.this, R.style.AppTheme_default, false);

            }
        }
    }


    private void setPaymentParameters() {

        if (Constants.isNetworkAvailable(this)) {

            Constants.showProgressDialog(this);

            JSONObject params = new JSONObject();
            try {

                params.put("userRegistrationId", Constants.USER_REGISTERATION_ID);
                params.put("amount", txtAmount.getText().toString());


//                params.put("rideId", Constants.getSavedData(this, Constants.KEY_RIDE_ID));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            String url = Constants.DEMO_BASE_URL + Constants.KEY_PAYMENT_REQUEST;

            RequestQueue queue = CycliqApplication.getInstance().getRequestQueue();
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            System.out.println(""+Constants.KEY_PAYMENT_REQUEST + "==" + response);

                            try {

                                appEnvironmentFurl = response.getString("appEnvironmentFurl");
                                appEnvironmentSurl = response.getString("appEnvironmentSurl");
                                email = response.getString("email");
                                firstName = response.getString("firstName");
                                key = response.getString("key");
                                merchantHash = response.getString("merchantHash");
                                merchantId = response.getString("merchantId");
                                phone = response.getString("phone");
                                productName = response.getString("productName");
                                transactionId = response.getString("transactionId");
                                isDebug = response.getBoolean("isDebug");

//                                launchPayUMoneyFlow();

                                startPayment();

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

                            Constants.showToast(MyWalletActivity.this, Constants.serverErrorMsg);

                        }
                    });

            queue.add(jsObjRequest);

        } else {

            Constants.showToast(this, Constants.internetAlert);

        }

    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        /**
         * Add your logic here for a successfull payment response
         */


        JSONObject params = new JSONObject();
        try {
            params.put("razorpayPaymentID", razorpayPaymentID);
            params.put("transactionId", transactionId);
            params.put("userRegistrationId", Constants.USER_REGISTERATION_ID);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        String url = appEnvironmentSurl;

        RequestQueue queue = CycliqApplication.getInstance().getRequestQueue();

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("response values == " + response);

                        try {



                        } catch (NullPointerException e) {

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

    @Override
    public void onPaymentError(int code, String response) {
        /**
         * Add your logic here for a failed payment response
         */


        JSONObject params = new JSONObject();
        try {
            params.put("errorResponse", response);
            params.put("transactionId", transactionId);
            params.put("errorResponseCode", String.valueOf(code));
            params.put("userRegistrationId", Constants.USER_REGISTERATION_ID);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        String url = appEnvironmentFurl;

        RequestQueue queue = CycliqApplication.getInstance().getRequestQueue();

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("response values == " + response);

                        try {



                        } catch (NullPointerException e) {

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

    public void startPayment() {
        /**
         * Instantiate Checkout
         */
        Checkout checkout = new Checkout();

        /**
         * Set your logo here
         */

        /**
         * Reference to current activity
         */
        final Activity activity = this;

        /**
         * Pass your payment options to the Razorpay Checkout as a JSONObject
         */
        try {
            JSONObject options = new JSONObject();

            /**
             * Merchant Name
             * eg: Rentomojo || HasGeek etc.
             */
            options.put("name", "Cycliq App");

            /**
             * Description can be anything
             * eg: Order #123123
             *     Invoice Payment
             *     etc.
             */
            options.put("description", transactionId);

            options.put("currency", "INR");

            /**
             * Amount is always passed in PAISE
             * Eg: "500" = Rs 5.00
             */

            Integer amountValue = Integer.parseInt(txtAmount.getText().toString()) * 100;

            options.put("amount", Integer.toString(amountValue));

            checkout.open(activity, options);
        } catch(Exception e) {
            Log.e(TAG, "Error in starting Razorpay Checkout", e);
        }
    }
}