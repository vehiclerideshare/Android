package com.cycliq.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cycliq.R;
import com.cycliq.model.MyPaymentModel;

import java.util.ArrayList;

public class HomeMenuAdapter extends BaseAdapter {

    private Activity activity;
    private static LayoutInflater inflater = null;
    View.OnClickListener onClickListener;
    ArrayList<String> arrMenulist;

    public HomeMenuAdapter(Activity a, View.OnClickListener onClickListener, ArrayList<String> arrMenulist) {
        activity = a;

        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.onClickListener = onClickListener;

        this.arrMenulist = arrMenulist;

    }


    public int getCount() {
        return 7;//arrMyPaymentList.size();
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
            vi = inflater.inflate(R.layout.adapter_home_menu, null);

        TextView tvMenuTitle = (TextView) vi.findViewById(R.id.tvMenuTitle);

        ImageView ivMenu = (ImageView) vi.findViewById(R.id.ivMenu);

        View viewLine = (View) vi.findViewById(R.id.viewLine);

        viewLine.setVisibility(View.GONE);

        if (position == 0) {

            tvMenuTitle.setText(R.string.menu_wallet);
            ivMenu.setImageResource(R.mipmap.ic_wallet);

        } else if (position == 1) {

            tvMenuTitle.setText(R.string.menu_my_payments);
            ivMenu.setImageResource(R.mipmap.ic_my_paymens);

        } else if (position == 2) {

            tvMenuTitle.setText(R.string.menu_my_trips);
            ivMenu.setImageResource(R.mipmap.ic_my_trips);

            viewLine.setVisibility(View.VISIBLE);

        } else if (position == 3) {

            tvMenuTitle.setText(R.string.menu_my_messages);
            ivMenu.setImageResource(R.mipmap.ic_messages);

        } else if (position == 4) {

            tvMenuTitle.setText(R.string.menu_invite_friends);
            ivMenu.setImageResource(R.mipmap.ic_invite_friends);

        } else if (position == 5) {

            tvMenuTitle.setText(R.string.menu_user_guide);
            ivMenu.setImageResource(R.mipmap.ic_user_guide);

        } else if (position == 6) {

            tvMenuTitle.setText(R.string.menu_settings);
            ivMenu.setImageResource(R.mipmap.ic_settings);

        }


//
//        MyPaymentModel myPaymentModel = arrMyPaymentList.get(position);
//
//        tvAmount.setText("₹"+myPaymentModel.getPaymentAmount());
//
//        tvDateTime.setText(myPaymentModel.getPaymentDateTime());

        return vi;
    }
}