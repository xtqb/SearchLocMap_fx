package com.lhzw.searchlocmap.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.lhzw.searchlocmap.R;
import com.lhzw.searchlocmap.application.SearchLocMapApplication;
import com.lhzw.searchlocmap.bean.AllBDInfosBean;
import com.lhzw.searchlocmap.bean.AllPersonInfoBean;
import com.lhzw.searchlocmap.bean.BaseBean;
import com.lhzw.searchlocmap.bean.BindingWatchBean;
import com.lhzw.searchlocmap.bean.HttpPersonInfo;
import com.lhzw.searchlocmap.bean.HttpRequstInfo;
import com.lhzw.searchlocmap.bean.LocPersonalInfo;
import com.lhzw.searchlocmap.bean.LocalBDNum;
import com.lhzw.searchlocmap.bean.PersonalInfo;
import com.lhzw.searchlocmap.bean.UserInfo;
import com.lhzw.searchlocmap.constants.Constants;
import com.lhzw.searchlocmap.constants.SPConstants;
import com.lhzw.searchlocmap.db.dao.CommonDBOperator;
import com.lhzw.searchlocmap.db.dao.DatabaseHelper;
import com.lhzw.searchlocmap.net.CallbackListObserver;
import com.lhzw.searchlocmap.net.CallbackObserver;
import com.lhzw.searchlocmap.net.SLMRetrofit;
import com.lhzw.searchlocmap.net.ThreadSwitchTransformer;
import com.lhzw.searchlocmap.utils.BaseUtils;
import com.lhzw.searchlocmap.utils.LogUtil;
import com.lhzw.searchlocmap.utils.SpUtils;
import com.lhzw.searchlocmap.utils.ToastUtil;
import com.lhzw.searchlocmap.view.ShowProgressDialog;
import com.lhzw.uploadmms.BDNum;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

/**
 * Created by xtqb on 2019/3/29.
 */
public class LoginActivity extends Activity implements View.OnClickListener {
    private EditText et_user_name;
    private EditText et_user_password;
    private Button bt_login;
    private Toast mGlobalToast;
    private DatabaseHelper helper;
    private Dao<HttpPersonInfo, Integer> httpPerDao;
    private ShowProgressDialog progress;
    private ImageView im_name_del;
    private Dao mBdNumDao;
    private Dao mLocPersonDao;
    private Dao mPersonalInfoDao;
    private String mLoginName;
    private String mPassword;
    private List<HttpRequstInfo> mHttpRequstInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SearchLocMapApplication.getInstance().bindService();
        if (!"".equals(SpUtils.getString(Constants.HTTP_TOOKEN, ""))) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
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
        mBdNumDao = helper.getBdNumDao();
        mLocPersonDao = helper.getLocPersonDao();
        mPersonalInfoDao = helper.getPersonalInfoDao();
    }

    private void initView() {
        et_user_name = (EditText) findViewById(R.id.et_user_name);
        et_user_password = (EditText) findViewById(R.id.et_user_password);
        bt_login = (Button) findViewById(R.id.bt_login);
        bt_login.setClickable(true);
        im_name_del = (ImageView) findViewById(R.id.im_name_del);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_login:
                mLoginName = et_user_name.getText().toString();
                mPassword = et_user_password.getText().toString();
                if (!BaseUtils.isNetConnected(this)) {
                    showToast(getString(R.string.net_net_connect_fail));
                    return;
                }
                if (BaseUtils.isStringEmpty(mLoginName) || BaseUtils.isStringEmpty(mPassword)) {
                    showToast(getString(R.string.login_note));
                    return;
                }
                // new AyncLoginTask().execute();
                SpUtils.putString(SPConstants.LOGIN_NAME, mLoginName);
                ShowProgressDialog();
                doLoginTask();//执行登录操作
                break;
            case R.id.im_name_del:
                et_user_name.setText("");
                break;
        }
    }

    private void doLoginTask() {
        Observable<BaseBean<UserInfo>> observable = SLMRetrofit.getInstance().getApi().loginCall(mLoginName, mPassword);
        observable.compose(new ThreadSwitchTransformer<BaseBean<UserInfo>>()).subscribe(new CallbackObserver<UserInfo>() {
            @Override
            protected void onSucceed(UserInfo info, String desc) {
                String token = info.getToken();//登录成功得到token
                LogUtil.e("获取Token成功,Token==" + token);
                if (!TextUtils.isEmpty(token)) {
                    CommonDBOperator.deleteAllItems(httpPerDao);
                    CommonDBOperator.deleteAllItems(mBdNumDao);
                    CommonDBOperator.deleteAllItems(mLocPersonDao);
                    SpUtils.putString(Constants.HTTP_TOOKEN, token);
//                    getAllPersonInfoBean();
                    getAllBDInfoFromServer();//获取平台的北斗号
                } else {
                    showToast("Token获取失败");
                    cancelProgressDialog();
                }

            }

            @Override
            protected void onFailed() {
                showToast(getString(R.string.net_request_fail));
                cancelProgressDialog();
            }
        });
    }

    /**
     * 获取全员信息
     */

    private void getAllPersonInfoBean() {
        Observable<AllPersonInfoBean> allPersonCall = SLMRetrofit.getInstance().getApi().getAllPersonCall();
        allPersonCall.compose(new ThreadSwitchTransformer<AllPersonInfoBean>()).subscribe(new CallbackListObserver<AllPersonInfoBean>() {
            @Override
            protected void onSucceed(AllPersonInfoBean bean) {
                if (bean.getCode() == 0) {
                    //全员信息的集合
                    mHttpRequstInfos = bean.getData();
                    if (mHttpRequstInfos != null && mHttpRequstInfos.size() > 0) {
                        // 执行数据库写入操作
                        new ProgressTask().execute();
                    }else {
                        showToast("获取人员数据为空");
                    }
                } else {
                    LogUtil.e(bean.getMessage());
                    showToast("获取全员信息失败,message==" + bean.getMessage());
                }
            }

            @Override
            protected void onFailed() {
                cancelProgressDialog();
                showToast(getString(R.string.net_request_fail));
            }
        });
    }

    private class ProgressTask extends AsyncTask<Object, Integer, Boolean> {
        private boolean isInitMax = false;

        @Override
        protected void onPreExecute() {
//            ShowProgressDialog();
        }

        @Override
        protected Boolean doInBackground(Object[] params) {
            boolean isSuccess = false;
            Integer[] values = new Integer[2];
            values[0] = mHttpRequstInfos.size() + mLocalBDNums.size();
            publishProgress(values);
            int delay = 50;
            int counter = 0;
            for (HttpRequstInfo info : mHttpRequstInfos) {
                counter++;
                values[1] = counter;
                publishProgress(values);
                CommonDBOperator.saveToDB(httpPerDao, translationItem(info));
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            for (LocalBDNum localBDNum : mLocalBDNums) {
                counter++;
                values[1] = counter;
                publishProgress(values);
                CommonDBOperator.saveToDB(mBdNumDao, localBDNum);
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (counter == values[0]) {
                isSuccess = true;
            }
            mHttpRequstInfos.clear();
            mLocalBDNums.clear();
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
        protected void onPostExecute(Boolean isSuccess) {
            if (isSuccess) {
                getBindingWatchFromServer();//获取当前手持机绑定的手表
//                startActivity(new Intent(LoginActivity.this, MainActivity.class));
//                LoginActivity.this.finish();
            } else {
                showToast(getString(R.string.net_request_fail));
            }
            cancelProgressDialog();
        }
    }

//    private class AyncLoginTask extends AsyncTask<Object, Integer, Boolean> {
//        private boolean isInitMax = false;
////        private String mDipperNum;
//
//        @Override
//        protected void onPreExecute() {
//            ShowProgressDialog();
////            mDipperNum = BaseUtils.getDipperNum(LoginActivity.this);
//        }
//
//        @Override
//        protected Boolean doInBackground(Object[] params) {
//            boolean isSuccess = false;
//            try {
//                String rev = null;
//                Integer[] values = new Integer[2];
//                if ("".equals(SpUtils.getString(Constants.HTTP_TOOKEN, ""))) {
//                    // LogUtil.e("准备登录");
//                    String token = NetUtils.doLoginClient(et_user_name.getText().toString().trim(), et_user_password.getText().toString().trim());
//                    if (token != null) {
//                        //LogUtil.e("登录成功Token不为空");
//                        CommonDBOperator.deleteAllItems(httpPerDao);
//                        CommonDBOperator.deleteAllItems(mBdNumDao);
//                        CommonDBOperator.deleteAllItems(mLocPersonDao);
//                        SpUtils.putString(Constants.HTTP_TOOKEN, token);
//                        getAllBDInfoFromServer();//获取平台的北斗号
//                        getBindingWatchFromServer();//获取当前手持机绑定的手表
//                        rev = NetUtils.doHttpGetClient(token, Constants.USER_PATH);
//                        //LogUtil.e("登录成功rev=="+rev);
//                        if (rev != null) {
//                            JSONObject obj = new JSONObject(rev);
//                            int code = obj.getInt("code");
//                            if (code == 0) {
//                                String data = obj.getString("data");
//                                List<com.lhzw.searchlocmap.bean.HttpRequstInfo> list = new Gson().fromJson(data, new TypeToken<List<com.lhzw.searchlocmap.bean.HttpRequstInfo>>() {
//                                }.getType());
//                                values[0] = list.size() + mLocalBDNums.size();//总进度
//                                LogUtil.d("size : " + list.size() + "mLocalBDNums:" + mLocalBDNums.size());
//                                int delay = 40 * 100 / (list.size() + mLocalBDNums.size());
//                                int counter = 0;
//                                for (HttpRequstInfo info : list) {
//                                    counter++;
//                                    values[1] = counter;
//                                    publishProgress(values);
//                                    CommonDBOperator.saveToDB(httpPerDao, translationItem(info));
//                                    Thread.sleep(delay);
//                                }
//
//                                for (LocalBDNum localBDNum : mLocalBDNums) {
//                                    counter++;
//                                    values[1] = counter;
//                                    publishProgress(values);
//                                    CommonDBOperator.saveToDB(mBdNumDao, localBDNum);
//                                    //上传到服务
//                                    Thread.sleep(delay);
//                                }
//                                try {//上传到服务接口
//                                    if (SearchLocMapApplication.getInstance() != null && SearchLocMapApplication.getInstance().getUploadService() != null) {
//                                        SearchLocMapApplication.getInstance().getUploadService().updateBDNum(mNumList);
//                                    } else {
//                                        SpUtils.putString(Constants.HTTP_TOOKEN, "");
//                                        return false;
//
//                                    }
//                                } catch (RemoteException e) {
//                                    e.printStackTrace();
//                                }
//                                isSuccess = true;
//                                list.clear();
//                                mLocalBDNums.clear();
//
//                            }
//                        } else {
//                            isSuccess = false;
//                        }
//                    }
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            return isSuccess;
//        }
//
//        @Override
//        protected void onProgressUpdate(Integer[] values) {
//            if (!isInitMax) {
//                progress.setMaxSeekBar(values[0]);
//                isInitMax = true;
//            } else {
//                progress.setSeekBar(values[1], values[1] * 100 / values[0]);
//            }
//        }
//
//        @Override
//        protected void onPostExecute(Boolean result) {
//            if (result) {
//                startActivity(new Intent(LoginActivity.this, MainActivity.class));
//                LoginActivity.this.finish();
//            } else {
//                showToast(getString(R.string.net_request_fail));
//            }
//            cancelProgressDialog();
//        }
//    }

    /**
     * 查找已绑定的手表   解决清除本地数据后登陆  无法解除绑定的问题
     */
    private void getBindingWatchFromServer() {
        String mac = BaseUtils.getMacFromHardware();
        if (TextUtils.isEmpty(mac)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showToast("mac为空");
                }
            });
            return;
        }
        Observable<BindingWatchBean> observable = SLMRetrofit.getInstance().getApi().getBindingWatch(mac);
        observable.compose(new ThreadSwitchTransformer<BindingWatchBean>()).subscribe(new CallbackListObserver<BindingWatchBean>() {
            @Override
            protected void onSucceed(BindingWatchBean bean) {
                if (bean.getCode() == 0) {
                    if (bean.getData() != null && bean.getData().size() > 0) {
                        //有绑定数据   插入表中
                        for (int i = 0; i < bean.getData().size(); i++) {
                            BindingWatchBean.DataBean dataBean = bean.getData().get(i);
                            List<HttpPersonInfo> httpPersonInfo = CommonDBOperator.queryByKeys(httpPerDao, "deviceNumbers", dataBean.getDeviceNumber());
                            String realName = "";

                            if(httpPersonInfo!=null && httpPersonInfo.size()>0){
                                realName = TextUtils.isEmpty(httpPersonInfo.get(0).getRealName())? "" : httpPersonInfo.get(0).getRealName();
                            }else {
                                LogUtil.e("httpPersonInfo=="+httpPersonInfo);
                            }
                            LocPersonalInfo perInfo = new LocPersonalInfo();
                            perInfo.setNum(dataBean.getDeviceNumber());
                            perInfo.setName(realName);
                            CommonDBOperator.saveToDB(mLocPersonDao, perInfo);

                            PersonalInfo personalInfo = new PersonalInfo();
                            personalInfo.setName(realName);
                            personalInfo.setNum(dataBean.getDeviceNumber());
                            personalInfo.setSex("0");
                            personalInfo.setState(Constants.PERSON_OFFLINE);
                            LogUtil.e("new = = "+personalInfo.getSex());
                            boolean saveToDB = CommonDBOperator.saveToDB(mPersonalInfoDao, personalInfo);
                            LogUtil.e("保存到mPersonalInfoDao=="+saveToDB);
                            uploadOffset();
                        }
                    }
                } else {
                    showToast("请求失败==" + bean.getCode());
                }
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                LoginActivity.this.finish();
            }

            @Override
            protected void onFailed() {
                showToast("网络错误,请先检查网络");
            }
        });

    }

    /**
     * 重新设置ID值
     */
    private void uploadOffset() {
        List<PersonalInfo> PersonalInfo = CommonDBOperator.queryByOrderKey(mPersonalInfoDao, "num");
        LogUtil.e("PersonalInfo表的大小=="+PersonalInfo.size());
        for (int i = 0; i < PersonalInfo.size(); i++) {
            PersonalInfo.get(i).setOffset(i);
            CommonDBOperator.updateItem(mPersonalInfoDao, PersonalInfo.get(i));
        }
    }

    private List<LocalBDNum> mLocalBDNums = new ArrayList<>();
    private List<BDNum> mNumList = new ArrayList<>();//准备上传服务的北斗bean集合

    /**
     * 获取北斗号信息  send =1 传服务 send=0 设置sp
     */
    private void getAllBDInfoFromServer() {
        //   登录成功开始下载设备信息
        Observable<AllBDInfosBean> observable = SLMRetrofit.getInstance().getApi().getAllBDInfos();
        observable.compose(new ThreadSwitchTransformer<AllBDInfosBean>()).subscribe(new CallbackListObserver<AllBDInfosBean>() {
            @Override
            protected void onSucceed(AllBDInfosBean bean) {
                if (bean != null) {
                    if (bean.getCode() == 0) {
                        //请求成功
                        if (bean.getData() != null && bean.getData().size() > 0) {
                            //有数据
                            for (int i = 0; i < bean.getData().size(); i++) {
                                AllBDInfosBean.DataBean dataBean = bean.getData().get(i);

                                if ("1".equals(dataBean.getSend())) {
                                    BDNum num = new BDNum(dataBean.getBdNumber(), Constants.TX_JZH);
                                    mNumList.add(num);//上传到服务接口的BdNum
                                }
                                if ("1".equals(dataBean.getReceive())) {
                                    SpUtils.putString(Constants.UPLOAD_JZH_NUM, dataBean.getBdNumber());
                                    try {
                                        if (SearchLocMapApplication.getInstance() != null && SearchLocMapApplication.getInstance().getUploadService() != null) {
                                            SearchLocMapApplication.getInstance().getUploadService().setNum(Constants.TX_JZH, dataBean.getBdNumber());
                                        } else {
                                            SpUtils.putString(Constants.HTTP_TOOKEN, "");
                                            ToastUtil.showToast("应急通信服务连接失败,退出APP");
                                            finish();
                                        }

                                    } catch (RemoteException e) {
                                        e.printStackTrace();
                                        SpUtils.putString(Constants.HTTP_TOOKEN, "");
                                        ToastUtil.showToast("北斗服务连接失败,退出APP");
                                        cancelProgressDialog();
                                        finish();
                                    }
                                }
                                //添加到本地数据库
                                LocalBDNum localBDNum = new LocalBDNum(dataBean.getBdNumber(), dataBean.getSend(), dataBean.getReceive());
                                mLocalBDNums.add(localBDNum);

                            }

                            try {//上传到服务接口
                                if (SearchLocMapApplication.getInstance() != null && SearchLocMapApplication.getInstance().getUploadService() != null) {
                                    SearchLocMapApplication.getInstance().getUploadService().updateBDNum(mNumList);
                                } else {
                                    SpUtils.putString(Constants.HTTP_TOOKEN, "");
                                    ToastUtil.showToast("北斗服务连接失败,退出APP");
                                    cancelProgressDialog();
                                    finish();
                                }
                            } catch (RemoteException e) {
                                e.printStackTrace();
                                SpUtils.putString(Constants.HTTP_TOOKEN, "");
                                ToastUtil.showToast("北斗服务连接失败,退出APP");
                                cancelProgressDialog();
                                finish();
                            }
                            getAllPersonInfoBean();
                        }
                    } else {
                        showToast(bean.getMessage() + "");
                        cancelProgressDialog();
                    }
                } else {
                    showToast("服务器未能获取北斗平台的信息");
                    cancelProgressDialog();
                }
            }

            @Override
            protected void onFailed() {
                showToast(getString(R.string.net_request_fail));
                cancelProgressDialog();
            }
        });
    }

    private HttpPersonInfo translationItem(HttpRequstInfo item) {
        HttpPersonInfo bean = null;
        if (item != null) {
            bean = new HttpPersonInfo();
            bean.setDeviceNumbers(item.getDeviceNumbers());
            if (item.getDevice() != null) {
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


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return false;
    }


    /** 判断登录是否是快速点击 */
    private  long  lastClickTime;

    public  boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 2000) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    /** 判断触摸时间派发间隔 */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (isFastDoubleClick()) {
                return true;
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}
