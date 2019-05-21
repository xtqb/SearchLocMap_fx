package com.lhzw.searchlocmap.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.lhzw.searchlocmap.R;

public class UploadPersonInfoActivity extends Activity implements
		OnClickListener {
	private ImageView report_back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload_perinfo);
		initView();
		initData();
		setListener();
	}

	private void initView() {
		report_back = (ImageView) findViewById(R.id.report_back);
	}

	private void initData() {
		// TODO Auto-generated method stub

	}

	private void setListener() {
		// TODO Auto-generated method stub
		report_back.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.report_back:
			this.finish();
			break;

		default:
			break;
		}
	}
}
