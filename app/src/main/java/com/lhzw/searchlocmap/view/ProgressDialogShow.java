package com.lhzw.searchlocmap.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;
import com.lhzw.searchlocmap.R;

public class ProgressDialogShow extends ProgressDialog {
	private TextView tv_content;

	public ProgressDialogShow(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_progress_show);
		init();
	}

	private void init() {
		setCancelable(false);
		tv_content = (TextView) findViewById(R.id.tv_content);
	}

	public void setContent(String str) {
		tv_content.setText(str);
	}

	public void dialogShow() {
		if (!isShowing()) {
			show();
		}
	}

	public void cancel() {
		if (isShowing()) {
			dismiss();
		}
	}

}
