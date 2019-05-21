package com.lhzw.searchlocmap.adapter;

import android.content.Context;
import android.content.pm.LabeledIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.lhzw.searchlocmap.R;
import com.lhzw.searchlocmap.bean.DipperInfoBean;
import com.lhzw.searchlocmap.utils.BaseUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by xtqb on 2019/1/8.
 */
public class DipperListAdapter extends BaseExpandableListAdapter implements
        ExpandableListView.OnGroupCollapseListener, ExpandableListView.OnGroupExpandListener {
    private Map<String, List<DipperInfoBean>> dataMap;
    private Context mContext;
    private boolean[] isOpen;
    private String[] groupName;
    private GroupViewHolder gHolder;
    private int gTotal;
    private int[] chTotal;
    private ChildViewHolder chHoder;
    Map<String, Integer> unreadMap;

    public DipperListAdapter(Context mContext, Map<String, List<DipperInfoBean>> dataMap, String[] groupName, Map<String, Integer> unreadMap){
        this.dataMap = dataMap;
        this.mContext = mContext;
        this.groupName = groupName;
        this.unreadMap = unreadMap;
        isOpen = new boolean[dataMap.size()];
        caculte();
    }
    private void caculte() {
        int counter = 0;
        gTotal = 0;
        chTotal = new int[groupName.length];
        for(String name : groupName) {
            if(dataMap.get(name) != null) {
                gTotal += dataMap.get(name).size();
                chTotal[counter] = dataMap.get(name).size();
            }
            counter++;
        }
    }

    @Override
    public int getGroupCount() {
        return dataMap.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if(dataMap.get(groupName[groupPosition]) == null) return 0;
        return dataMap.get(groupName[groupPosition]).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupName[groupPosition];
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return dataMap.get(groupName[groupPosition]);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.pre_status_group_list, null);
            gHolder = new GroupViewHolder();
            gHolder.tv_gName = (TextView) convertView.findViewById(R.id.group_name);
            gHolder.tv_statistics = (TextView) convertView.findViewById(R.id.group_sum);
            gHolder.im_icon = (ImageView) convertView.findViewById(R.id.list_image);
            convertView.setTag(gHolder);
        } else {
            gHolder = (GroupViewHolder) convertView.getTag();
        }
        gHolder.tv_gName.setText(groupName[groupPosition]);
        gHolder.tv_statistics.setText(chTotal[groupPosition] + "/" + gTotal);
        if (isOpen[groupPosition]) {
            gHolder.im_icon.setBackgroundResource(R.drawable.icon_open_32);
        } else {
            gHolder.im_icon.setBackgroundResource(R.drawable.icon_close_32);
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_dipper_child, null);
            chHoder = new ChildViewHolder();
            chHoder.tv_chName = (TextView) convertView.findViewById(R.id.tv_name);
            chHoder.tv_remark = (TextView) convertView.findViewById(R.id.tv_remark);
            chHoder.tv_new_num = (TextView) convertView.findViewById(R.id.tv_new_num);
            chHoder.im_icon = (ImageView) convertView.findViewById(R.id.im_potrait);
            convertView.setTag(chHoder);
        } else {
            chHoder = (ChildViewHolder) convertView.getTag();
        }
        /*
        int counter = unreadMap.get(dataMap.get(groupName[groupPosition]).get(childPosition).getNum());
        if(counter == 0) {
            chHoder.tv_new_num.setVisibility(View.GONE);
            chHoder.tv_new_num.setText("");
        } else {
            chHoder.tv_new_num.setVisibility(View.VISIBLE);
            if(counter > 99) {
                chHoder.tv_new_num.setText("99+");
            } else {
                chHoder.tv_new_num.setText(counter + "");
            }
        }
        */
        chHoder.tv_chName.setText(dataMap.get(groupName[groupPosition]).get(childPosition).getNum());
        String remark = dataMap.get(groupName[groupPosition]).get(childPosition).getRemark();
        if(!BaseUtils.isStringEmpty(remark)) {
            chHoder.tv_remark.setText("("+dataMap.get(groupName[groupPosition]).get(childPosition).getRemark() + ")");
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public void onGroupCollapse(int groupPosition) {
        isOpen[groupPosition] = false;
    }

    @Override
    public void onGroupExpand(int groupPosition) {
        isOpen[groupPosition] = true;
    }

    private class ChildViewHolder{
        private TextView tv_chName;
        private TextView tv_remark;
        private ImageView im_icon;
        private TextView tv_new_num;
    }

    private class GroupViewHolder {
        private TextView tv_gName;
        private TextView tv_statistics;
        private ImageView im_icon;
    }

    public void reflesh(){
        caculte();
        notifyDataSetChanged();
    }

}
