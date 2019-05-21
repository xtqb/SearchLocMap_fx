package com.lhzw.searchlocmap.ui;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.lhzw.searchlocmap.R;
import com.lhzw.searchlocmap.utils.BaseUtils;
import com.lhzw.searchlocmap.view.ProgressBarUpdateDialog;

import java.io.File;

/**
 * Created by xtqb on 2019/4/25.
 */
public class UpdateUploadServiceActivity extends Activity implements View.OnClickListener {

    private ProgressBarUpdateDialog dialog;
    private Button bt_update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_service);
        initView();
        initData();
        setListener();
    }

    private void setListener() {
        bt_update.setOnClickListener(this);
    }

    private void initData() {
//        new UpdateAsynTask().execute();
    }

    private void initView() {
        bt_update = (Button) findViewById(R.id.bt_update);
    }

    private void showUpdateDialogProgess(){
        if(dialog == null) {
            dialog = new ProgressBarUpdateDialog(this);
        }
        dialog.show();
    }

    private void cancelUpdateDialogProgress(){
        if(dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_update:
                File file = new File("/sdcard/apk/UploadMMsService.apk");
                if(file.exists()) {
                    BaseUtils.installApk(this, file);
                } else {
                    Toast.makeText(this, "安装文件不存在", Toast.LENGTH_LONG).show();
                }
//                new UpdateAsynTask().execute();
                break;
        }
    }

    private class UpdateAsynTask extends AsyncTask<Void, Void, Boolean>{

        @Override
        protected void onPreExecute() {
            showUpdateDialogProgess();
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            cancelUpdateDialogProgress();
        }
    }
}
