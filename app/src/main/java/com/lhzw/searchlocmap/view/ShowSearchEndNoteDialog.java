package com.lhzw.searchlocmap.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lhzw.searchlocmap.R;

/**
 * Created by xtqb on 2019/3/26.
 */
public class ShowSearchEndNoteDialog extends Dialog implements View.OnClickListener{

    private Button dialog_upload;
    private TextView tv_search_num;
    private TextView tv_search_total;
    private TextView tv_search_success;
    private TextView tv_search_fail;
    private Button dialog_cancel;

    protected ShowSearchEndNoteDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
    public ShowSearchEndNoteDialog(Context context, int themeResId) {
        super(context, themeResId);
    }
    public ShowSearchEndNoteDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_total_search_end_note);
        init();
    }

    private void init(){
        setCancelable(false);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog_cancel = (Button) findViewById(R.id.dialog_timer_cancel);
        dialog_upload = (Button) findViewById(R.id.dialog_upload);
        tv_search_num = (TextView) findViewById(R.id.tv_search_num);
        tv_search_total = (TextView) findViewById(R.id.tv_search_total);
        tv_search_success = (TextView) findViewById(R.id.tv_search_success);
        tv_search_fail = (TextView) findViewById(R.id.tv_search_fail);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_cancel:
                dismiss();
                break;
        }
    }

    public void setListener(View.OnClickListener listener) {
        dialog_upload.setOnClickListener(listener);
        dialog_cancel.setOnClickListener(listener);
    }

    public void setContent(int num, int total, int success, int fail){
        tv_search_num.setText(num + "次");
        tv_search_total.setText(total + "人");
        tv_search_success.setText(success + "人");
        tv_search_fail.setText(fail + "人");
    }

}
