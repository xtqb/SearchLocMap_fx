package com.lhzw.searchlocmap.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.dao.Dao;
import com.lhzw.searchlocmap.R;
import com.lhzw.searchlocmap.bean.HttpPersonInfo;
import com.lhzw.searchlocmap.bean.HttpRequstInfo;
import com.lhzw.searchlocmap.constants.Constants;
import com.lhzw.searchlocmap.db.dao.CommonDBOperator;
import com.lhzw.searchlocmap.db.dao.DatabaseHelper;
import com.lhzw.searchlocmap.utils.BaseUtils;
import com.lhzw.searchlocmap.utils.NetUtils;
import com.lhzw.searchlocmap.utils.SpUtils;
import com.lhzw.searchlocmap.view.ShowProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by xtqb on 2019/3/29.
 */
public class LoginActivity extends Activity implements View.OnClickListener{

    private EditText et_user_name;
    private EditText et_user_password;
    private Button bt_login;
    private View decorView;
    private Toast mGlobalToast;
    private DatabaseHelper helper;
    private Dao<HttpPersonInfo, Integer> httpPerDao;
    private ShowProgressDialog progress;
    private final int CANCEL_DIALOG = 0x0021;
    private ScrollView loginBinding;
    private ImageView im_name_del;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (!"".equals(SpUtils.getString(Constants.HTTP_TOOKEN, ""))) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            this.finish();
        } else {
            initView();
            initData();
            setListener();
        }
    }

    private void setListener() {
        bt_login.setOnClickListener(this);
    }

    private void initData() {
        helper = DatabaseHelper.getHelper(this);
        im_name_del.setOnClickListener(this);
        httpPerDao = helper.getHttpPerDao();
    }

    private void initView() {
        et_user_name = (EditText) findViewById(R.id.et_user_name);
        et_user_password = (EditText) findViewById(R.id.et_user_password);
        bt_login = (Button) findViewById(R.id.bt_login);
        im_name_del = (ImageView) findViewById(R.id.im_name_del);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_login:
                Log.e("Tag", "123");
                if (BaseUtils.isStringEmpty(et_user_name.getText().toString()) || BaseUtils.isStringEmpty(et_user_password.getText().toString())) {
                    showToast(getString(R.string.login_note));
                } else {
                    if(!BaseUtils.isNetConnected(this)) {
                        showToast(getString(R.string.net_net_connect_fail));
                        return;
                    }
                    new AyncLoginTask().execute();
                }
                break;
            case R.id.im_name_del:
                et_user_name.setText("");
                break;
        }
    }


    private class AyncLoginTask extends AsyncTask<Object, Integer, Boolean> {
        private boolean isInitMax = false;

        @Override
        protected void onPreExecute() {
            ShowProgressDialog();
        }

        @Override
        protected Boolean doInBackground(Object[] params) {
            boolean isSuccess = false;
            try {
                String rev = null;
                Integer[] values = new Integer[2];
                if ("".equals(SpUtils.getString(Constants.HTTP_TOOKEN, ""))) {
                    String token = NetUtils.doLoginClient(et_user_name.getText().toString().trim(), et_user_password.getText().toString().trim());
                    if (token != null) {
                        SpUtils.putString(Constants.HTTP_TOOKEN, token);
                        rev = NetUtils.doHttpGetClient(token, Constants.USER_PATH);
                        if (rev != null) {
                            JSONObject obj = new JSONObject(rev);
                            int code = obj.getInt("code");
                            if (code == 0) {
                                String data = obj.getString("data");
                                List<HttpRequstInfo> list = new Gson().fromJson(data, new TypeToken<List<HttpRequstInfo>>() {
                                }.getType());
                                values[0] = list.size();
                                Log.e("Tag", "size : " + list.size());
                                int delay = 40 * 100 / list.size();
                                int counter = 0;
                                for (HttpRequstInfo info : list) {
                                    counter++;
                                    values[1] = counter;
                                    publishProgress(values);
                                    CommonDBOperator.saveToDB(httpPerDao, translationItem(info));
                                    Thread.sleep(delay);
                                }
                                isSuccess = true;
                                list.clear();
                            }
                        }
                    }
                } else {
                    isSuccess = true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return isSuccess;
        }

        @Override
        protected void onProgressUpdate(Integer[] values) {
            if (!isInitMax) {
                progress.setMaxSeekBar(values[0]);
                isInitMax = true;
            } else {
                progress.setSeekBar(values[1], values[1] * 100 / values[0]);
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                LoginActivity.this.finish();
            } else {
                showToast(getString(R.string.net_request_fail));
            }
            cancelProgressDialog();
        }
    }

    private HttpPersonInfo translationItem(HttpRequstInfo item) {
        HttpPersonInfo bean = null;
        if(item != null){
            bean = new HttpPersonInfo();
            bean.setDeviceNumbers(item.getDeviceNumbers());
            if(item.getDevice() != null) {
                bean.setDeviceType(item.getDevice().getDeviceType());
            } else {
                bean.setDeviceType(0);
            }
            bean.setGender(item.getGender());
            bean.setId(item.getId());
            bean.setLoginName(item.getLoginName());
            bean.setOrg(item.getOrg());
            bean.setOrgName(item.getOrgName());
            bean.setRealName(item.getRealName());
            bean.setXynumber(item.getXynumber());
        }
        return bean;
    }

    private void ShowProgressDialog() {
        progress = new ShowProgressDialog(this);
        progress.show();
    }

    private void cancelProgressDialog() {
        if (progress != null) {
            progress.dismiss();
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
                case CANCEL_DIALOG:
                    cancelProgressDialog();
                    break;
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return false;
    }
}
