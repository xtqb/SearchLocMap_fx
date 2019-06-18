package com.lhzw.searchlocmap.adapter;

import android.graphics.Color;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lhzw.searchlocmap.R;
import com.lhzw.searchlocmap.bean.PersonalInfo;

import java.util.List;

/**
 * Created by hecuncun on 2019/6/4
 */
public class CommandStateAdapter extends BaseQuickAdapter<PersonalInfo,BaseViewHolder> {
    public CommandStateAdapter(List<PersonalInfo> data) {
        super(R.layout.item_command_state, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, PersonalInfo item) {
        TextView tvPosition = helper.getView(R.id.tv_position);
        TextView tvName = helper.getView(R.id.tv_name);
        TextView tvNum= helper.getView(R.id.tv_num);
        TextView tvState= helper.getView(R.id.tv_state);

        tvPosition.setText(String.valueOf(item.getId()));
        tvName.setText(item.getName());
        tvNum.setText(item.getNum());
        tvState.setText(item.getFeedback() == 1 ? "已确认" : "未确认");
        tvState.setTextColor(item.getFeedback() == 1 ? Color.parseColor("#00a944"): Color.parseColor("#F78434"));
    }
}
