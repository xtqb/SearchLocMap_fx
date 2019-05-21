package com.lhzw.searchlocmap.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.lhzw.searchlocmap.R;
import com.lhzw.searchlocmap.application.SearchLocMapApplication;
import com.lhzw.searchlocmap.constants.Constants;
import com.lhzw.searchlocmap.constants.SPConstants;
import com.lhzw.searchlocmap.utils.SpUtils;

public class BDSettingActivity extends Activity implements
        OnClickListener, RadioGroup.OnCheckedChangeListener {
    private RadioGroup rg_bd;
    private RadioButton rb_jzh;
    private RadioButton rb_jqzh;
    private ImageView im_bd_back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bd_setting);
        initView();
        initData();
        setListener();
    }

    private void initView() {
        im_bd_back = (ImageView) findViewById(R.id.im_bd_back);
        rg_bd = (RadioGroup) findViewById(R.id.rg_bd);
        rb_jzh = (RadioButton) findViewById(R.id.rb_jzh);
        rb_jqzh = (RadioButton) findViewById(R.id.rb_jqzh);
    }

    private void initData() {
        // TODO Auto-generated method stub
        switch (SpUtils.getInt(SPConstants.SP_BD_MODE, Constants.UOLOAD_STATE_0)) {
            case Constants.UOLOAD_STATE_0:
                rb_jzh.setChecked(true);
                break;
            case Constants.UOLOAD_STATE_1:
                rb_jqzh.setChecked(true);
                break;
        }
    }

    private void setListener() {
        // TODO Auto-generated method stub
        rg_bd.setOnCheckedChangeListener(this);
        im_bd_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.im_bd_back:
                this.finish();
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        try {
            switch (checkedId) {
                case R.id.rb_jzh:
                    SpUtils.putInt(SPConstants.SP_BD_MODE, Constants.UOLOAD_STATE_0);
                    SearchLocMapApplication.getInstance().getUploadService().setUploadState(Constants.UOLOAD_STATE_0);
                    break;
                case R.id.rb_jqzh:
                    SpUtils.putInt(SPConstants.SP_BD_MODE, Constants.UOLOAD_STATE_1);
                    SearchLocMapApplication.getInstance().getUploadService().setUploadState(Constants.UOLOAD_STATE_1);
                    break;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
