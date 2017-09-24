package com.droidprogramming.automatedearlywarningsystem.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.droidprogramming.automatedearlywarningsystem.R;
import com.droidprogramming.automatedearlywarningsystem.models.Warnings;


/**
 * Created by Mzdhr on 9/11/16.
 */

public class ListAdapter extends BaseAdapter {
    private final Context mContext;
    private final Warnings[] mWarningses;


    public ListAdapter(Context context, Warnings[] warningses) {
        mContext = context;
        mWarningses = warningses;
    }


    @Override
    public int getCount() {
        return mWarningses.length;
    }

    @Override
    public Object getItem(int position) {
        return mWarningses[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list, null);

            holder = new ViewHolder();
            holder.regionsTextView = (TextView) convertView.findViewById(R.id.regionsTextView);
            holder.statusTextView = (TextView) convertView.findViewById(R.id.statusTextView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.regionsTextView.setText(mWarningses[position].getRegion());
        holder.statusTextView.setText(mWarningses[position].getStatus());

        return convertView;
    }


    private static class ViewHolder {
        TextView regionsTextView;
        TextView statusTextView;
    }
}