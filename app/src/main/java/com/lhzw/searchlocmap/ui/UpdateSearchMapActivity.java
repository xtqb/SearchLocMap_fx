package com.lhzw.searchlocmap.ui;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.dao.Dao;
import com.lhzw.searchlocmap.R;
import com.lhzw.searchlocmap.bean.ApkBean;
import com.lhzw.searchlocmap.bean.DipperInfoBean;
import com.lhzw.searchlocmap.constants.Constants;
import com.lhzw.searchlocmap.db.dao.CommonDBOperator;
import com.lhzw.searchlocmap.db.dao.DatabaseHelper;
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
 * Created by xtqb on 2019/4/25.
 */
public class UpdateSearchMapActivity extends Activity implements View.OnClickListener {

    private ProgressBarUpdateDialog dialog;
    private Button bt_update;
    private Toast mGlobalToast;
    private String saveFileName = Constants.SAVEPATH + "/app-debug.apk";
    private final int DOWN_UPDATE = 0x001;
    private final int DOWN_OVER = 0x002;
    private final int DOWN_START = 0x003;
    private ShowUpdateApkDialog updateDialog;
    private TextView tv_search_version_code;
    private String dowloadApk = "/attachments/@";
    private int attachments = -1;
    private ShowProgressDialog progress;
    private Dao<DipperInfoBean, Integer> dipDao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_searchmap);
        initView();
        requestPermission();
        initData();
        setListener();
    }
    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }
    }

    private void setListener() {
        bt_update.setOnClickListener(this);
    }

    private void initData() {
        tv_search_version_code.setText(getString(R.string.update_search_version) + "\t\t" + BaseUtils.getPackageName(this));
        dipDao = DatabaseHelper.getHelper(this).getDipperDao();
    }

    private void initView() {
        tv_search_version_code = (TextView) findViewById(R.id.tv_search_version_code);
        bt_update = (Button) findViewById(R.id.bt_update);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_update:
                if (!BaseUtils.isNetConnected(this)) {
                    showToast(getString(R.string.net_net_connect_fail));
                    return;
                } else {
                    new UpdateAsynTask().execute();
                }
                break;
            case R.id.dialog_update:
                Log.e("Tag", "Update apk");
                updateDialog.dismiss();
                if(!"0337009".equals(SpUtils.getString(Constants.UPLOAD_JZH_NUM, "0337009"))) {
                    SpUtils.putString(Constants.UPLOAD_JZH_NUM, "0337009");
                }
                List<DipperInfoBean> list = CommonDBOperator.queryByKeys(dipDao, "type", Constants.TX_JZH+"");
                if(list != null && list.size() > 0) {
                    list.get(0).setNum("0337009");
                    CommonDBOperator.updateItem(dipDao, list.get(0));
                    list.clear();
                } else {
                    DipperInfoBean item = new DipperInfoBean("0337009", Constants.TX_JZH, "", "");
                    CommonDBOperator.saveToDB(dipDao, item);
                }
                downloadApk(BaseUtils.getBaseIP() + dowloadApk.replace("@", attachments + ""));
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
                Log.e("Tag", "rev : " + rev + "  current version code " + BaseUtils.getPackageCode(UpdateSearchMapActivity.this));
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
                                    if (bean.getVersionCode() > BaseUtils.getPackageCode(UpdateSearchMapActivity.this)) {
                                        attachmentId = bean.getAttachmentId();
                                        Log.e("Tag", "has a new version " + attachmentId);
                                    } else {
                                        Log.e("Tag", "has a new version " + attachmentId);
                                    }
                                }
                            }
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
                attachments = result;
                showUpdateApkNote();
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
                    progress.setSeekBar((int)dowload[1], (int)dowload[1] * 100 / (int)dowload[0]);
                    Log.e("Tag", "total : " + dowload[0]+ "  len :" + dowload[1]);
                    break;
                case DOWN_OVER:
                    cancelProgressDialog();
                    File file = new File(saveFileName);
                    if(file.exists()) {
                        BaseUtils.installApk(UpdateSearchMapActivity.this, file);

                    } else {
                        Toast.makeText(UpdateSearchMapActivity.this, "安装文件不存在", Toast.LENGTH_LONG).show();
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
                if(httpResponse.getStatusLine().getStatusCode() == 200) {
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
                        file.createNewFile();
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

}
