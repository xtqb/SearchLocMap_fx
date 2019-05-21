package com.lhzw.searchlocmap.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lhzw.searchlocmap.R;

/**
 * Created by xtqb on 2019/4/2.
 */
public class CommandHAdapter extends BaseAdapter{
    private Context mContext;
    private String[] commandArr;
    private ViewHoler holder;

    public CommandHAdapter (Context mContext) {
        this.mContext = mContext;
        commandArr = mContext.getResources().getStringArray(R.array.command_action_arr);
    }
    @Override
    public int getCount() {
        if(commandArr == null) return 0;
        return commandArr.length;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_h_command_action, null);
            holder = new ViewHoler();
            holder.tv_command = (TextView) convertView.findViewById(R.id.tv_command);
            holder.im_icon = (ImageView) convertView.findViewById(R.id.im_icon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHoler) convertView.getTag();
        }
        holder.tv_command.setText(commandArr[position]);
        return convertView;
    }

    private class ViewHoler {
        private TextView tv_command;
        private ImageView im_icon;
    }
}
