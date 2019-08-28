package com.lhzw.searchlocmap.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.lhzw.searchlocmap.R;
import com.lhzw.searchlocmap.bean.PlotItemInfo;
import com.lhzw.searchlocmap.constants.Constants;
import com.lhzw.searchlocmap.db.dao.CommonDBOperator;
import com.lhzw.searchlocmap.db.dao.DatabaseHelper;
import com.lhzw.searchlocmap.view.ShowMesureDisDialog;

import java.util.LinkedList;
import java.util.List;

/**
 * description：可实现懒加载的fragment - 若把初始化内容放到initData实现,就是采用Lazy方式加载的Fragment
 * 若不需要Lazy加载则initData方法内留空,初始化内容放到initViews即可 -注1:
 * 如果是与ViewPager一起使用，调用的是setUserVisibleHint。
 * ------可以调用mViewPager.setOffscreenPageLimit(size),若设置了该属性
 * 则viewpager会缓存指定数量的Fragment -注2:
 * 如果是通过FragmentTransaction的show和hide的方法来控制显示，调用的是onHiddenChanged. -注3:
 * 针对初始就show的Fragment 为了触发onHiddenChanged事件 达到lazy效果 需要先hide再show
 */
public abstract class BaseFragment extends Fragment implements View.OnClickListener {
    private Toast mGlobalToast;
    protected boolean isSosFlash = false;
    private SoilderInfoChangeReceiver receiver;
    private Dao<PlotItemInfo, Integer> syncDao;
    private Toast searchToast;
    private TextView tv_search_content;

    protected boolean isVisible;// 是否可见状态
    private boolean isPrepared;// 标志位，View已经初始化完成。
    private boolean isFirstLoad = true;// 是否第一次加载
    private boolean isReflesh = false;
    protected Context mContext;
    protected View view;

    private List<Intent> taskQueue = new LinkedList<>();
    private boolean isRunning;
    private Handler taskHandler;

    protected abstract void initSoilderInfoList();

    protected abstract void drawSyncFireLine();

    protected abstract void initMessageNum();

    protected abstract void updateFeedback(int sendID);

    protected abstract void refleshBdSignal(Intent intent);

    private ShowMesureDisDialog mesureDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        isFirstLoad = true;
        view = inflater.inflate(initView(), container, false);
        isPrepared = true;
        lazyLoad();
        return view;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        try {
            if (receiver != null && getActivity() != null) {
                getActivity().unregisterReceiver(receiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showToast(String text) {
        if (mGlobalToast == null) {
            mGlobalToast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
            mGlobalToast.show();
        } else {
            mGlobalToast.setText(text);
            mGlobalToast.setDuration(Toast.LENGTH_SHORT);
            mGlobalToast.show();
        }
    }

    protected void showMesureDistantNote() {
        mesureDialog = new ShowMesureDisDialog(getActivity());
        mesureDialog.show();
        mesureDialog.setListener(BaseFragment.this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_input_distance:
                mesureDialog.dismiss();
                break;
            case R.id.tv_click_mesure:
                mesureDialog.dismiss();
                break;
        }
    }

    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.lhzw.soildersos.change");
        filter.addAction(Constants.ACTION_SYNC_FIRELINE);
        filter.addAction(Constants.BD_Mms_ACTION);
//        filter.addAction(Constants.ACTION_FEEDBACK);
        filter.addAction(Constants.BD_SIGNAL_LIST);
        receiver = new SoilderInfoChangeReceiver();
        getActivity().registerReceiver(receiver, filter);
    }

    private class SoilderInfoChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (isVisible && intent.getAction().equals("com.lhzw.soildersos.change")) {
                initSoilderInfoList();
            } else if (intent.getAction().equals(Constants.ACTION_SYNC_FIRELINE)) {
                taskQueue.add(intent);
            } else if (isVisible && intent.getAction().equals(Constants.BD_Mms_ACTION)) {
                initMessageNum();
            } else if (isVisible && intent.getAction().equals(Constants.ACTION_FEEDBACK)) {
//                updateFeedback(intent.getIntExtra("sendID", -1));
            } else if (isVisible && !isRunning && intent.getAction().equals(Constants.BD_SIGNAL_LIST)) {
                taskQueue.add(intent);
            }
            if (!isRunning) {
                doTask();
            }
        }
    }

    private void doTask() {
        if (!isRunning && !taskQueue.isEmpty()) {
            isRunning = true;
            taskHandler.post(new Action());
        }
    }


    private class Action implements Runnable {
        @Override
        public void run() {
            final Intent intent = taskQueue.get(0);
            taskQueue.remove(0);
            if (intent.getAction().equals(Constants.ACTION_SYNC_FIRELINE)) {
                String fireLine = intent.getStringExtra("fireLine");
                long time = intent.getLongExtra("time", System.currentTimeMillis());
                String num = intent.getStringExtra("num");
//                int tx_type = intent.getIntExtra("tx_type", Constants.TX_JZH);
                DatabaseHelper helper = DatabaseHelper.getHelper(getActivity());
                syncDao = helper.getPlotItemDao();
                Log.e("Tag", "receive sync fire line countent : " + fireLine);
                PlotItemInfo plot = new PlotItemInfo("", -1, time, fireLine, Constants.TX_SYNCFL, 0, Constants.TX_JZH, Constants.UPLOAD_STATE_ON);
                CommonDBOperator.saveToDB(syncDao, plot);
                //绘制下发火线
                if (isVisible && getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            drawSyncFireLine();
                        }
                    });
                }
            } else if (intent.getAction().equals(Constants.BD_Mms_ACTION)) {
                initMessageNum();
            } else if (intent.getAction().equals(Constants.ACTION_FEEDBACK)) {
//                updateFeedback(intent.getIntExtra("sendID", -1));
            } else if (intent.getAction().equals(Constants.BD_SIGNAL_LIST)) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refleshBdSignal(intent);
                        }
                    });
                }
            }
            isRunning = false;
        }
    }


    /**
     * 69      * 将Toast封装在一个方法中，在其它地方使用时直接输入要弹出的内容即可
     * 70
     */
    protected void showSearchToast(String content) {
        //LayoutInflater的作用：对于一个没有被载入或者想要动态载入的界面，都需要LayoutInflater.inflate()来载入，LayoutInflater是用来找res/layout/下的xml布局文件，并且实例化
        if (searchToast == null) {
            LayoutInflater inflater = getActivity().getLayoutInflater();//调用Activity的getLayoutInflater()
            View view = inflater.inflate(R.layout.toast_search_end, null); //加載layout下的布局
            tv_search_content = (TextView) view.findViewById(R.id.tv_search_content);
            searchToast = new Toast(getActivity());
            searchToast.setGravity(Gravity.CENTER, 0, -400);//setGravity用来设置Toast显示的位置，相当于xml中的android:gravity或android:layout_gravity
            searchToast.setDuration(Toast.LENGTH_LONG);//setDuration方法：设置持续时间，以毫秒为单位。该方法是设置补间动画时间长度的主要方法
            searchToast.setView(view); //添加视图文件
        }
        tv_search_content.setText(content);
        searchToast.show();
    }

    /**
     * 如果是与ViewPager一起使用，调用的是setUserVisibleHint
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    /**
     * 如果是通过FragmentTransaction的show和hide的方法来控制显示，调用的是onHiddenChanged.
     * 若是初始就show的Fragment 为了触发该事件 需要先hide再show
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    protected void onVisible() {
        lazyLoad();
        if (isReflesh && !isFirstLoad) {
            onReflesh();
        } else {
            isReflesh = true;
        }
    }

    protected void onInvisible() {
    }

    protected void lazyLoad() {
        if (!isPrepared || !isVisible || !isFirstLoad) {
            return;
        }
        isFirstLoad = false;
        isRunning = false;
        HandlerThread thread = new HandlerThread("doTaskThread");
        thread.start();
        taskHandler = new Handler(thread.getLooper());
        registerBroadcastReceiver();
        initData();
    }

    /**
     * 初始化contentView
     *
     * @return 返回contentView的layout id
     */
    protected abstract int initView();

    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * 显示刷新
     */

    protected abstract void onReflesh();


}
