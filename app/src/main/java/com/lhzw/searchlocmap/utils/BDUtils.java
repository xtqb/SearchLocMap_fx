package com.lhzw.searchlocmap.utils;

import android.os.RemoteException;
import com.lhzw.searchlocmap.application.SearchLocMapApplication;
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

    public static BDUtils getInstance(){
        if(instance == null) {
            instance = new BDUtils();
        }
        return instance;
    }

    public synchronized void uploadBena(UploadInfoBean bean){
        uploadQueue.add(bean);
        doTask();
    }

    public synchronized void uploadBena(List<UploadInfoBean> list){
        uploadQueue.addAll(list);
        doTask();
        list.clear();
    }

    private void doTask() {
        if(!isRunnable && !uploadQueue.isEmpty()) {
            isRunnable = true;
            new Thread(new Action()).start();
        }
    }

    private class Action implements Runnable {
        @Override
        public void run() {
            try {
                List<UploadInfoBean> uploadList = new ArrayList<>();
                do {
                    uploadList.add(uploadQueue.poll());
                }while(!uploadQueue.isEmpty());
                if (SearchLocMapApplication.getInstance() != null && SearchLocMapApplication.getInstance().getUploadService() != null) {
                    SearchLocMapApplication.getInstance().getUploadService().doTask(uploadList);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            isRunnable = false;
            doTask();
        }
    }
}
