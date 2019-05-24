package com.lhzw.searchlocmap.view;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.lhzw.searchlocmap.R;

public class ShowUpdateApkDialog extends AlertDialog implements View.OnClickListener {
    private Button dialog_cancel;
    private Button dialog_update;

    private ShowUpdateApkDialog(Context context, int theme) {
        super(context, theme);
        // TODO Auto-generated constructor stub
    }

    public ShowUpdateApkDialog(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_update_apk_note);
        init();
    }

    private void init() {
        setCancelable(false);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog_cancel = (Button) findViewById(R.id.dialog_cancel);
        dialog_cancel.setOnClickListener(this);
        dialog_update = (Button) findViewById(R.id.dialog_update);
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
        dialog_update.setOnClickListener(listener);
    }
}
