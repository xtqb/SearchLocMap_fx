package com.lhzw.searchlocmap.ui;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lhzw.searchlocmap.R;
import com.lhzw.searchlocmap.fragment.BindingWatchFragment;
import com.lhzw.searchlocmap.fragment.UnBingingWatchFragment;
import com.lhzw.searchlocmap.view.PageViewChange;

import java.util.ArrayList;
import java.util.List;

public class BindingWatchActivity extends FragmentActivity implements View.OnClickListener {

    private TextView tv_binding;
    private TextView tv_unbinding;
    private ImageView iv_binding;
    private ImageView iv_unbinding;
    private boolean isState;
    private List<Fragment> list;
    private PageViewChange viewpager;
    private UnBingingWatchFragment unbingding;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//强制竖屏
        setContentView(R.layout.activity_binding_watch);
        initView();
        initData();
        setListener();
    }

    private void setListener() {

    }

    private void initData() {
        list = new ArrayList<Fragment>();

        BindingWatchFragment binding = new BindingWatchFragment();
        unbingding = new UnBingingWatchFragment();
        list.add(unbingding);
        list.add(binding);
        viewpager.setAdapter(adapter);
        viewpager.setOffscreenPageLimit(1);
    }

    private void initView() {
        viewpager = (PageViewChange) findViewById(R.id.viewpager);
        LinearLayout ll_bingding = (LinearLayout) findViewById(R.id.ll_bingding);
        LinearLayout ll_unbingding = (LinearLayout) findViewById(R.id.ll_unbingding);
        ll_bingding.setOnClickListener(this);
        ll_unbingding.setOnClickListener(this);

        tv_binding = (TextView) findViewById(R.id.tv_binding);
        tv_unbinding = (TextView) findViewById(R.id.tv_unbinding);

        iv_binding = (ImageView) findViewById(R.id.iv_binding);
        iv_unbinding = (ImageView) findViewById(R.id.iv_unbinding);
        isState = false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_bingding: {
                if (!isState) {
                    tv_binding.setTextColor(getColor(R.color.green_dark));
                    tv_unbinding.setTextColor(getColor(R.color.gray2));
                    iv_binding.setBackgroundResource(R.drawable.icon_bingding_press);
                    iv_unbinding.setBackgroundResource(R.drawable.icon_unbingding_def);
                    isState = !isState;
                    viewpager.setCurrentItem(1);
                }
                break;
            }

            case R.id.ll_unbingding: {
                if (isState) {
                    tv_binding.setTextColor(getColor(R.color.gray2));
                    tv_unbinding.setTextColor(getColor(R.color.green_dark));
                    iv_binding.setBackgroundResource(R.drawable.icon_bingding_def);
                    iv_unbinding.setBackgroundResource(R.drawable.icon_unbingding_pre);
                    isState = !isState;
                    viewpager.setCurrentItem(0);
                }
                break;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(unbingding != null) {
                unbingding.onKeyDown();
            }
        }
        return false;
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
    }
}