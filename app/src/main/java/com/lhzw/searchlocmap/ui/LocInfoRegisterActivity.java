package com.lhzw.searchlocmap.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import com.lhzw.searchlocmap.R;
import com.lhzw.searchlocmap.view.DropListPopupWindows;

public class LocInfoRegisterActivity extends Activity implements
		OnClickListener, OnItemClickListener, OnDismissListener {
	private TextView et_server_addr;
	private EditText et_user_name;
	private TextView et_dipper_num;
	private EditText et_device_num;
	private EditText et_loc_contact;
	private TextView tv_register_commit;
	private ImageView im_state;
	private DropListPopupWindows popup;
	private String[] strArrs;
	private ImageView per_statu_back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_locinfo_register);
		initView();
		initData();
		setListener();
	}

	private void initView() {
		et_user_name = (EditText) findViewById(R.id.et_user_name);
		et_dipper_num = (TextView) findViewById(R.id.et_dipper_num);
		et_device_num = (EditText) findViewById(R.id.et_device_num);
		et_loc_contact = (EditText) findViewById(R.id.et_loc_contact);
		et_server_addr = (TextView) findViewById(R.id.et_server_addr);
		tv_register_commit = (TextView) findViewById(R.id.tv_register_commit);

		im_state = (ImageView) findViewById(R.id.im_state);
		per_statu_back = (ImageView) findViewById(R.id.per_statu_back);
	}

	private void initData() {
		// TODO Auto-generated method stub

	}

	private void setListener() {
		// TODO Auto-generated method stub
		et_server_addr.setOnClickListener(this);
		tv_register_commit.setOnClickListener(this);
		per_statu_back.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.et_server_addr:
			im_state.setBackgroundResource(R.drawable.icon_unfold_list);
			strArrs = new String[] { "sdfsa", "kljskaldjf" };
			popup = new DropListPopupWindows(LocInfoRegisterActivity.this,
					strArrs, this);
			popup.setOnDismissListener(this);
			popup.showAsDropDown(et_server_addr, 0, 2);
			break;
		case R.id.tv_register_commit:

			break;
		case R.id.per_statu_back:
			this.finish();
			break;

		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		popup.dismiss();
		et_server_addr.setText(strArrs[position]);
	}

	@Override
	public void onDismiss() {
		// TODO Auto-generated method stub
		im_state.setBackgroundResource(R.drawable.icon_close_32);
		popup = null;
	}
}
