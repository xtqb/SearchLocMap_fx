package com.lhzw.searchlocmap.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lhzw.searchlocmap.R;

/**
 * Created by xtqb on 2019/1/8.
 */
public class SpinnerAdapter extends BaseAdapter{
    private Context mContext;
    private String[] datas;
    private boolean isShow;
    private ViewHolder holder;

    public SpinnerAdapter (Context mContext, String[] datas) {
        this.mContext = mContext;
        this.datas = datas;
        isShow = false;
    }
    @Override
    public int getCount() {
        if(isShow) {
            return datas.length;
        } else {
            return 0;
        }
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_spinner_tv, null);
            holder = new ViewHolder();
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_name.setText(datas[position]);
        return convertView;
    }

    public void showListView(boolean isShow) {
        this.isShow = isShow;
        notifyDataSetChanged();
    }

    public boolean getIsShow(){
        return isShow;
    }

    private class ViewHolder {
        private TextView tv_name;
    }
}
