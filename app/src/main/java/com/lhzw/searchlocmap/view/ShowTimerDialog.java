package com.lhzw.searchlocmap.view;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.lhzw.searchlocmap.R;

public class ShowTimerDialog extends AlertDialog implements View.OnClickListener, AdapterView.OnItemClickListener {
    private Button bt_timer_cancel;
    private ListView listview;
    private onTimeItemClickListener onItemListner;

    private ShowTimerDialog(Context context, int theme) {
        super(context, theme);
        // TODO Auto-generated constructor stub
    }

    public ShowTimerDialog(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_search_timer);
        init();
    }

    private void init() {
        setCancelable(false);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        bt_timer_cancel = (Button) findViewById(R.id.bt_timer_cancel);
        bt_timer_cancel.setOnClickListener(this);
        listview = (ListView) findViewById(R.id.listview);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_timer_cancel:
                this.dismiss();
                break;
        }
    }

    public void setAdapter(BaseAdapter adapter) {
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(ShowTimerDialog.this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(onItemListner != null) {
            onItemListner.onTimerClick(position);
        }
        Log.e("Tag", position + "   " + id);
        this.dismiss();
    }

    public void setOnTimeItemClickListener(onTimeItemClickListener onItemListner){
        this.onItemListner = onItemListner;
    }

    public interface onTimeItemClickListener{
        public void onTimerClick(long pos);
    }
}
