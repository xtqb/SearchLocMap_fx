package com.lhzw.searchlocmap.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lhzw.searchlocmap.R;

public class ShowStateDetailDialog extends AlertDialog implements DialogInterface.OnDismissListener, View.OnClickListener {
    private StateTreeView1 his_state_tree;
    private Button dialog_cancel;
    private TextView tv_detail;
    private Context mContext;
    private int total;
    private int success;
    private int fail;
    private String body;
    private ShowDetailDialog dialog;

    private ShowStateDetailDialog(Context mContext, int theme) {
        super(mContext, theme);
        // TODO Auto-generated constructor stub
        this.mContext = mContext;
    }

    public ShowStateDetailDialog(Context mContext, int total, int success, int fail, String body) {
        super(mContext);
        // TODO Auto-generated constructor stub
        this.mContext = mContext;
        this.total = total;
        this.success = success;
        this.fail = fail;
        this.body = body;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_state_detail);
        init();
    }

    private void init() {
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        initView();
        initData();
        setListener();
    }

    private void initView() {
        his_state_tree = (StateTreeView1) findViewById(R.id.his_state_tree);
        dialog_cancel = (Button) findViewById(R.id.dialog_cancel);
        tv_detail = (TextView) findViewById(R.id.tv_detail);
    }

    private void initData() {
    }

    private void setListener() {
        dialog_cancel.setOnClickListener(this);
        setOnDismissListener(this);
        tv_detail.setOnClickListener(this);
    }


    public void refleshView(String[] data) {
        his_state_tree.refleshView(data);
    }


    @Override
    public void onDismiss(DialogInterface dialog) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_cancel:
                this.dismiss();
                break;
            case R.id.tv_detail:
                showDetailDialog();
                break;
        }
    }

    private void showDetailDialog() {
        if(dialog == null) {
            dialog = new ShowDetailDialog(mContext, total, success, fail, body);
            dialog.show();
            dialog.intContent();
        } else {
            dialog.show();
        }

    }
}
