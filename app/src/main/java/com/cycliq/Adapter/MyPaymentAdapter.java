package com.cycliq.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cycliq.R;
import com.cycliq.model.MyPaymentModel;

import java.util.ArrayList;

public class MyPaymentAdapter extends BaseAdapter {

    private Activity activity;
    private static LayoutInflater inflater = null;
    View.OnClickListener onClickListener;
    ArrayList<MyPaymentModel> arrMyPaymentList;

    public MyPaymentAdapter(Activity a, View.OnClickListener onClickListener, ArrayList<MyPaymentModel> arrMyPaymentList) {
        activity = a;

        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.onClickListener = onClickListener;

        this.arrMyPaymentList = arrMyPaymentList;

    }



    public int getCount() {
        return arrMyPaymentList.size();
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
            vi = inflater.inflate(R.layout.adapter_my_payemets, null);

        TextView tvAmount = (TextView) vi.findViewById(R.id.tvAmount);

        TextView tvDateTime = (TextView) vi.findViewById(R.id.tvDateTime);

        MyPaymentModel myPaymentModel = arrMyPaymentList.get(position);

        tvAmount.setText("₹"+myPaymentModel.getPaymentAmount());

        tvDateTime.setText(myPaymentModel.getPaymentDateTime());

        return vi;
    }
}