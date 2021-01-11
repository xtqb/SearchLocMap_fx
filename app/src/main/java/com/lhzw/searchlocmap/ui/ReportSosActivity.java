package com.lhzw.searchlocmap.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lhzw.searchlocmap.R;
import com.lhzw.searchlocmap.application.SearchLocMapApplication;
import com.lhzw.searchlocmap.constants.SPConstants;
import com.lhzw.searchlocmap.interfaces.OnDipperCancelListener;
import com.lhzw.searchlocmap.utils.BaseUtils;
import com.lhzw.searchlocmap.utils.SpUtils;
import com.lhzw.searchlocmap.view.ToggleButtonView;
import com.lhzw.searchlocmap.view.ToggleButtonView.onToggleClickListener;

public class ReportSosActivity extends Activity implements
		onToggleClickListener, OnClickListener {
	private ToggleButtonView anto_report_toggle;
	private Button report_back;
	private TextView custom_report;
	private TextView select_report;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sos_report);
		initView();
		setListener();
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
////				initData();
//				setListener();
//			}
//		}).start();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
//		setDipperState();
		anto_report_toggle.setSliderState(SpUtils.getBoolean(SPConstants.AUTO_REPORT, true));
	}

	private void initData() {
		SearchLocMapApplication.getInstance().setOnDipperCancelListener(
				cancelListener);
		anto_report_toggle.setSliderState(SpUtils.getBoolean(
				SPConstants.AUTO_REPORT, true));
		select_report.setEnabled(!SpUtils.getBoolean(SPConstants.AUTO_REPORT,
				true));
	}

	private void setDipperState() {
		if (SpUtils.getBoolean(SPConstants.ISJUMP, false)) {
			if (Settings.Global.getInt(getContentResolver(),
					Settings.Global.BD_MODE_ON, 0) != 1) {
				SpUtils.putBoolean(SPConstants.AUTO_REPORT, false);
				anto_report_toggle.setSliderState(SpUtils.getBoolean(
						SPConstants.AUTO_REPORT, true));
				select_report.setEnabled(!SpUtils.getBoolean(
						SPConstants.AUTO_REPORT, true));
			} else {
				if (BaseUtils.isStringEmpty(BaseUtils.getDipperNum(this))) {
					Toast.makeText(this,
							getString(R.string.dipper_card_check_note),
							Toast.LENGTH_SHORT).show();
					SpUtils.putBoolean(SPConstants.AUTO_REPORT, false);
					anto_report_toggle.setSliderState(false);
					select_report.setEnabled(true);
				} else {
					SearchLocMapApplication.getInstance().autoUploadStart(
							ReportSosActivity.this);
				}

			}
			SpUtils.putBoolean(SPConstants.ISJUMP, false);
		}
	}

	private void setListener() {
		anto_report_toggle.setOnToggleClickListener(this);
		report_back.setOnClickListener(this);
		select_report.setOnClickListener(this);
		custom_report.setOnClickListener(this);
	}

	private void initView() {
		// TODO Auto-generated method stub
		anto_report_toggle = (ToggleButtonView) findViewById(R.id.anto_report_toggle);
		report_back = (Button) findViewById(R.id.report_back);
		custom_report = (TextView) findViewById(R.id.custom_report);
		select_report = (TextView) findViewById(R.id.select_report);
	}

	@Override
	public void onToggleClick(View view) {
		switch (view.getId()) {
		case R.id.anto_report_toggle:
//			if(BaseUtils.getDBManager(ReportSosActivity.this) == null) {
//				anto_report_toggle.setSliderState(false);
//			} else {
//				SpUtils.putBoolean(SPConstants.AUTO_REPORT,
//						!SpUtils.getBoolean(SPConstants.AUTO_REPORT, true));
//				select_report.setEnabled(!SpUtils.getBoolean(
//						SPConstants.AUTO_REPORT, true));
//			}
			SpUtils.putBoolean(SPConstants.AUTO_REPORT,
					!SpUtils.getBoolean(SPConstants.AUTO_REPORT, true));
//			select_report.setEnabled(SpUtils.getBoolean(
//					SPConstants.AUTO_REPORT, true));
//			if (SpUtils.getBoolean(SPConstants.AUTO_REPORT, true)) {
////				SearchLocMapApplication.getIntance().autoUploadStart(
////						ReportSosActivity.this);
//			} else {
//				SearchLocMapApplication.getIntance().autoUploadStop();
//			}
			break;
		default:
			break;
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.report_back:
			finish();
			break;
		case R.id.select_report:
			if(BaseUtils.getDBManager(ReportSosActivity.this) != null) {
				startActivity(new Intent(ReportSosActivity.this,
						SelelctSosReportActivity.class));				
			}
			
			break;
		case R.id.custom_report:
			if(BaseUtils.getDBManager(ReportSosActivity.this) != null) {
				startActivity(new Intent(ReportSosActivity.this,
						ShortMessUploadActivity.class));
			}
			break;

		default:
			break;
		}

	}

	private OnDipperCancelListener cancelListener = new OnDipperCancelListener() {

		@Override
		public void cancel() {
			// TODO Auto-generated method stub
			if (Settings.Global.getInt(getContentResolver(),
					Settings.Global.BD_MODE_ON, 0) != 1) {
				SpUtils.putBoolean(SPConstants.AUTO_REPORT, false);
				anto_report_toggle.setSliderState(false);
				select_report.setEnabled(true);
			}
		}

	};

	protected void onDestroy() {
		super.onDestroy();
		cancelListener = null;
	}

}
