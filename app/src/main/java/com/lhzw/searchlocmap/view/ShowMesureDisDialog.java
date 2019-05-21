package com.lhzw.searchlocmap.view;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lhzw.searchlocmap.R;

public class ShowMesureDisDialog extends AlertDialog implements View.OnClickListener {
	private TextView tv_cancel;
	private TextView tv_input_distance;
	private TextView tv_click_mesure;

	private ShowMesureDisDialog(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub

	}

	public ShowMesureDisDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_mesure_distance_note);
		init();
	}

	public void showDialog() {
		show();
	}

	public void cancelDialog() {
		cancel();
	}

	private void init() {
		setCancelable(true);
		tv_cancel = (TextView) findViewById(R.id.tv_cancel);
		tv_cancel.setOnClickListener(this);
		tv_input_distance = (TextView) findViewById(R.id.tv_input_distance);
		tv_click_mesure = (TextView) findViewById(R.id.tv_click_mesure);
	}

	public void setListener(View.OnClickListener listener){
		tv_input_distance.setOnClickListener(listener);
		tv_click_mesure.setOnClickListener(listener);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.tv_cancel:
				dismiss();
				break;
		}
	}
}
