package com.lhzw.searchlocmap.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.lhzw.searchlocmap.bdsignal.BDSignal;
import com.lhzw.searchlocmap.bean.HttpPersonInfo;
import com.lhzw.searchlocmap.bean.MessageInfoIBean;
import com.lhzw.searchlocmap.constants.Constants;
import com.lhzw.searchlocmap.db.dao.CommonDBOperator;
import com.lhzw.searchlocmap.db.dao.DatabaseHelper;
import com.lhzw.searchlocmap.ui.ShortMessUploadActivity;
import com.lhzw.searchlocmap.utils.BaseUtils;
import com.lhzw.uploadmms.UploadInfoBean;

import java.util.ArrayList;
import java.util.List;
import com.lhzw.searchlocmap.R;

public class BDUploadReceiver extends BroadcastReceiver {
    private DatabaseHelper<?> helper;
    private Dao<MessageInfoIBean, Integer> mesDao;
    private Dao<HttpPersonInfo, Integer> httpDao;
    private final String TAG = "BDUploadReceiver";
    private List<UploadInfoBean> list = new ArrayList<UploadInfoBean>();
    private static final String Instruction = "instruction";
    private static final String BDSCK = "BDSCK";
    private static final String mDBPOW = "BDPOW";
    private static final String strRevSendBroadcastActionName = "nci.hq.bd.service.RevSend";
    private static String[] SIG_INFO = new String[]{Constants.SIG_RET_INFO_0,
            Constants.SIG_RET_INFO_1, Constants.SIG_RET_INFO_2,
            Constants.SIG_RET_INFO_3, Constants.SIG_RET_INFO_4,
            Constants.SIG_RET_INFO_5, Constants.SIG_RET_INFO_6,
            Constants.SIG_RET_INFO_7, Constants.SIG_RET_INFO_8,
            Constants.SIG_RET_INFO_9};
    private static List<Intent> taskQueue = new ArrayList<Intent>();
    private static boolean isRunning = false;
    private Context mContext;
    private static float[] values = new float[10];

    @Override
    public void onReceive(Context mContext, Intent intent) {
        // TODO Auto-generated method stub
        helper = DatabaseHelper.getHelper(mContext);
        mesDao = helper.getMesgInfoDao();
        httpDao = helper.getHttpPerDao();
        this.mContext = mContext;
        taskQueue.add(intent);
        if (!isRunning) {
            doTask();
        }
    }

    private synchronized void doTask() {
        if (!isRunning && !taskQueue.isEmpty()) {
            isRunning = true;
            new Thread(new Action()).start();
        }
    }

    private class Action implements Runnable {
        @Override
        public void run() {
            do {
                Intent intent = taskQueue.get(0);
                taskQueue.remove(0);
                if (intent.getAction().equals(Constants.ACTION_MESSAGE)) {
                    if (intent.getIntExtra("bdType", -1) == 2) {
                        String num = intent.getStringExtra("bdNum");
                        int msg_Id = intent.getIntExtra("msg_Id", -1);
                        List<HttpPersonInfo> dipList = CommonDBOperator.queryByKeys(httpDao, "id", msg_Id + "");
                        if (dipList != null && dipList.size() > 0) {
                            Log.e("Tag", "searchLocMap receive message  type 2  " + num);
                            try {
                                String str = intent.getStringExtra("result");
                                MessageInfoIBean item = new MessageInfoIBean(num, System.currentTimeMillis(), str, ShortMessUploadActivity.MESSAGE_RECEIVE, Constants.MESSAGE_UNREAD, msg_Id);
                                CommonDBOperator.saveToDB(mesDao, item);
                                Intent mmsIntent = new Intent(Constants.BD_Mms_ACTION);
                                mmsIntent.putExtra("ID", msg_Id);
                                mContext.sendBroadcast(mmsIntent);
                                showNotifcationInfo(mContext, dipList.get(0).getRealName(), R.drawable.icon_chat);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            dipList.clear();
                        }
                    }
                } else if (intent.getAction().equals(strRevSendBroadcastActionName)) {
                    if (intent.getExtras().getString(Instruction).equals(mDBPOW) || intent.getExtras().getString(Instruction).equals(BDSCK)) {
                        String sig = intent.getStringExtra(Constants.SIG_RET);
                        if ("1".equals(sig)) {
                            int tmp = obtainMaxValue(intent.getExtras());
                            Intent bgsinal_list = new Intent(Constants.BD_SIGNAL_LIST);
                            bgsinal_list.putExtra("values", values);
                            mContext.sendBroadcast(bgsinal_list);
                            if (BDSignal.value != tmp) {
                                BDSignal.value = tmp;
                                Intent sinalIntent = new Intent(Constants.BD_SIG_ACTION);
                                mContext.sendBroadcast(sinalIntent);
                                Log.e("Tag", "value = " + tmp);
                            }
                        }
                    }
                }
            } while (!taskQueue.isEmpty());
            isRunning = false;
            doTask();
        }
    }

    private int obtainMaxValue(Bundle bundle) {
        int max = 0;
        try {
            for (int pos = 0; pos < SIG_INFO.length; pos++) {
                String value = bundle.getString(SIG_INFO[pos]);
                if (!BaseUtils.isStringEmpty(value)) {
                    int tmp = Integer.parseInt(value);
                    if (tmp > max) {
                        max = tmp;
                    }
                    values[pos] = Float.valueOf(value) / 4;
                } else {
                    values[pos] = 0;
                }

            }
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            return max;
        }
        return max;
    }

    public void showNotifcationInfo(Context mContext, String num, int id) {
        // 设置通知到类型SOS和通知的详细信息（接收到XXX发送的SOS请求）
        Notification n = new Notification.Builder(mContext)
                .setContentTitle(mContext.getString(R.string._noe_short_message)).setContentText(mContext.getString(R.string._noe_receiver_signal).replace("@", num))
                .setSmallIcon(id).setWhen(System.currentTimeMillis())
                .build();
        n.flags = Notification.FLAG_AUTO_CANCEL;
        // n.number=count;
//		n.defaults = Notification.DEFAULT_SOUND;
        Uri sound= Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.beep);
        n.sound = sound;
        // 从系统服务中获得通知管理器
        NotificationManager nm = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        // 执行通知
        nm.notify("Communication", id, n);
    }
}
