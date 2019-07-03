package com.lhzw.searchlocmap.utils;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.RemoteException;

import com.lhzw.searchlocmap.application.SearchLocMapApplication;
import com.lhzw.searchlocmap.bean.NetResponseBean;
import com.lhzw.searchlocmap.bean.RequestCommonBean;
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

    public static ComUtils getInstance() {
        if (instance == null) {
            instance = new ComUtils();
        }
        return instance;
    }

    private ComUtils() {
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
            switch (SpUtils.getInt(SPConstants.COM_MODE, Constants.COM_MODE_BD)) {
                case Constants.COM_MODE_BD:
                    bdCom();
                    break;
                case Constants.COM_MODE_NET:
                    netCom();
                    break;
                case Constants.COM_MODE_AUTO:
                    autoCom();
                    break;
            }
            isRunnable = false;
            doTask();
        }
    }

    private void bdCom() {
        try {
            List<UploadInfoBean> uploadList = new ArrayList<>();
            do {
                uploadList.add(uploadQueue.poll());
            } while (!uploadQueue.isEmpty());
            if (SearchLocMapApplication.getInstance() != null && SearchLocMapApplication.getInstance().getUploadService() != null) {
                SearchLocMapApplication.getInstance().getUploadService().doTask(uploadList);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void netCom() {
//        public static final int TX_FIREPOIT = 0;
//        public static final int TX_COMMON = 1;
//        public static final int TX_SOS = 2;
//        public static final int TX_MMS = 3;
//        public static final int TX_FIRELINE = 4;
        UploadInfoBean infoBean = uploadQueue.poll();
        LogUtil.d("通信类型=="+infoBean.getData_type());
        switch (infoBean.getData_type()) {

            case Constants.TX_FIREPOIT:

                break;
            case Constants.TX_COMMON:
                RequestCommonBean commonBean = new RequestCommonBean(Constants.CMD_COMMON, "handsetsession", "HANDSET",
                        BaseUtils.getDipperNum(SearchLocMapApplication.getContext()), SpUtils.getFloat(SPConstants.LAT_ADDR, Constants.CENTRE_LAT),
                        SpUtils.getFloat(SPConstants.LON_ADDR, Constants.CENTRE_LON), BaseUtils.sdf.format(SpUtils.getLong(SPConstants.LOC_TIME,
                        System.currentTimeMillis())), BaseUtils.getWatchLocList(infoBean));
                uploadToNet(commonBean, infoBean);
                break;
            case Constants.TX_SOS:
                break;
            case Constants.TX_MMS:
                RequestMMSBean request = new RequestMMSBean(Constants.CMD_SMS, "handsetsession", "HANDSET", String.valueOf(infoBean.getTime()),
                        BaseUtils.getDipperNum(SearchLocMapApplication.getContext()), infoBean.getBody());
                uploadToNet(request, infoBean);
                break;
            case Constants.TX_FIRELINE:
                break;

        }

        //todo  失败重试
        //uploadQueue.add(infoBean)
    }

    private void autoCom() {
        if (BaseUtils.isNetConnected(SearchLocMapApplication.getContext())) {
            netCom();
        } else {
            bdCom();
        }
    }

    private void uploadToNet(Object request, final UploadInfoBean infoBean){
        //短消息
        Observable<NetResponseBean> observable = SLMRetrofit.getInstance().getApi().uploadInfo(BaseUtils.getRequestBody(request));
        observable.compose(new ThreadSwitchTransformer<NetResponseBean>()).subscribe(new CallbackListObserver<NetResponseBean>() {
            @Override
            protected void onSucceed(NetResponseBean bean) {
                LogUtil.e(bean.toString());
                if("OK".equals(bean.getStatus())){
                    //请求成功
                    ToastUtil.showToast("成功");
                }else {
                    //请求成功
                    ToastUtil.showToast("失败"+bean.getStatus());
                    uploadQueue.add(infoBean);
                }
            }

            @Override
            protected void onFailed() {
                //请求成功
                ToastUtil.showToast("网络错误");
                uploadQueue.add(infoBean);
            }
        });

    }

}
