package com.lhzw.searchlocmap.view;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.lhzw.searchlocmap.R;
import com.lhzw.searchlocmap.bdsignal.BDSignal;
import com.lhzw.searchlocmap.constants.Constants;

public class ShowStateTreeDialog extends AlertDialog implements DialogInterface.OnDismissListener, View.OnClickListener {
    private Button bt_timer_cancel;
    private ListView listview;
    private HistogramBar his_bar;
    private StateTreeView1 his_state_tree;
    private Button dialog_timer_close;
    private Button dialog_cancel;
    private Context mContext;
    private OnSearchCancelListener listener;
    private TextView tv_detail;
    private int total;
    private int success;
    private int fail;
    private String body;
    private ShowDetailDialog dialog;
    private float bdValue;
    private boolean isChange;
    private TextView tv_signal_level;
    private boolean isDestroy;

    private ShowStateTreeDialog(Context mContext, int theme) {
        super(mContext, theme);
        // TODO Auto-generated constructor stub
        this.mContext = mContext;
    }

    public ShowStateTreeDialog(Context mContext) {
        super(mContext);
        // TODO Auto-generated constructor stub
        this.mContext = mContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bdsignal);
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
        his_bar = (HistogramBar) findViewById(R.id.his_bar);
        his_state_tree = (StateTreeView1) findViewById(R.id.his_state_tree);
        dialog_timer_close = (Button) findViewById(R.id.dialog_timer_close);
        dialog_cancel = (Button) findViewById(R.id.dialog_cancel);
        tv_detail = (TextView) findViewById(R.id.tv_detail);
        tv_signal_level = (TextView) findViewById(R.id.tv_signal_level);
    }

    private void initData() {
        registerBroadcastReceiver();
        bdValue = 0;
        isChange = false;
    }

    private void setListener() {
        dialog_timer_close.setOnClickListener(this);
        dialog_cancel.setOnClickListener(this);
        tv_detail.setOnClickListener(this);
        setOnDismissListener(this);
    }

    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.BD_SIGNAL_LIST);
        mContext.registerReceiver(receiver, filter);
    }

    public void refleshView(String[] data) {
        if(data != null) {
            his_state_tree.refleshView(data);
        }
    }

    public void setEnable(boolean state){
        if(state){
            tv_detail.setVisibility(View.VISIBLE);
        } else {
            tv_detail.setVisibility(View.GONE);
        }

    }

    public void showDialog() {
        isDestroy = false;
        this.show();
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(isDestroy) return;
            // 刷新 柱状图
            float[] values = intent.getFloatArrayExtra("values");
            his_bar.refleshView(values);
            switch (BDSignal.value) {
                case 1:
                    tv_signal_level.setText("差");
                    tv_signal_level.setBackgroundColor(mContext.getResources().getColor(R.color.red));
                    break;
                case 2:
                    tv_signal_level.setText("中");
                    tv_signal_level.setBackgroundColor(mContext.getResources().getColor(R.color.yellow));
                    break;
                case 3:
                    tv_signal_level.setText("良");
                    tv_signal_level.setBackgroundColor(mContext.getResources().getColor(R.color.green3));
                    break;
                case 4:
                    tv_signal_level.setText("优");
                    tv_signal_level.setBackgroundColor(mContext.getResources().getColor(R.color.green));
                    break;
            }
        }
    };

    @Override
    public void onDismiss(DialogInterface dialog) {

        isDestroy = true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_timer_close:
                if(listener != null) {
                    listener.onCancel();
                }
                try {
                    if (receiver != null) {
                        mContext.unregisterReceiver(receiver);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                this.dismiss();
                break;
            case R.id.dialog_cancel:
                this.dismiss();
                break;
            case R.id.tv_detail:
                showDetailDialog();
                break;
        }
    }

    public void initDetail(int total, int success, int fail, String body){
        this.total = total;
        this.success = success;
        this.fail = fail;
        this.body = body;
    }

    public void showDetailDialog() {
        if(dialog == null) {
            dialog = new ShowDetailDialog(mContext, total, success, fail, body);
            dialog.show();
            dialog.intContent();
        } else {
            dialog.show();
        }

    }

    public void setOnSearchCancelListener(OnSearchCancelListener listener){
        this.listener = listener;
    }

    public interface OnSearchCancelListener {
        public void onCancel();
    }

}
