package com.lhzw.searchlocmap.view;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import com.lhzw.searchlocmap.R;

public class ShowPlotDialog extends AlertDialog {
	private EditText et_plot_name;
	private Button bt_plot_save;
	private Button bt_plot_cancle;

	private ShowPlotDialog(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub

	}

	public ShowPlotDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_plot_note);
		init();
	}

	public void showDialog() {
		show();
	}

	public void cancelDialog() {
		cancel();
	}
	public EditText getPlotEditext(){
		return et_plot_name;
	}
	private void init() {
		setCancelable(false);
		et_plot_name = (EditText) findViewById(R.id.et_plot_name);
		bt_plot_save = (Button) findViewById(R.id.bt_plot_save);
		bt_plot_cancle = (Button) findViewById(R.id.bt_plot_cancle);
		
	}
	
	public void setListener(android.view.View.OnClickListener listener){
		bt_plot_save.setOnClickListener(listener);
		bt_plot_cancle.setOnClickListener(listener);
	}


	public String getPlotName() {
		return et_plot_name.getText().toString();
	}

	public void clear() {
		this.dismiss();
	}
}
