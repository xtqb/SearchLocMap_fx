package com.lhzw.searchlocmap.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gtmap.util.GeoPoint;
import com.gtmap.views.MapController;
import com.gtmap.views.MapView;
import com.lhzw.searchlocmap.R;
import com.lhzw.searchlocmap.utils.BaseUtils;

public class LocationDialog extends AlertDialog implements
		View.OnClickListener, TextWatcher, OnFocusChangeListener {

	private TextView tv_back;
	private TextView tv_caculate;
	private Activity mContext;
	private int viewId;
	private String lastStr;
	private Toast mGlobalToast;
	private View et_View;
	private MapView mMapView;
	private EditText et_lon_degree;
	private EditText et_lon_minute;
	private EditText et_lon_second;
	private EditText et_lat_degree;
	private EditText et_lat_minute;
	private EditText et_lat_second;

	public LocationDialog(Activity context, MapView mMapView) {
		super(context);
		// TODO Auto-generated constructor stub
		this.mContext = context;
		this.mMapView = mMapView;
		setCanceledOnTouchOutside(false);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_location_show);
		viewId = R.id.et_lon_degree;
		initView();
		setListener();
	}

	private void setListener() {
		// TODO Auto-generated method stub
		tv_back.setOnClickListener(this);
		tv_caculate.setOnClickListener(this);
		et_lon_degree.addTextChangedListener(this);
		et_lon_degree.setOnFocusChangeListener(this);
		et_lon_minute.addTextChangedListener(this);
		et_lon_minute.setOnFocusChangeListener(this);
		et_lon_second.addTextChangedListener(this);
		et_lon_second.setOnFocusChangeListener(this);
		et_lat_degree.addTextChangedListener(this);
		et_lat_degree.setOnFocusChangeListener(this);
		et_lat_minute.addTextChangedListener(this);
		et_lat_minute.setOnFocusChangeListener(this);
		et_lat_second.addTextChangedListener(this);
		et_lat_second.setOnFocusChangeListener(this);
	}

	private void initView() {
		tv_back = (TextView) findViewById(R.id.tv_back);
		tv_caculate = (TextView) findViewById(R.id.tv_caculate);
		et_lon_degree = (EditText) findViewById(R.id.et_lon_degree);
		et_lon_minute = (EditText) findViewById(R.id.et_lon_minute);
		et_lon_second = (EditText) findViewById(R.id.et_lon_second);
		et_lat_degree = (EditText) findViewById(R.id.et_lat_degree);
		et_lat_minute = (EditText) findViewById(R.id.et_lat_minute);
		et_lat_second = (EditText) findViewById(R.id.et_lat_second);

	}

	public void showDialog() {
		this.show();
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.tv_caculate:
			if(BaseUtils.isStringEmpty(et_lon_degree.getText().toString()) || BaseUtils.isStringEmpty(et_lon_minute.getText().toString())
					|| BaseUtils.isStringEmpty(et_lon_second.getText().toString()) || BaseUtils.isStringEmpty(et_lat_degree.getText().toString())
					|| BaseUtils.isStringEmpty(et_lat_minute.getText().toString()) || BaseUtils.isStringEmpty(et_lat_second.getText().toString())) {
				showToast(mContext.getString(R.string.location_latlon_null_note));
				return;
			}
			//快速定位
			//将镜头平移到当前手持机定位的中心
			double lon = Double.valueOf(et_lon_degree.getText().toString()) + Double.valueOf(et_lon_minute.getText().toString()) / 60 + Double.valueOf(et_lon_second.getText().toString())/ 3600;
			double lat = Double.valueOf(et_lat_degree.getText().toString()) + Double.valueOf(et_lat_minute.getText().toString()) / 60 + Double.valueOf(et_lat_second.getText().toString())/ 3600;
			MapController mapController = mMapView.getController();
			mapController.setZoom(16);
			mapController.setCenter(new GeoPoint(lat, lon));
			this.dismiss();
			break;
		case R.id.tv_back:
			this.dismiss();
			break;
		}
	}

	@Override
	public void afterTextChanged(Editable editable) {
		// TODO Auto-generated method stub
		try {
			String content = editable.toString();
			if (content != null && !"".equals(content)) {
                double value = Double.valueOf(content);
                switch (viewId) {
					case R.id.et_lon_degree:
						if(value< 0 || value >= 180) {
							showToast(mContext.getString(R.string.location_degree_lon_note));
							et_lon_degree.setText("");
							return;
						} else {
							if(value > 18) {
								et_lon_minute.setFocusable(true);
								et_lon_minute.setFocusableInTouchMode(true);
								et_lon_minute.requestFocus();
							}
						}
						break;
					case R.id.et_lon_minute:
						if(value < 0 || value >= 60) {
							showToast(mContext.getString(R.string.location_minute_second_note));
							et_lon_minute.setText("");
							return;
						} else {
							if(content.length() == 2 || (content.length() == 1 && value >= 6)) {
								et_lon_second.setFocusable(true);
								et_lon_second.setFocusableInTouchMode(true);
								et_lon_second.requestFocus();
							}
						}
						break;
					case R.id.et_lon_second:
						if(value < 0 || value >= 60) {
							showToast(mContext.getString(R.string.location_minute_second_note));
							et_lon_second.setText("");
							return;
						} else {
							if(content.length() == 2 || (content.length() == 1 && value >= 6)) {
								et_lat_degree.setFocusable(true);
								et_lat_degree.setFocusableInTouchMode(true);
								et_lat_degree.requestFocus();
							}
						}
						break;
					case R.id.et_lat_degree:
						if(value < 0 || value >= 90) {
							showToast(mContext.getString(R.string.location_degree_lat_note));
							et_lat_degree.setText("");
							return;
						} else {
							if(content.length() == 2 || (content.length() == 1 && value == 9)) {
								et_lat_minute.setFocusable(true);
								et_lat_minute.setFocusableInTouchMode(true);
								et_lat_minute.requestFocus();
							}
						}
						break;
					case R.id.et_lat_minute:
						if(value < 0 || value >= 60) {
							showToast(mContext.getString(R.string.location_minute_second_note));
							et_lat_minute.setText("");
							return;
						} else {
							if(content.length() == 2 || (content.length() == 1 && value >= 6)) {
								et_lat_second.setFocusable(true);
								et_lat_second.setFocusableInTouchMode(true);
								et_lat_second.requestFocus();
							}
						}
						break;
					case R.id.et_lat_second:
						if(value < 0 || value >= 60) {
							showToast(mContext.getString(R.string.location_minute_second_note));
							et_lat_second.setText("");
							return;
						} else {
							if(content.length() == 2 || (content.length() == 1 && value >= 6)) {
								et_lon_degree.setFocusable(true);
								et_lon_degree.setFocusableInTouchMode(true);
								et_lon_degree.requestFocus();
							}
						}
						break;
                }
            }
		} catch (NumberFormatException e) {
			showToast(mContext.getString(R.string.location_latlon_null_note));
		}
	}

	private void hideInputMethod() {
		InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mContext.getCurrentFocus().getWindowToken(), 0); //ǿ�����ؼ���
	}

	@Override
	public void beforeTextChanged(CharSequence str, int start, int before,
			int count) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onTextChanged(CharSequence str, int start, int before, int count) {
		// TODO Auto-generated method stub

	}
	
	public void showToast(String text) {

		if (mGlobalToast == null) {
			mGlobalToast = Toast.makeText(mContext, text,
					Toast.LENGTH_SHORT);
			mGlobalToast.show();
		} else {
			mGlobalToast.setText(text);
			mGlobalToast.setDuration(Toast.LENGTH_SHORT);
			mGlobalToast.show();
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		switch (v.getId()){
			case R.id.et_lon_degree:
				viewId = R.id.et_lon_degree;
				break;
			case R.id.et_lon_minute:
				viewId = R.id.et_lon_minute;
				break;
			case R.id.et_lon_second:
				viewId = R.id.et_lon_second;
				break;
			case R.id.et_lat_degree:
				viewId = R.id.et_lat_degree;
				break;
			case R.id.et_lat_minute:
				viewId = R.id.et_lat_minute;
				break;
			case R.id.et_lat_second:
				viewId = R.id.et_lat_second;
				break;
		}
	}
}
