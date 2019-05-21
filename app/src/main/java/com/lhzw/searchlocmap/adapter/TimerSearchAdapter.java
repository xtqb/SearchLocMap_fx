package com.lhzw.searchlocmap.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lhzw.searchlocmap.R;

/**
 * Created by xtqb on 2019/3/7.
 */
public class TimerSearchAdapter extends BaseAdapter{
    private String[] timeArr;
    private Viewholder holder;
    private Context mContext;
    public TimerSearchAdapter(Context mContext, String[] timeArr){
        this.timeArr = timeArr;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return timeArr.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_time_list, null);
            holder = new Viewholder();
            holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            convertView.setTag(holder);
        } else {
            holder = (Viewholder) convertView.getTag();
        }
        holder.tv_content.setText(timeArr[position]);
        return convertView;
    }

    private class Viewholder {
        private TextView tv_content;
    }
}
