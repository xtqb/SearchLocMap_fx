package com.lhzw.searchlocmap.view;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lhzw.searchlocmap.R;

public class ShowAlertDialog extends AlertDialog {
	private TextView tv_content;
	private TextView tv_cancel;
	private TextView tv_sure;

	private ShowAlertDialog(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub

	}

	public ShowAlertDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_show_note);
		init();
	}

	public void showDialog() {
		show();
	}

	public void cancelDialog() {
		cancel();
	}

	private void init() {
		setCancelable(false);
		tv_content = (TextView) findViewById(R.id.tv_content);
		tv_cancel = (TextView) findViewById(R.id.tv_cancel);
		tv_sure = (TextView) findViewById(R.id.tv_sure);
	}

	public void setListener(View.OnClickListener listener) {
		tv_cancel.setOnClickListener(listener);
		tv_sure.setOnClickListener(listener);
	}

	public void setContent(String content) {
		tv_content.setText(content);
	}

	public void clear() {
		this.dismiss();
	}
}
