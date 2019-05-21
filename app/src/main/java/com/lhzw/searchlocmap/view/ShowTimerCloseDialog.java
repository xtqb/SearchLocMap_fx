package com.lhzw.searchlocmap.view;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.lhzw.searchlocmap.R;

public class ShowTimerCloseDialog extends AlertDialog implements View.OnClickListener {
    private Button dialog_cancel;
    private Button dialog_timer_close;

    private ShowTimerCloseDialog(Context context, int theme) {
        super(context, theme);
        // TODO Auto-generated constructor stub
    }

    public ShowTimerCloseDialog(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_search_stop_note);
        init();
    }

    private void init() {
        setCancelable(false);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog_cancel = (Button) findViewById(R.id.dialog_cancel);
        dialog_cancel.setOnClickListener(this);
        dialog_timer_close = (Button) findViewById(R.id.dialog_timer_close);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_cancel:
                this.dismiss();
                break;
        }
    }

    public void setListener(View.OnClickListener listener) {
        dialog_timer_close.setOnClickListener(listener);
    }
}
