package com.lhzw.searchlocmap.fragment;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.j256.ormlite.dao.Dao;
import com.lhzw.searchlocmap.R;
import com.lhzw.searchlocmap.adapter.ShortMsgAdapter;
import com.lhzw.searchlocmap.bean.HttpPersonInfo;
import com.lhzw.searchlocmap.bean.MessageInfoIBean;
import com.lhzw.searchlocmap.constants.Constants;
import com.lhzw.searchlocmap.db.dao.CommonDBOperator;
import com.lhzw.searchlocmap.db.dao.DatabaseHelper;
import com.lhzw.searchlocmap.event.EventBusBean;
import com.lhzw.searchlocmap.ui.ShortMessUploadActivity;
import com.lhzw.searchlocmap.utils.LogUtil;
import com.lhzw.searchlocmap.view.PopupWindowList;
import com.lhzw.searchlocmap.view.ShowAlertDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * Created by hecuncun on 2019/5/22
 *
 * 最近短消息
 */
public class ShortMessageFragment extends BaseLazyFragment {
    @BindView(R.id.rv_short_msg)
    RecyclerView mRvShortMsg;
    @BindView(R.id.tv_no_message)
    TextView tvNoMessage;
    private List<MessageInfoIBean> mMessageInfoIBeanList=new ArrayList<>();
    private List<HttpPersonInfo> mPersonInfoList=new ArrayList<>();
    private ShortMsgAdapter mAdapter;
    private DatabaseHelper mHelper;
    private Map<Integer,Long> mHashMap =new LinkedHashMap<>();//<id,time> id为消息所属人id   time 为消息时间
    private Dao mHttpPerDao;
    private Dao mMesgInfoDao;

    @Override
    protected int initView() {
        return R.layout.fragment_short_message;
    }

    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
        mHelper = DatabaseHelper.getHelper(mContext);
        mHttpPerDao = mHelper.getHttpPerDao();
        mMesgInfoDao = mHelper.getMesgInfoDao();
        initCurrentContactRv();//初始化RV配置
        updateRecentContactList();//更新联系人列表数据

    }

    private void initCurrentContactRv() {
        //初始化Rv
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRvShortMsg.setLayoutManager(linearLayoutManager);
        mRvShortMsg.setHasFixedSize(true);
        mAdapter = new ShortMsgAdapter(mPersonInfoList);
        mRvShortMsg.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                HttpPersonInfo httpPersonInfo = (HttpPersonInfo) adapter.getItem(position);
                Intent intent = new Intent(mContext, ShortMessUploadActivity.class);
                intent.putExtra("msg_Id", httpPersonInfo.getId());
                intent.putExtra("realName", httpPersonInfo.getRealName());
                intent.putExtra("org", httpPersonInfo.getOrgName());
                intent.putExtra("bdNum",httpPersonInfo.getDeviceNumbers());
                startActivity(intent);
            }
        });
        mAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                HttpPersonInfo httpPersonInfo = (HttpPersonInfo) adapter.getItem(position);
                showPopWindows(view,httpPersonInfo,position);
                return false;
            }
        });
    }

    private PopupWindowList mPopupWindowList;
    private void showPopWindows(View view, final HttpPersonInfo httpPersonInfo,final int itemPosition){
        List<String> dataList = new ArrayList<>();
            dataList.add("删除该聊天");

        if (mPopupWindowList == null){
            mPopupWindowList = new PopupWindowList(view.getContext());
        }
        mPopupWindowList.setAnchorView(view);
        mPopupWindowList.setItemData(dataList);
        mPopupWindowList.setModal(true);
        mPopupWindowList.show();
        mPopupWindowList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPopupWindowList.hide();
                final ShowAlertDialog dialog = new ShowAlertDialog(getActivity());
                dialog.show();
                dialog.setContent("删除后,将清空该聊天的消息记录");
                dialog.setListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(v.getId() == R.id.tv_sure){
                            mAdapter.remove(itemPosition);
                            CommonDBOperator.deleteByKeys(mMesgInfoDao,"ID",String.valueOf(httpPersonInfo.getId()));
                            if(mAdapter.getData().size() == 0){
                                tvNoMessage.setVisibility(View.VISIBLE);
                            }else {
                                tvNoMessage.setVisibility(View.GONE);
                            }

                        }
                        dialog.dismiss();
                    }
                });

            }
        });
    }

    /**
     * 按时间排列
     * @param list
     */
    private void sortByTimeCurrentToLast(List<HttpPersonInfo> list) {
        Collections.sort(list, new Comparator<HttpPersonInfo>() {
            @Override
            public int compare(HttpPersonInfo lhs, HttpPersonInfo rhs) {
                if(lhs.getCurrentMsgTime()>rhs.getCurrentMsgTime()){
                    return -1;
                }else if(lhs.getCurrentMsgTime()<rhs.getCurrentMsgTime()){
                    return 1;
                }else {
                    return 0;
                }
            }
        });

    }


    /**
     * 刷新最近联系人
     */
    private void updateRecentContactList() {
        //先清数据
        mPersonInfoList.clear();
        mMessageInfoIBeanList.clear();
        mHashMap.clear();

            //1.再获取短消息数据
        mMessageInfoIBeanList= CommonDBOperator.getList(mMesgInfoDao);
        if(mMessageInfoIBeanList !=null && mMessageInfoIBeanList.size() > 0){
          //原来查出的数据就是升序排列的  所以你无须排序
            tvNoMessage.setVisibility(View.GONE);
            //2.把消息的所属人id  和 最新消息的时间放入到 hashMap 去重
            for (int i = 0; i < mMessageInfoIBeanList.size(); i++) {
                mHashMap.put( mMessageInfoIBeanList.get(i).getID(),mMessageInfoIBeanList.get(i).getTime());
            }
            //LinkedHashMap  遍历时会按照加入时的顺序输出  而HashMap不会
            Iterator<Map.Entry<Integer, Long>> entries = mHashMap.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry<Integer, Long> entry = entries.next();
               // System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
                //3.根据hashMap的key查所有的人表的信息,并设置最近消息的时间到bean
                List<HttpPersonInfo> list = CommonDBOperator.queryByKeys(mHttpPerDao, "id", entry.getKey() + "");
                HttpPersonInfo info = list.get(0);
                //根据消息关联人的ID 和state  查未读消息数
                Map<String, String> msgMap = new HashMap<>();
                msgMap.put("ID", entry.getKey()+ "");
                msgMap.put("state", Constants.MESSAGE_UNREAD + "");
                int count = (int)CommonDBOperator.countByMultiKeys(mMesgInfoDao, msgMap);
                info.setUnReadMsgNum(count);//此人的未读消息数
                info.setCurrentMsgTime(entry.getValue());//设置最近消息时间
                mPersonInfoList.add(info);
            }
            //4.展示bean到列表
            LogUtil.d("最近联系人个数=="+mPersonInfoList.size());
            //排序
            sortByTimeCurrentToLast(mPersonInfoList);
            mAdapter.setNewData(mPersonInfoList);
    }else {
            tvNoMessage.setVisibility(View.VISIBLE);
        }

}


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }



    @Subscribe(threadMode= ThreadMode.MAIN)
    public void getEventBus(EventBusBean eventBusBean){
        if(eventBusBean!=null){
            switch (eventBusBean.getCode()){
                case Constants.EVENT_CODE_REFRESH_MSG_LIST://刷新最近联系人列表
                    LogUtil.d("eventBus 刷新列表");
                    updateRecentContactList();
                    break;
            }
        }
    }

}
