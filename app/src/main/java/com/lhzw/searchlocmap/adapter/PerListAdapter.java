package com.lhzw.searchlocmap.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.lhzw.searchlocmap.R;
import com.lhzw.searchlocmap.bean.DipperInfoBean;
import com.lhzw.searchlocmap.bean.HttpPersonInfo;
import com.lhzw.searchlocmap.utils.BaseUtils;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Map;

/**
 * Created by xtqb on 2019/1/8.
 */
public class PerListAdapter extends BaseExpandableListAdapter implements
        ExpandableListView.OnGroupCollapseListener, ExpandableListView.OnGroupExpandListener {
    private Map<String, List<HttpPersonInfo>> dataMap;
    private Context mContext;
    private boolean[] isOpen;
    private List<String> groupName;
    private GroupViewHolder gHolder;
    private ChildViewHolder chHoder;
    private Map<String, Integer> unreadMap;
    private Map<String, List<Integer>> unChReadMap;

    public PerListAdapter(Context mContext, Map<String, List<HttpPersonInfo>> dataMap, List<String> groupName, Map<String, Integer> unreadMap, Map<String, List<Integer>> unChReadMap){
        this.dataMap = dataMap;
        this.mContext = mContext;
        this.groupName = groupName;
        this.unreadMap = unreadMap;
        this.unChReadMap = unChReadMap;
        isOpen = new boolean[dataMap.size()];
    }

    @Override
    public int getGroupCount() {
        return dataMap.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if(dataMap.get(groupName.get(groupPosition)) == null) return 0;
        return dataMap.get(groupName.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupName.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return dataMap.get(groupName.get(groupPosition));
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.pre_status_group_list2, null);
            gHolder = new GroupViewHolder();
            gHolder.tv_gName = (TextView) convertView.findViewById(R.id.group_name);
            gHolder.im_icon = (ImageView) convertView.findViewById(R.id.list_image);
            gHolder.tv_new_num = (TextView) convertView.findViewById(R.id.tv_new_num);
            convertView.setTag(gHolder);
        } else {
            gHolder = (GroupViewHolder) convertView.getTag();
        }
        gHolder.tv_gName.setText(groupName.get(groupPosition));
        if (isOpen[groupPosition]) {
            gHolder.im_icon.setBackgroundResource(R.drawable.icon_open_32);
        } else {
            gHolder.im_icon.setBackgroundResource(R.drawable.icon_close_32);
        }
        int num = unreadMap.get(groupName.get(groupPosition) + "");
        if(num > 99) {
            gHolder.tv_new_num.setVisibility(View.VISIBLE);
            gHolder.tv_new_num.setText("+99");
        } else if(num == 0) {
            gHolder.tv_new_num.setVisibility(View.GONE);
            gHolder.tv_new_num.setText("");
        } else {
            gHolder.tv_new_num.setVisibility(View.VISIBLE);
            gHolder.tv_new_num.setText(num + "");
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
            convertView.findViewById(R.id.spread).setVisibility(View.GONE);
            convertView.setTag(chHoder);
        } else {
            chHoder = (ChildViewHolder) convertView.getTag();
        }
        int num = unChReadMap.get(groupName.get(groupPosition)).get(childPosition);
        if(num > 99) {
            chHoder.tv_new_num.setVisibility(View.VISIBLE);
            chHoder.tv_new_num.setText("+99");
        } else if(num == 0) {
            chHoder.tv_new_num.setVisibility(View.GONE);
            chHoder.tv_new_num.setText("");
        } else {
            chHoder.tv_new_num.setVisibility(View.VISIBLE);
            chHoder.tv_new_num.setText(num + "");
        }
        chHoder.tv_chName.setText(dataMap.get(groupName.get(groupPosition)).get(childPosition).getRealName());
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
        private ImageView im_icon;
        private TextView tv_new_num;
    }

    public void reflesh(){
        notifyDataSetChanged();
    }
}
