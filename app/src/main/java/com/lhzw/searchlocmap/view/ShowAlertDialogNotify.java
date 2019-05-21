package com.lhzw.searchlocmap.view;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lhzw.searchlocmap.R;

public class ShowAlertDialogNotify extends AlertDialog {
	private TextView tv_content;
	private TextView tv_notify_cancel;
	private static int layout;

	public ShowAlertDialogNotify(Context context, int layoutID) {
		super(context);
		// TODO Auto-generated constructor stub
		layout = layoutID;
	}

	private ShowAlertDialogNotify(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(layout);
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
		tv_notify_cancel = (TextView) findViewById(R.id.tv_notify_cancel);
	}

	public void setListener(View.OnClickListener listener) {
		tv_notify_cancel.setOnClickListener(listener);
	}

	public void setContent(String content) {
		tv_content.setText(content);
	}

	public void clear() {
		this.dismiss();
	}
}
