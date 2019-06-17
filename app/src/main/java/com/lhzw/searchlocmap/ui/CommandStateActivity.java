package com.lhzw.searchlocmap.ui;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;
import com.lhzw.searchlocmap.R;
import com.lhzw.searchlocmap.adapter.CommandStateAdapter;
import com.lhzw.searchlocmap.bean.PersonalInfo;
import com.lhzw.searchlocmap.constants.Constants;
import com.lhzw.searchlocmap.db.dao.CommonDBOperator;
import com.lhzw.searchlocmap.db.dao.DatabaseHelper;
import com.lhzw.searchlocmap.event.EventBusBean;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by hecuncun on 2019/5/31
 * <p>
 * 指令反馈页
 */
public class CommandStateActivity extends BaseActivity {
    @BindView(R.id.rv_command_state)
    RecyclerView mRvCommandState;
    @BindView(R.id.tv_no_person)
    TextView tvNoPerson;
    private List<PersonalInfo> mPersonalInfos=new ArrayList<>();
    private CommandStateAdapter mAdapter;
    private DatabaseHelper mHelper;
    private Dao mInfoDao;

    @Override
    protected int initView() {
        return R.layout.activity_command_state;
    }

    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
        mHelper = DatabaseHelper.getHelper(this);
        mInfoDao = mHelper.getPersonalInfoDao();

        //接收到消息反馈展示到页面上
        initCommandRv();
        updateCommandState();

    }

    private void updateCommandState() {
        mPersonalInfos.clear();
        mPersonalInfos = CommonDBOperator.getList(mInfoDao);//查询数据库
        if(mPersonalInfos != null && mPersonalInfos.size()>0){
            sortByCommandReceive(mPersonalInfos);
            mAdapter.setNewData(mPersonalInfos);
            tvNoPerson.setVisibility(View.GONE);
        }else {
            tvNoPerson.setVisibility(View.VISIBLE);
        }

    }

    private void initCommandRv() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRvCommandState.setLayoutManager(linearLayoutManager);
        mRvCommandState.setHasFixedSize(true);
        mRvCommandState.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new CommandStateAdapter(mPersonalInfos);
        mRvCommandState.setAdapter(mAdapter);
    }
    /**
     * 按状态排序
     * @param list
     */
    private void sortByCommandReceive(List<PersonalInfo> list) {
        Collections.sort(list, new Comparator<PersonalInfo>() {
            @Override
            public int compare(PersonalInfo lhs, PersonalInfo rhs) {
                if(lhs.getFeedback()>rhs.getFeedback()){
                    return -1;
                }else if(lhs.getFeedback()<rhs.getFeedback()){
                    return 1;
                }else {
                    return 0;
                }
            }
        });

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBus(EventBusBean eventBusBean) {
        switch (eventBusBean.getCode()) {
            case Constants.EVENT_CODE_REFRESH_COMMAND_STATE://刷新列表
                 updateCommandState();
                break;
        }
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }
}
