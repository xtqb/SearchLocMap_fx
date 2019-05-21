package com.lhzw.searchlocmap.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lhzw.searchlocmap.R;
import com.lhzw.searchlocmap.bean.PlotItem;

import java.util.List;

/**
 * Created by xtqb on 2019/1/4.
 */
public class PlotHorizonAdapter extends BaseAdapter {
    private List<PlotItem> list;
    private Context mContext;
    private int itemId = -1;
    private ViewHolder holder;

    public PlotHorizonAdapter(Context mContext, List<PlotItem> list) {
        this.list = list;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        if (list == null) return 0;
        return list.size();
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
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_hplot, null);
            holder = new ViewHolder();
            holder.tv_name = (TextView) convertView.findViewById(R.id.plot_name);
            holder.im_picId = (ImageView) convertView.findViewById(R.id.plot_pic);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_name.setText(list.get(position).getName());
        holder.im_picId.setImageResource(list.get(position).getPicId());
        if (position == itemId) {
            convertView.setBackgroundColor(mContext.getColor(R.color.transparent_green));
        } else {
            convertView.setBackgroundColor(mContext.getColor(R.color.alltransparent_gray));
        }
        convertView.setOnClickListener(new MyOnClickListener(position));
        return convertView;
    }

    private class MyOnClickListener implements View.OnClickListener {
        private int position;

        private MyOnClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            itemId = position;
            notifyDataSetChanged();
        }
    }

    private class ViewHolder {
        private TextView tv_name;
        private ImageView im_picId;
    }

    public void resetState() {
        if (itemId != -1) {
            itemId = -1;
            notifyDataSetChanged();
        }
    }
}
