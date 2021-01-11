package com.lhzw.searchlocmap.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BDManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lhzw.searchlocmap.R;
import com.lhzw.searchlocmap.application.SearchLocMapApplication;
import com.lhzw.searchlocmap.bean.BaseBean;
import com.lhzw.searchlocmap.constants.Constants;
import com.lhzw.searchlocmap.constants.SPConstants;
import com.lhzw.searchlocmap.fragment.PersManagerFragment;
import com.lhzw.searchlocmap.fragment.SecurityFragment;
import com.lhzw.searchlocmap.fragment.SettingFragment;
import com.lhzw.searchlocmap.net.CallbackListObserver;
import com.lhzw.searchlocmap.net.SLMRetrofit;
import com.lhzw.searchlocmap.net.ThreadSwitchTransformer;
import com.lhzw.searchlocmap.utils.BaseUtils;
import com.lhzw.searchlocmap.utils.ComUtils;
import com.lhzw.searchlocmap.utils.LogUtil;
import com.lhzw.searchlocmap.utils.SpUtils;
import com.lhzw.searchlocmap.utils.ToastUtil;
import com.lhzw.searchlocmap.view.PageViewChange;
import com.lhzw.searchlocmap.view.ShowAlertDialog;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

public class MainActivity extends FragmentActivity implements View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback {
    private PageViewChange viewPager;
    private List<Fragment> list;
    private int currentPage = 0;
    private int tabLineLength;// 1/3屏宽
    private TextView security_tv;
    private TextView manageper_tv;
    private TextView setting_tv;
    private ImageView tabline;
    private LinearLayout.LayoutParams line;
    private ViewGroup.LayoutParams lp;
    private ImageView security_im;
    private ImageView persons_im;
    private ImageView settings_im;
    private final int SUCCESS = 0;
    private final int REQUESTCODE = 0x2345;
    private final int FAIL = -1;
    private String TAG = "Tag";
    private ShowAlertDialog alertdialog;
    private AlerDialogshow dialog;
    private BDManager mBDManager;
    private PowerManager.WakeLock wakeLock;
    private SettingFragment settingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initTabLine();
        initView();
        initData();
        setListener();
        initBDChangeCheck();
        initWakeLock();
    }

    @SuppressLint("InvalidWakeLockTag")
    private void initWakeLock(){
        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyTAG");
        wakeLock.acquire();
    }

    /**
     * 每次打开APP就判定北斗号是否发生了改变   改变就通知后台
     */
    private Intent mIntent;

    private void initBDChangeCheck() {
        LogUtil.e("MAC="+BaseUtils.getMacFromHardware());
//        String bdNum = BaseUtils.getDipperNum(this);
        String bdNum = "12345678";
        LogUtil.e("初始化本机北斗号=="+bdNum);
        //本机北斗号为空  不处理
        if(TextUtils.isEmpty(bdNum)){
            ToastUtil.showToast("北斗号为空,请安装北斗卡,并打开北斗开关");
            return;
        }
        //不为空  检测是否变更

        if(!SpUtils.getString(Constants.BD_NUM_lOC_DEF,"").equals(bdNum)){
            LogUtil.e("检测到北斗号发生改变了,原北斗号=="+SpUtils.getString(Constants.BD_NUM_lOC_DEF,"")+",新北斗号=="+bdNum);
            //发生了改变     更新本地的储存     通知后台和北斗
            SpUtils.putString(Constants.BD_NUM_lOC_DEF,bdNum);
            mIntent = new Intent(Constants.BD_NUM_ISCHANGING);
            //判断有无网络
            if(BaseUtils.isNetConnected(this)) {
                //有网络  通知服务器北斗号
                Observable<BaseBean> observable = SLMRetrofit.getInstance().getApi().uploadMacAndBdNum(BaseUtils.getMacFromHardware(), bdNum);
                observable.compose(new ThreadSwitchTransformer<BaseBean>())
                        .subscribe(new CallbackListObserver<BaseBean>() {
                            @Override
                            protected void onSucceed(BaseBean bean) {
                                if("0".equals(bean.getCode())){
                                    //上传成功
                                    ToastUtil.showToast("北斗卡切换成功");
                                    mIntent.putExtra("state",0);
                                    sendBroadcast(mIntent);
                                }else {
                                    ToastUtil.showToast("北斗卡切换失败,正在尝试使用北斗通信服务上传");
                                    mIntent.putExtra("state",1);
                                    sendBroadcast(mIntent);
                                }
                            }

                            @Override
                            protected void onFailed() {
                                ToastUtil.showToast("北斗卡切换失败,正在尝试使用北斗通信服务上传");
                                mIntent.putExtra("state",1);
                                sendBroadcast(mIntent);
                            }
                        });
            }else {
                //无网络  上传 北斗服务
                LogUtil.e("打开北斗服务");
//                Handler handler = new Handler();
//                //延时5s 打开服务
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        Intent intent1 = new Intent("com.lhzw.intent.action_UPLOAD_SERVICE");
//                        intent1.putExtra("state", 1);
//                        MainActivity.this.startActivity(intent1);
//                    }
//                },5000);
                mIntent.putExtra("state",1);
                sendBroadcast(mIntent);

            }
        }else {
            //ToastUtil.showToast("检测到本机北斗号未改变");
            LogUtil.e("检测到本机北斗号未改变");
        }
    }

    @Override
    protected void onResume() {
        SearchLocMapApplication.getInstance().bindService();
        super.onResume();
        if (SpUtils.getBoolean(SPConstants.ISJUMP, false)) {
            if (Settings.Global.getInt(getContentResolver(), Settings.Global.BD_MODE_ON, 0) == 0 &&
                    Settings.Global.getInt(getContentResolver(), Settings.Global.BD_INLINK_MODE_ON, 0) == 0 &&
                    Settings.Global.getInt(getContentResolver(), Settings.Global.BD_OUTLINK_MODE_ON, 0) == 0) {
//                SpUtils.putBoolean(SPConstants.AUTO_REPORT, false); //北斗开关已经打开
            } else {
                if (BaseUtils.isStringEmpty(BaseUtils.getDipperNum(this))) {
                    Toast.makeText(this, getString(R.string.dipper_card_check_note), Toast.LENGTH_LONG).show();
//                    SpUtils.putBoolean(SPConstants.AUTO_REPORT, false);
                }
            }
            SpUtils.putBoolean(SPConstants.ISJUMP, false);
        }
//        mBDManager.systemCheck(6 + "");
//        mBDManager.queryBDPower("6");
       // LogUtil.e("mac = "+BaseUtils.getMacFromHardware());
    }

    private void initTabLine() {
        Display display = getWindow().getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        tabLineLength = metrics.widthPixels / 3;
        tabline = (ImageView) findViewById(R.id.tabline);
        lp = tabline.getLayoutParams();
        lp.width = tabLineLength;
        tabline.setLayoutParams(lp);
    }

    private void initView() {
        viewPager = (PageViewChange) findViewById(R.id.viewpager);
        viewPager.setScanScroll(false);
        viewPager.setOffscreenPageLimit(2);
        security_tv = (TextView) findViewById(R.id.security);
        manageper_tv = (TextView) findViewById(R.id.manageper);
        setting_tv = (TextView) findViewById(R.id.setting);

        security_im = (ImageView) findViewById(R.id.security_im);
        persons_im = (ImageView) findViewById(R.id.persons_im);
        settings_im = (ImageView) findViewById(R.id.settings_im);

        LinearLayout ll_security = (LinearLayout) findViewById(R.id.ll_security);
        LinearLayout ll_persons = (LinearLayout) findViewById(R.id.ll_persons);
        LinearLayout ll_settings = (LinearLayout) findViewById(R.id.ll_settings);
        list = new ArrayList<Fragment>();

        ll_security.setOnClickListener(this);
        ll_persons.setOnClickListener(this);
        ll_settings.setOnClickListener(this);
        line = (LinearLayout.LayoutParams) tabline.getLayoutParams();

        SecurityFragment fragment1 = new SecurityFragment();
        PersManagerFragment fragment2 = new PersManagerFragment();
        settingFragment = new SettingFragment();

        SpUtils.putBoolean(SPConstants.PERSON_ENTER, false);
        list.add(fragment1);
        list.add(fragment2);
        list.add(settingFragment);

        viewPager.setAdapter(adapter);
    }

    @SuppressLint("WrongConstant")
    private void initData() {
//        mBDManager = (BDManager) getSystemService(Context.BD_SERVICE);
        ComUtils.getInstance().registerBroadcastReceiver();
        if (SpUtils.getBoolean(SPConstants.AUTO_REPORT, true)) {
            if (Settings.Global.getInt(getContentResolver(), Settings.Global.BD_MODE_ON, 0) == 0 &&
                    Settings.Global.getInt(getContentResolver(), Settings.Global.BD_INLINK_MODE_ON, 0) == 0 &&
                    Settings.Global.getInt(getContentResolver(), Settings.Global.BD_OUTLINK_MODE_ON, 0) == 0) {// 北斗定位没开
                alertdialog = new ShowAlertDialog(MainActivity.this);
                alertdialog.show();
                alertdialog.setContent(getString(R.string.dipper_auto_upload_switch_note));
                alertdialog.setListener(this);
                SpUtils.getBoolean(SPConstants.ISJUMP, true);
            } else {
                if (BaseUtils.isStringEmpty(BaseUtils.getDipperNum(this))) {
                    Toast.makeText(this, getString(R.string.dipper_card_check_note), Toast.LENGTH_LONG).show();
//                    SpUtils.putBoolean(SPConstants.AUTO_REPORT, false);
                }
            }
        }
    }

    private void setListener() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_security:
                if (currentPage == 0) {
                    return;
                }
                viewPager.setCurrentItem(0);
                currentPage = 0;
                line.leftMargin = 0;
                tabline.setLayoutParams(line);

                security_tv.setTextColor(getResources().getColor(R.color.green_dark));
                manageper_tv.setTextColor(Color.GRAY);
                setting_tv.setTextColor(Color.GRAY);

                security_im.setBackgroundResource(R.drawable.people_pre);
                persons_im.setBackgroundResource(R.drawable.jiankong_def);
                settings_im.setBackgroundResource(R.drawable.icon_setting_def);

                break;

            case R.id.ll_persons:
                if (currentPage == 1) {
                    return;
                }
                viewPager.setCurrentItem(1);
                security_tv.setTextColor(Color.GRAY);
                manageper_tv.setTextColor(getResources().getColor(R.color.green_dark));
                setting_tv.setTextColor(Color.GRAY);

                currentPage = 1;
                line.leftMargin = tabLineLength;
                tabline.setLayoutParams(line);

                persons_im.setBackgroundResource(R.drawable.jiankong_pre);
                security_im.setBackgroundResource(R.drawable.people_def);
                settings_im.setBackgroundResource(R.drawable.icon_setting_def);

                break;
            case R.id.ll_settings:
                if (currentPage == 2) {
                    return;
                }
                viewPager.setCurrentItem(2);
                security_tv.setTextColor(Color.GRAY);
                manageper_tv.setTextColor(Color.GRAY);
                setting_tv.setTextColor(getResources().getColor(R.color.green_dark));

                currentPage = 2;
                line.leftMargin = 2 * tabLineLength;
                tabline.setLayoutParams(line);

                settings_im.setBackgroundResource(R.drawable.icon_setting_pre);
                persons_im.setBackgroundResource(R.drawable.jiankong_def);
                security_im.setBackgroundResource(R.drawable.people_def);
                break;
            case R.id.tv_cancel:
                alertdialog.dismiss();
                alertdialog = null;
                if (Settings.Global.getInt(getContentResolver(), Settings.Global.BD_MODE_ON, 0) != 1) {
//                    SpUtils.putBoolean(SPConstants.AUTO_REPORT, false);
                }
                break;
            case R.id.tv_sure:
                // 开启北斗
//                Intent intent_BDSetting = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
                Intent intent_BDSetting = new Intent("android.settings.action.DIPPER");
                startActivity(intent_BDSetting);
                SpUtils.putBoolean(SPConstants.ISJUMP, true);
                alertdialog.dismiss();
                alertdialog = null;
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            dialog = new AlerDialogshow(MainActivity.this);
            dialog.show();
            dialog.setContent(getString(R.string.security_system_back_note));
            dialog.setListener();
            return true;
        }
        return false;
    }

    private class AlerDialogshow extends AlertDialog implements View.OnClickListener {
        private TextView tv_content;
        private TextView tv_cancel;
        private TextView tv_sure;

        public AlerDialogshow(Context context) {
            super(context);
            // TODO Auto-generated constructor stub
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            // TODO Auto-generated method stub
            super.onCreate(savedInstanceState);
            setContentView(R.layout.dialog_show_note);
            init();
        }

        private void init() {
            setCancelable(false);
            tv_content = (TextView) findViewById(R.id.tv_content);
            tv_cancel = (TextView) findViewById(R.id.tv_cancel);
            tv_sure = (TextView) findViewById(R.id.tv_sure);
        }

        public void setListener() {
            tv_cancel.setOnClickListener(AlerDialogshow.this);
            tv_sure.setOnClickListener(AlerDialogshow.this);
        }

        public void setContent(String content) {
            tv_content.setText(content);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_cancel:
                    dialog.dismiss();
                    dialog = null;
                    break;
                case R.id.tv_sure:
                    // 主动搜救
                    dialog.dismiss();
                    dialog = null;
                    MainActivity.this.finish();
                    break;
            }
        }
    }

    private FragmentPagerAdapter adapter = new FragmentPagerAdapter(
            getSupportFragmentManager()) {

        @Override
        public Fragment getItem(int arg0) {
            // TODO Auto-generated method stub
            return list.get(arg0);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
        }
    };

    // 刷新设置统计
    public void refleshStatistics(){
        if(settingFragment != null) {
            settingFragment.refleshStatistics();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ComUtils.getInstance().unRegisterBroadcastReceiver();
        if(wakeLock != null) {
            wakeLock.release();
        }
    }
}
