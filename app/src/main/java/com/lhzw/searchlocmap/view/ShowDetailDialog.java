package com.lhzw.searchlocmap.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.lhzw.searchlocmap.R;
import com.lhzw.searchlocmap.bean.DetailItem;

import java.util.ArrayList;
import java.util.List;

public class ShowDetailDialog extends AlertDialog implements DialogInterface.OnDismissListener, View.OnClickListener {

    private Button dialog_cancel;
    private ListView listview;
    private List<DetailItem> list;
    private int total;
    private int success;
    private int fail;
    private String body;
    private TextView tv_total;
    private TextView tv_succeess;
    private TextView tv_fail;
    private Context mContext;

    private ShowDetailDialog(Context mContext, int theme) {
        super(mContext, theme);
        // TODO Auto-generated constructor stub
    }

    public ShowDetailDialog(Context mContext, int total, int success, int fail, String body) {
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
        setContentView(R.layout.dialog_detail);
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
        dialog_cancel = (Button) findViewById(R.id.dialog_cancel);
        listview = (ListView) findViewById(R.id.listview);
        tv_total = (TextView) findViewById(R.id.tv_total);
        tv_succeess = (TextView) findViewById(R.id.tv_succeess);
        tv_fail = (TextView) findViewById(R.id.tv_fail);
    }

    private void initData() {
        list = new ArrayList<>();
        initList();
    }

    private void initList() {
        String[] item = body.split("-");
        int counter = 1;
        for (String str : item) {
            String[] bean = str.split(",");
            DetailItem detail = null;
            if (counter <= success) {
                detail = new DetailItem(bean[0], bean[1], "成功");
            } else {
                detail = new DetailItem(bean[0], bean[1], "失败");
            }
            counter++;
            list.add(detail);
        }
    }

    private void setListener() {
        setOnDismissListener(this);
        dialog_cancel.setOnClickListener(this);
    }

    private BaseAdapter adapter = new BaseAdapter() {
        private ViewHolder holder;

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_history_state, null);
                holder = new ViewHolder();
                holder.tv_num = (TextView) convertView.findViewById(R.id.tv_num);
                holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                holder.tv_state = (TextView) convertView.findViewById(R.id.tv_state);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tv_num.setText((position + 1) + " " + list.get(position).getName() + "   ");
            holder.tv_time.setText(list.get(position).getNum());
            if ("成功".equals(list.get(position).getState())) {
                holder.tv_state.setTextColor(mContext.getResources().getColor(R.color.text_color));
            } else {
                holder.tv_state.setTextColor(mContext.getResources().getColor(R.color.red));
            }
            holder.tv_state.setText(list.get(position).getState());
            return convertView;
        }
    };

    private class ViewHolder {
        private TextView tv_num;
        private TextView tv_time;
        private TextView tv_state;
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
        }
    }

    public void intContent() {
        tv_total.setText(total + "");
        tv_succeess.setText(success + "");
        tv_fail.setText(fail + "");
        listview.setAdapter(adapter);
    }
}
