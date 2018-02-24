package com.cycliq.Application;


import android.app.Application;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.cycliq.Payment.AppEnvironment;

import java.util.Locale;

public class CycliqApplication extends MultiDexApplication {

    AppEnvironment appEnvironment;

    public static final String TAG = CycliqApplication.class
            .getSimpleName();

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private static CycliqApplication mInstance;

    public static Typeface typeface;

    private Typeface ubuFont, openSans;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        appEnvironment = AppEnvironment.SANDBOX;

        AssetManager am = getApplicationContext().getAssets();

         typeface = Typeface.createFromAsset(getAssets(), "Ubuntu-Regular.ttf");




        ubuFont = Typeface.createFromAsset(getAssets(), "Ubuntu-Regular.ttf");
        openSans = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
    }

    public Typeface getUbunthuFont() {
        return ubuFont;
    }

    public Typeface getOpenSans() {
        return openSans;
    }


    public static synchronized CycliqApplication getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }



    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }


    public AppEnvironment getAppEnvironment() {
        return appEnvironment;
    }

    public void setAppEnvironment(AppEnvironment appEnvironment) {
        this.appEnvironment = appEnvironment;
    }
}