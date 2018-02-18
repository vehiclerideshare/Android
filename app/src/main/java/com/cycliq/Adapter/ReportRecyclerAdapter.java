package com.cycliq.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cycliq.R;

import java.util.HashMap;
import java.util.List;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by matts on 16.01.2016.
 */
public class ReportRecyclerAdapter extends RecyclerView.Adapter<ReportRecyclerAdapter.ViewHolder> {
    private ArrayList<Bitmap> arrBitmap;
    private ArrayList<String> list;
    View.OnClickListener onClickListener;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View MyView;
        TextView textView;
        ImageView ivMain;
        ImageButton btnAdd, btnDelete;

        public ViewHolder(View view) {
            super(view);
            MyView = view;

            ivMain = (ImageView) view.findViewById(R.id.ivMain);
            btnAdd = (ImageButton) view.findViewById(R.id.btnAdd);
            btnDelete = (ImageButton) view.findViewById(R.id.btnDelete);

        }
    }


    public ReportRecyclerAdapter(ArrayList<Bitmap> arrBitmap, View.OnClickListener onClickListener) {
        this.arrBitmap = arrBitmap;
        this.onClickListener = onClickListener;

    }


    @Override
    public ReportRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_report, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        // holder.textView.setText(list.get(position));

        holder.btnAdd.setOnClickListener(onClickListener);
        holder.btnDelete.setOnClickListener(onClickListener);
        holder.btnDelete.setTag(position);

        if(arrBitmap.size() != 0) {

            if (position == arrBitmap.size()) {

                holder.ivMain.setVisibility(View.GONE);
                holder.btnAdd.setVisibility(View.VISIBLE);
                holder.btnDelete.setVisibility(View.GONE);

            } else {

                holder.ivMain.setVisibility(View.VISIBLE);
                holder.btnAdd.setVisibility(View.GONE);
                holder.ivMain.setImageBitmap(arrBitmap.get(position));
                holder.btnDelete.setVisibility(View.VISIBLE);

            }
        }
        else
        {
            holder.ivMain.setVisibility(View.GONE);
            holder.btnAdd.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.GONE);

        }

    }

    @Override
    public int getItemCount() {
        return arrBitmap.size() + 1;
    }

}