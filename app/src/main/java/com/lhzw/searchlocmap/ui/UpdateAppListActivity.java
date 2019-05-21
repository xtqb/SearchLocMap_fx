package com.lhzw.searchlocmap.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.lhzw.searchlocmap.R;

/**
 * Created by xtqb on 2019/4/26.
 */
public class UpdateAppListActivity extends Activity implements View.OnClickListener {

    private RelativeLayout rl_search_map;
    private RelativeLayout rl_upload_service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_list);
        initView();
        initData();
        setListener();
    }

    private void setListener() {
        rl_search_map.setOnClickListener(this);
        rl_upload_service.setOnClickListener(this);
    }

    private void initData() {

    }

    private void initView() {
        rl_search_map = (RelativeLayout) findViewById(R.id.rl_search_map);
        rl_upload_service = (RelativeLayout) findViewById(R.id.rl_upload_service);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_search_map:
                startActivity(new Intent(this, UpdateSearchMapActivity.class));
                break;
            case R.id.rl_upload_service:
                startActivity(new Intent(this, UpdateUploadServiceActivity.class));
                break;
        }
    }
}
