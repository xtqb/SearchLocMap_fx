package com.lhzw.searchlocmap.view;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lhzw.searchlocmap.R;

public class ShowAlertDialogCommand extends AlertDialog {
	private TextView tv_command_cancel;
	private ListView listview;
	private TextView mTvState;

	private ShowAlertDialogCommand(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
	}

	public ShowAlertDialogCommand(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_show_command);
		init();
	}

	public void showDialog() {
		show();
	}

	private void init() {
		setCancelable(false);
		tv_command_cancel = (TextView) findViewById(R.id.tv_command_cancel);
		mTvState = (TextView) findViewById(R.id.tv_state);
		listview = (ListView) findViewById(R.id.listview);
	}

	public void setListener(View.OnClickListener listener) {
		tv_command_cancel.setOnClickListener(listener);
		mTvState.setOnClickListener(listener);
	}

	public void setAdapter (BaseAdapter adapter) {
		listview.setAdapter(adapter);
	}

	public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
		listview.setOnItemClickListener(listener);
	}

	public void clear() {
		this.dismiss();
	}
}
