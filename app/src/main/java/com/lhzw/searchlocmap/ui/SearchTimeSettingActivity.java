package com.lhzw.searchlocmap.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lhzw.searchlocmap.R;
import com.lhzw.searchlocmap.constants.SPConstants;
import com.lhzw.searchlocmap.picker.PickerScrollView;
import com.lhzw.searchlocmap.picker.Pickers;
import com.lhzw.searchlocmap.utils.SpUtils;
import com.lhzw.searchlocmap.view.ShowAlertDialog;
import com.lhzw.searchlocmap.view.ShowAlertDialogNotify;

public class SearchTimeSettingActivity extends Activity implements
		PickerScrollView.onSelectListener, OnClickListener {
	private List<Pickers> pickers_list = new ArrayList<Pickers>();
	private List<Pickers> pickers_minu_list = new ArrayList<Pickers>();
	private List<Pickers> pickers_second_list = new ArrayList<Pickers>();
	private PickerScrollView pick_hour_view;
	private PickerScrollView pick_minute_view;
	private PickerScrollView pick_second_view;
	private ImageView im_set_time_back;
	private TextView tv_register_commit;
	private int hour;
	private int minute;
	private int second;
	private Toast mGlobalToast;
	private ShowAlertDialog dialog;
	private ShowAlertDialogNotify alertDialogNotify;

	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_time);
		initView();
		initData();
		setListener();
	}

	private void setListener() {
		// TODO Auto-generated method stub
		pick_hour_view.setOnSelectListener(this);
		pick_minute_view.setOnSelectListener(this);
		pick_second_view.setOnSelectListener(this);

		im_set_time_back.setOnClickListener(this);
		tv_register_commit.setOnClickListener(this);
	}

	private void initData() {
		// TODO Auto-generated method stub
		initPickerData();
	}

	private void initView() {
		// TODO Auto-generated method stub
		pick_hour_view = (PickerScrollView) findViewById(R.id.pick_hour_view);
		pick_minute_view = (PickerScrollView) findViewById(R.id.pick_minute_view);
		pick_second_view = (PickerScrollView) findViewById(R.id.pick_second_view);

		im_set_time_back = (ImageView) findViewById(R.id.im_set_time_back);
		tv_register_commit = (TextView) findViewById(R.id.tv_register_commit);
	}

	@Override
	public void onSelect(Pickers pickers, View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.pick_hour_view:
			hour = Integer.valueOf(pickers.getShowConetnt());
			break;
		case R.id.pick_minute_view:
			minute = Integer.valueOf(pickers.getShowConetnt());
			break;
		case R.id.pick_second_view:
			second = Integer.valueOf(pickers.getShowConetnt());
			break;

		default:
			break;
		}
	};

	/*
	 * 初始化数据
	 */
	private void initPickerData() {
		String[] pickers_hour_id = getResources().getStringArray(
				R.array.pick_hour_id);
		String[] pickers_hour_name = getResources().getStringArray(
				R.array.pick_hour_arr);
		String[] pickers_mit_se_id = getResources().getStringArray(
				R.array.pick_minute_second_id);
		String[] pickers_mit_se_name = getResources().getStringArray(
				R.array.pick_minute_second_arr);
		for (int i = 0; i < pickers_hour_id.length; i++) {
			pickers_list.add(new Pickers(pickers_hour_name[i],
					pickers_hour_id[i]));
		}
		for (int i = 0; i < pickers_mit_se_id.length; i++) {
			pickers_minu_list.add(new Pickers(pickers_mit_se_name[i],
					pickers_mit_se_id[i]));
		}
		for (int i = 0; i < pickers_mit_se_id.length; i++) {
			pickers_second_list.add(new Pickers(pickers_mit_se_name[i],
					pickers_mit_se_id[i]));
		}
		pick_hour_view.setData(pickers_list);
		pick_minute_view.setData(pickers_minu_list);
		pick_second_view.setData(pickers_second_list);

		hour = SpUtils.getInt(SPConstants.SETTING_HOUR, 0);
		minute = SpUtils.getInt(SPConstants.SETTING_MINUTE, 2);
		second = SpUtils.getInt(SPConstants.SETTING_SECOND, 5);

		// 初始化
		pick_hour_view.setSelected(hour);
		pick_minute_view.setSelected(minute);
		pick_second_view.setSelected(second);

		pick_hour_view.setContent(getString(R.string.time_settings_hour));
		pick_minute_view.setContent(getString(R.string.time_settings_minute));
		pick_second_view.setContent(getString(R.string.time_settings_second));
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.im_set_time_back:
			if (hour == SpUtils.getInt(SPConstants.SETTING_HOUR, 0)
					&& minute == SpUtils.getInt(SPConstants.SETTING_MINUTE, 2)
					&& second == SpUtils.getInt(SPConstants.SETTING_SECOND, 5)) {
				this.finish();
			} else {
				dialog = new ShowAlertDialog(SearchTimeSettingActivity.this);
				dialog.show();
				dialog.setContent(getString(R.string.time_settings_time_back_note));
				dialog.setListener(this);
			}
			break;
		case R.id.tv_register_commit:
			/*
			if (SpUtils.getBoolean(SPConstants.COMMON_SWITCH, false)) {
				showAlertDialogNotify(
						getString(R.string.time_settings_time_search_note),
						R.layout.dialog_show_notigy1);
				return;
			}
			*/
			if ((hour * 3600 + minute * 60 + second) >= 60) {
				SpUtils.putInt(SPConstants.SETTING_HOUR, hour);
				SpUtils.putInt(SPConstants.SETTING_MINUTE, minute);
				SpUtils.putInt(SPConstants.SETTING_SECOND, second);
				this.finish();
			} else {
				showToast(getString(R.string.time_settings_time_note));
			}
			break;
		case R.id.tv_cancel:
			dialog.dismiss();
			dialog = null;
			break;
		case R.id.tv_sure:
			// 主动搜救
			dialog.dismiss();
			dialog = null;
			this.finish();
			break;
		case R.id.tv_notify_cancel:
			alertDialogNotify.dismiss();
			alertDialogNotify = null;
			break;
		default:
			break;
		}
	}

	private void showToast(String text) {

		if (mGlobalToast == null) {
			mGlobalToast = Toast.makeText(SearchTimeSettingActivity.this, text,
					Toast.LENGTH_SHORT);
			mGlobalToast.show();
		} else {
			mGlobalToast.setText(text);
			mGlobalToast.setDuration(Toast.LENGTH_SHORT);
			mGlobalToast.show();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (hour == SpUtils.getInt(SPConstants.SETTING_HOUR, 0)
					&& minute == SpUtils.getInt(SPConstants.SETTING_MINUTE, 2)
					&& second == SpUtils.getInt(SPConstants.SETTING_SECOND, 5)) {
				this.finish();
			} else {
				dialog = new ShowAlertDialog(SearchTimeSettingActivity.this);
				dialog.show();
				dialog.setContent(getString(R.string.time_settings_time_back_note));
				dialog.setListener(this);
			}
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		pickers_list.clear();
		pickers_list = null;
		pickers_minu_list.clear();
		pickers_minu_list = null;
		pickers_second_list.clear();
		pickers_second_list = null;
		mGlobalToast = null;
	}

	private void showAlertDialogNotify(String content, int layoutID) {
		alertDialogNotify = new ShowAlertDialogNotify(
				SearchTimeSettingActivity.this, layoutID);
		alertDialogNotify.showDialog();
		alertDialogNotify.setContent(content);
		alertDialogNotify.setListener(this);
	}

}
