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
import com.lhzw.searchlocmap.bean.PerItem;
import com.lhzw.searchlocmap.bean.PersonalInfo;
import com.lhzw.searchlocmap.constants.Constants;
import com.lhzw.searchlocmap.interfaces.OnHoriItemClickListener;
import com.lhzw.searchlocmap.view.OnClickLinearLayout;

import java.util.ArrayList;
import java.util.List;

public class UndeterminedAdapter extends BaseAdapter {
    private Context mContext;
    private List<PersonalInfo> list;
    private boolean cardinal;
    private ViewHolder holder;
    private OnHoriItemClickListener listener;
    private List<PerItem> itemList = new ArrayList<PerItem>();

    public UndeterminedAdapter(Context mContext, List<PersonalInfo> list,
                               OnHoriItemClickListener listener) {
        cardinal = false;
        this.mContext = mContext;
        this.list = list;
        initItemList();
        this.listener = listener;
    }

    private void initItemList() {
        if (itemList.size() != 0) {
            itemList.clear();
        }
        int counter = list.size() / 2;
        if (list.size() % 2 == 0) {
            cardinal = false;
        } else {
            cardinal = true;
        }
        for (int i = 0; i < counter; i++) {
            PerItem item = new PerItem();
            if (Constants.PERSON_COMMON.equals(list.get(2 * i).getState1())) {
                item.setPerIcon_1(R.drawable.icon_qita2);
            } else if (Constants.PERSON_SOS.equals(list.get(2 * i).getState1())) {
                item.setPerIcon_1(R.drawable.icon_sos2);
            } else if (Constants.PERSON_OFFLINE.equals(list.get(2 * i).getState())) {
                item.setPerIcon_1(R.drawable.icon_offline);
            }
            item.setPerName_1(list.get(2 * i).getName());
            if (Constants.PERSON_COMMON.equals(list.get(2 * i + 1).getState1())) {
                item.setPerIcon_2(R.drawable.icon_qita2);
            } else if (Constants.PERSON_SOS.equals(list.get(2 * i + 1).getState1())) {
                item.setPerIcon_2(R.drawable.icon_sos2);
            } else if (Constants.PERSON_OFFLINE.equals(list.get(2 * i + 1).getState())) {
                item.setPerIcon_2(R.drawable.icon_offline);
            }
            item.setPerName_2(list.get(2 * i + 1).getName());
            itemList.add(item);
        }
        if (cardinal) {
            PerItem item = new PerItem();
            if (Constants.PERSON_COMMON.equals(list.get(list.size() - 1).getState1())) {
                item.setPerIcon_1(R.drawable.icon_qita2);
            } else if (Constants.PERSON_SOS.equals(list.get(list.size() - 1).getState1())) {
                item.setPerIcon_1(R.drawable.icon_sos2);
            } else if (Constants.PERSON_OFFLINE.equals(list.get(list.size() - 1).getState())) {
                item.setPerIcon_1(R.drawable.icon_offline);
            }
            item.setPerName_1(list.get(list.size() - 1).getName());
            itemList.add(item);
        }
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    public void setList(List<PersonalInfo> list) {
        this.list = list;
        initItemList();
        notifyDataSetChanged();
    }

    public void refleshList() {
        initItemList();
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_undetermined_region, null);
            holder.im_potrait_up = (ImageView) convertView
                    .findViewById(R.id.im_potrait_up);
            holder.tv_name_up = (TextView) convertView
                    .findViewById(R.id.tv_name_up);
            holder.im_potrait_down = (ImageView) convertView
                    .findViewById(R.id.im_potrait_down);
            holder.tv_name_down = (TextView) convertView
                    .findViewById(R.id.tv_name_down);
            holder.ll_undermined_up = (OnClickLinearLayout) convertView
                    .findViewById(R.id.ll_undermined_up);
            holder.ll_undermined_down = (OnClickLinearLayout) convertView
                    .findViewById(R.id.ll_undermined_down);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.im_potrait_up.setBackgroundResource(itemList.get(position).getPerIcon_1());
        holder.ll_undermined_up.setOnHoriItemClickListener(listener);
        holder.ll_undermined_up.setPos(2 * position);
        holder.tv_name_up.setText(itemList.get(position).getPerName_1());
        if (itemList.get(position).getPerName_2() != null) {
            holder.im_potrait_down.setVisibility(View.VISIBLE);
            holder.tv_name_down.setVisibility(View.VISIBLE);
            holder.im_potrait_down.setBackgroundResource(itemList.get(position).getPerIcon_2());
            holder.ll_undermined_down.setOnHoriItemClickListener(listener);
            holder.ll_undermined_down.setPos(2 * position + 1);
            holder.tv_name_down.setText(itemList.get(position).getPerName_2());
        } else {
            holder.im_potrait_down.setVisibility(View.GONE);
            holder.tv_name_down.setText("");
            holder.tv_name_down.setVisibility(View.GONE);
        }
        return convertView;
    }

    private class ViewHolder {
        private ImageView im_potrait_up;
        private TextView tv_name_up;
        private ImageView im_potrait_down;
        private TextView tv_name_down;
        private OnClickLinearLayout ll_undermined_up;
        private OnClickLinearLayout ll_undermined_down;
    }
}
