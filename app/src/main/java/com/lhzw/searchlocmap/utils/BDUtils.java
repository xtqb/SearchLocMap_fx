package com.lhzw.searchlocmap.utils;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.RemoteException;

import com.lhzw.searchlocmap.application.SearchLocMapApplication;
import com.lhzw.searchlocmap.constants.Constants;
import com.lhzw.searchlocmap.constants.SPConstants;
import com.lhzw.uploadmms.UploadInfoBean;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by xtqb on 2019/3/27.
 */
public class BDUtils {
    public static ConcurrentLinkedQueue<UploadInfoBean> uploadQueue = new ConcurrentLinkedQueue<UploadInfoBean>();
    private static boolean isRunnable = false;
    private static BDUtils instance;
    private final Handler threadHandlerer;

    public static BDUtils getInstance() {
        if (instance == null) {
            instance = new BDUtils();
        }
        return instance;
    }

    private BDUtils() {
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

    private void bdCom(){
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

    private void netCom(){

    }

    public void autoCom(){
        if(BaseUtils.isNetConnected(SearchLocMapApplication.getContext())){
            netCom();
        } else {
            bdCom();
        }
    }

}
