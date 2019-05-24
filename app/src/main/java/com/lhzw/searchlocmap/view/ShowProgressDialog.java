package com.lhzw.searchlocmap.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.lhzw.searchlocmap.R;

/**
 * Created by xtqb on 2019/3/25.
 */
public class ShowProgressDialog extends Dialog {

    private TextView tv_num;
    private SeekBar seekBar;
    private TextView tv_task;
    private TextView tv_num_title;

    public ShowProgressDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
    public ShowProgressDialog(Context context, int themeResId) {
        super(context, themeResId);
    }
    public ShowProgressDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_progress);
        init();
    }

    private void init() {
        setCancelable(false);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        tv_task = (TextView) findViewById(R.id.tv_task);
        tv_num_title = (TextView) findViewById(R.id.tv_num_title);
        tv_num = (TextView) findViewById(R.id.tv_num);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        tv_num.setText("0%");
    }

    public void setMaxSeekBar(int value){
        seekBar.setMax(value);
    }

    public void setSeekBar(int progress, int percent){
        tv_num.setText(percent + "%");
        seekBar.setProgress(progress);
    }

    public void setContent(String title, String apknote){
        tv_task.setText(title);
        tv_num_title.setText(apknote);
    }
}
