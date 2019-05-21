package com.lhzw.searchlocmap.ui;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.lhzw.searchlocmap.R;
import com.lhzw.searchlocmap.bean.LocPersonalInfo;
import com.lhzw.searchlocmap.bean.PersonalInfo;
import com.lhzw.searchlocmap.db.dao.CommonDBOperator;
import com.lhzw.searchlocmap.db.dao.DatabaseHelper;
import com.lhzw.searchlocmap.utils.BaseUtils;
import com.lhzw.searchlocmap.utils.Utils;

public class PerDetailsActivity extends Activity implements OnClickListener,
		OnGlobalLayoutListener {
	private TextView edit_btn;
	private Button back_btn;
	private EditText et_temp;
	private EditText num_et;
	private EditText per_name_et;
	private RadioGroup rg_two;
	private EditText per_phone_et;
	private EditText per_phone1_et;
	private EditText per_phone2_et;
	private EditText per_blood_et;
	private EditText per_allergy_et;
	private LocPersonalInfo perInfo;
	private DatabaseHelper helper;
	private Dao<PersonalInfo, Integer> dao;
	private Dao<LocPersonalInfo, Integer> perdao;
	private RadioButton btn1;
	private RadioButton btn2;
	private ScrollView login;
	private String person_sex;
	private View decorView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.per_details);
		initView();
		setListener();
	}

	private void initView() {
		edit_btn = (TextView) findViewById(R.id.edit);
		back_btn = (Button) findViewById(R.id.per_back);
		num_et = (EditText) findViewById(R.id.coding_input);
		per_name_et = (EditText) findViewById(R.id.per_put_name);
		rg_two = (RadioGroup) findViewById(R.id.rg_two);
		btn1 = (RadioButton) findViewById(R.id.btn1);
		btn2 = (RadioButton) findViewById(R.id.btn2);
		per_phone_et = (EditText) findViewById(R.id.per_put_phone);
		per_phone1_et = (EditText) findViewById(R.id.per_put_phone1);
		per_phone2_et = (EditText) findViewById(R.id.per_put_phone2);
		per_blood_et = (EditText) findViewById(R.id.per_put_blood);
		per_allergy_et = (EditText) findViewById(R.id.per_put_allergy);
		login = (ScrollView) findViewById(R.id.pre_scrollview);
		edit_btn.setOnClickListener(this);
		back_btn.setOnClickListener(this);

		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		perInfo = (LocPersonalInfo) bundle.getSerializable("PreID");
		num_et.setText(perInfo.getNum());
		per_name_et.setText(perInfo.getName());
		person_sex = perInfo.getSex();
		if (getString(R.string.person_sex_man).equals(person_sex)) {
			btn1.setChecked(true);
		} else if (getString(R.string.person_sex_women).equals(person_sex)) {
			btn2.setChecked(true);
		}

		per_phone_et.setText(perInfo.getPhone());
		per_phone1_et.setText(perInfo.getContact1());
		per_phone2_et.setText(perInfo.getContact2());
		per_blood_et.setText(perInfo.getBloodtype());
		per_allergy_et.setText(perInfo.getAllergy());

		setViewEditeState(false);

		helper = DatabaseHelper.getHelper(PerDetailsActivity.this);
		perdao = helper.getLocPersonDao();
		dao = helper.getPersonalInfoDao();
		decorView = getWindow().getDecorView();
		decorView.getViewTreeObserver().addOnGlobalLayoutListener(this);
	}

	private void setListener() {
		rg_two.setOnCheckedChangeListener(listener);
	}

	private OnCheckedChangeListener listener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub
			switch (checkedId) {
			case R.id.btn1:
				person_sex = getString(R.string.person_sex_man);
				break;
			case R.id.btn2:
				person_sex = getString(R.string.person_sex_women);
				break;

			default:
				break;
			}

		}

	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.edit:
			if (getString(R.string.person_detail_edite).equals(
					edit_btn.getText().toString())) {
				edit_btn.setText(getString(R.string.person_detail_save));
				back_btn.setText(getString(R.string.person_deatail_cancel));
				back_btn.setTextColor(getResources().getColor(R.color.white));
				back_btn.setBackgroundColor(Color.TRANSPARENT);
				setMultipleEditTextFocusable(true);
				setViewEditeState(true);
			} else if (getString(R.string.person_detail_save).equals(
					edit_btn.getText().toString())) {
				if (BaseUtils.isStringEmpty(per_name_et.getText().toString()
						.trim())) {
					Toast.makeText(this,
							getString(R.string.person_info_requried_name),
							Toast.LENGTH_SHORT).show();
					return;
				}
				edit_btn.setText(R.string.person_detail_edite);
				back_btn.setText("");
				back_btn.setBackgroundResource(R.drawable.btn_back_state);
				setViewEditeState(false);
				setMultipleEditTextFocusable(false);
				Utils.closeSoftInput(PerDetailsActivity.this);
				// 更新
				perInfo.setName(per_name_et.getText() + "");
				perInfo.setSex(person_sex);
				perInfo.setPhone(per_phone_et.getText() + "");
				perInfo.setContact1(per_phone1_et.getText() + "");
				perInfo.setContact2(per_phone2_et.getText() + "");
				perInfo.setBloodtype(per_blood_et.getText() + "");
				perInfo.setAllergy(per_allergy_et.getText() + "");
				CommonDBOperator.updateItem(perdao, perInfo);
				perInfo = null;
				// 更新 personItem
				List<PersonalInfo> list = CommonDBOperator.queryByKeys(dao,
						"num", num_et.getText().toString());
				list.get(0).setName(per_name_et.getText() + "");
				list.get(0).setSex(person_sex);
				list.get(0).setPhone(per_phone_et.getText() + "");
				list.get(0).setContact1(per_phone1_et.getText() + "");
				list.get(0).setContact2(per_phone2_et.getText() + "");
				list.get(0).setBloodtype(per_blood_et.getText() + "");
				list.get(0).setAllergy(per_allergy_et.getText() + "");
				CommonDBOperator.updateItem(dao, list.get(0));
				list.clear();
				// 通知地图更新界面
				Intent intent = new Intent("com.lhzw.soildersos.change");
				intent.putExtra("has_new", false);
				sendBroadcast(intent);
				this.finish();
			}

			break;

		case R.id.per_back:
			if (getString(R.string.person_detail_edite).equals(
					edit_btn.getText().toString())) {
				setResult(200);
				finish();
				break;
			} else if (getString(R.string.person_detail_save).equals(
					edit_btn.getText().toString())) {
				edit_btn.setText(R.string.person_detail_edite);
				back_btn.setText("");
				back_btn.setBackgroundResource(R.drawable.btn_back_state);
				setMultipleEditTextFocusable(false);
				Utils.closeSoftInput(PerDetailsActivity.this);
			}

		}

	}

	/*
	 * 允许编辑
	 */
	private void setMultipleEditTextFocusable(boolean isFocus) {
		int count = -1;
		ViewGroup editTextLayout = (ViewGroup) findViewById(R.id.pre_details_linear);
		for (int i = 0; i < editTextLayout.getChildCount(); i++) {
			View v = editTextLayout.getChildAt(i);
			if (v instanceof ViewGroup) {
				ViewGroup v2 = (ViewGroup) v;
				for (int k = 0; k < v2.getChildCount(); k++) {
					count++;
					View v3 = v2.getChildAt(k);
					if (v3 instanceof EditText) {
						et_temp = (EditText) findViewById(v3.getId());
						et_temp.setFocusable(isFocus);
						et_temp.setFocusableInTouchMode(isFocus);
						if (count == 0 && isFocus == true) {
							et_temp.requestFocus();
						}
					}
				}
			}

		}

	}

	// 可否编辑
	private void setViewEditeState(boolean state) {
		per_name_et.setEnabled(state);
		btn1.setEnabled(state);
		btn2.setEnabled(state);
		per_phone_et.setEnabled(state);
		per_phone1_et.setEnabled(state);
		per_phone2_et.setEnabled(state);
		per_blood_et.setEnabled(state);
		per_allergy_et.setEnabled(state);
	}

	@Override
	public void onGlobalLayout() {
		Rect rect = new Rect();
		decorView.getWindowVisibleDisplayFrame(rect);
		int screenHeight = decorView.getRootView().getHeight();
		int heightDifference = screenHeight - rect.bottom;
		LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) login
				.getLayoutParams();
		layoutParams.setMargins(0, 0, 0, heightDifference);
		login.requestLayout();

	}

}
