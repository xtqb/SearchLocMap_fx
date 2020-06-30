package com.lhzw.searchlocmap.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lhzw.searchlocmap.application.SearchLocMapApplication;
import com.lhzw.searchlocmap.bean.NetResponseBean;
import com.lhzw.searchlocmap.bean.RequestCommonBean;
import com.lhzw.searchlocmap.bean.RequestFireLineBean;
import com.lhzw.searchlocmap.bean.RequestFirePointBean;
import com.lhzw.searchlocmap.bean.RequestMMSBean;
import com.lhzw.searchlocmap.constants.Constants;
import com.lhzw.searchlocmap.constants.SPConstants;
import com.lhzw.searchlocmap.net.CallbackListObserver;
import com.lhzw.searchlocmap.net.SLMRetrofit;
import com.lhzw.searchlocmap.net.ThreadSwitchTransformer;
import com.lhzw.uploadmms.UploadInfoBean;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import io.reactivex.Observable;

/**
 * Created by xtqb on 2019/3/27.
 */
public class ComUtils {
    public static ConcurrentLinkedQueue<UploadInfoBean> uploadQueue = new ConcurrentLinkedQueue<UploadInfoBean>();
    private static boolean isRunnable = false;
    private static ComUtils instance;
    private final Handler threadHandlerer;
    private boolean isNetConnection;

    public static ComUtils getInstance() {
        if (instance == null) {
            instance = new ComUtils();
        }
        return instance;
    }

    private ComUtils() {
        isNetConnection = false;
        HandlerThread thread = new HandlerThread("dataCommunication");
        thread.start();
        threadHandlerer = new Handler(thread.getLooper());
    }

    public synchronized void uploadBena(UploadInfoBean bean) {
        uploadQueue.add(bean);
        doTask();
    }

    public synchronized void uploadBena(List<UploadInfoBean> list) {
        uploadQueue.addAll(list);
        doTask();
        list.clear();
    }

    private void doTask() {
        if (!isRunnable && !uploadQueue.isEmpty()) {
            isRunnable = true;
            threadHandlerer.post(new Action());
        }
    }

    private class Action implements Runnable {
        @Override
        public void run() {
            try {
                List<UploadInfoBean> uploadList = new ArrayList<>();
                do {
                    uploadList.add(uploadQueue.poll());
                } while (!uploadQueue.isEmpty());
                if (SearchLocMapApplication.getInstance() != null && SearchLocMapApplication.getInstance().getUploadService() != null) {
                    UploadInfoBean item = uploadList.get(0);
                    Log.e("Proxy", item.getBdNum() + "\n" +
                            item.getBody() + "\n" +
                            item.getData_state() + "\n" +
                            item.getLocal_latlon() + "\n" +
                            item.getLocTimes() + "\n" +
                            item.getOffsets()+ "\n" +
                            item.getComFont()+ "\n" +
                            item.getLocal_time()+ "\n" +
                            item.getNum()+ "\n" +
                            item.getSendID()+ "\n" +
                            item.getTime() + "\n" +
                            item.getTx_type() + "\n" +
                            item.getData_state()
                    );

                    SearchLocMapApplication.getInstance().getUploadService().doTask(uploadList);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            isRunnable = false;
            doTask();
        }
    }

//    private void bdCom() {
//        try {
//            List<UploadInfoBean> uploadList = new ArrayList<>();
//            uploadList.add(uploadQueue.poll());
//            if (SearchLocMapApplication.getInstance() != null && SearchLocMapApplication.getInstance().getUploadService() != null) {
//                SearchLocMapApplication.getInstance().getUploadService().doTask(uploadList);
//            }
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//    }

    public void netCom(UploadInfoBean infoBean) {
        if (!isNetConnection) {
            Toast.makeText(SearchLocMapApplication.getContext(), "当前网络已断开，请连接网络或者切换北斗模式传输", Toast.LENGTH_SHORT).show();
            uploadQueue.add(infoBean);
            doTask();
            return;
        }
//        public static final int TX_FIREPOIT = 0;
//        public static final int TX_COMMON = 1;
//        public static final int TX_SOS = 2;
//        public static final int TX_MMS = 3;
//        public static final int TX_FIRELINE = 4;
        LogUtil.d("通信类型==" + infoBean.getData_type());
        switch (infoBean.getData_type()) {
            case Constants.TX_FIREPOIT:
                RequestFirePointBean firePointBean = new RequestFirePointBean(Constants.CMD_FIRE_POINT, "handsetsession", "HANDSET",
                        BaseUtils.getMacFromHardware(), SpUtils.getFloat(SPConstants.LAT_ADDR, Constants.CENTRE_LAT),
                        SpUtils.getFloat(SPConstants.LON_ADDR, Constants.CENTRE_LON), BaseUtils.sdf.format(SpUtils.getLong(SPConstants.LOC_TIME,
                        System.currentTimeMillis())), BaseUtils.getFirePointList(infoBean));
                LogUtil.e("TX_FIREPOIT   " + new Gson().toJson(firePointBean));
                uploadToNet(firePointBean, infoBean);

                break;
            case Constants.TX_COMMON:
                RequestCommonBean commonBean = new RequestCommonBean(Constants.CMD_COMMON, "handsetsession", "HANDSET",
                        BaseUtils.getMacFromHardware(), SpUtils.getFloat(SPConstants.LAT_ADDR, Constants.CENTRE_LAT),
                        SpUtils.getFloat(SPConstants.LON_ADDR, Constants.CENTRE_LON), BaseUtils.sdf.format(SpUtils.getLong(SPConstants.LOC_TIME,
                        System.currentTimeMillis())), BaseUtils.getWatchLocList(infoBean, "normal"));
                LogUtil.e("TX_COMMON   " + new Gson().toJson(commonBean));
                uploadToNet(commonBean, infoBean);
//                Log.e("TxMess", "send message  TX_COMMON");
                break;
            case Constants.TX_SOS:
                RequestCommonBean sosBean = new RequestCommonBean(Constants.CMD_SOS, "handsetsession", "HANDSET",
                        BaseUtils.getMacFromHardware(), SpUtils.getFloat(SPConstants.LAT_ADDR, Constants.CENTRE_LAT),
                        SpUtils.getFloat(SPConstants.LON_ADDR, Constants.CENTRE_LON), BaseUtils.sdf.format(SpUtils.getLong(SPConstants.LOC_TIME,
                        System.currentTimeMillis())), BaseUtils.getWatchLocList(infoBean, "sosing"));
                LogUtil.e("TX_SOS   " + new Gson().toJson(sosBean));
//                Log.e("TxMess", "send message  TX_SOS");
                uploadToNet(sosBean, infoBean);
                break;
            case Constants.TX_MMS:
                RequestMMSBean request = new RequestMMSBean(Constants.CMD_SMS, "handsetsession", "HANDSET", String.valueOf(infoBean.getTime()),
                        BaseUtils.getMacFromHardware(), infoBean.getBody());
                LogUtil.e("TX_MMS   " + new Gson().toJson(request));
                uploadToNet(request, infoBean);
                break;
            case Constants.TX_FIRELINE:
                RequestFireLineBean fireLineBean = new RequestFireLineBean(Constants.CMD_FIRELINE, "handsetsession", "HANDSET",
                        BaseUtils.getMacFromHardware(), SpUtils.getFloat(SPConstants.LAT_ADDR, Constants.CENTRE_LAT),
                        SpUtils.getFloat(SPConstants.LON_ADDR, Constants.CENTRE_LON), BaseUtils.sdf.format(SpUtils.getLong(SPConstants.LOC_TIME,
                        System.currentTimeMillis())), BaseUtils.sdf.format(infoBean.getTime()), BaseUtils.getFirePoint(infoBean));
                LogUtil.e("TX_FIRELINE   " + new Gson().toJson(fireLineBean));
                uploadToNet(fireLineBean, infoBean);
                break;

        }
        //todo  失败重试
        //uploadQueue.add(infoBean)
    }

//    private void autoCom() {
//        if (BaseUtils.isNetConnected(SearchLocMapApplication.getContext())) {
////            netCom();
//        } else {
//            bdCom();
//        }
//    }

    public void uploadToNet(Object request, final UploadInfoBean infoBean) {
        //短消息
        Observable<NetResponseBean> observable = SLMRetrofit.getInstance().getApi().uploadInfo(BaseUtils.getRequestBody(request));
        observable.compose(new ThreadSwitchTransformer<NetResponseBean>()).subscribe(new CallbackListObserver<NetResponseBean>() {
            @Override
            protected void onSucceed(NetResponseBean bean) {
                LogUtil.e(bean.toString());
                if ("OK".equals(bean.getStatus())) {
                    //请求成功
                    ToastUtil.showToast("上传成功");
                } else {
                    //请求成功
                    ToastUtil.showToast(bean.getReason());
                    LogUtil.e("uplpad fail ：" + bean.getStatus() + " , " + bean.getReason());
                    if (infoBean != null) {
                        uploadQueue.add(infoBean);
                        doTask();
                    }
                }
            }

            @Override
            protected void onFailed() {
                //请求成功
                ToastUtil.showToast("网络错误,请检查网络是否打开");
                if (infoBean != null) {
                    uploadQueue.add(infoBean);
                    doTask();
                }
            }
        });

    }

    public void registerBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        SearchLocMapApplication.getContext().registerReceiver(receiver, intentFilter);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            isNetConnection = BaseUtils.isNetConnected(SearchLocMapApplication.getContext());
            try {
                switch (SpUtils.getInt(SPConstants.COM_MODE, Constants.COM_MODE_BD)) {
                    case Constants.COM_MODE_BD:
                        break;
                    case Constants.COM_MODE_NET:
                        if (isNetConnection) {
                            SearchLocMapApplication.getInstance().getUploadService().setCom(Constants.COM_MODE_NET);
                        }
                        break;
                    case Constants.COM_MODE_AUTO:
                        SearchLocMapApplication.getInstance().getUploadService().setCom(Constants.COM_MODE_AUTO);
                        break;
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    public void unRegisterBroadcastReceiver() {
        try {
            if (receiver != null) {
                SearchLocMapApplication.getContext().unregisterReceiver(receiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
