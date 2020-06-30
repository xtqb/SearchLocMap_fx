package com.lhzw.searchlocmap.fragment;

import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;

import com.j256.ormlite.dao.Dao;
import com.lhzw.searchlocmap.R;
import com.lhzw.searchlocmap.adapter.BindingWatchAdapter;
import com.lhzw.searchlocmap.bean.BindingOfWatchBean;
import com.lhzw.searchlocmap.bean.HttpPersonInfo;
import com.lhzw.searchlocmap.bean.multicheck.ChildExpandBean;
import com.lhzw.searchlocmap.bean.multicheck.GroupExpandBean;
import com.lhzw.searchlocmap.db.dao.CommonDBOperator;
import com.lhzw.searchlocmap.db.dao.DatabaseHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BindingWatchFragment extends BaseLazyFragment implements ExpandableListView.OnChildClickListener {
    private ExpandableListView expand_listview;
    private DatabaseHelper helper;
    private Dao<BindingOfWatchBean, Integer> bindingOfWatchDao;
    private Map<String, List<ChildExpandBean>> dataMap;
    private List<GroupExpandBean> groupList;
    private BindingWatchAdapter adapter;

    @Override
    protected int initView() {
        return R.layout.frag_binding_watch;
    }

    @Override
    protected void initData() {
        expand_listview = (ExpandableListView) convertView.findViewById(R.id.expand_listview);
        if (helper == null) helper = DatabaseHelper.getHelper(getActivity());
        bindingOfWatchDao = helper.getBindingOfWatchDao();
        onReflesh();
    }

    private BindingWatchAdapter getAdapter() {
        List<BindingOfWatchBean> list = CommonDBOperator.queryByKeys(bindingOfWatchDao, "state", 0 + "");
        if (list != null) {
            groupList = new ArrayList<>();
            dataMap = new HashMap<String, List<ChildExpandBean>>();
            for (BindingOfWatchBean item : list) {
                if (dataMap.get(item.getBelongdeviceNumber()) == null) {
                    Dao<HttpPersonInfo, Integer> httpDao = helper.getHttpPerDao();
                    List<HttpPersonInfo> httpInofList = CommonDBOperator.queryByKeys(httpDao, "deviceNumbers", item.getBelongdeviceNumber());
                    String orgName = "";
                    if(httpInofList != null && httpInofList.size() > 0) {
                        orgName = httpInofList.get(0).getRealName();
                    } else {
                        orgName = "手持";
                    }
                    groupList.add(new GroupExpandBean(false, orgName, item.getBelongdeviceNumber(), 0, false));
                    dataMap.put(item.getBelongdeviceNumber(), new ArrayList<ChildExpandBean>());
                }
                dataMap.get(item.getBelongdeviceNumber()).add(new ChildExpandBean(false, item.getRealName(), item.getDeviceNumber(), 0, false, false, 0));
            }
            list.clear();
        }
        return new BindingWatchAdapter(getActivity(), dataMap, groupList, true);
    }

    @Override
    protected void onReflesh() {
        if (adapter == null) {
            adapter = getAdapter();
            expand_listview.setAdapter(adapter);
            expand_listview.setOnGroupCollapseListener(adapter);
            expand_listview.setOnGroupExpandListener(adapter);
            expand_listview.setOnChildClickListener(this);
        } else {
            updateList();
        }
    }

    private void updateList() {
        if (dataMap != null && dataMap.size() > 0) {
            dataMap.clear();
        }
        if (groupList != null && groupList.size() > 0) {
            groupList.clear();
        }
        List<BindingOfWatchBean> list = CommonDBOperator.queryByKeys(bindingOfWatchDao, "state", 0 + "");
        if (list != null) {
            Log.e("Query", "query    " + list.size());
            if (groupList == null) {
                groupList = new ArrayList<>();
            }
            if (dataMap == null) {
                dataMap = new HashMap<String, List<ChildExpandBean>>();
            }
            for (BindingOfWatchBean item : list) {
                if (dataMap.get(item.getBelongdeviceNumber()) == null) {
                    Dao<HttpPersonInfo, Integer> httpDao = helper.getHttpPerDao();
                    List<HttpPersonInfo> httpInofList = CommonDBOperator.queryByKeys(httpDao, "deviceNumbers", item.getBelongdeviceNumber());
                    String orgName = "";
                    if(httpInofList != null && httpInofList.size() > 0) {
                        orgName = httpInofList.get(0).getRealName();
                    } else {
                        orgName = "手持";
                    }
                    groupList.add(new GroupExpandBean(false, orgName, item.getBelongdeviceNumber(), 0, false));
                    dataMap.put(item.getBelongdeviceNumber(), new ArrayList<ChildExpandBean>());
                }
                dataMap.get(item.getBelongdeviceNumber()).add(new ChildExpandBean(false, item.getRealName(), item.getDeviceNumber(), 0, false, false, 0));
            }
            list.clear();
        }
        adapter.reflesh();
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        return false;
    }
}