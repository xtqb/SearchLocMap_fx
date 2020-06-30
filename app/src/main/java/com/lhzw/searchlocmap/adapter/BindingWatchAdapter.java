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
import com.lhzw.searchlocmap.bean.multicheck.ChildExpandBean;
import com.lhzw.searchlocmap.bean.multicheck.GroupExpandBean;

import java.util.List;
import java.util.Map;

public class BindingWatchAdapter extends BaseExpandableListAdapter implements ExpandableListView.OnGroupCollapseListener, ExpandableListView.OnGroupExpandListener, View.OnLongClickListener {
    private Map<String, List<ChildExpandBean>> dataMap;
    private Context mContext;
    private List<GroupExpandBean> groupList;
    private BindingWatchAdapter.GroupViewHolder gHolder;
    private int[] checkedTotal;
    private BindingWatchAdapter.ChildViewHolder chHoder;
    private OnGroupItemLondClick onLongClick;
    private String[] groupMacs;
    private boolean bindingState;
    private int total = 0;

    public BindingWatchAdapter(Context mContext, Map<String, List<ChildExpandBean>> dataMap, List<GroupExpandBean> groupList, boolean bindingState) {
        this.dataMap = dataMap;
        this.mContext = mContext;
        this.groupList = groupList;
        this.bindingState = bindingState;
        if (groupList != null && groupList.size() > 0) {
            caculte();
        }
    }

    @Override
    public int getGroupCount() {
        if (dataMap == null || dataMap.size() == 0) return 0;
        return dataMap.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (dataMap.get(groupMacs[groupPosition]) == null) return 0;
        return dataMap.get(groupMacs[groupPosition]).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return dataMap.get(groupMacs[groupPosition]).get(childPosition);
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
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_binding_expand_group, null);
            gHolder = new BindingWatchAdapter.GroupViewHolder();
            gHolder.iv_portrait = (ImageView) convertView.findViewById(R.id.iv_portrait);
            gHolder.tv_group_name = (TextView) convertView.findViewById(R.id.tv_group_name);
            gHolder.group_sum = (TextView) convertView.findViewById(R.id.group_sum);
            gHolder.iv_expand_state = (ImageView) convertView.findViewById(R.id.iv_expand_state);
            convertView.setTag(gHolder);
        } else {
            gHolder = (BindingWatchAdapter.GroupViewHolder) convertView.getTag();
        }
        String mac = groupList.get(groupPosition).getMac();
        if (mac == null || mac.isEmpty()) {
            mac = "无";
        }
        String name = "";
        if (bindingState) {
            name = groupList.get(groupPosition).getOrgName();
        } else {
            name = "手持";
        }
        gHolder.tv_group_name.setText(name + "\n(" + mac + ")");
        gHolder.group_sum.setText(getChildrenCount(groupPosition) + "/" + total);
        if (groupList.get(groupPosition).isExpand()) {
            gHolder.iv_expand_state.setBackgroundResource(R.drawable.icon_group_expand);

        } else {
            gHolder.iv_expand_state.setBackgroundResource(R.drawable.icon_group_closed);
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_binding_expand_child, null);
            chHoder = new BindingWatchAdapter.ChildViewHolder();
            chHoder.iv_portrait = (ImageView) convertView.findViewById(R.id.iv_portrait);
            chHoder.tv_child_name = (TextView) convertView.findViewById(R.id.tv_child_name);
            chHoder.tv_child_state = (TextView) convertView.findViewById(R.id.tv_child_state);
            chHoder.iv_check_state = (ImageView) convertView.findViewById(R.id.iv_check_state);
            convertView.setTag(chHoder);
        } else {
            chHoder = (BindingWatchAdapter.ChildViewHolder) convertView.getTag();
        }
        if (isLastChild) {

        }
        ChildExpandBean child = dataMap.get(groupList.get(groupPosition).getMac()).get(childPosition);
        chHoder.tv_child_name.setText(child.getName() + "(" + child.getRegister() + ")");
        if (child.isVisible()) {
            chHoder.iv_check_state.setVisibility(View.VISIBLE);
            if (child.isChecked()) {
                chHoder.iv_check_state.setBackgroundResource(R.drawable.icon_checkbox_pre);
            } else {
                chHoder.iv_check_state.setBackgroundResource(R.drawable.icon_checkbox_def);
            }
        } else {
            chHoder.iv_check_state.setVisibility(View.GONE);
        }
        Log.e("group", child.getName() + "   " + childPosition);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public void onGroupCollapse(int groupPosition) {
        groupList.get(groupPosition).setExpand(false);
        List<ChildExpandBean> list = dataMap.get(groupMacs[groupPosition]);
        for (ChildExpandBean bean : list) {
            if (bean.isVisible()) {
                bean.setChecked(false);
                bean.setVisible(false);
            }
        }
        groupList.get(groupPosition).setVisible(false);
        boolean isVisible = false;
        for (GroupExpandBean bean : groupList) {
            if (bean.isVisible()) {
                isVisible = true;
                break;
            }
        }
        if (!isVisible && onLongClick != null) {
            onLongClick.onGroupLondClick(false);
        }
    }

    @Override
    public void onGroupExpand(int groupPosition) {
        groupList.get(groupPosition).setExpand(true);
    }

    private void caculte() {
        int grouppos = 0;
        total = 0;
        checkedTotal = new int[groupList.size()];
        groupMacs = new String[groupList.size()];
        for (GroupExpandBean bean : groupList) {
            int counter = 0;
            List<ChildExpandBean> list = dataMap.get(bean.getMac());
            for (ChildExpandBean item : list) {
                if (item.isChecked()) {
                    counter++;
                }
                total++;
            }
            groupMacs[grouppos] = bean.getMac();
            checkedTotal[grouppos] = counter;
            grouppos++;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        int groupPoistion = (int) v.getTag();
        List<ChildExpandBean> list = dataMap.get(groupList.get(groupPoistion).getMac());
        for (ChildExpandBean child : list) {
            child.setChecked(true);
        }
        groupList.get(groupPoistion).setExpand(true);
        if (onLongClick != null) {
            onLongClick.onGroupLondClick(true);
        }
        return false;
    }

    private class GroupViewHolder {
        private ImageView iv_portrait;
        private TextView tv_group_name;
        private TextView group_sum;
        private ImageView iv_expand_state;
    }

    private class ChildViewHolder {
        private ImageView iv_portrait;
        private TextView tv_child_name;
        private TextView tv_child_state;
        private ImageView iv_check_state;
    }

    public interface OnGroupItemLondClick {
        void onGroupLondClick(boolean visible);
    }

    public void setOnGroupItemLondClick(OnGroupItemLondClick onLongClick) {
        this.onLongClick = onLongClick;
    }

    public void reflesh() {
        if(groupList != null && groupList.size() > 0) {
            caculte();
        }
        notifyDataSetChanged();
    }
}
