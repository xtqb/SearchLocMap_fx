package com.lhzw.searchlocmap.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.ProtocolParser;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.gtmap.common.GT_GeoArithmetic;
import com.gtmap.util.GeoPoint;
import com.j256.ormlite.dao.Dao;
import com.lhzw.searchlocmap.R;
import com.lhzw.searchlocmap.bean.PersonalInfo;
import com.lhzw.searchlocmap.constants.Constants;
import com.lhzw.searchlocmap.constants.SPConstants;
import com.lhzw.searchlocmap.db.dao.CommonDBOperator;
import com.lhzw.searchlocmap.db.dao.DatabaseHelper;
import com.lhzw.searchlocmap.event.EventBusBean;
import com.lhzw.searchlocmap.ui.MainActivity;
import com.lhzw.searchlocmap.utils.BaseUtils;
import com.lhzw.searchlocmap.utils.ComUtils;
import com.lhzw.searchlocmap.utils.LogWrite;
import com.lhzw.searchlocmap.utils.SpUtils;
import com.lhzw.uploadmms.UploadInfoBean;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class WatchSignalReceiver extends BroadcastReceiver {
    private Context mContext;
    private DatabaseHelper<?> helper;
    private Dao<PersonalInfo, Integer> persondao;
    private static List<UploadInfoBean> uploadList = new ArrayList<UploadInfoBean>();
    private static final int DistanRANGE = 15;//距离限定
    private static boolean isUpload;
    private static int data_type;
    public static DecimalFormat df = new DecimalFormat("######0.0");
    private static ConcurrentLinkedQueue<ProtocolParser> taskQueue = new ConcurrentLinkedQueue<ProtocolParser>();
    private static boolean isRuning = false;
    private static final int TOAST = 0x0001;
    private static final int timeOut = 3 * 60 * 1000;
    private ComUtils mComUtils = ComUtils.getInstance();

    @Override
    public void onReceive(Context context, Intent intent) {
        // 数据库
        helper = DatabaseHelper.getHelper(context);
        persondao = helper.getPersonalInfoDao();
        mContext = context;
        if (SpUtils.getBoolean(SPConstants.PERSON_ENTER, false)) { //控制是否正在人员录入
            return;
        }
        try {
            if (intent != null) {
                ProtocolParser parser = intent.getParcelableExtra("result");
                if (parser == null) return;
                taskQueue.add(parser);
                Log.e("Tag", "receive 433 communication");
                doTask();
            }
        } catch (Exception e) {
            Log.e("Tag", "catch exception");
        }
    }

    private void initDB(ProtocolParser parser) {
        byte[] typeKey = parser.getCmdKey();
//		if (SpUtils.getBoolean(SPConstants.COMMON_SWITCH, false)                  // 搜索状态停止，禁止接收搜索信息
//				|| typeKey[0] == ((byte) 0x12) || typeKey[0] == ((byte) 0xA1) || typeKey[0] == ((byte) 0x19)) {
        double lon = BaseUtils.ByteToStringForLocInfo(parser.getLongitude());
        double lat = BaseUtils.ByteToStringForLocInfo(parser.getLatitude());
        String register_num = BaseUtils.traslation(parser.getPersonNum()).substring(0, 10);
        long locTime = BaseUtils.byteArrToTime(parser.getTimeStamp());
        Log.e("locTime", "locTime : " + BaseUtils.getDateStr(locTime) + "  register : " + register_num);
        List<PersonalInfo> list = CommonDBOperator.queryByKeys(persondao, "num", register_num);
        if (list != null && list.size() > 0) {
            Intent sosflash = new Intent("com.lhzw.soildersos.change");
            isUpload = false;
            data_type = Constants.TX_COMMON;
            switch (typeKey[0]) {
                case (byte) 0xA3:// common
                case (byte) 0x11: // search
                    sosflash.putExtra("has_sos", false);
                    if (Math.abs(lon) < 0.00001 && Math.abs(lat) < 0.00001) {
                        list.get(0).setState(Constants.PERSON_UNDETERMINED);
                        list.get(0).setState1(Constants.PERSON_COMMON);
                    } else {
                        list.get(0).setState(Constants.PERSON_COMMON);
                    }
                    if (!SpUtils.getBoolean(SPConstants.UPLOAD_PATTERN, Constants.UPLOAD_PATTERN_DEFAULT)) {
                        showNotifcationInfo(mContext, register_num, R.drawable.icon_qita2, "COMMON");
                    }
//                    Log.e("receive433", "receive 433 TX_COMMON");
                    break;
                case (byte) 0xA1: // SOS
                case (byte) 0x12: // SOS
                    data_type = Constants.TX_SOS;
                    isUpload = true;
                    if (Math.abs(lon) < 0.00001 && Math.abs(lat) < 0.00001) {
                        list.get(0).setState(Constants.PERSON_UNDETERMINED);
                        list.get(0).setState1(Constants.PERSON_SOS);
                        sosflash.putExtra("has_sos", false);
                    } else {
                        sosflash.putExtra("has_sos", true);
                        list.get(0).setState(Constants.PERSON_SOS);
                    }
                    if (!SpUtils.getBoolean(SPConstants.UPLOAD_PATTERN, Constants.UPLOAD_PATTERN_DEFAULT)) {
                        showNotifcationInfo(mContext, register_num, R.drawable.icon_sos2, "SOS");
                    }
//                    Log.e("receive433", "receive 433 TX_SOS");
                    break;
                case (byte) 0x19:// 指令反馈,刷新列表
                    //先更新数据库的数据
                    Log.e("callback", "----------------------");
                    list.get(0).setFeedback(Constants.COMMAND_CONFIRMED);
                    CommonDBOperator.updateItem(persondao, list.get(0));//首先更新数据库数据
                    //发消息更新列表
                    EventBusBean eventBusBean = new EventBusBean();
                    eventBusBean.setCode(Constants.EVENT_CODE_REFRESH_COMMAND_STATE);
                    EventBus.getDefault().post(eventBusBean);
                    return;
            }
            //比较数据大小
            double distance = 0;
            //类型为sos,并可以上报,定位成功且距离有变动
            if (data_type == Constants.TX_SOS && isUpload && !"".equals(list.get(0).getLatitude()) && !"".equals(list.get(0).getLongitude()) && list.get(0).getLatitude() != null && Math.abs(Double.valueOf(list.get(0).getLatitude())) > 0.0001
                    && list.get(0).getLongitude() != null && Math.abs(Double.valueOf(list.get(0).getLongitude())) > 0.0001) {
                distance = GT_GeoArithmetic.ComputeDistanceOfTwoPoints(new GeoPoint(Double.valueOf(list.get(0).getLatitude()), Double.valueOf(list.get(0).getLongitude())),
                        new GeoPoint(lat, lon));
                if (distance > DistanRANGE) {//距离大于15m
                    isUpload = true;
                } else {
                    isUpload = false;
                }
            }

            //类型是sos就写入日志
            if (data_type == Constants.TX_SOS) {
                //写入日志
                try {
                    LogWrite writer = LogWrite.open();
                    String log = register_num + "\t" + "id : " + list.get(0).getOffset() + "\t" + LogWrite.df.format(System.currentTimeMillis()) + "\t  data_type = " + data_type + "\t lat = " + lat + "\t lon = " +
                            lon + "\t distance = " + df.format(distance) + " m";
                    writer.writeLog(log);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //间隔时间大于三分钟,换为可上传状态
            if (System.currentTimeMillis() - list.get(0).getTime() > timeOut) {
                isUpload = true;
                list.get(0).setTime(System.currentTimeMillis());
            }
            //指令时间大于1天,把当前时间变成指令时间
            if (Math.abs(locTime - System.currentTimeMillis()) > 24 * 60 * 60 * 1000) {
                locTime = System.currentTimeMillis();
            }
            list.get(0).setLatitude(lat + "");
            list.get(0).setLongitude(lon + "");
            list.get(0).setLocTime(locTime);
            //sos ,可上传状态,自动上报打开
            if (data_type == Constants.TX_SOS && isUpload && SpUtils.getBoolean(SPConstants.AUTO_REPORT, true)) {
                Log.e("Tag", "doTask  insert databases");
                uploadList.clear();
                String latLonStr = SpUtils.getFloat(SPConstants.LAT_ADDR, Constants.CENTRE_LAT) + ","
                        + SpUtils.getFloat(SPConstants.LON_ADDR, Constants.CENTRE_LON);
                UploadInfoBean bean = new UploadInfoBean(Constants.TX_JZH, Constants.TX_SOS, System.currentTimeMillis(), lat + "," +
                        lon, locTime + "", list.get(0).getOffset() + "", latLonStr, SpUtils.getLong(SPConstants.LOC_TIME, System.currentTimeMillis()), 1+"",
                        SpUtils.getString(Constants.UPLOAD_JZH_NUM, Constants.BD_NUM_DEF), 0, -1, register_num);
                uploadList.add(bean);
                if (SpUtils.getInt(SPConstants.SP_BD_MODE, Constants.UOLOAD_STATE_0) == Constants.UOLOAD_STATE_1) {
                    bean.setTx_type(Constants.TX_QZH);
                    bean.setNum(SpUtils.getString(Constants.UPLOAD_QZH_NUM, Constants.BD_NUM_DEF));
                    uploadList.add(bean);
                }
                mComUtils.uploadBena(uploadList);  //上传到北斗服务
                mHnadler.sendEmptyMessage(TOAST);  //显示上报数据弹窗
            }

            CommonDBOperator.updateItem(persondao, list.get(0));
            mContext.sendBroadcast(sosflash);
            list.clear();
        } else {
            if (!SpUtils.getBoolean(SPConstants.UPLOAD_PATTERN, Constants.UPLOAD_PATTERN_DEFAULT)) {
                switch (typeKey[0]) {
                    case (byte) 0xA3:// common
                    case (byte) 0x11: // search
                        showNotifcationInfo(mContext, register_num,
                                R.drawable.icon_qita2, "COMMON",
                                mContext.getString(R.string.notify_note));
                        break;
                    case (byte) 0xA1: // SOS
                    case (byte) 0x12: // SOS
                        showNotifcationInfo(mContext, register_num,
                                R.drawable.icon_sos2, "SOS",
                                mContext.getString(R.string.notify_note));
                        break;
                }
            }
        }
    }

    public void showNotifcationInfo(Context context, String text, int id, String type, String subText) {
        Notification n = new Notification.Builder(context)
                .setContentTitle(context.getString(R.string._noe_receiver_signal).replace("@", type)).setContentText(text)
                .setSubText(subText).setSmallIcon(id).setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), id))
                .setWhen(System.currentTimeMillis())
                // .setContentIntent(contentIntent)
                .build();
        n.flags = Notification.FLAG_AUTO_CANCEL;
        // n.number=count;
        n.defaults = Notification.DEFAULT_SOUND;
        // 从系统服务中获得通知管理器
        NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        // 执行通知
        nm.notify(id, n);
    }

    public void showNotifcationInfo(Context context, String text, int id, String type) {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);
        // 设置通知到类型SOS和通知的详细信息（接收到XXX发送的SOS请求）
        Notification n = new Notification.Builder(context).setContentTitle(
                context.getString(R.string._noe_receiver_signal).replace("@", type)).setContentText(text)
                .setSmallIcon(id).setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), id)).setWhen(System.currentTimeMillis())
                .setContentIntent(contentIntent).build();
        n.flags = Notification.FLAG_AUTO_CANCEL;
        // n.number=count;
        n.defaults = Notification.DEFAULT_SOUND;
        // 从系统服务中获得通知管理器
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // 执行通知
        nm.notify(1, n);
    }

    private void doTask() {
        if (!isRuning && !taskQueue.isEmpty()) {
            isRuning = true;
            new Thread(new Action()).start();
        }
    }

    private class Action implements Runnable {

        @Override
        public void run() {
            try {
                do {
                    initDB(taskQueue.poll());
                } while (!taskQueue.isEmpty());
                isRuning = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Handler mHnadler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TOAST:
                    Toast.makeText(mContext, R.string.upload_success, Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };
}
