package com.lhzw.searchlocmap.ui;

import android.app.Activity;
import android.content.BDManager;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import com.lhzw.searchlocmap.R;
import com.lhzw.searchlocmap.utils.BaseUtils;
import com.lhzw.searchlocmap.view.ProgressDialogShow;

/**
 * Created by xtqb on 2019/1/4.
 */
public class PlotBaseActivity extends Activity{
    private Toast mGlobalToast;
    private ProgressDialogShow progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    protected void showToast(String text) {

        if (mGlobalToast == null) {
            mGlobalToast = Toast.makeText(this, text,
                    Toast.LENGTH_SHORT);
            mGlobalToast.show();
        } else {
            mGlobalToast.setText(text);
            mGlobalToast.setDuration(Toast.LENGTH_SHORT);
            mGlobalToast.show();
        }
    }

    /**
     * Post ProgressDialog
     *
     * @param msg
     * @param isCancelable
     */
    protected void showProgressDialog(Context mContext, String msg, boolean isCancelable) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialogShow(mContext);
            progressDialog.dialogShow();
            progressDialog.setContent(msg);
        } else if (!progressDialog.isShowing()) {
            progressDialog.dialogShow();
            progressDialog.setContent(msg);
        }
    }

    /**
     * Cancel ProgressDialog
     */
    protected void cancelProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.cancel();
        }
        progressDialog = null;
    }

    public BDManager getDBManager(){
        //检查北斗开关
        if (Settings.Global.getInt(getContentResolver(),
                Settings.Global.BD_MODE_ON, 0) != 1) {// 北斗定位没开
            showToast(getString(R.string.dipper_switch_check_note));
            return null;
        }
        final BDManager mBDManager = (BDManager) getSystemService(Context.BD_SERVICE);
        //检查北斗卡
        if (BaseUtils.isStringEmpty(mBDManager.getBDCardNumber())) {
            showToast(getString(R.string.dipper_card_check_note));
            return null;
        }
        return mBDManager;
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }
}
