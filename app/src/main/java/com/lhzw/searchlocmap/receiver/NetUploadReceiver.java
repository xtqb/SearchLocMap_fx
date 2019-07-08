package com.lhzw.searchlocmap.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lhzw.searchlocmap.utils.ComUtils;
import com.lhzw.uploadmms.UploadInfoBean;

public class NetUploadReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        UploadInfoBean item = intent.getParcelableExtra("uploadBean");
        ComUtils.getInstance().netCom(item);
    }
}
