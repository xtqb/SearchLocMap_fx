package com.lhzw.searchlocmap.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.j256.ormlite.dao.Dao;
import com.lhzw.searchlocmap.R;
import com.lhzw.searchlocmap.adapter.DipperListAdapter;
import com.lhzw.searchlocmap.adapter.SpinnerAdapter;
import com.lhzw.searchlocmap.bean.DipperInfoBean;
import com.lhzw.searchlocmap.bean.MessageInfoIBean;
import com.lhzw.searchlocmap.constants.Constants;
import com.lhzw.searchlocmap.db.dao.CommonDBOperator;
import com.lhzw.searchlocmap.db.dao.DatabaseHelper;
import com.lhzw.searchlocmap.utils.BaseUtils;
import com.lhzw.searchlocmap.utils.SpUtils;
import com.lhzw.searchlocmap.view.MyDrawerLayout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xtqb on 2019/1/7.
 */
public class DipperNumSettingtActivity extends Activity implements AdapterView.OnItemClickListener, ExpandableListView.OnChildClickListener, View.OnClickListener {

    private ExpandableListView dipper_listview;
    private String[] gNames;
    private Map<String, List<DipperInfoBean>> dataMap;
    private Dao<DipperInfoBean, Integer> dipDao;
    private Dao<MessageInfoIBean, Integer> msgDao;
    private DatabaseHelper helper;
    private DipperListAdapter adapter;
    private TextView tv_add_dipper;
    private Button dipper_back;
    private MyDrawerLayout dipper_drawer;
    private String[] arrayStr;
    private ListView listview;
    private SpinnerAdapter spinnerAdapter;
    private TextView tv_spinner;
    private int currentID;
    private TextView tv_sure;
    private TextView tv_cancel;
    private EditText et_input_num;
    private Toast mGlobalToast;
    private int[] typeArr;
    private EditText et_input_remark;
    private Map<String, Integer> unReadMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dipper_num);
        initView();
        initData();
        setListener();
    }

    private void initView() {
        dipper_drawer = (MyDrawerLayout) findViewById(R.id.dipper_drawer);
        dipper_listview = (ExpandableListView) findViewById(R.id.dipper_listview);
        tv_add_dipper = (TextView) findViewById(R.id.tv_add_dipper);
        dipper_back = (Button) findViewById(R.id.dipper_back);
        listview = (ListView) findViewById(R.id.listview);
        tv_spinner = (TextView) findViewById(R.id.tv_spinner);
        tv_sure = (TextView) findViewById(R.id.tv_sure);
        tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        et_input_num = (EditText) findViewById(R.id.et_input_num);
        et_input_remark = (EditText) findViewById(R.id.et_input_remark);
    }

    private void initData() {
//        registerBroadcastReceiver();
        gNames = getResources().getStringArray(R.array.dipper_type);
//        typeArr = getResources().getIntArray(R.array.dipper_type_id);
        typeArr = new int[]{5, 1};
        dataMap = new HashMap<String, List<DipperInfoBean>>();
        unReadMap = new HashMap<>();
        helper = DatabaseHelper.getHelper(this);
        dipDao = helper.getDipperDao();
        msgDao = helper.getMesgInfoDao();
//        initUnreadNum();
        initExpandListView();
        adapter = new DipperListAdapter(this, dataMap, gNames, unReadMap);
        dipper_drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        spinnerAdapter = new SpinnerAdapter(this, gNames);
        currentID = -1;
    }

    private void initUnreadNum() {
        if(unReadMap.size() > 0) {
            unReadMap.clear();
        }
        List<DipperInfoBean> dipperList = CommonDBOperator.getList(dipDao);
        Map<String, String> map = new HashMap<>();
        map.put("type", ShortMessUploadActivity.MESSAGE_RECEIVE + "");
        map.put("state", Constants.MESSAGE_UNREAD + "");
        for(DipperInfoBean bean : dipperList){
            map.put("num",bean.getNum());
            List<MessageInfoIBean> msgList = CommonDBOperator.queryByMultiKeys(msgDao, map);
            if(msgList == null && msgList.size() == 0) {
                unReadMap.put(bean.getNum(), 0);
            } else {
                unReadMap.put(bean.getNum(), msgList.size());
                msgList.clear();
            }
        }
    }

    private void initExpandListView() {
        if(dataMap.size() > 0) {
            dataMap.clear();
        }
        int counter = 0;
        for(Integer name : typeArr) {
            List<DipperInfoBean> list =  CommonDBOperator.queryByKeys(dipDao, "type", name+"");
            dataMap.put(gNames[counter], list);
            counter++;
        }
    }

    private void setListener() {
        dipper_listview.setAdapter(adapter);
        dipper_listview.setOnGroupCollapseListener(adapter);
        dipper_listview.setOnGroupExpandListener(adapter);
        dipper_listview.setOnChildClickListener(this);
        tv_add_dipper.setOnClickListener(this);
        tv_sure.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
        dipper_back.setOnClickListener(this);
        tv_spinner.setOnClickListener(this);
        listview.setAdapter(spinnerAdapter);
        listview.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.listview:
                currentID = position;
                tv_spinner.setText(gNames[position]);
                spinnerAdapter.showListView(false);
                break;
        }
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
//        Intent intent = new Intent(this, ShortMessUploadActivity.class);
//        intent.putExtra("dipper_num", dataMap.get(gNames[groupPosition]).get(childPosition).getNum());
//        intent.putExtra("dipper_remark", dataMap.get(gNames[groupPosition]).get(childPosition).getRemark());
//        intent.putExtra("dipper_type", dataMap.get(gNames[groupPosition]).get(childPosition).getType());
//        startActivityForResult(intent, 0x4545);
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if(resultCode == 7) {
//            initUnreadNum();
//            adapter.notifyDataSetChanged();
//        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dipper_back:
                setResult(6);
                this.finish();
                break;
            case R.id.tv_add_dipper:
                if(dipper_drawer.isDrawerOpen(Gravity.LEFT)) {
                    dipper_drawer.closeDrawer(Gravity.LEFT);
                } else {
                    currentID = -1;
                    et_input_num.setText("");
                    et_input_remark.setText("");
                    spinnerAdapter.showListView(false);
                    tv_spinner.setText(getString(R.string.dipper_select_type_note));
                    dipper_drawer.openDrawer(Gravity.LEFT);
                }
                break;
            case R.id.tv_spinner:
                if(!spinnerAdapter.getIsShow()){
                    spinnerAdapter.showListView(true);
                }
                break;
            case R.id.tv_cancel:
                if(dipper_drawer.isDrawerOpen(Gravity.LEFT)) {
                    currentID = -1;
                    spinnerAdapter.showListView(false);
                    dipper_drawer.closeDrawer(Gravity.LEFT);
                }
                break;
            case R.id.tv_sure:
                if(BaseUtils.isStringEmpty(et_input_num.getText().toString())) {
                    showToast(getString(R.string.input_dipper_num_null_note));
                    return;
                }

                if(tv_spinner.getText().toString().equals(getString(R.string.dipper_select_type_note)) ||
                        BaseUtils.isStringEmpty(tv_spinner.getText().toString())) {
                    showToast(getString(R.string.input_dipper_type_null_note));
                    return;
                }
                List<DipperInfoBean> list = CommonDBOperator.queryByKeys(dipDao, "num", et_input_num.getText().toString());
                if(list == null || list.size() == 0) {
                    saveItem();
                } else if(list.size() == 1) {
                   if(list.get(0).getType() == typeArr[currentID] && (typeArr[currentID] == Constants.TX_QZH || typeArr[currentID] == Constants.TX_JZH)) {
                       saveItem();
                   } else {
                       showToast("该人员已录入");
                       return;
                    }
                }
                dipper_drawer.closeDrawer(Gravity.LEFT);
                break;
        }
    }

    private void saveItem() {
        Log.e("Tag", "currentID = " + currentID + "  "+ Constants.TX_QZH);
        switch (typeArr[currentID]){
            case Constants.TX_QZH:
                saveBDNum();
                SpUtils.putString(Constants.UPLOAD_QZH_NUM, et_input_num.getText().toString());
                break;
            case Constants.TX_JZH:
                saveBDNum();
                SpUtils.putString(Constants.UPLOAD_JZH_NUM, et_input_num.getText().toString());
                break;
            case Constants.TX_BJ:
                Map<String, String> map = new HashMap<String, String>();
                map.put("type", Constants.TX_BJ +"");
                map.put("num", et_input_num.getText().toString()+"");
                List<DipperInfoBean> list1 =  CommonDBOperator.queryByMultiKeys(dipDao, map);
                if(list1 == null || list1.size() == 0) {
                    DipperInfoBean item = new DipperInfoBean(et_input_num.getText().toString(), Constants.TX_BJ, "", et_input_remark.getText().toString());
                    CommonDBOperator.saveToDB(dipDao, item);
                } else {
                    list1.get(0).setRemark(et_input_remark.getText().toString());
                    CommonDBOperator.updateItem(dipDao, list1.get(0));
                    list1.clear();
                }
                map.clear();
                break;
        }
//        initUnreadNum();
        initExpandListView();
        adapter.reflesh();
    }

    private void saveBDNum(){
        List<DipperInfoBean> list = CommonDBOperator.queryByKeys(dipDao, "type", typeArr[currentID]+"");
        if(list == null || list.size() == 0) {
            DipperInfoBean item = new DipperInfoBean(et_input_num.getText().toString(), typeArr[currentID], "", et_input_remark.getText().toString());
            CommonDBOperator.saveToDB(dipDao, item);
        } else {
            CommonDBOperator.deleteByKeys(helper.getMesgInfoDao(), "num", list.get(0).getNum());//覆盖之前删除原来消息
            list.get(0).setNum(et_input_num.getText().toString());
            list.get(0).setRemark(et_input_remark.getText().toString());
            CommonDBOperator.updateItem(dipDao, list.get(0));
            list.clear();
        }



    }

    public void showToast(String text) {
        if (mGlobalToast == null) {
            mGlobalToast = Toast.makeText(this, text,
                    Toast.LENGTH_SHORT);
            mGlobalToast.show();
        } else {
            mGlobalToast.setText(text);
            mGlobalToast.setDuration(Toast.LENGTH_SHORT);
            mGlobalToast.show();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            setResult(6);
            this.finish();
            return true;
        }
        return false;
    }

    private void registerBroadcastReceiver() {
        try {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Constants.BD_Mms_ACTION);
            registerReceiver(receiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
//            if (intent.getAction().equals(Constants.BD_Mms_ACTION)) {
//                initUnreadNum();
//                adapter.notifyDataSetChanged();
//            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(receiver == null) {
            unregisterReceiver(receiver);
        }
    }
}
