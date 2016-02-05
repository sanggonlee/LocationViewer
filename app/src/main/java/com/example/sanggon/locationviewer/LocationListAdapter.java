package com.example.sanggon.locationviewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class LocationListAdapter extends BaseAdapter {
    private final List<LocationItem> mItems = new ArrayList<>();
    private Context mContext;

    public LocationListAdapter(Context context) {
        mContext = context;
    }

    public void add(LocationItem item) {
        mItems.add(item);
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public LocationItem getItem(int pos) {
        return mItems.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LocationItem item = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.location_item, parent, false);
        }

        TextView titleView = (TextView)convertView.findViewById(R.id.location_title);
        titleView.setText(item.getTitle());

        TextView descriptionView = (TextView)convertView.findViewById(R.id.location_description);
        descriptionView.setText(item.getDescription());

        ImageView imageView = (ImageView)convertView.findViewById(R.id.location_image);
        imageView.setImageBitmap(Bitmap.createScaledBitmap(item.getImage(), 180, 180, false));

        return convertView;
    }
}