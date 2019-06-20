package com.lhzw.searchlocmap.ui;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.lhzw.searchlocmap.view.LoadingView;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {

    InputMethodManager manager;
    protected Context mContext;

    protected LoadingView mLoadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        // 竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 进入界面后不让软键盘弹出
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //初始化布局
        setContentView(initView());
        //绑定黄油刀
        ButterKnife.bind(this);
        initData();
        mLoadingView = new LoadingView(mContext);
    }


    protected abstract int initView();//初始化xml布局
    protected abstract void initData();//初始化数据
    protected void showLoading(){
        showLoading("加载中...");
    }

    protected void showLoading(String loadingTitle){
        if (mLoadingView != null){
            mLoadingView.setLoadingTitle(loadingTitle);
            mLoadingView.show();
        }
    }

    protected void dismissLoading(){
        if (mLoadingView != null){
            mLoadingView.dismiss();
        }
    }
}
