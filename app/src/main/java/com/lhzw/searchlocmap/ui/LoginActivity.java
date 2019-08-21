package com.lhzw.searchlocmap.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.dao.Dao;
import com.lhzw.searchlocmap.R;
import com.lhzw.searchlocmap.application.SearchLocMapApplication;
import com.lhzw.searchlocmap.bean.AllBDInfosBean;
import com.lhzw.searchlocmap.bean.BindingWatchBean;
import com.lhzw.searchlocmap.bean.HttpPersonInfo;
import com.lhzw.searchlocmap.bean.HttpRequstInfo;
import com.lhzw.searchlocmap.bean.LocPersonalInfo;
import com.lhzw.searchlocmap.bean.LocalBDNum;
import com.lhzw.searchlocmap.bean.PersonalInfo;
import com.lhzw.searchlocmap.constants.Constants;
import com.lhzw.searchlocmap.db.dao.CommonDBOperator;
import com.lhzw.searchlocmap.db.dao.DatabaseHelper;
import com.lhzw.searchlocmap.net.CallbackListObserver;
import com.lhzw.searchlocmap.net.SLMRetrofit;
import com.lhzw.searchlocmap.net.ThreadSwitchTransformer;
import com.lhzw.searchlocmap.utils.BaseUtils;
import com.lhzw.searchlocmap.utils.LogUtil;
import com.lhzw.searchlocmap.utils.NetUtils;
import com.lhzw.searchlocmap.utils.SpUtils;
import com.lhzw.searchlocmap.view.ShowProgressDialog;
import com.lhzw.uploadmms.BDNum;
import org.json.JSONException;
import org.json.JSONObject;
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
    private View decorView;
    private Toast mGlobalToast;
    private DatabaseHelper helper;
    private Dao<HttpPersonInfo, Integer> httpPerDao;
    private ShowProgressDialog progress;
    private final int CANCEL_DIALOG = 0x0021;
    private ScrollView loginBinding;
    private ImageView im_name_del;
    private Dao mBdNumDao;
    private Dao mLocPersonDao;
    private Dao mPersonalInfoDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SearchLocMapApplication.getInstance().bindService();
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
        mBdNumDao = helper.getBdNumDao();
        mLocPersonDao = helper.getLocPersonDao();
        mPersonalInfoDao = helper.getPersonalInfoDao();
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
                    if (!BaseUtils.isNetConnected(this)) {
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
//        private String mDipperNum;

        @Override
        protected void onPreExecute() {
            ShowProgressDialog();
//            mDipperNum = BaseUtils.getDipperNum(LoginActivity.this);
        }

        @Override
        protected Boolean doInBackground(Object[] params) {
            boolean isSuccess = false;
            try {
                String rev = null;
                Integer[] values = new Integer[2];
                if ("".equals(SpUtils.getString(Constants.HTTP_TOOKEN, ""))) {
                   // LogUtil.e("准备登录");
                    String token = NetUtils.doLoginClient(et_user_name.getText().toString().trim(), et_user_password.getText().toString().trim());
                    if (token != null) {
                        //LogUtil.e("登录成功Token不为空");
                        CommonDBOperator.deleteAllItems(httpPerDao);
                        CommonDBOperator.deleteAllItems(mBdNumDao);
                        CommonDBOperator.deleteAllItems(mLocPersonDao);
                        SpUtils.putString(Constants.HTTP_TOOKEN, token);
                        getAllBDInfoFromServer();//获取平台的北斗号
                        getBindingWatchFromServer();//获取当前手持机绑定的手表
                        rev = NetUtils.doHttpGetClient(token, Constants.USER_PATH);
                        //LogUtil.e("登录成功rev=="+rev);
                        if (rev != null) {
                            JSONObject obj = new JSONObject(rev);
                            int code = obj.getInt("code");
                            if (code == 0) {
                                String data = obj.getString("data");
                                List<HttpRequstInfo> list = new Gson().fromJson(data, new TypeToken<List<HttpRequstInfo>>() {
                                }.getType());
                                values[0] = list.size() + mLocalBDNums.size();//总进度
                                LogUtil.d("size : " + list.size()+"mLocalBDNums:"+ mLocalBDNums.size());
                                int delay = 40 * 100 / (list.size()+ mLocalBDNums.size());
                                int counter = 0;
                                for (HttpRequstInfo info : list) {
                                    counter++;
                                    values[1] = counter;
                                    publishProgress(values);
                                    CommonDBOperator.saveToDB(httpPerDao, translationItem(info));
                                    Thread.sleep(delay);
                                }

                                for (LocalBDNum localBDNum : mLocalBDNums){
                                    counter++;
                                    values[1]=counter;
                                    publishProgress(values);
                                    CommonDBOperator.saveToDB(mBdNumDao, localBDNum);
                                    //上传到服务
                                    Thread.sleep(delay);
                                }
                                try {//上传到服务接口
                                    if (SearchLocMapApplication.getInstance() != null && SearchLocMapApplication.getInstance().getUploadService() != null) {
                                        SearchLocMapApplication.getInstance().getUploadService().updateBDNum(mNumList);
                                    }else {
                                        SpUtils.putString(Constants.HTTP_TOOKEN, "");
                                        return false;

                                    }
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                                isSuccess = true;
                                list.clear();
                                mLocalBDNums.clear();

                            }
                        }else {
                            isSuccess = false;
                        }
                    }
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
                //记录初始的本机北斗号
                SpUtils.putString(Constants.BD_NUM_lOC_DEF, BaseUtils.getDipperNum(LoginActivity.this));
            } else {
                showToast(getString(R.string.net_request_fail));
            }
            cancelProgressDialog();
        }
    }

    /**
     * 查找已绑定的手表   解决清除本地数据后登陆  无法解除绑定的问题
     */
    private void getBindingWatchFromServer() {
       // String bdNum = BaseUtils.getDipperNum(LoginActivity.this);
        String mac = BaseUtils.getMacFromHardware();
        if(TextUtils.isEmpty(mac)){
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
           if(bean.getCode() == 0){
               if(bean.getData() != null && bean.getData().size()>0){
                   //有绑定数据   插入表中
                   for (int i = 0; i < bean.getData().size(); i++) {
                       BindingWatchBean.DataBean dataBean = bean.getData().get(i);
                       LocPersonalInfo perInfo = new LocPersonalInfo();
                       perInfo.setNum(dataBean.getDeviceNumber());
                       perInfo.setName("");
                       CommonDBOperator.saveToDB(mLocPersonDao, perInfo);

                       PersonalInfo personalInfo = new PersonalInfo();
                       personalInfo.setName("");
                       personalInfo.setSex("0");
                       personalInfo.setState(Constants.PERSON_OFFLINE);
                       CommonDBOperator.saveToDB(mPersonalInfoDao,perInfo);
                       uploadOffset();
                   }
               }
            }else {
             showToast("请求失败=="+bean.getCode());
           }
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
        List<PersonalInfo> rxList = CommonDBOperator.queryByOrderKey(mPersonalInfoDao, "num");
        for (int offset = 0; offset < rxList.size(); offset++) {
            rxList.get(offset).setOffset(offset);
            CommonDBOperator.updateItem(mPersonalInfoDao, rxList.get(offset));
        }
    }

    private List<LocalBDNum> mLocalBDNums = new ArrayList<>();
    private List<BDNum> mNumList =new ArrayList<>();//准备上传服务的北斗bean集合
    /**
     * 获取北斗号信息  send =1 传服务 send=0 设置sp
     */
    private void getAllBDInfoFromServer() {
        //   登录成功开始下载设备信息
        Observable<AllBDInfosBean> observable = SLMRetrofit.getInstance().getApi().getAllBDInfos();
        observable.compose(new ThreadSwitchTransformer<AllBDInfosBean>()).subscribe(new CallbackListObserver<AllBDInfosBean>() {
            @Override
            protected void onSucceed(AllBDInfosBean bean) {
                if(bean!=null){
                    if(bean.getCode()==0){
                        //请求成功
                        if(bean.getData()!=null && bean.getData().size()>0){
                            //有数据
                            for (int i = 0; i <bean.getData().size() ; i++) {
                                AllBDInfosBean.DataBean dataBean = bean.getData().get(i);

                                if ("1".equals(dataBean.getSend())||"1".equals(dataBean.getReceive())){
                                    BDNum num = new BDNum(dataBean.getBdNumber(), Constants.TX_JZH);
                                    mNumList.add(num);//上传到服务接口的BdNum
                                }else if("0".equals(dataBean.getSend())||"0".equals(dataBean.getReceive())) {
                                    SpUtils.putString(Constants.UPLOAD_JZH_NUM, dataBean.getBdNumber());
                                    try {
                                        if(SearchLocMapApplication.getInstance().getUploadService()!=null){
                                            SearchLocMapApplication.getInstance().getUploadService().setNum(Constants.TX_JZH,dataBean.getBdNumber());
                                        }else {
                                            SpUtils.putString(Constants.HTTP_TOOKEN, "");
                                            return;
                                        }

                                    } catch (RemoteException e) {
                                        e.printStackTrace();
                                        SpUtils.putString(Constants.HTTP_TOOKEN, "");
                                        return;
                                    }
                                }
                                //添加到本地数据库
                                LocalBDNum localBDNum = new LocalBDNum(dataBean.getBdNumber(), dataBean.getSend(),dataBean.getReceive());
                                mLocalBDNums.add(localBDNum);
                            }
                        }
                    }else {
                        showToast(bean.getMessage()+"");
                    }
                }else {
                    showToast("服务器未能获取北斗平台的信息");
                }
            }

            @Override
            protected void onFailed() {

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
