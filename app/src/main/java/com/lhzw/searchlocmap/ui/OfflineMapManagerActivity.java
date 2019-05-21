package com.lhzw.searchlocmap.ui;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lhzw.searchlocmap.R;
import com.lhzw.searchlocmap.utils.CheckBoxState;
import com.lhzw.searchlocmap.view.ShowAlertDialog;

public class OfflineMapManagerActivity extends BaseMapActivity implements
		OnClickListener, OnTouchListener {
	private ImageView offline_back;
	private TextView tv_manager;
	private EditText offline_keyWord;
	private ListView offline_listview;
	private CheckBox select;
	private TextView delete;
	private List<CheckBoxState> checkList;
	private ShowAlertDialog dialog;
	private boolean isOnClick = false;
	private final int ROTATE = 60001;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_offline_map);
		initView();
		initData();
		setListener();
	}

	private void initView() {
		offline_back = (ImageView) findViewById(R.id.offline_back);
		tv_manager = (TextView) findViewById(R.id.tv_manager);
		offline_keyWord = (EditText) findViewById(R.id.offline_keyWord);
		offline_listview = (ListView) findViewById(R.id.offline_listview);
		select = (CheckBox) findViewById(R.id.select);
		delete = (TextView) findViewById(R.id.delete);

	}

	private void initData() {
		checkList = new ArrayList<CheckBoxState>();
		showProgressDialog(getString(R.string.offline_city_map_init), false);
		// mHandler.sendEmptyMessageDelayed(ROTATE, 75000);
	}

	private void initCheckState() {
		if (checkList != null && checkList.size() > 0)
			checkList.clear();
	}

	private void setCheckStateList(boolean isCheck) {
		for (CheckBoxState item : checkList) {
			item.setCheck(isCheck);
		}
	}

	private void setListener() {
		// TODO Auto-generated method stub
		offline_back.setOnClickListener(this);
		tv_manager.setOnClickListener(this);
		delete.setOnClickListener(this);
		select.setOnCheckedChangeListener(checkListener);
		select.setOnTouchListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.offline_back:
			this.finish();
			break;
		case R.id.tv_manager:
			// try {
			// mapManager.downloadByCityName("南京市");
			// } catch (AMapException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// startActivity(new Intent(OfflineMapManagerActivity.this,
			// OfflineDownloadActivity.class));

			break;
		case R.id.delete:
			if (!isHasSelect()) {
				showToast(getString(R.string.offline_map_no_data_note));
				return;
			}
			// if (cityList != null && cityList.size() > 0) {
			// dialog = new ShowAlertDialog(OfflineMapManagerActivity.this);
			// dialog.show();
			// dialog.setContent(getString(R.string.offline_map_delete_note));
			// dialog.setListener(this);
			// } else {
			// Toast.makeText(OfflineMapManagerActivity.this,
			// getString(R.string.offline_map_no_data_note),
			// Toast.LENGTH_SHORT).show();
			// }
			break;
		case R.id.tv_sure:
			dialog.dismiss();
			dialog = null;
			showToast("暂不支持");
			// for(int i = checkList.size()-1; i > -1; i--){
			// if(checkList.get(i).isCheck()){
			// mapManager.remove(cityList.get(checkList.get(i).getID()).getCity());
			// offlineCity = cityList.get(checkList.get(i).getID());
			// Log.e("Tag", "remove i : " + i);
			// }
			// }
			break;
		case R.id.tv_cancel:
			dialog.dismiss();
			dialog = null;
			break;

		default:
			break;
		}
	}

	private BaseAdapter adapter = new BaseAdapter() {
		private ViewHolder holder;

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(
						OfflineMapManagerActivity.this).inflate(
						R.layout.report_sos_item, null);
				holder.checkBox = (CheckBox) convertView
						.findViewById(R.id.report_choose);
				holder.tv_cityName = (TextView) convertView
						.findViewById(R.id.report_name_tv);
				holder.tv_download_time = (TextView) convertView
						.findViewById(R.id.report_num_tv);
				holder.im_spread = (ImageView) convertView
						.findViewById(R.id.spread);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.checkBox.setTag(position);
			holder.checkBox.setChecked(checkList.get(position).isCheck());
			holder.checkBox.setOnCheckedChangeListener(checkListener);
			holder.im_spread.setVisibility(View.GONE);
			// holder.tv_cityName.setText(cityList.get(position).getCity());
			holder.tv_download_time.setText("2017.12.28 12:30");
			return convertView;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			// return cityList.size();
			return 0;
		}
	};

	private OnCheckedChangeListener checkListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			// TODO Auto-generated method stub
			if (buttonView.getId() == R.id.select) {
				if (isOnClick) {
					isOnClick = false;
					setCheckStateList(isChecked);
					adapter.notifyDataSetChanged();
				}
			} else {
				int pos = (Integer) buttonView.getTag();
				checkList.get(pos).setCheck(isChecked);
				adapter.notifyDataSetChanged();
				checkAllCheckBoxState();
			}
		}

	};

	private void checkAllCheckBoxState() {
		boolean allCheck = true;
		for (CheckBoxState item : checkList) {
			allCheck = allCheck && item.isCheck();
		}
		select.setChecked(allCheck);
	}

	private boolean isHasSelect() {
		if (checkList == null || (checkList != null && checkList.size() == 0))
			return false;
		for (CheckBoxState item : checkList) {
			if (item.isCheck())
				return true;
		}
		return false;
	}

	private class ViewHolder {
		private CheckBox checkBox;
		private TextView tv_cityName;
		private TextView tv_download_time;
		private ImageView im_spread;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.select:
			isOnClick = true;
			break;

		default:
			break;
		}

		return false;
	}
}
