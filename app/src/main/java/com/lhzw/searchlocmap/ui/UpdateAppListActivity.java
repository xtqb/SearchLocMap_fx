package com.lhzw.searchlocmap.ui;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lhzw.searchlocmap.R;
import com.lhzw.searchlocmap.bean.ApkBean;
import com.lhzw.searchlocmap.constants.Constants;
import com.lhzw.searchlocmap.utils.BaseUtils;
import com.lhzw.searchlocmap.utils.NetUtils;
import com.lhzw.searchlocmap.utils.SpUtils;
import com.lhzw.searchlocmap.view.ProgressBarUpdateDialog;
import com.lhzw.searchlocmap.view.ShowProgressDialog;
import com.lhzw.searchlocmap.view.ShowUpdateApkDialog;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by xtqb on 2019/4/26.
 */
public class UpdateAppListActivity extends Activity implements View.OnClickListener {
    private Toast mGlobalToast;
    private ShowUpdateApkDialog updateDialog;
    private ShowProgressDialog progress;
    private final int DOWN_UPDATE = 0x001;
    private final int DOWN_OVER = 0x002;
    private final int DOWN_START = 0x003;
    private ProgressBarUpdateDialog dialog;
    private String saveFileName;
    private String dowloadApk = "/attachments/@";
    private int[] attachments = new int[]{-1, -1};
    private String[] versionNmaes = new String[2];
    private String upload_packageName;
    private int upload_packageCode;
    private TextView tv_map_update;
    private TextView tv_bd_update;
    private TextView tv_map_version;
   private TextView tv_map_new_version;
    private TextView tv_bd_version;
    private TextView tv_bd_new_version;
    private boolean isCancel;
    private Button bt_update_back;
    private RelativeLayout mRlSearchMapNew;
    private RelativeLayout mRlBdServiceNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_list_new);
        initView();
        initData();
        setListener();
    }

    private void setListener() {
        tv_map_update.setOnClickListener(this);
        tv_bd_update.setOnClickListener(this);
        bt_update_back.setOnClickListener(this);
    }

    private void initData() {
        isCancel = false;
        initServiceInfo();
        tv_map_version.setText("version:\t" + BaseUtils.getPackageName(this)+"  当前版本号");
        tv_bd_version.setText("version:\t" + upload_packageName+"  当前版本号");
        if (!BaseUtils.isNetConnected(this)) {
            showToast(getString(R.string.net_net_connect_fail));
            return;
        } else {
            new UpdateAsynTask().execute();
        }
    }

    private void initServiceInfo() {
        List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            if(getString(R.string.communication_service_package_name).equals(packageInfo.packageName)){
                upload_packageName = packageInfo.versionName;
                upload_packageCode =  packageInfo.versionCode;
            }
        }
        packages.clear();
    }

    private void initView() {
        bt_update_back = (Button) findViewById(R.id.bt_update_back);
        tv_map_update = (TextView) findViewById(R.id.tv_map_update);
        tv_bd_update = (TextView) findViewById(R.id.tv_bd_update);
        tv_map_version = (TextView) findViewById(R.id.tv_map_version);
        tv_map_new_version = (TextView) findViewById(R.id.tv_map_new_version);

        tv_bd_version = (TextView) findViewById(R.id.tv_bd_version);
        tv_bd_new_version = (TextView) findViewById(R.id.tv_bd_new_version);
        mRlSearchMapNew = (RelativeLayout) findViewById(R.id.rl_search_map_new);
        mRlBdServiceNew = (RelativeLayout) findViewById(R.id.rl_bd_service_new);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_map_update:
                saveFileName = Constants.SAVEPATH + "app-debug.apk";
                downloadApk(BaseUtils.getBaseIP() + dowloadApk.replace("@", attachments[0]+""));
                break;
            case R.id.tv_bd_update:
                saveFileName = Constants.SAVEPATH + "UploadMMsService.apk";
                downloadApk(BaseUtils.getBaseIP() + dowloadApk.replace("@", attachments[1]+""));
                break;

            case R.id.bt_update_back:
                this.finish();
                break;
        }
    }

    private class UpdateAsynTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected void onPreExecute() {
            showUpdateDialogProgess();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            int attachmentId = 0;
            try {
                String rev = NetUtils.reqAppClient(SpUtils.getString(Constants.HTTP_TOOKEN, ""), Constants.APK_NEW, Constants.APK_PATH);
                Log.e("Tag", "rev : " + rev + "  current version code " + BaseUtils.getPackageCode(UpdateAppListActivity.this));
                if (rev == null) {
                    attachmentId = -2;
                } else if ("".equals(rev)) {
                    attachmentId = -1;
                } else {
                    JSONObject obj = new JSONObject(rev);
                    int code = obj.getInt("code");
                    if (code == 0) {
                        String data = obj.getString("data");
                        List<ApkBean> list = new Gson().fromJson(data, new TypeToken<List<ApkBean>>() {
                        }.getType());
                        if (list != null && list.size() > 0) {
                            for (ApkBean bean : list) {
                                if (Constants.APK_map.equals(bean.getAppCode())) {
                                    if (bean.getVersionCode() > BaseUtils.getPackageCode(UpdateAppListActivity.this)) {
                                        versionNmaes[0] = bean.getVersionName();
                                        attachments[0] = bean.getAttachmentId();
                                    }
                                } else if (Constants.APK_BD.equals(bean.getAppCode())) {
                                    if (bean.getVersionCode() > upload_packageCode) {
                                        versionNmaes[1] = bean.getVersionName();
                                        attachments[1] = bean.getAttachmentId();
                                    }
                                }
                            }
                            attachmentId = 3;
                            list.clear();
                        } else {
                            attachmentId = -1;
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return attachmentId;
        }

        @Override
        protected void onPostExecute(Integer result) {
            cancelUpdateDialogProgress();
            if (result == -1 || result == 0) {
                showToast(getString(R.string.apk_update_no_new_version));
            } else if (result == -2) {
                showToast(getString(R.string.net_request_fail));
            } else {
                // 下载apk 安装apk
                Log.e("Tag", "request success");
                if(attachments[0] == -1) {//没有新版本
                    tv_map_new_version.setVisibility(View.GONE);
                    tv_map_update.setVisibility(View.GONE);
                    mRlSearchMapNew.setVisibility(View.GONE);
                } else {//有新版本
                   tv_map_new_version.setText("version:\t" + versionNmaes[0]+"  最新版本号");
                   tv_map_new_version.setVisibility(View.VISIBLE);
                    tv_map_update.setVisibility(View.VISIBLE);
                    mRlSearchMapNew.setVisibility(View.VISIBLE);
                }

                if(attachments[1] == -1) {
                   tv_bd_update.setVisibility(View.GONE);
                   tv_bd_new_version.setVisibility(View.GONE);
                    mRlBdServiceNew.setVisibility(View.GONE);
                } else {
                    tv_bd_new_version.setText("version:\t" + versionNmaes[1]+"  最新版本号");
                    tv_bd_new_version.setVisibility(View.VISIBLE);
                    tv_bd_update.setVisibility(View.VISIBLE);
                    mRlBdServiceNew.setVisibility(View.VISIBLE);
                }
//                showUpdateApkNote();
            }
        }
    }

    public void showToast(String text) {
        if (mGlobalToast == null) {
            mGlobalToast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
            mGlobalToast.show();
        } else {
            mGlobalToast.setText(text);
            mGlobalToast.setDuration(Toast.LENGTH_SHORT);
            mGlobalToast.show();
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_START:
                    long len = (long) msg.obj;
                    ShowProgressDialog();
                    progress.setMaxSeekBar((int) len);
                    break;
                case DOWN_UPDATE:
                    long[] dowload = (long[]) msg.obj;
                    progress.setSeekBar((int) dowload[1], (int) dowload[1] * 100 / (int) dowload[0]);
                    Log.e("Tag", "total : " + dowload[0] + "  len :" + dowload[1]);
                    break;
                case DOWN_OVER:
                    cancelProgressDialog();
                    File file = new File(saveFileName);
                    if (file.exists()) {
                        BaseUtils.installApk(UpdateAppListActivity.this, file);
                        finish();
                    } else {
                        Toast.makeText(UpdateAppListActivity.this, "安装文件不存在", Toast.LENGTH_LONG).show();
                    }
                    //  安装文件
                    break;
            }
        }
    };

    private class DownLoadApk implements Runnable {
        private String apkUrl;

        public DownLoadApk(String apkUrl) {
            this.apkUrl = apkUrl;
        }

        @Override
        public void run() {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            OutputStream out = null;
            InputStream in = null;
            try {
                HttpGet httpGet = new HttpGet(apkUrl);
                httpGet.addHeader(Constants.HTTP_TOOKEN, SpUtils.getString(Constants.HTTP_TOOKEN, ""));
                HttpResponse httpResponse = httpClient.execute(httpGet);
                if (httpResponse.getStatusLine().getStatusCode() == 200) {
                    HttpEntity entity = httpResponse.getEntity();
                    in = entity.getContent();
                    long length = entity.getContentLength();
                    Message msg = new Message();
                    msg.what = DOWN_START;
                    msg.obj = length;
                    mHandler.sendMessage(msg);
                    if (length <= 0) {
                        Log.e("Tag", "下载文件不存在！");
                        return;
                    }
                    File file = new File(Constants.SAVEPATH);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    out = new FileOutputStream(saveFileName);
                    byte[] buffer = new byte[4096];
                    long[] dowload = new long[2];
                    dowload[0] = length;
                    int readLength = 0;
                    while ((readLength = in.read(buffer)) > 0) {
                        byte[] bytes = new byte[readLength];
                        System.arraycopy(buffer, 0, bytes, 0, readLength);
                        out.write(bytes);
                        dowload[1] += readLength;
                        Message msg1 = new Message();
                        msg1.what = DOWN_UPDATE;
                        msg1.obj = dowload;
                        mHandler.sendMessage(msg1);
                        Log.e("Tag", "downloading  ...");
                    }
                    out.flush();
                    mHandler.sendEmptyMessage(DOWN_OVER);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void downloadApk(String url) {
        new Thread(new DownLoadApk(url)).start();
    }

    private void showUpdateApkNote() {
        updateDialog = new ShowUpdateApkDialog(this);
        updateDialog.show();
        updateDialog.setListener(this);
    }

    private void ShowProgressDialog() {
        progress = new ShowProgressDialog(this);
        progress.show();
        progress.setContent(getString(R.string.download_apk), getString(R.string.download_apk_progress));
    }

    private void cancelProgressDialog() {
        if (progress != null) {
            progress.dismiss();
        }
    }

    private void showUpdateDialogProgess() {
        if (dialog == null) {
            dialog = new ProgressBarUpdateDialog(this);
        }
        dialog.show();
    }

    private void cancelUpdateDialogProgress() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.finish();
            return true;
        }
        return false;
    }


}
