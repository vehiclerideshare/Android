package com.cycliq.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cycliq.R;
import com.cycliq.model.TripSummaryModel;

import java.util.ArrayList;


public class MyTripsAdapter extends BaseAdapter {

    private Activity activity;
    private static LayoutInflater inflater = null;
    View.OnClickListener onClickListener;
    ArrayList<TripSummaryModel> arrTripSummary;

    public MyTripsAdapter(Activity a, View.OnClickListener onClickListener, ArrayList<TripSummaryModel> arrTripSummary) {
        activity = a;

        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.onClickListener = onClickListener;

        this.arrTripSummary = arrTripSummary;

    }

    public int getCount() {

        return 20;
       // return arrTripSummary.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.adapter_my_trips, null);

//        LinearLayout linearMain = (LinearLayout) vi.findViewById(R.id.linearMain);
//
//        TextView tvBikeId = (TextView) vi.findViewById(R.id.tvBikeId);
//        TextView tvTripAmount = (TextView) vi.findViewById(R.id.tvTripAmount);
//        TextView tvTripId = (TextView) vi.findViewById(R.id.tvTripId);
//        TextView tvDateTime = (TextView) vi.findViewById(R.id.tvDateTime);
//
//        linearMain.setOnClickListener(onClickListener);
//
//        linearMain.setTag(position);
//
//        TripSummaryModel tripSummaryModel = arrTripSummary.get(position);
//
//        tvBikeId.setText("BIKE ID: " + tripSummaryModel.getBikeID());
//
//        tvTripAmount.setText("₹" + tripSummaryModel.getTripAmount());
//
//        tvTripId.setText(tripSummaryModel.getTripID());
//
//        tvDateTime.setText(tripSummaryModel.getTripDateTime());


        return vi;
    }
}