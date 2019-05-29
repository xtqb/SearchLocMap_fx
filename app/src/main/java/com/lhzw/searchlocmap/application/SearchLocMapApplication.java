package com.lhzw.searchlocmap.application;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import com.lhzw.searchlocmap.R;
import com.lhzw.searchlocmap.constants.SPConstants;
import com.lhzw.searchlocmap.interfaces.OnDipperCancelListener;
import com.lhzw.searchlocmap.utils.LogUtil;
import com.lhzw.searchlocmap.utils.SpUtils;
import com.lhzw.searchlocmap.view.ShowAlertDialog;
import com.lhzw.uploadmms.BDUploadEvent;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

public class SearchLocMapApplication extends Application implements View.OnClickListener{
    private static SearchLocMapApplication instance;
    private BDUploadEvent uploadEvent;
    private ShowAlertDialog alertdialog;
    private OnDipperCancelListener cancelListener;
    private Context mContext;
    private Intent mIntent;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = SearchLocMapApplication.this;
        mIntent = new Intent();
        mIntent.setAction("com.lhzw.uploadmms.service.UPLOADMMS");
        mIntent.setPackage("com.lhzw.uploadmms");
        Log.e("Service", "state = "+getApplicationContext().bindService(mIntent, serviceConnect, Context.BIND_AUTO_CREATE));

        Logger.addLogAdapter(new AndroidLogAdapter());//添加日志库
    }

    public static SearchLocMapApplication getInstance(){
        return instance;
    }

    public void autoUploadStart(Context mContext) {
        if (Settings.Global.getInt(getContentResolver(),
                Settings.Global.BD_MODE_ON, 0) != 1) {// 北斗定位没开
            alertdialog = new ShowAlertDialog(mContext);
            this.mContext = mContext;
            alertdialog.show();
            alertdialog
                    .setContent(getString(R.string.dipper_auto_upload_switch_note));
            alertdialog.setListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.tv_cancel:
                alertdialog.dismiss();
                alertdialog = null;
                if (cancelListener != null) {
                    cancelListener.cancel();
                }
                break;
            case R.id.tv_sure:
                // 开启北斗
                Intent intent_BDSetting = new Intent(
                        Settings.ACTION_AIRPLANE_MODE_SETTINGS);
                mContext.startActivity(intent_BDSetting);
                SpUtils.putBoolean(SPConstants.ISJUMP, true);
                alertdialog.dismiss();
                alertdialog = null;
                break;

            default:
                break;
        }
    }

    public void setOnDipperCancelListener(OnDipperCancelListener cancelListener) {
        this.cancelListener = cancelListener;
    }

    private ServiceConnection serviceConnect = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            // TODO Auto-generated method stub

            LogUtil.e("北斗服务已断开连接");
            //断开时  重新绑定
           // getApplicationContext().bindService(mIntent, serviceConnect, Context.BIND_AUTO_CREATE);
            uploadEvent = null;
        }

        @Override
        public void onServiceConnected(ComponentName arg0, IBinder ibinder) {
            LogUtil.e("北斗服务成功连接");

            uploadEvent = BDUploadEvent.Stub.asInterface(ibinder);
            if(uploadEvent == null) {
                Log.e("Tag", "null point");
            } else {
                Log.e("Tag", "bind service success");
            }
        }
    };

    public BDUploadEvent getUploadService(){
        if(uploadEvent == null) {
            Intent intent = new Intent();
            intent.setAction("com.lhzw.uploadmms.service.UPLOADMMS");
            intent.setPackage("com.lhzw.uploadmms");
            Log.e("Service", "state = "+getApplicationContext().bindService(intent, serviceConnect, Context.BIND_AUTO_CREATE));
            return getUploadService();
        }
        return uploadEvent;
    }

}
