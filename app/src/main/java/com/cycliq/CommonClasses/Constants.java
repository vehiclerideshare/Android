package com.cycliq.CommonClasses;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

public class Constants {



    public static String DEMO_BASE_URL = "http://13.71.113.199:8080/vrapps/rest/";

    // Local Storage Keys:
    public static final String USER_REGISTERATION_ID = "12345";

    public static final String CHECK_STATUS = "checkstatus";
    public static final String LOCATE_NEAR_BY_VEHICLES = "locatenearbyvehicles";
    public static final String UNLOCK_VEHICLE = "unlockvehicle";
    public static final String KEY_USER_PROFILE = "userprofile";
    public static final String KEY_TRIP_SUMMARY = "tripsummary";
    public static final String KEY_PAYMENT_DETAIL_SCREEN = "paymentdetailsscreen";
    public static final String KEY_RESERVE_VEHICLE = "reservevehicle";
    public static final String KEY_CANCEL_VEHICLE = "cancelvehicle";
    public static final String KEY_PAYMENT_REQUEST = "paymentrequest";
    public static final String UPDATE_RIDE_STATUS = "updateRideStatus";



    public static final String KEY_RIDE_ID = "rideId";
    public static final String KEY_RIDE_STATUS = "rideStatus";

    public static final String internetAlert = "Please check your internet connection";
    public static final String serverErrorMsg = "Something went wrong please try again.";

    // RIDESTATUS:
    public static final String AUTHORIZED = "Authorized";
    public static final String IN_PROGRESS = "In progress";
    public static final String CANCELLED = "Cancelled";
    public static final String STOPPED = "Stopped";
    public static final String DENIED = "Denied";
    public static final String NONE = "None";

    // Local Storage Keys:

    public static final String KEY_USER_PHONE = "KEY_USER_PHONE";


    public static ProgressDialog pDialog = null;

    public static void saveData(Activity activity, String strKey, String object) {

        try {

            DB snappydb = DBFactory.open(activity);

            if (isDataAvailable(activity, strKey)) {

                snappydb.put(strKey, object);

            } else {

                if (!snappydb.isOpen()) {

                    snappydb = DBFactory.open(activity);

                }

                snappydb.put(strKey, object);

            }

            if(snappydb.isOpen()) {
                snappydb.close();
            }

        } catch (SnappydbException e) {

            e.printStackTrace();
        }

    }

    public static boolean isDataAvailable(Activity activity, String key) {

        try {

            DB snappydb = DBFactory.open(activity);

            if (snappydb.exists(key)) {

                return true;
            }

            if(snappydb.isOpen()) {
                snappydb.close();
            }

            return false;

        } catch (SnappydbException e) {

            e.printStackTrace();

            return true;
        }
    }

    public static String getSavedData(Activity activity, String strKey) {

        try {

            DB snappydb = DBFactory.open(activity);

            if (isDataAvailable(activity, strKey)) {

                return snappydb.get(strKey);

            }

            if(snappydb.isOpen()) {

                snappydb.close();

            }

            return "";

        } catch (SnappydbException e) {

            e.printStackTrace();

            return "";

        }

    }

    public static boolean isNetworkAvailable(Activity activity) {

        ConnectivityManager cm =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static void showToast(Activity activity, String msg) {

        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();

    }

    public static void showProgressDialog(Activity activity) {

        pDialog = new ProgressDialog(activity);
        pDialog.setMessage("Loading...");
        pDialog.show();
    }

    public static void hideProgressDialog() {

        if(pDialog.isShowing())
        {
            pDialog.dismiss();
        }
    }
}
