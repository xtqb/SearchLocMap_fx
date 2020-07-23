package com.lhzw.searchlocmap.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lhzw.searchlocmap.R;
import com.lhzw.searchlocmap.bean.LocPersonalInfo;
import com.lhzw.searchlocmap.constants.SPConstants;
import com.lhzw.searchlocmap.utils.CheckBoxState;
import com.lhzw.searchlocmap.utils.SpUtils;

import java.util.ArrayList;
import java.util.List;

public class PerManageAdapter extends BaseAdapter implements View.OnTouchListener {
    private int resourceId;
    private List<CheckBoxState> list = new ArrayList<CheckBoxState>();
    private TextView tv_num;
    private List<LocPersonalInfo> listPer;
    private Context context;
    private onCheckBoxChangeListener checkListener;
    private boolean isClickCheck;

    public PerManageAdapter() {
        super();
        // TODO Auto-generated constructor stub
    }

    public PerManageAdapter(List<LocPersonalInfo> listPer, Context context,
                            int textViewResourceId, TextView tv_num) {
        super();
        this.listPer = listPer;
        this.context = context;
        resourceId = textViewResourceId;
        this.tv_num = tv_num;
        isClickCheck = false;
        initList();
    }

    public void initList() {
        list.clear();
        if (null == listPer || listPer.size() == 0)
            return;
        for (int i = 0; i < listPer.size(); i++) {
            CheckBoxState item = new CheckBoxState(false, i, listPer.get(i)
                    .getNum());
            list.add(item);
        }
    }

    public void setCheckStateList(boolean isCheck) {
        for (CheckBoxState item : list) {
            item.setCheck(false);
        }
        for(int pos = 0; pos < list.size() && pos < 11; pos ++) {
            if(pos < 10) {
                list.get(pos).setCheck(true);
            } else if(pos == 10) {
                Toast.makeText(context, "最大解绑数量不能超过10个人", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public List<LocPersonalInfo> getListPer() {
        return listPer;
    }

    public void setListPer(List<LocPersonalInfo> listPer) {
        this.listPer = listPer;
    }

    public void updateItem(List<LocPersonalInfo> updateData) {
        listPer = updateData;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        if (listPer != null) {
            tv_num.setText(context.getResources().getString(R.string.total)
                    .replace("@", listPer.size() + ""));
            return listPer.size();
        } else {
            tv_num.setText(context.getResources().getString(R.string.total)
                    .replace("@", 0 + ""));
            return 0;
        }
    }

    @Override
    public LocPersonalInfo getItem(int position) {
        // TODO Auto-generated method stub
        return listPer.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    public void setList(ArrayList<LocPersonalInfo> list) {
        this.listPer = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LocPersonalInfo personInfo = getItem(position);
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resourceId, null);
            holder = new ViewHolder();
            holder.address = (TextView) convertView.findViewById(R.id.address_tv);
            holder.name = (TextView) convertView.findViewById(R.id.name_tv);
            holder.spread = (ImageView) convertView.findViewById(R.id.spread);
            holder.choose = (CheckBox) convertView.findViewById(R.id.choose);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (SpUtils.getBoolean(SPConstants.CHECKBOX_ISSHOW, false)) {
            holder.choose.setVisibility(View.VISIBLE);
        } else {
            holder.choose.setVisibility(View.GONE);
        }
        holder.choose.setTag(position);
        holder.choose.setChecked(list.get(position).isCheck());
        holder.choose.setOnCheckedChangeListener(listener);
        holder.choose.setOnTouchListener(this);
        holder.name.setText(personInfo.getName() + "(" + personInfo.getNum()
                + ")");
        return convertView;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                isClickCheck = true;
                break;
        }
        return false;
    }

    static class ViewHolder {
        TextView address;
        TextView name;
        ImageView spread;
        CheckBox choose;
    }

    private OnCheckedChangeListener listener = new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            if (!isClickCheck) return;
            isClickCheck = false;
            int counter = 0;
            for (CheckBoxState state : list) {
                if (state.isCheck()) {
                    counter++;
                }
            }
            if (counter > 9 && isChecked) {
                Toast.makeText(context, "最大解绑数量不能超过10个人", Toast.LENGTH_SHORT).show();
                buttonView.setChecked(false);
                notifyDataSetChanged();
                return;
            }
            if (buttonView.getTag() != null) {
                int pos = (Integer) buttonView.getTag();
                list.get(pos).setCheck(isChecked);
                list.get(pos).setID(pos);
                checkListener.itemSelected(pos);
            }
        }
    };

    public interface onCheckBoxChangeListener {
        public void itemSelected(int pos);
    }

    public List<CheckBoxState> getCheckStateList() {
        return list;
    }

    public void setItemcheckListener(onCheckBoxChangeListener checkListener) {
        this.checkListener = checkListener;
    }
}
