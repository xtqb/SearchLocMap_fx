package com.lhzw.searchlocmap.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.lhzw.searchlocmap.R;
import com.lhzw.searchlocmap.constants.Constants;
import com.lhzw.searchlocmap.constants.SPConstants;
import com.lhzw.searchlocmap.net.SLMRetrofit;
import com.lhzw.searchlocmap.utils.SpUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class NetSettingActivity extends Activity{

    private Unbinder mUnbinder;
    @BindView(R.id.et_ip)
    protected EditText et_ip;
    @BindView(R.id.et_com)
    protected EditText et_com;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_setting);
        mUnbinder = ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        et_ip.setText(SpUtils.getString(SPConstants.NET_BASE_IP, Constants.BASE_IP_DEF));
        et_com.setText(SpUtils.getString(SPConstants.NET_BASE_COM, Constants.BASE_COM_DEF));
    }

    @OnClick({R.id.im_set_time_back, R.id.tv_commit})
    protected void onClick(View view) {
        switch (view.getId()) {
            case R.id.im_set_time_back:
                this.finish();
                break;
            case R.id.tv_commit:
                String ip = et_ip.getText().toString();
                String com = et_com.getText().toString();
                if(ip != null && !"".equals(ip) && com != null && !"".equals(com)) {
                    SpUtils.putString(SPConstants.NET_BASE_IP, ip);
                    SpUtils.putString(SPConstants.NET_BASE_COM, com);
                    SLMRetrofit.resetInstance();
                    this.finish();
                } else {
                    Toast.makeText(this, "请检查IP或者端口号是否录入", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }
}
