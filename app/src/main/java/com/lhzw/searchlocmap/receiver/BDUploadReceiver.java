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
import com.lhzw.searchlocmap.R;
import com.lhzw.searchlocmap.bdsignal.BDSignal;
import com.lhzw.searchlocmap.bean.LocalBDNum;
import com.lhzw.searchlocmap.bean.HttpPersonInfo;
import com.lhzw.searchlocmap.bean.MessageInfoIBean;
import com.lhzw.searchlocmap.constants.Constants;
import com.lhzw.searchlocmap.db.dao.CommonDBOperator;
import com.lhzw.searchlocmap.db.dao.DatabaseHelper;
import com.lhzw.searchlocmap.event.EventBusBean;
import com.lhzw.searchlocmap.ui.ShortMessUploadActivity;
import com.lhzw.searchlocmap.utils.BaseUtils;
import com.lhzw.uploadmms.UploadInfoBean;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

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
    private Dao<LocalBDNum, Integer> mBdNumDao;

    @Override
    public void onReceive(Context mContext, Intent intent) {
        // TODO Auto-generated method stub
        helper = DatabaseHelper.getHelper(mContext);
        mesDao = helper.getMesgInfoDao();
        httpDao = helper.getHttpPerDao();
        mBdNumDao = helper.getBdNumDao();
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
                        //TODO 此处要消息分清是来自平台还是手持  平台按照msg_id查的是平台自己的信息即发送者的信息  目前是没有问题的
                        // TODO 手持机的话要查询北斗号确定发送者的信息  并在本机中将消息保存到发送人的名下(messageId 为发送人   如根据北斗号查不到此人的信息 则属于陌生消息 存表的话msg_id 为北斗号)
                        String num = intent.getStringExtra("bdNum");//发送者的北斗号
                        int msg_Id = intent.getIntExtra("msg_Id", -1);//平台的人的ID
                        String str = intent.getStringExtra("result");//内容
                        List<LocalBDNum> localBDNums = CommonDBOperator.queryByKeys(mBdNumDao, "num", num);
                        boolean isPlatform = false;
                        if (localBDNums != null && localBDNums.size() > 0) {
                            //此消息来源于平台
                            isPlatform = true;
                        }

                        //消息来源于平台
                        if (isPlatform) {
                            List<HttpPersonInfo> dipList = CommonDBOperator.queryByKeys(httpDao, "id", msg_Id + "");//这个查到的是平台发的消息
                            if (dipList != null && dipList.size() > 0) {
                                Log.e("Tag", "searchLocMap receive message  type 2  " + num);
                                MessageInfoIBean item = new MessageInfoIBean(num, System.currentTimeMillis(), str, ShortMessUploadActivity.MESSAGE_RECEIVE, Constants.MESSAGE_UNREAD, msg_Id);
                                CommonDBOperator.saveToDB(mesDao, item);
                                Intent mmsIntent = new Intent(Constants.BD_Mms_ACTION);
                                mmsIntent.putExtra("ID", msg_Id);
                                mContext.sendBroadcast(mmsIntent);
                                showNotifcationInfo(mContext, dipList.get(0).getRealName(), R.drawable.icon_chat);
                                dipList.clear();
                            }
                        } else {//消息来源于手持机
                            List<HttpPersonInfo> personInfos = CommonDBOperator.queryByKeys(httpDao, "deviceNumbers", num);
                            if (personInfos != null && personInfos.size() > 0) {//发送人在本地的数据库里
                                HttpPersonInfo personInfo = personInfos.get(0);
                                MessageInfoIBean msgBean = new MessageInfoIBean(num, System.currentTimeMillis(), str, ShortMessUploadActivity.MESSAGE_RECEIVE, Constants.MESSAGE_UNREAD, personInfo.getId());
                                CommonDBOperator.saveToDB(mesDao, msgBean);//保存为发送人的消息
                                Intent mmsIntent = new Intent(Constants.BD_Mms_ACTION);
                                mmsIntent.putExtra("ID", personInfo.getId());
                                mContext.sendBroadcast(mmsIntent);
                                showNotifcationInfo(mContext, personInfo.getRealName(), R.drawable.icon_chat);
                                personInfos.clear();
                            } else {//发送人不在数据库
                                //todo 发送人不在人员表 则新增人员到表里
                                MessageInfoIBean msgBean = new MessageInfoIBean(num, System.currentTimeMillis(), str, ShortMessUploadActivity.MESSAGE_RECEIVE, Constants.MESSAGE_UNREAD, Integer.parseInt(num));
                                CommonDBOperator.saveToDB(mesDao, msgBean);//保存为发送人的消息
                                Intent mmsIntent = new Intent(Constants.BD_Mms_ACTION);
                                mmsIntent.putExtra("ID", num);
                                mContext.sendBroadcast(mmsIntent);
                                showNotifcationInfo(mContext, num, R.drawable.icon_chat);
                            }

                        }
                        //收到消息  更新完信息数据库,通知最近联系人列表刷新
                        EventBusBean eventBusBean = new EventBusBean();
                        eventBusBean.setCode(Constants.EVENT_CODE_REFRESH_MSG_LIST);
                        EventBus.getDefault().post(eventBusBean);
                    }
                } else if (intent.getAction().equals(strRevSendBroadcastActionName)) {
                    if (intent.getExtras().getString(Instruction).equals(mDBPOW) || intent.getExtras().getString(Instruction).equals(BDSCK)) {
                        String sig = intent.getStringExtra(Constants.SIG_RET);
                        if ("1".equals(sig)) {
                            int tmp = obtainMaxValue(intent.getExtras());
                            Intent bgsinal_list = new Intent(Constants.BD_SIGNAL_LIST);
                            bgsinal_list.putExtra("values", values);
                            mContext.sendBroadcast(bgsinal_list);
                            BDSignal.value = tmp;
                            Intent sinalIntent = new Intent(Constants.BD_SIG_ACTION);
                            mContext.sendBroadcast(sinalIntent);
                            Log.e("Tag", "value = " + tmp);
                        }
                    }
                }
            } while (!taskQueue.isEmpty());
            isRunning = false;
            doTask();
        }
    }

    private int obtainMaxValue(Bundle bundle) {
        int[] maxArr = new int[3];
        int strength = 1;
        try {
            for (int pos = 0; pos < SIG_INFO.length; pos++) {
                String value = bundle.getString(SIG_INFO[pos]);
                if (!BaseUtils.isStringEmpty(value)) {
                    int tmp = Integer.parseInt(value);
                    for (int i = 0; i < 3; i++) {
                        if (maxArr[i] < tmp) {
                            int temp = maxArr[i];
                            maxArr[i] = tmp;
                            tmp = temp;
                        }
                    }
                    values[pos] = Float.valueOf(value) / 4;
                } else {
                    values[pos] = 0;
                }
            }
            int total = maxArr[0] + maxArr[1] + maxArr[2];
            if (total >= 9) {
                strength = 4;
            } else if (total == 8 || total == 7) {
                strength = 3;
            } else if (total == 6) {
                if ((maxArr[0] == 4 && maxArr[1] == 1 && maxArr[2] == 1) ||
                        (maxArr[0] == 1 && maxArr[1] == 4 && maxArr[2] == 1) || (
                        maxArr[0] == 1 && maxArr[1] == 1 && maxArr[2] == 4)) {
                    strength = 2;
                } else {
                    strength = 3;
                }
            } else if (total == 5) {
                if ((maxArr[0] == 3 && maxArr[1] == 2 && maxArr[2] == 0) ||
                        (maxArr[0] == 3 && maxArr[1] == 0 && maxArr[2] == 2) ||
                        (maxArr[0] == 2 && maxArr[1] == 3 && maxArr[2] == 0) ||
                        (maxArr[0] == 2 && maxArr[1] == 0 && maxArr[2] == 3) ||
                        (maxArr[0] == 0 && maxArr[1] == 2 && maxArr[2] == 3) ||
                        (maxArr[0] == 0 && maxArr[1] == 3 && maxArr[2] == 2)) {
                    strength = 2;
                } else {
                    strength = 1;
                }
            } else {
                strength = 1;
            }
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            return strength;
        }
        return strength;
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
        Uri sound = Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.beep);
        n.sound = sound;
        // 从系统服务中获得通知管理器
        NotificationManager nm = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        // 执行通知
        nm.notify("Communication", id, n);
    }
}
