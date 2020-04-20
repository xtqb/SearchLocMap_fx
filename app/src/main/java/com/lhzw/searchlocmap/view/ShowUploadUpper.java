package com.lhzw.searchlocmap.view;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.lhzw.searchlocmap.R;

public class ShowUploadUpper extends AlertDialog {
	private EditText et_upload_num;
	private Button bt_upload_pattern_save;
	private Button bt_upload_pattern_cancle;

	private ShowUploadUpper(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub

	}

	public ShowUploadUpper(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_upload_pattern);
		init();
	}

	public void showDialog() {
		show();
	}

	public void cancelDialog() {
		cancel();
	}
	public EditText getPlotEditext(){
		return et_upload_num;
	}
	private void init() {
		setCancelable(false);
		et_upload_num = (EditText) findViewById(R.id.et_upload_num);
		bt_upload_pattern_save = (Button) findViewById(R.id.bt_upload_pattern_save);
		bt_upload_pattern_cancle = (Button) findViewById(R.id.bt_upload_pattern_cancle);
		
	}
	
	public void setListener(android.view.View.OnClickListener listener){
		bt_upload_pattern_save.setOnClickListener(listener);
		bt_upload_pattern_cancle.setOnClickListener(listener);
	}


	public String getUpperNum() {
		return et_upload_num.getText().toString();
	}

	public void cleanEtContent() {
		 et_upload_num.setText("");
	}

	public void clear() {
		this.dismiss();
	}
}
