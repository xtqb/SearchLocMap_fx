package com.lhzw.searchlocmap.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;
import com.lhzw.searchlocmap.R;
import com.lhzw.searchlocmap.adapter.PerListAdapter;
import com.lhzw.searchlocmap.bean.HttpPersonInfo;
import com.lhzw.searchlocmap.bean.HttpRequstInfo;
import com.lhzw.searchlocmap.bean.MessageInfoIBean;
import com.lhzw.searchlocmap.constants.Constants;
import com.lhzw.searchlocmap.db.dao.CommonDBOperator;
import com.lhzw.searchlocmap.db.dao.DatabaseHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xtqb on 2019/4/1.
 */
public class DipperCommunicationActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener, ExpandableListView.OnChildClickListener {
    private ImageView im_bd_communication_back;
    private ExpandableListView listview;
    private DatabaseHelper helper;
    private Dao<HttpPersonInfo, Integer> httpDao;
    private List<HttpPersonInfo> list;
    private Map<String, List<HttpPersonInfo>> dataMap;
    private List<String> groupName;
    private Map<String, Integer> unReadMap;
    private Map<String, List<Integer>> unChReadMap;
    private Dao<MessageInfoIBean, Integer> msgDao;
    private PerListAdapter adapter;
    private List<String> values;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communication_per);
        initView();
        initData();
        setListener();
    }

    private void initView() {
        im_bd_communication_back = (ImageView) findViewById(R.id.im_bd_communication_back);
        listview = (ExpandableListView) findViewById(R.id.listview);
    }

    private void initData() {
        registerBroadcastReceiver();
        helper = DatabaseHelper.getHelper(this);
        httpDao = helper.getHttpPerDao();
        msgDao = helper.getMesgInfoDao();
        unReadMap = new HashMap<>();
        groupName = new ArrayList<>();
        dataMap = new HashMap<>();
        unChReadMap = new HashMap<>();
        values = new ArrayList<>();
        values.add( 0 + "");
        values.add(1 + "");
        list = CommonDBOperator.queryByMultiKeysEqual(httpDao, "deviceType", values);
        values.clear();
        initUnreadNum();
        adapter = new PerListAdapter(this, dataMap, groupName, unReadMap, unChReadMap);
        listview.setAdapter(adapter);
        listview.setOnGroupCollapseListener(adapter);
        listview.setOnGroupExpandListener(adapter);
        listview.setOnChildClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setListener() {
        im_bd_communication_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.im_bd_communication_back:
                setResult(6);
                this.finish();
                break;
        }
    }

    private void initUnreadNum() {
        if (dataMap.size() > 0) {
            dataMap.clear();
        }
        if (groupName.size() > 0) {
            groupName.clear();
        }
        if (unReadMap.size() > 0) {
            unReadMap.clear();
        }
        if(unChReadMap.size() > 0) {
            unChReadMap.clear();
        }
        Map<String, String> map = new HashMap<>();
        Map<String, String> msgMap = new HashMap<>();
        for(HttpPersonInfo item : list) {
            if(map.get(item.getOrgName()) == null){
                map.put(item.getOrgName(), item.getOrgName());
                groupName.add(item.getOrgName());
                unReadMap.put(item.getOrgName(), 0);
            }
            msgMap.put("ID", item.getId() + "");
            msgMap.put("state", Constants.MESSAGE_UNREAD + "");
            List<MessageInfoIBean> msgList = CommonDBOperator.queryByMultiKeys(msgDao, msgMap);
            if(msgList != null) {
                unReadMap.put(item.getOrgName(), unReadMap.get(item.getOrgName()) + msgList.size());
                msgList.clear();
            }
        }

        for(String org : groupName) {
            if(dataMap.get(org) == null) {
                List<HttpPersonInfo> revList = new ArrayList<>();
                dataMap.put(org, revList);
            }
        }
        for(HttpPersonInfo item : list) {
            dataMap.get(item.getOrgName()).add(item);
        }

        for (Map.Entry<String, List<HttpPersonInfo>> entry : dataMap.entrySet()) {
            List<Integer> revList = new ArrayList<>();
            for(HttpPersonInfo info : entry.getValue()) {
                msgMap.put("ID", info.getId() + "");
                List<MessageInfoIBean> msgList = CommonDBOperator.queryByMultiKeys(msgDao, msgMap);
                if(revList == null) {
                    revList.add(0);
                } else {
                    revList.add(msgList.size());
                }
                msgList.clear();
            }
            unChReadMap.put(entry.getKey(), revList);
        }



        map.clear();
        msgMap.clear();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.BD_Mms_ACTION);
        registerReceiver(receiver, filter);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (intent.getAction().equals(Constants.BD_Mms_ACTION)) {
                initUnreadNum();
                adapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(6);
            this.finish();
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == 7) {
            initUnreadNum();
           adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if(receiver != null) {
                unregisterReceiver(receiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        Intent intent = new Intent(this, ShortMessUploadActivity.class);
        intent.putExtra("msg_Id", dataMap.get(groupName.get(groupPosition)).get(childPosition).getId());
        intent.putExtra("realName", dataMap.get(groupName.get(groupPosition)).get(childPosition).getRealName());
        intent.putExtra("org", dataMap.get(groupName.get(groupPosition)).get(childPosition).getOrgName());
        intent.putExtra("bdNum", dataMap.get(groupName.get(groupPosition)).get(childPosition).getDeviceNumbers());
        startActivityForResult(intent, 0x4545);
        return false;
    }
}
