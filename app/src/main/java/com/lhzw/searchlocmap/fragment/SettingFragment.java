package com.lhzw.searchlocmap.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.lhzw.searchlocmap.R;
import com.lhzw.searchlocmap.application.SearchLocMapApplication;
import com.lhzw.searchlocmap.bean.AllBDInfosBean;
import com.lhzw.searchlocmap.bean.AllPersonInfoBean;
import com.lhzw.searchlocmap.bean.HttpPersonInfo;
import com.lhzw.searchlocmap.bean.HttpRequstInfo;
import com.lhzw.searchlocmap.bean.LocalBDNum;
import com.lhzw.searchlocmap.constants.Constants;
import com.lhzw.searchlocmap.constants.SPConstants;
import com.lhzw.searchlocmap.db.dao.CommonDBOperator;
import com.lhzw.searchlocmap.db.dao.DatabaseHelper;
import com.lhzw.searchlocmap.net.CallbackListObserver;
import com.lhzw.searchlocmap.net.SLMRetrofit;
import com.lhzw.searchlocmap.net.ThreadSwitchTransformer;
import com.lhzw.searchlocmap.ui.BDSettingActivity;
import com.lhzw.searchlocmap.ui.CompassActivity;
import com.lhzw.searchlocmap.ui.DipperNumSettingtActivity;
import com.lhzw.searchlocmap.ui.LocInfoRegisterActivity;
import com.lhzw.searchlocmap.ui.LocalInfoActivity;
import com.lhzw.searchlocmap.ui.NetSettingActivity;
import com.lhzw.searchlocmap.ui.OfflineMapManagerActivity;
import com.lhzw.searchlocmap.ui.ReportSosActivity;
import com.lhzw.searchlocmap.ui.RescueServerActivity;
import com.lhzw.searchlocmap.ui.SearchTimeSettingActivity;
import com.lhzw.searchlocmap.ui.UpdateAppListActivity;
import com.lhzw.searchlocmap.ui.UploadPersonInfoActivity;
import com.lhzw.searchlocmap.utils.BaseUtils;
import com.lhzw.searchlocmap.utils.LogUtil;
import com.lhzw.searchlocmap.utils.SpUtils;
import com.lhzw.searchlocmap.utils.ToastUtil;
import com.lhzw.searchlocmap.view.ShowProgressDialog;
import com.lhzw.searchlocmap.view.ToggleButtonView;
import com.lhzw.searchlocmap.view.ToggleButtonView.onToggleClickListener;
import com.lhzw.uploadmms.BDNum;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

public class SettingFragment extends Fragment implements OnClickListener,
        onToggleClickListener {
    private View view;
    private TextView tv_back;
    private ToggleButtonView toggle_bell;
    private ToggleButtonView toggle_vibrator;
    private RelativeLayout rl_sos_upload;
    private RelativeLayout rl_person_upload;
    private RelativeLayout rl_time_setting;
    private RelativeLayout rl_loc_register;
    private RelativeLayout rl_loc_rescue;
    private RelativeLayout rl_loc_info;
    private RelativeLayout rl_offline_map;
    private RelativeLayout rl_compass;
    private boolean isPlaySound;
    private boolean isVibrate;
    private AlerDialogshow alertdialog;
    private RelativeLayout rl_bd_setting;
    private RelativeLayout rl_bd_service;
    private RelativeLayout rl_bd_num_setting;
    private RelativeLayout rl_update;
    private RelativeLayout rl_sync_data;
    private ShowProgressDialog progress;
    private Toast mGlobalToast;
    private RelativeLayout rl_net_setting;
    private TextView tv_name;
    private TextView tv_amc;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.setting_fg, container, false);
        initView();
        initData();
        setListener();
        return view;
    }

    private void initView() {
        tv_back = (TextView) view.findViewById(R.id.tv_back);
        rl_sos_upload = (RelativeLayout) view.findViewById(R.id.rl_sos_upload);
        rl_person_upload = (RelativeLayout) view
                .findViewById(R.id.rl_person_upload);
        rl_time_setting = (RelativeLayout) view
                .findViewById(R.id.rl_time_setting);
        rl_loc_register = (RelativeLayout) view
                .findViewById(R.id.rl_loc_register);
        rl_bd_setting = (RelativeLayout) view
                .findViewById(R.id.rl_bd_setting);
        rl_bd_service = (RelativeLayout) view.findViewById(R.id.rl_bd_service);
        rl_loc_rescue = (RelativeLayout) view.findViewById(R.id.rl_loc_rescue);
        rl_loc_info = (RelativeLayout) view.findViewById(R.id.rl_loc_info);
        rl_offline_map = (RelativeLayout) view
                .findViewById(R.id.rl_offline_map);
        rl_compass = (RelativeLayout) view.findViewById(R.id.rl_compass);
        toggle_bell = (ToggleButtonView) view.findViewById(R.id.toggle_bell);
        toggle_vibrator = (ToggleButtonView) view
                .findViewById(R.id.toggle_vibrator);
        rl_bd_num_setting = (RelativeLayout) view.findViewById(R.id.rl_bd_num_setting);
        rl_update = (RelativeLayout) view.findViewById(R.id.rl_update);
        rl_sync_data = (RelativeLayout) view.findViewById(R.id.rl_sync_data);
        rl_net_setting = (RelativeLayout) view.findViewById(R.id.rl_net_setting);

        tv_name = (TextView) view.findViewById(R.id.tv_name);
        tv_amc = (TextView) view.findViewById(R.id.tv_amc);
    }

    private void initData() {
        isPlaySound = SpUtils.getBoolean(SPConstants.SLIDE_SOUND, true);
        isVibrate = SpUtils.getBoolean(SPConstants.SLIDE_VIBRATOR, true);
        toggle_bell.setSliderState(isPlaySound);
        toggle_vibrator.setSliderState(isVibrate);

        tv_name.setText(SpUtils.getString(SPConstants.LOGIN_NAME, "lisi"));
        tv_amc.setText("mac地址：" + BaseUtils.getMacFromHardware());
    }

    private void setListener() {
        tv_back.setOnClickListener(this);
        toggle_bell.setOnToggleClickListener(this);
        toggle_vibrator.setOnToggleClickListener(this);
        rl_sos_upload.setOnClickListener(this);
        rl_person_upload.setOnClickListener(this);
        rl_time_setting.setOnClickListener(this);
        rl_loc_register.setOnClickListener(this);
        rl_loc_rescue.setOnClickListener(this);
        rl_loc_info.setOnClickListener(this);
        rl_offline_map.setOnClickListener(this);
        rl_compass.setOnClickListener(this);
        rl_bd_setting.setOnClickListener(this);
        rl_bd_service.setOnClickListener(this);
        rl_bd_num_setting.setOnClickListener(this);
        rl_update.setOnClickListener(this);
        rl_sync_data.setOnClickListener(this);
        rl_net_setting.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.tv_back:
                alertdialog = new AlerDialogshow(getActivity());
                alertdialog.show();
                alertdialog
                        .setContent(getString(R.string.security_system_back_note));
                alertdialog.setListener(this);
                break;
            case R.id.rl_sos_upload:
                startActivity(new Intent(getActivity(), ReportSosActivity.class));
                break;
            case R.id.rl_loc_info:
                startActivity(new Intent(getActivity(), LocalInfoActivity.class));
                break;
            case R.id.rl_person_upload:
                startActivity(new Intent(getActivity(),
                        UploadPersonInfoActivity.class));
                break;
            case R.id.rl_time_setting:
                startActivity(new Intent(getActivity(),
                        SearchTimeSettingActivity.class));
                break;
            case R.id.rl_loc_register:
                startActivity(new Intent(getActivity(),
                        LocInfoRegisterActivity.class));
                break;
            case R.id.rl_loc_rescue:
                startActivity(new Intent(getActivity(), RescueServerActivity.class));
                break;
            case R.id.rl_offline_map:
                startActivity(new Intent(getActivity(), OfflineMapManagerActivity.class));
                break;
            case R.id.rl_compass:
                startActivity(new Intent(getActivity(), CompassActivity.class));
                break;
            case R.id.tv_cancel:
                alertdialog.dismiss();
                alertdialog = null;
                break;
            case R.id.tv_sure:
                alertdialog.dismiss();
                getActivity().finish();
                break;
            case R.id.rl_bd_setting:
                startActivity(new Intent(getActivity(), BDSettingActivity.class));
                break;
            case R.id.rl_bd_service:
                try {
                    startActivity(new Intent("com.lhzw.intent.action_UPLOAD_SERVICE"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.rl_bd_num_setting:
                startActivity(new Intent(getActivity(), DipperNumSettingtActivity.class));
                break;
            case R.id.rl_update:
                startActivity(new Intent(getActivity(), UpdateAppListActivity.class));
                break;
            case R.id.rl_sync_data:
                if (!BaseUtils.isNetConnected(getActivity())) {
                    showToast(getString(R.string.net_net_connect_fail));
                    return;
                }
                //new AyncLoginTask().execute();
                doLoginTask();//执行登录操作
                break;
            case R.id.rl_net_setting:
                startActivityForResult(new Intent(getActivity(), NetSettingActivity.class), 0x0002);
                break;
        }
    }

    @Override
    public void onToggleClick(View view) {
        // TODO Auto-generated method stub
        switch (view.getId()) {
            case R.id.toggle_bell:
                SpUtils.putBoolean(SPConstants.SLIDE_SOUND,
                        !SpUtils.getBoolean(SPConstants.SLIDE_SOUND, true));
                break;
            case R.id.toggle_vibrator:
                SpUtils.putBoolean(SPConstants.SLIDE_VIBRATOR,
                        !SpUtils.getBoolean(SPConstants.SLIDE_VIBRATOR, true));
                break;

            default:
                break;
        }
    }

    private class AlerDialogshow extends AlertDialog {
        private TextView tv_content;
        private TextView tv_cancel;
        private TextView tv_sure;

        public AlerDialogshow(Context context) {
            super(context);
            // TODO Auto-generated constructor stub
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            // TODO Auto-generated method stub
            super.onCreate(savedInstanceState);
            setContentView(R.layout.dialog_show_note);
            init();
        }

        private void init() {
            setCancelable(false);
            tv_content = (TextView) findViewById(R.id.tv_content);
            tv_cancel = (TextView) findViewById(R.id.tv_cancel);
            tv_sure = (TextView) findViewById(R.id.tv_sure);
        }

        public void setListener(View.OnClickListener listener) {
            tv_cancel.setOnClickListener(listener);
            tv_sure.setOnClickListener(listener);
        }

        public void setContent(String content) {
            tv_content.setText(content);
        }
    }


    private List<LocalBDNum> mLocalBDNums = new ArrayList<>();
    private List<BDNum> mNumList = new ArrayList<>();

    private void doLoginTask() {
        ShowProgressDialog();
        getAllBDInfoFromServer();//获取平台的北斗号

    }

    /**
     * 获取全员信息
     */
    private List<HttpRequstInfo> mHttpRequstInfos;

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
                    } else {
                        showToast("获取人员数据为空");
                        cancelProgressDialog();
                    }
                } else {
                    LogUtil.e(bean.getMessage());
                    showToast("获取全员信息失败,message==" + bean.getMessage());
                    cancelProgressDialog();
                }
            }

            @Override
            protected void onFailed() {
                cancelProgressDialog();
                showToast(getString(R.string.net_request_fail));
                cancelProgressDialog();
            }
        });
    }

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
                                            cancelProgressDialog();
                                            ToastUtil.showToast("应急通信服务连接失败,退出APP");
                                            getActivity().finish();
                                        }

                                    } catch (RemoteException e) {
                                        e.printStackTrace();
                                        cancelProgressDialog();
                                        SpUtils.putString(Constants.HTTP_TOOKEN, "");
                                        ToastUtil.showToast("应急通信服务连接失败,退出APP");
                                        getActivity().finish();
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
                                    cancelProgressDialog();
                                    SpUtils.putString(Constants.HTTP_TOOKEN, "");
                                    ToastUtil.showToast("北斗服务连接失败,退出APP");
                                    getActivity().finish();

                                }
                            } catch (RemoteException e) {
                                e.printStackTrace();
                                cancelProgressDialog();
                                SpUtils.putString(Constants.HTTP_TOOKEN, "");
                                ToastUtil.showToast("北斗服务连接失败,退出APP");
                                getActivity().finish();

                            }


                            getAllPersonInfoBean();//获取人员基础信息

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

    /**
     * 获取北斗号信息  send =1 传服务 send=0 设置sp
     */
//    private void getAllBDInfoFromServer() {
//        //   登录成功开始下载设备信息
//        Observable<AllBDInfosBean> observable = SLMRetrofit.getInstance().getApi().getAllBDInfos();
//        observable.compose(new ThreadSwitchTransformer<AllBDInfosBean>()).subscribe(new CallbackListObserver<AllBDInfosBean>() {
//            @Override
//            protected void onSucceed(final AllBDInfosBean bean) {
//                if (bean != null) {
//                    if (bean.getCode() == 0) {
//                        //请求成功
//                        if (bean.getData() != null && bean.getData().size() > 0) {
//                            //有数据
//                            for (int i = 0; i < bean.getData().size(); i++) {
//
//                                AllBDInfosBean.DataBean dataBean = bean.getData().get(i);
//                                if ("1".equals(dataBean.getSend())) {
//                                    BDNum num = new BDNum(dataBean.getBdNumber(), Constants.TX_JZH);
//                                    mNumList.add(num);//上传到服务接口的BdNum
//                                }
//                                if ("1".equals(dataBean.getReceive())) {
//                                    SpUtils.putString(Constants.UPLOAD_JZH_NUM, dataBean.getBdNumber());
//                                    try {
//                                        if (SearchLocMapApplication.getInstance().getUploadService() != null) {
//                                            SearchLocMapApplication.getInstance().getUploadService().setNum(Constants.TX_JZH, dataBean.getBdNumber());
//                                        } else {
//                                            SpUtils.putString(Constants.HTTP_TOOKEN, "");
//                                            return;
//                                        }
//
//                                    } catch (RemoteException e) {
//                                        e.printStackTrace();
//                                        SpUtils.putString(Constants.HTTP_TOOKEN, "");
//                                        return;
//                                    }
//                                }
//                                //添加到本地数据库
//                                LocalBDNum localBDNum = new LocalBDNum(dataBean.getBdNumber(), dataBean.getSend(), dataBean.getReceive());
//                                mLocalBDNums.add(localBDNum);
//
//                            }
//
//                        }
//                    } else {
//                        showToast(bean.getMessage() + "");
//                    }
//                } else {
//                    showToast("服务器未能获取北斗平台的信息");
//                }
//
//            }
//
//            @Override
//            protected void onFailed() {
//
//            }
//
//        });
//    }

    private class ProgressTask extends AsyncTask<Object, Integer, Boolean> {
        private DatabaseHelper helper;
        private Dao<HttpPersonInfo, Integer> httpPerDao;
        private Dao mBdNumDao;
        private boolean isInitMax = false;

        @Override
        protected void onPreExecute() {
            helper = DatabaseHelper.getHelper(getActivity());
            httpPerDao = helper.getHttpPerDao();
            mBdNumDao = helper.getBdNumDao();
            CommonDBOperator.deleteAllItems(httpPerDao);
            CommonDBOperator.deleteAllItems(mBdNumDao);
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
            LogUtil.e("counter=" + counter + "values[0]=" + values[0]);
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
                showToast(getString(R.string.settings_sync_data_note));
            } else {
                showToast(getString(R.string.net_request_fail));
            }
            cancelProgressDialog();
        }

    }
//    private class AyncLoginTask extends AsyncTask<Object, Integer, Boolean> {
//        private DatabaseHelper helper;
//        private Dao<HttpPersonInfo, Integer> httpPerDao;
//        private Dao mBdNumDao;
//        private boolean isInitMax = false;
//
//        @Override
//        protected void onPreExecute() {
//            helper = DatabaseHelper.getHelper(getActivity());
//            httpPerDao = helper.getHttpPerDao();
//            mBdNumDao = helper.getBdNumDao();
//            ShowProgressDialog();
//        }
//
//        @Override
//        protected Boolean doInBackground(Object[] params) {
//            boolean isSuccess = false;
//            //清空数据
//            try {
//                CommonDBOperator.deleteAllItems(httpPerDao);
//                Integer[] values = new Integer[2];
//                String token = SpUtils.getString(Constants.HTTP_TOOKEN, "");
//                getAllBDInfoFromServer();
//                String rev = NetUtils.doHttpGetClient(token, Constants.USER_PATH);
//                if (rev != null) {
//                    JSONObject obj = new JSONObject(rev);
//                    int code = obj.getInt("code");
//                    if (code == 0) {
//                        String data = obj.getString("data");
//                        List<HttpRequstInfo> list = new Gson().fromJson(data, new TypeToken<List<HttpRequstInfo>>() {
//                        }.getType());
//                        values[0] = list.size() + mLocalBDNums.size();
//                        LogUtil.d("size : " + list.size() + "mLocalBDNums:" + mLocalBDNums.size());
//                        int delay = 40 * 100 / (list.size() + mLocalBDNums.size());
//                        int counter = 0;
//                        for (HttpRequstInfo info : list) {
//                            counter++;
//                            values[1] = counter;
//                            publishProgress(values);
//                            CommonDBOperator.saveToDB(httpPerDao, translationItem(info));
//                            Thread.sleep(delay);
//                        }
//
//                        for (LocalBDNum localBDNum : mLocalBDNums) {
//                            counter++;
//                            values[1] = counter;
//                            publishProgress(values);
//                            CommonDBOperator.saveToDB(mBdNumDao, localBDNum);
//                            //上传到服务
//                            Thread.sleep(delay);
//                        }
//                        try {//上传到服务接口
//                            if (SearchLocMapApplication.getInstance() != null && SearchLocMapApplication.getInstance().getUploadService() != null) {
//                                SearchLocMapApplication.getInstance().getUploadService().updateBDNum(mNumList);
//                            } else {
//                                SpUtils.putString(Constants.HTTP_TOOKEN, "");
//                                return false;
//                            }
//                        } catch (RemoteException e) {
//                            e.printStackTrace();
//                            SpUtils.putString(Constants.HTTP_TOOKEN, "");
//                        }
//
//                        isSuccess = true;
//                        list.clear();
//                        mLocalBDNums.clear();
//                    }
//                } else {
//                    isSuccess = false;
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
//                showToast(getString(R.string.settings_sync_data_note));
//            } else {
//                showToast(getString(R.string.net_request_fail));
//            }
//            cancelProgressDialog();
//        }
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        getActivity().finish();
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
        progress = new ShowProgressDialog(getActivity());
        progress.show();
    }

    private void cancelProgressDialog() {
        if (progress != null) {
            progress.dismiss();
        }
    }

    public void showToast(String text) {
        if (mGlobalToast == null) {
            mGlobalToast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
            mGlobalToast.show();
        } else {
            mGlobalToast.setText(text);
            mGlobalToast.setDuration(Toast.LENGTH_SHORT);
            mGlobalToast.show();
        }
    }
}
