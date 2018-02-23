package com.cycliq.Adapter;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.cycliq.R;

import java.util.ArrayList;

/**
 * Created by matts on 16.01.2016.
 */
public class ReportCyclePartsRecyclerAdapter extends RecyclerView.Adapter<ReportCyclePartsRecyclerAdapter.ViewHolder> {
    private ArrayList<String> arrPartsName;
    private ArrayList<String> list;
    View.OnClickListener onClickListener;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View MyView;
        TextView txtParts;

        public ViewHolder(View view) {
            super(view);
            MyView = view;

            txtParts = (TextView) view.findViewById(R.id.txtParts);

        }
    }

    public ReportCyclePartsRecyclerAdapter(ArrayList<String> arrPartsName, View.OnClickListener onClickListener) {
        this.arrPartsName = arrPartsName;

    }

    @Override
    public ReportCyclePartsRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_report_selected_cycle_parts, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
         holder.txtParts.setText(arrPartsName.get(position));

    }

    @Override
    public int getItemCount() {
        return arrPartsName.size();
    }

}