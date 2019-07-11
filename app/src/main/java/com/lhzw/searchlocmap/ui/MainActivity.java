package com.lhzw.searchlocmap.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BDManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.DisplayMetrics;
import android.util.Log;
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
import com.lhzw.searchlocmap.constants.Constants;
import com.lhzw.searchlocmap.constants.SPConstants;
import com.lhzw.searchlocmap.fragment.PersManagerFragment;
import com.lhzw.searchlocmap.fragment.SecurityFragment;
import com.lhzw.searchlocmap.fragment.SettingFragment;
import com.lhzw.searchlocmap.utils.BaseUtils;
import com.lhzw.searchlocmap.utils.ComUtils;
import com.lhzw.searchlocmap.utils.SpUtils;
import com.lhzw.searchlocmap.view.PageViewChange;
import com.lhzw.searchlocmap.view.ShowAlertDialog;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initTabLine();
        initView();
        initData();
        setListener();
    }

    @Override
    protected void onResume() {
        SearchLocMapApplication.getInstance().bindService();
        super.onResume();
        if (SpUtils.getBoolean(SPConstants.ISJUMP, false)) {
            if (Settings.Global.getInt(getContentResolver(), Settings.Global.BD_MODE_ON, 0) != 1) {
                SpUtils.putBoolean(SPConstants.AUTO_REPORT, false); //北斗开关已经打开
            } else {
                if (BaseUtils.isStringEmpty(BaseUtils.getDipperNum(this))) {
                    Toast.makeText(this, getString(R.string.dipper_card_check_note), Toast.LENGTH_LONG).show();
                    SpUtils.putBoolean(SPConstants.AUTO_REPORT, false);
                }
            }
            SpUtils.putBoolean(SPConstants.ISJUMP, false);
        }
//        mBDManager.systemCheck(6 + "");
        mBDManager.queryBDPower("6");
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
        SettingFragment fragment3 = new SettingFragment();

        SpUtils.putBoolean(SPConstants.PERSON_ENTER, false);
        list.add(fragment1);
        list.add(fragment2);
        list.add(fragment3);

        viewPager.setAdapter(adapter);
    }

    @SuppressLint("WrongConstant")
    private void initData() {
        mBDManager = (BDManager) getSystemService(Context.BD_SERVICE);
        ComUtils.getInstance().registerBroadcastReceiver();
        if (SpUtils.getBoolean(SPConstants.AUTO_REPORT, true)) {
            if (Settings.Global.getInt(getContentResolver(), Settings.Global.BD_MODE_ON, 0) != 1) {// 北斗定位没开
                alertdialog = new ShowAlertDialog(MainActivity.this);
                alertdialog.show();
                alertdialog.setContent(getString(R.string.dipper_auto_upload_switch_note));
                alertdialog.setListener(this);
                SpUtils.getBoolean(SPConstants.ISJUMP, true);
            } else {
                if (BaseUtils.isStringEmpty(BaseUtils.getDipperNum(this))) {
                    Toast.makeText(this, getString(R.string.dipper_card_check_note), Toast.LENGTH_LONG).show();
                    SpUtils.putBoolean(SPConstants.AUTO_REPORT, false);
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
                    SpUtils.putBoolean(SPConstants.AUTO_REPORT, false);
                }
                break;
            case R.id.tv_sure:
                // 开启北斗
                Intent intent_BDSetting = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ComUtils.getInstance().unRegisterBroadcastReceiver();
    }
}
