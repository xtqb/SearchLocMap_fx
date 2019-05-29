package com.lhzw.searchlocmap.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lhzw.searchlocmap.R;
import com.lhzw.searchlocmap.bean.HttpPersonInfo;
import com.lhzw.searchlocmap.utils.DateUtils;

import java.util.List;

/**
 * Created by hecuncun on 2019/5/24
 */
public class ShortMsgAdapter extends BaseQuickAdapter<HttpPersonInfo,BaseViewHolder>{
    public ShortMsgAdapter(List<HttpPersonInfo> list) {
        super(R.layout.item_short_msg, list);
    }

    @Override
    protected void convert(BaseViewHolder helper, HttpPersonInfo item) {
         TextView tvName = helper.getView(R.id.tv_name);
         TextView tvTime = helper.getView(R.id.tv_time);
         TextView tvMsgNum = helper.getView(R.id.tv_msg_num);

         tvName.setText(TextUtils.isEmpty(item.getRealName()) ? "未知名" : item.getRealName());
         tvTime.setText(DateUtils.getLongToDateString(item.getCurrentMsgTime()));
         if(item.getUnReadMsgNum() == 0){
             tvMsgNum.setVisibility(View.GONE);
         }else {
             tvMsgNum.setVisibility(View.VISIBLE);
             tvMsgNum.setText(String.valueOf(item.getUnReadMsgNum()));
         }


    }
}
