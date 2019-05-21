package com.lhzw.searchlocmap.view;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lhzw.searchlocmap.R;

public class DropListPopupWindows extends PopupWindow {

    private View view;
    private int w;
    private ListView listview;
    private String[] strArrs;
    private Context mContext;
    private OnItemClickListener listener;

    public DropListPopupWindows(Activity mContext, String[] strArr,
                                OnItemClickListener listener) {
        view = LayoutInflater.from(mContext).inflate(
                R.layout.popuwin_drop_list, null);
        setContentView(view);
        this.mContext = mContext;
        this.listener = listener;
        this.strArrs = strArr;
        w = mContext.getWindowManager().getDefaultDisplay().getWidth();
        init();
    }

    private void init() {
        // TODO Auto-generated method stub
        initPara();
        listview = (ListView) view.findViewById(R.id.listview);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(listener);
    }

    private void initPara() {
        // TODO Auto-generated method stub
        // 设置selectPicPopupWindow 弹出窗体的宽
        this.setWidth(w - 91);
        // 设置selectPicPopupWindow 弹出窗体的高
        this.setHeight(LayoutParams.WRAP_CONTENT);

        // 设置SelectPicPopupWindow 弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);

        // 设置SelectPopupWindow弹出窗体动画效果
        this.setAnimationStyle(android.R.style.Animation_Dialog);
    }

    private BaseAdapter adapter = new BaseAdapter() {
        ViewHolder holder;

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.item_droplist, null);
                holder = new ViewHolder();
                holder.tv_name = (TextView) convertView
                        .findViewById(R.id.tv_name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tv_name.setText(strArrs[position]);
            return convertView;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return strArrs.length;
        }
    };

    private class ViewHolder {
        private TextView tv_name;
    }

}
