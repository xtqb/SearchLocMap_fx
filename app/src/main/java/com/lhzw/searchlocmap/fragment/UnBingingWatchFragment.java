package com.lhzw.searchlocmap.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.LoRaManager;
import android.content.ProtocolParser;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.lhzw.searchlocmap.R;
import com.lhzw.searchlocmap.adapter.BindingWatchAdapter;
import com.lhzw.searchlocmap.adapter.SelectChannelAdapter;
import com.lhzw.searchlocmap.application.SearchLocMapApplication;
import com.lhzw.searchlocmap.bean.BaseBean;
import com.lhzw.searchlocmap.bean.BindingOfWatchBean;
import com.lhzw.searchlocmap.bean.HttpPersonInfo;
import com.lhzw.searchlocmap.bean.LocPersonalInfo;
import com.lhzw.searchlocmap.bean.PersonalInfo;
import com.lhzw.searchlocmap.bean.multicheck.ChildExpandBean;
import com.lhzw.searchlocmap.bean.multicheck.GroupExpandBean;
import com.lhzw.searchlocmap.constants.Constants;
import com.lhzw.searchlocmap.constants.SPConstants;
import com.lhzw.searchlocmap.db.dao.CommonDBOperator;
import com.lhzw.searchlocmap.db.dao.DatabaseHelper;
import com.lhzw.searchlocmap.net.CallbackListObserver;
import com.lhzw.searchlocmap.net.SLMRetrofit;
import com.lhzw.searchlocmap.net.ThreadSwitchTransformer;
import com.lhzw.searchlocmap.utils.BaseUtils;
import com.lhzw.searchlocmap.utils.LogUtil;
import com.lhzw.searchlocmap.utils.SpUtils;
import com.lhzw.searchlocmap.utils.ToastUtil;
import com.lhzw.searchlocmap.view.LoadingView;
import com.lhzw.searchlocmap.view.ShowSelectChanelDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;

public class UnBingingWatchFragment extends BaseLazyFragment implements View.OnClickListener, ExpandableListView.OnChildClickListener,
        BindingWatchAdapter.OnGroupItemLondClick, AdapterView.OnItemLongClickListener, ShowSelectChanelDialog.onChannelItemClickListener {

    private TextView tv_binding;
    private LoadingView loadingView;
    private ExpandableListView un_expand_listview;
    private DatabaseHelper helper;
    private Dao<BindingOfWatchBean, Integer> bindingOfWatchDao;
    private Map<String, List<ChildExpandBean>> dataMap;
    private List<GroupExpandBean> groupList;
    private BindingWatchAdapter adapter;
    private ImageView im_channel_edit;
    private byte[] bdByteArr;
    private ShowSelectChanelDialog dialog;
    private boolean isSelectChannel = false;
    private int CHANNEL;
    private Dao<LocPersonalInfo, Integer> perdao;
    private Dao<PersonalInfo, Integer> dao;
    private TextView tv_channel;
    @SuppressLint("WrongConstant")
    private LoRaManager loRaManager;
    private int backCounter = 0;
    private boolean isFinish;
    private Toast mGlobalToast;
    private List<ChildExpandBean> netBindingList = new ArrayList<>();
    private List<ChildExpandBean> localBindingList = new ArrayList<>();
    private List<ChildExpandBean> sendToWatchList = new ArrayList<>();
    private String mac = BaseUtils.getMacFromHardware();
    private WatchSignalReceiver receiver;
    private final int SEND = 0x0001;
    private boolean isRunning = false;
    private boolean isSending = false;
    private final int DISMISS = 0x0005;
    private final int COMAND = 0x0010;
    private int lastOffset = 0;
    private List<Integer> offsetList = new ArrayList();

    @Override
    protected int initView() {
        return R.layout.frag_unbinding_watch;
    }

    @Override
    public void onResume() {
        super.onResume();
        SpUtils.putBoolean(SPConstants.PERSON_ENTER, true);
        registerBroadcastReceiver();
    }

    @SuppressLint("WrongConstant")
    @Override
    protected void initData() {
        tv_binding = (TextView) convertView.findViewById(R.id.tv_binding);
        tv_binding.setOnClickListener(this);
        if (helper == null) helper = DatabaseHelper.getHelper(mContext);
        bindingOfWatchDao = helper.getBindingOfWatchDao();
        perdao = helper.getLocPersonDao();
        dao = helper.getPersonalInfoDao();
        loRaManager = (LoRaManager) mContext.getSystemService(Context.LORA_SERVICE);
        im_channel_edit = (ImageView) convertView.findViewById(R.id.im_channel_edit);
        im_channel_edit.setOnClickListener(this);
        tv_channel = (TextView) convertView.findViewById(R.id.tv_channel);
        CHANNEL = SpUtils.getInt(SPConstants.CHANNEL_NUM, Constants.CHANNEL_DEF);
        if (SpUtils.getInt(SPConstants.CHANNEL_NUM, Constants.CHANNEL_DEF) == Constants.CHANNEL_DEF) {
            dialog = new ShowSelectChanelDialog(mContext);
            dialog.show();
            dialog.setAdapter(new SelectChannelAdapter(mContext, getResources().getStringArray(R.array.signal_channel)), this);
            dialog.setOnSelecteChannelClickListener(this);
        } else {
            isSelectChannel = true;
            bdByteArr = BaseUtils.obtainSearchBytes();
        }
        tv_channel.setText(CHANNEL + "");
        isFinish = true;
        setBDType(Constants.CHANNEL_DEF);

        // 获取期间没有使用的ID
        List<LocPersonalInfo> locList1 = CommonDBOperator.queryByOrderKey(perdao, "offset", true);
        if (locList1 != null && locList1.size() > 0) {
            int offset = 1;
            for (LocPersonalInfo per : locList1) {
                if (offset == per.getOffset()) {
                    offset++;
                } else {
                    offsetList.add(offset);
                }
            }
            lastOffset = locList1.get(locList1.size() - 1).getOffset();
        }

        // 插入测试数据
//        BindingOfWatchBean item = new BindingOfWatchBean("0529000002", "李四", "2", 0, 1, "王五", BaseUtils.getMacFromHardware());
//        CommonDBOperator.saveToDB(bindingOfWatchDao, item);
//        BindingOfWatchBean item0 = new BindingOfWatchBean("0529000001", "李四0", "2", 1, 1, "王五", BaseUtils.getMacFromHardware());
//        CommonDBOperator.saveToDB(bindingOfWatchDao, item0);
//        BindingOfWatchBean item1 = new BindingOfWatchBean("0529000003", "李四1", "2", 1, 1, "王五", "ADEFBCADFEBE");
//        CommonDBOperator.saveToDB(bindingOfWatchDao, item1);
//        BindingOfWatchBean item2 = new BindingOfWatchBean("0529000004", "李四2", "2", 1, 1, "王五", "ADEFBCADFEBE");
//        CommonDBOperator.saveToDB(bindingOfWatchDao, item2);
//        BindingOfWatchBean item3 = new BindingOfWatchBean("0529000005", "李四3", "2", 0, 1, "王五", "ADEFBCADFFFE");
//        CommonDBOperator.saveToDB(bindingOfWatchDao, item3);
//        BindingOfWatchBean item4 = new BindingOfWatchBean("0529000006", "李四4", "2", 0, 1, "王五", BaseUtils.getMacFromHardware());
//        CommonDBOperator.saveToDB(bindingOfWatchDao, item4);

        un_expand_listview = (ExpandableListView) convertView.findViewById(R.id.un_expand_listview);
        if (BaseUtils.isNetConnected(getContext())) {
            Dao<HttpPersonInfo, Integer> httpDao = helper.getHttpPerDao();
            Log.e("MAC", "mac  " + BaseUtils.getMacFromHardware());
            List<HttpPersonInfo> list = CommonDBOperator.queryByKeys(httpDao, "deviceNumbers",  BaseUtils.getMacFromHardware());
            if (list != null && list.size() > 0) {
                syncBindingWatch(list.get(0).getOrg());
                list.clear();
            } else {
                Toast.makeText(mContext, "该手持MAC未在平台录入", Toast.LENGTH_LONG).show();
            }
        } else {
            initList();
            Toast.makeText(mContext, "网络连接异常,请检查网络", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onReflesh() {
        initList();
    }

    private void initList() {
        if (adapter == null) {
            adapter = getAdapter();
            un_expand_listview.setAdapter(adapter);
            un_expand_listview.setOnGroupCollapseListener(adapter);
            un_expand_listview.setOnGroupExpandListener(adapter);
            un_expand_listview.setOnChildClickListener(this);
            un_expand_listview.setOnItemLongClickListener(this);
            adapter.setOnGroupItemLondClick(this);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    private BindingWatchAdapter getAdapter() {
        List<BindingOfWatchBean> list = CommonDBOperator.queryByKeys(bindingOfWatchDao, "state", 1 + "");
        if (list != null) {
            groupList = new ArrayList<>();
            dataMap = new HashMap<String, List<ChildExpandBean>>();
            for (BindingOfWatchBean item : list) {
                if (dataMap.get(item.getBelongdeviceNumber()) == null) {
                    groupList.add(new GroupExpandBean(false, item.getRealName(), item.getBelongdeviceNumber(), 0, false));
                    dataMap.put(item.getBelongdeviceNumber(), new ArrayList<ChildExpandBean>());
                }
                dataMap.get(item.getBelongdeviceNumber()).add(new ChildExpandBean(false, item.getRealName(), item.getDeviceNumber(), 0, false, false, 0));
            }
            list.clear();
        }
        return new BindingWatchAdapter(mContext, dataMap, groupList, false);
    }

    private void syncBindingWatch(String org) {
        try {
            int orgnization = Integer.parseInt(org);
            if (loadingView == null) {
                loadingView = new LoadingView(mContext);
            }
            loadingView.setLoadingTitle("同步获取腕表数据...");
            if (!loadingView.isShowing()) {
                loadingView.show();
            }

            Observable<BaseBean<List<BindingOfWatchBean>>> observable = SLMRetrofit.getInstance().getApi().getBindingWatchs(orgnization);
            observable.compose(new ThreadSwitchTransformer<BaseBean<List<BindingOfWatchBean>>>()) //从数据流中得到原始Observable<T>的操作符
                    .subscribe(new CallbackListObserver<BaseBean<List<BindingOfWatchBean>>>() {

                        @Override
                        protected void onSucceed(BaseBean<List<BindingOfWatchBean>> baseBean) {
                            // 获取数据成功 保存数据
                            if ("0".equals(baseBean.getCode())) {

                                //删除数据表
                                CommonDBOperator.deleteAllItems(bindingOfWatchDao);
                                List<BindingOfWatchBean> list = baseBean.getData();

                                if (list != null && list.size() > 0) {
                                    for (BindingOfWatchBean bean : list) {
                                        if (bean.isBound()) {
                                            bean.setState(0);
                                        } else {
                                            bean.setState(1);
                                        }
                                        CommonDBOperator.saveToDB(bindingOfWatchDao, bean);
                                    }
                                }
                                showToast("同步成功");
                            } else {
                                showToast("同步失败");
                            }
                            // 刷新列表

                            initList();

                            loadingView.dismiss();
                            loadingView.cancel();
                        }

                        @Override
                        protected void onFailed() {
                            //获取数据失败
                            initList();
                            showToast("同步失败");
                            loadingView.dismiss();
                            loadingView.cancel();
                        }
                    });
        } catch (Exception e) {
            Log.e("UnBingingWatchFragment", "org parser exception");
        }

    }

    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.bspsos");
        filter.addAction("android.intent.action.SOS_RECEIVE");
        receiver = new WatchSignalReceiver();
        mContext.registerReceiver(receiver, filter);
    }

    private class WatchSignalReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            try {
                //
                if (isSelectChannel) {
                    ProtocolParser parser = intent.getParcelableExtra("result");
                    byte[] typeKey = parser.getCmdKey();
                    if (typeKey[0] == (byte) 0x11) {// 普通人员不接收
                        return;
                    }
                    String register_num = BaseUtils.traslation(parser.getPersonNum()).substring(0, 10);//注册码

                    for (ChildExpandBean bean : localBindingList) {
                        if (register_num.equals(bean.getRegister())) {
                            netBindingList.add(bean);
                            localBindingList.remove(bean);
                            break;
                        }
                    }
                    netBindingWatch();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onClick(View v) {
        //点击 多选绑定
        switch (v.getId()) {
            case R.id.tv_binding: {
                for (GroupExpandBean group : groupList) {
                    List<ChildExpandBean> childs = dataMap.get(group.getMac());
                    for (ChildExpandBean child : childs) {
                        if (child.isChecked()) {
                            Log.e("Bind", "---------------------------------  ");
                            boolean isExist = false;
                            for (ChildExpandBean bean : localBindingList) {
                                if (child.getRegister().equals(bean.getRegister())) {
                                    isExist = true;
                                    break;
                                }
                            }
                            if (!isExist) {
                                localBindingList.add(child);
                            }

                            isExist = false;
                            for (ChildExpandBean item : sendToWatchList) {
                                if (child.getRegister().equals(item.getRegister())) {
                                    isExist = true;
                                    break;
                                }
                            }
                            if (!isExist) {
                                sendToWatchList.add(child);
                            }
                            child.setChecked(false);
                            child.setVisible(false);
                        }
                    }

                    if (group.isVisible()) {
                        group.setVisible(false);
                    }
                    if (group.isExpand()) {
                        group.setExpand(false);
                    }

                }
                if (adapter != null) {
                    adapter.reflesh();
                }
                tv_binding.setVisibility(View.GONE);
//                localBingingWatchs();
                doSend();
                break;
            }
            case R.id.bt_back:
                dialog.dismiss();
                if (isFinish) {
                    mContext.finish();
                }
                break;
            case R.id.im_channel_edit:
                if (dialog == null) {
                    dialog = new ShowSelectChanelDialog(mContext);
                    dialog.show();
                    dialog.setAdapter(new SelectChannelAdapter(mContext, getResources().getStringArray(R.array.signal_channel)), this);
                    dialog.setOnSelecteChannelClickListener(this);
                    dialog.setConten(getString(R.string.dialog_cancel));
                } else {
                    dialog.show();
                    dialog.setConten(getString(R.string.dialog_cancel));
                }
                isFinish = false;
                break;
        }
    }


    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        if (groupList.get(groupPosition).isVisible()) {
            ChildExpandBean item = dataMap.get(groupList.get(groupPosition).getMac()).get(childPosition);
            item.setChecked(!item.isChecked());
            adapter.reflesh();
        } else {
            // 单选 绑定
            Log.e("Bind", "---------------------------------  ");
            boolean isExist = false;
            for (ChildExpandBean bean : localBindingList) {
                if (dataMap.get(groupList.get(groupPosition).getMac()).get(childPosition).getRegister().equals(bean.getRegister())) {
                    isExist = true;
                    break;
                }
            }
            if (!isExist) {
                localBindingList.add(dataMap.get(groupList.get(groupPosition).getMac()).get(childPosition));
            }
            isExist = false;
            for (ChildExpandBean bean : sendToWatchList) {
                if (dataMap.get(groupList.get(groupPosition).getMac()).get(childPosition).getRegister().equals(bean.getRegister())) {
                    isExist = true;
                    break;
                }
            }
            if (!isExist) {
                sendToWatchList.add(dataMap.get(groupList.get(groupPosition).getMac()).get(childPosition));
            } else {
                showToast("已经在发送队列中");
                return false;
            }

//            localBingingWatchs();
            doSend();
        }
        return false;
    }

    private void doSend() {
        if (!isSending) {
            isSending = true;
            mHandler.sendEmptyMessage(COMAND);
        }
    }

    private void localBingingWatchs() {
        if (sendToWatchList != null && sendToWatchList.size() > 0) {
            // 获取最新的排序序号
            ChildExpandBean item = sendToWatchList.get(0);
            int offset = -1;
            for (ChildExpandBean bean : localBindingList) {
                if (item.getRegister().equals(bean.getRegister())) {
                    offset = bean.getOffset();
                    if (offset == 0) {
                        if (offsetList.size() > 0) {
                            offset = offsetList.get(0);
                            offsetList.remove(0);
                        } else {
                            lastOffset++;
                            offset = lastOffset;
                        }
                        bean.setOffset(offset);
                    }
                    break;
                }
            }
            byte[] sndBytes = BaseUtils.getPerRegisterByteArr(item.getRegister());
            for (int j = 0; j < 5; j++) {
                sndBytes[5 + j] = bdByteArr[j];
            }
            sndBytes[10] = (byte) offset;
            sendToWatchList.remove(0);
            sendCMDSearch(sndBytes);
            showToast("向 " + item.getRegister() + "下发绑定指令");
        }
    }

    private void netBindingWatch() {
        if (!BaseUtils.isNetConnected(mContext)) {
            localBindingList.clear();
            netBindingList.clear();
            showToast("网络连接异常,请检查网络");
            return;
        }

        if (!isRunning && netBindingList.size() > 0) {
            isRunning = true;
            Message msg = new Message();
            msg.what = SEND;
            msg.obj = netBindingList.get(0);
            netBindingList.remove(0);
            mHandler.sendMessage(msg);
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SEND: {
                    ChildExpandBean child = (ChildExpandBean) msg.obj;
                    AskServerToBind(child);
                    if (netBindingList.size() > 0) {
                        Message send = new Message();
                        send.what = SEND;
                        send.obj = netBindingList.get(0);
                        netBindingList.remove(0);
                        mHandler.sendMessageDelayed(send, 500);
                    } else {
                        isRunning = false;
                        // 更新数据库
//                        updateList();
                    }
                    break;
                }
                case DISMISS: {
                    if (loadingView != null && loadingView.isShowing()) {
                        loadingView.dismiss();
                        loadingView.cancel();
                    }
                    break;
                }
                case COMAND: {
                    if (isSending && sendToWatchList != null && sendToWatchList.size() > 0) {
                        localBingingWatchs();
                        mHandler.sendEmptyMessageDelayed(COMAND, 4000);
                    } else {
                        isSending = false;
                    }
                }
            }
        }
    };

    private void updateList() {
        if (dataMap != null && dataMap.size() > 0) {
            dataMap.clear();
        }
        if (groupList != null && groupList.size() > 0) {
            groupList.clear();
        }
        List<BindingOfWatchBean> list = CommonDBOperator.queryByKeys(bindingOfWatchDao, "state", 1 + "");
        Log.e("Query", "query    " + list.size());
        if (list != null) {
            if (groupList == null) {
                groupList = new ArrayList<>();
            }
            if (dataMap == null) {
                dataMap = new HashMap<String, List<ChildExpandBean>>();
            }
            for (BindingOfWatchBean item : list) {
                if (dataMap.get(item.getBelongdeviceNumber()) == null) {
                    groupList.add(new GroupExpandBean(false, item.getLoginName(), item.getBelongdeviceNumber(), 0, false));
                    dataMap.put(item.getBelongdeviceNumber(), new ArrayList<ChildExpandBean>());
                }
                dataMap.get(item.getBelongdeviceNumber()).add(new ChildExpandBean(false, item.getRealName(), item.getDeviceNumber(), 0, false, false, 0));
            }
            list.clear();
        }
        adapter.reflesh();
    }

    /**
     * 向服务器请求是否可以绑定
     */

    private void AskServerToBind(final ChildExpandBean child) {
        LogUtil.d("mac==" + mac + "<=====>deviceNum==" + child.getRegister());
        if (TextUtils.isEmpty(mac)) {
            showToast("mac为空");
            return;
        }
        if (loadingView == null) {
            loadingView = new LoadingView(mContext);
        }
        loadingView.setLoadingTitle("绑定中...");
        if (!loadingView.isShowing()) {
            loadingView.show();
        }

        Observable<BaseBean> observable = SLMRetrofit.getInstance().getApi().canBinding(mac, child.getRegister());
        observable.compose(new ThreadSwitchTransformer<BaseBean>())
                .subscribe(new CallbackListObserver<BaseBean>() {
                    @Override
                    protected void onSucceed(BaseBean bean) {
                        loadingView.dismiss();
                        if (bean != null) {
                            if ("0".equals(bean.getCode())) {
                                LogUtil.d("请求成功可以绑定");
                                ToastUtil.showToast("绑定成功");
                                // 两个数据库连接
                                List<LocPersonalInfo> list = CommonDBOperator.queryByKeys(perdao,
                                        "num", child.getRegister());
                                if (null != list && list.size() > 0) {
                                    // 更新
                                    list.get(0).setName(child.getName() + "");
                                    list.get(0).setOffset(child.getOffset());
                                    CommonDBOperator.updateItem(perdao, list.get(0));
                                    list.clear();
                                    // 更新 personItem
                                    List<PersonalInfo> list1 = CommonDBOperator.queryByKeys(dao,
                                            "num", child.getRegister());
                                    list1.get(0).setName(child.getName());
                                    CommonDBOperator.updateItem(dao, list1.get(0));
                                    list1.clear();

                                } else {
                                    // 保存
                                    LocPersonalInfo perInfo = new LocPersonalInfo();
                                    perInfo.setNum(child.getRegister());
                                    perInfo.setName(child.getName());
                                    perInfo.setSex("男");
                                    perInfo.setOffset(child.getOffset());
                                    CommonDBOperator.saveToDB(perdao, perInfo);
                                    perInfo = null;

                                    // 插入到 personItem
                                    PersonalInfo item = new PersonalInfo();
                                    item.setNum(child.getRegister());
                                    item.setName(child.getName());
                                    item.setSex("男");
                                    item.setState(Constants.PERSON_OFFLINE);
                                    CommonDBOperator.saveToDB(dao, item);
                                }
//                                // 更新数据库
                                List<BindingOfWatchBean> watchs = CommonDBOperator.queryByKeys(bindingOfWatchDao, "deviceNumber", child.getRegister());
                                if (watchs != null && watchs.size() > 0) {
                                    watchs.get(0).setState(0);
                                    watchs.get(0).setBelongdeviceNumber(BaseUtils.getMacFromHardware());
                                    CommonDBOperator.updateItem(bindingOfWatchDao, watchs.get(0));
                                }
                                if (netBindingList.size() == 0) {
                                    updateList();
                                }

                            } else {
                                showToast(bean.getMessage() + "");
                            }
                        } else {
                            LogUtil.d("返回bean为空");
                        }
                    }

                    @Override
                    protected void onFailed() {
                        loadingView.dismiss();
                        showToast("网络连接失败");
                        LogUtil.e("请求失败");
                    }
                });

    }

    @Override
    public void onPause() {
        super.onPause();
        if (receiver != null) {
            mContext.unregisterReceiver(receiver);
        }
        SpUtils.putBoolean(SPConstants.PERSON_ENTER, false);
        receiver = null;
    }

    @Override
    public void onGroupLondClick(boolean visible) {
        tv_binding.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tv_binding = null;
        loadingView = null;
        bindingOfWatchDao = null;
        helper = null;
        if (dataMap != null) {
            dataMap.clear();
        }

        dataMap = null;
        if (groupList != null) {
            groupList.clear();
        }
        loRaManager = null;
        adapter = null;
        perdao = null;
        mGlobalToast = null;

        netBindingList.clear();
        netBindingList = null;

        localBindingList.clear();
        localBindingList = null;

        sendToWatchList.clear();
        sendToWatchList = null;

        mac = null;

        dao = null;
        groupList = null;

        offsetList.clear();
        offsetList = null;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        long packedPosition = un_expand_listview.getExpandableListPosition(position);
        int groupPosition = ExpandableListView.getPackedPositionGroup(packedPosition);
        int childPosition = ExpandableListView.getPackedPositionChild(packedPosition);

        List<ChildExpandBean> list = dataMap.get(groupList.get(groupPosition).getMac());
        for (ChildExpandBean child : list) {
            child.setVisible(true);
        }
        tv_binding.setVisibility(View.VISIBLE);
        adapter.reflesh();
        groupList.get(groupPosition).setVisible(true);
        return false;
    }

    @Override
    public void onChannelClick(int pos) {
        CHANNEL = pos + 1;
        SpUtils.putInt(SPConstants.CHANNEL_NUM, CHANNEL);
        isSelectChannel = true;
        tv_channel.setText(CHANNEL + "");
        dialog.dismiss();
        bdByteArr = BaseUtils.obtainSearchBytes();
    }

    public void onKeyDown() {
        backCounter++;
        if (backCounter == 2) {
            setBDType(CHANNEL);
            mContext.finish();
        } else {
            showToast("再按一次退出");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    backCounter = 0;
                }
            }, 1000);
        }
    }

    private boolean sendCMDSearch(final byte[] num) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (loRaManager != null) {
                    loRaManager.searchCard(num);
                }
            }
        }).start();
        return true;
    }

    private void setBDType(final int type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (loRaManager != null) {
                    loRaManager.changeWatchType(type);
                }
            }
        }).start();
    }

    public void showToast(String text) {
        if (mGlobalToast == null) {
            mGlobalToast = Toast.makeText(SearchLocMapApplication.getContext(), text,
                    Toast.LENGTH_SHORT);
            mGlobalToast.show();
        } else {
            mGlobalToast.setText(text);
            mGlobalToast.setDuration(Toast.LENGTH_SHORT);
            mGlobalToast.show();
        }
    }
}