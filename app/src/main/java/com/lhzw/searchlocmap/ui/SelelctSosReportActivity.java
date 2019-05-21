package com.lhzw.searchlocmap.ui;

import java.text.DecimalFormat;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.lhzw.searchlocmap.R;
import com.lhzw.searchlocmap.adapter.SelectReportSosAdapter;
import com.lhzw.searchlocmap.application.SearchLocMapApplication;
import com.lhzw.searchlocmap.bean.PersonalInfo;
import com.lhzw.searchlocmap.constants.Constants;
import com.lhzw.searchlocmap.db.dao.CommonDBOperator;
import com.lhzw.searchlocmap.db.dao.DatabaseHelper;
import com.lhzw.searchlocmap.dipper.UploadInfo;
import com.lhzw.searchlocmap.utils.CheckBoxState;

public class SelelctSosReportActivity extends Activity implements
		OnClickListener {
	private ListView perlistview;
	private SelectReportSosAdapter peradapter;
	private Dao<PersonalInfo, Integer> dao;
	private DatabaseHelper<?> helper;
	private List<PersonalInfo> sosList;
	private TextView confirm;
	private DecimalFormat df = new DecimalFormat("######0.000000");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_sos_report);
		initView();
	}

	private void initView() {
		Button report_back = (Button) findViewById(R.id.select_report_back);
		confirm = (TextView) findViewById(R.id.confirm);
		report_back.setOnClickListener(this);
		confirm.setOnClickListener(this);
		helper = DatabaseHelper.getHelper(this);
		dao = helper.getPersonalInfoDao();
		perlistview = (ListView) findViewById(R.id.sos_report_listview);
		sosList = CommonDBOperator.queryByKeys(dao, "state",
				Constants.PERSON_SOS); // sos
		peradapter = new SelectReportSosAdapter(sosList, this,
				R.layout.report_sos_item);
		perlistview.setAdapter(peradapter);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.select_report_back:
			finish();
			break;
		case R.id.confirm:
			boolean isHas = false;
			for (CheckBoxState item : peradapter.getCheckStateList()) {
				if (item.isCheck()) {
					addUploadItem(item.getID());
					isHas = true;
				}
			}
			if (isHas) {
				SearchLocMapApplication.getInstance().autoUploadStart(
						SelelctSosReportActivity.this);
				if (Settings.Global.getInt(getContentResolver(),
						Settings.Global.BD_MODE_ON, 0) != 1) {
					Toast.makeText(SelelctSosReportActivity.this,
							getString(R.string.upload_sos_info_fail),
							Toast.LENGTH_SHORT).show();
				} else {
					this.finish();
				}
			}
			break;

		}

	}

	private void addUploadItem(int pos) {

	}

}
