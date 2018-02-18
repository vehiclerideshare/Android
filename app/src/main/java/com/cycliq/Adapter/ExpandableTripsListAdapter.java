package com.cycliq.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cycliq.R;

import java.util.HashMap;
import java.util.List;

public class ExpandableTripsListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> _listDataChild;
    private View.OnClickListener btnClickListener;

    public ExpandableTripsListAdapter(Context context, List<String> listDataHeader,
                                      HashMap<String, List<String>> listChildData, View.OnClickListener btnClickListener) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
        this.btnClickListener = btnClickListener;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.trips_list_item, null);
        }

        ImageView ivSideLine = (ImageView) convertView.findViewById(R.id.ivSideLine);
        View viewBottomLine = (View) convertView.findViewById(R.id.viewBottomLine);

        if (isLastChild) {

            viewBottomLine.setVisibility(View.VISIBLE);

        } else {

            viewBottomLine.setVisibility(View.GONE);

        }

        //TextView txtListChild = (TextView) convertView
        //	.findViewById(R.id.lblListItem);

        //txtListChild.setText(childText);

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.trips_list_group, null);
        }

        LinearLayout layoutGroupMain = (LinearLayout) convertView.findViewById(R.id.layoutGroupMain);


        ImageView ivSideLine = (ImageView) convertView.findViewById(R.id.ivSideLine);
        View viewBottomLine = (View) convertView.findViewById(R.id.viewBottomLine);
        ImageView ivExpand = (ImageView) convertView.findViewById(R.id.ivExpand);
        ivExpand.setOnClickListener(btnClickListener);
        ivExpand.setTag(groupPosition);

        layoutGroupMain.setOnClickListener(btnClickListener);
        layoutGroupMain.setTag(groupPosition);

        if (isExpanded) {

            ivExpand.setImageResource(R.mipmap.ic_minus);
            ivSideLine.setVisibility(View.VISIBLE);
            viewBottomLine.setVisibility(View.GONE);

        } else {

            ivExpand.setImageResource(R.mipmap.ic_plus);

            ivSideLine.setVisibility(View.INVISIBLE);
            viewBottomLine.setVisibility(View.VISIBLE);

        }

//		TextView lblListHeader = (TextView) convertView
//				.findViewById(R.id.lblListHeader);
//		lblListHeader.setTypeface(null, Typeface.BOLD);
//		lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
