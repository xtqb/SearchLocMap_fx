package com.lhzw.searchlocmap.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.LoRaManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.lhzw.searchlocmap.R;
import com.lhzw.searchlocmap.application.SearchLocMapApplication;
import com.lhzw.searchlocmap.bean.AllBDInfosBean;
import com.lhzw.searchlocmap.bean.AllPersonInfoBean;
import com.lhzw.searchlocmap.bean.BaseBean;
import com.lhzw.searchlocmap.bean.BindingOfWatchBean;
import com.lhzw.searchlocmap.bean.BindingWatchBean;
import com.lhzw.searchlocmap.bean.HttpPersonInfo;
import com.lhzw.searchlocmap.bean.HttpRequstInfo;
import com.lhzw.searchlocmap.bean.LocPersonalInfo;
import com.lhzw.searchlocmap.bean.LocalBDNum;
import com.lhzw.searchlocmap.bean.PersonalInfo;
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
import com.lhzw.searchlocmap.view.LoadingView;
import com.lhzw.searchlocmap.view.ShowProgressDialog;
import com.lhzw.searchlocmap.view.ShowUploadUpper;
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
    private ToggleButtonView toggle_statistics_postion;
    private ToggleButtonView toggle_storage_pattern;
    private ToggleButtonView toggle_rescue_pattern;
    private RelativeLayout rl_sos_upload;
    private RelativeLayout rl_person_upload;
    private RelativeLayout rl_time_setting;
    private RelativeLayout rl_loc_register;
    private RelativeLayout rl_loc_rescue;
    private RelativeLayout rl_loc_info;
    private RelativeLayout rl_offline_map;
    private RelativeLayout rl_compass;
    private ToggleButtonView toggle_upload_pattern;
    private boolean isPlaySound;
    private boolean isVibrate;
    private boolean isRescueFlood;
    private boolean isMapState;
    private boolean isUpload;
    private boolean isStatistics;
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
    private TextView tv_type_pattern;
    private TextView tv_storage_type;
    private TextView tv_amc;
    private TextView tv_pattern_content;
    private TextView tv_statistics_postion_counter;
    private ShowUploadUpper uploadUpper;
    private LoRaManager loRaManager;
    private LoadingView loadingView;
    private Dao<LocPersonalInfo, Integer> perdao;
    private Dao<PersonalInfo, Integer> dao;
    private List<Integer> offsetList = new ArrayList();
    private DatabaseHelper helper;
    private Dao<BindingOfWatchBean, Integer> bindDao;
    private Dao<HttpPersonInfo, Integer> httpDao;

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
        toggle_rescue_pattern = (ToggleButtonView) view.findViewById(R.id.toggle_rescue_pattern);

        toggle_vibrator = (ToggleButtonView) view
                .findViewById(R.id.toggle_vibrator);
        rl_bd_num_setting = (RelativeLayout) view.findViewById(R.id.rl_bd_num_setting);
        rl_update = (RelativeLayout) view.findViewById(R.id.rl_update);
        rl_sync_data = (RelativeLayout) view.findViewById(R.id.rl_sync_data);
        rl_net_setting = (RelativeLayout) view.findViewById(R.id.rl_net_setting);
        toggle_upload_pattern = (ToggleButtonView) view.findViewById(R.id.toggle_upload_pattern);
        toggle_statistics_postion = (ToggleButtonView) view.findViewById(R.id.toggle_statistics_postion);
        toggle_storage_pattern = (ToggleButtonView) view.findViewById(R.id.toggle_storage_pattern);
        tv_pattern_content = (TextView) view.findViewById(R.id.tv_pattern_content);
        tv_statistics_postion_counter = (TextView) view.findViewById(R.id.tv_statistics_postion_counter);
        tv_name = (TextView) view.findViewById(R.id.tv_name);
        tv_amc = (TextView) view.findViewById(R.id.tv_amc);
        tv_type_pattern = (TextView) view.findViewById(R.id.tv_type_pattern);
        tv_storage_type = (TextView) view.findViewById(R.id.tv_storage_type);
    }

    @SuppressLint("WrongConstant")
    private void initData() {
        isPlaySound = SpUtils.getBoolean(SPConstants.SLIDE_SOUND, true);
        isVibrate = SpUtils.getBoolean(SPConstants.SLIDE_VIBRATOR, true);
        isUpload = SpUtils.getBoolean(SPConstants.UPLOAD_PATTERN, false);
        isStatistics = SpUtils.getBoolean(SPConstants.STATISTICS_REPORT_POISTION, false);
        isRescueFlood = SpUtils.getBoolean(SPConstants.RESCUE_PATTERN_FLOOD, false);
        isMapState = SpUtils.getBoolean(SPConstants.MAP_SWITCH, false);
        toggle_bell.setSliderState(isPlaySound);
        toggle_vibrator.setSliderState(isVibrate);
        toggle_upload_pattern.setSliderState(isUpload);
        toggle_statistics_postion.setSliderState(isStatistics);
        toggle_storage_pattern.setSliderState(isMapState);
        toggle_rescue_pattern.setSliderState(isRescueFlood);
        tv_name.setText(SpUtils.getString(SPConstants.LOGIN_NAME, "lisi"));
        tv_amc.setText("mac地址：" + BaseUtils.getMacFromHardware());
        tv_pattern_content.setText(isUpload ? getString(R.string.upload_pattern_auto).replace("@",
                SpUtils.getInt(SPConstants.UPLOAD_UPPER, Constants.UPLOAD_UPPER_DEAFAULT) + "")
                : getString(R.string.upload_pattern_default));
        if (isStatistics) {
            tv_statistics_postion_counter.setText(getString(R.string.setting_upload_statistics_num).replace
                    ("@", SpUtils.getInt(SPConstants.STATISTICS_REPORT_NUM, 0) + ""));
        } else {
            tv_statistics_postion_counter.setVisibility(View.GONE);
            tv_statistics_postion_counter.setText("");
        }
        tv_type_pattern.setText(getString(isRescueFlood ? R.string.rescue_pattern_flood : R.string.rescue_pattern_fire));
        tv_storage_type.setText(getString(isMapState ? R.string.settings_storage_type_inner : R.string.settings_storage_type_outer));
        loRaManager = (LoRaManager) getActivity().getSystemService(Context.LORA_SERVICE);
        if (helper == null) helper = DatabaseHelper.getHelper(getActivity());
        perdao = helper.getLocPersonDao();
        dao = helper.getPersonalInfoDao();
        bindDao = helper.getBindingOfWatchDao();
        httpDao = helper.getHttpPerDao();
    }

    private void setListener() {
        tv_back.setOnClickListener(this);
        toggle_bell.setOnToggleClickListener(this);
        toggle_vibrator.setOnToggleClickListener(this);
        toggle_rescue_pattern.setOnToggleClickListener(this);
        toggle_upload_pattern.setOnToggleClickListener(this);
        toggle_statistics_postion.setOnToggleClickListener(this);
        toggle_storage_pattern.setOnToggleClickListener(this);
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
        tv_pattern_content.setOnClickListener(this);
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
                SpUtils.putString(Constants.HTTP_TOOKEN, "");
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
            case R.id.tv_pattern_content:
                if (!isUpload) {
                    return;
                }
                showUploadUpperDialog();
                break;
            case R.id.bt_upload_pattern_save:
                if (uploadUpper != null) {
                    if ("".equals(uploadUpper.getUpperNum())) {
                        showToast(getString(R.string.upload_pattern_auto_input_et_fail));
                        return;
                    } else if (Integer.parseInt(uploadUpper.getUpperNum()) < 8) {
                        showToast(getString(R.string.diglog_note_input_upper_num_save));
                        return;
                    } else {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm.isActive()) {
                            imm.hideSoftInputFromWindow(uploadUpper.getPlotEditext().getApplicationWindowToken(), 0);
                        }
                        SpUtils.putInt(SPConstants.UPLOAD_UPPER, Integer.parseInt(uploadUpper.getUpperNum()));
                        showToast(getString(R.string.diglog_upper_num_save_success));
                        tv_pattern_content.setText(getString(R.string.upload_pattern_auto).replace("@", uploadUpper.getUpperNum()));
                    }
                    uploadUpper.dismiss();
                }
                break;
            case R.id.bt_upload_pattern_cancle:
                if (uploadUpper != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(uploadUpper.getPlotEditext().getApplicationWindowToken(), 0);
                    }
                    uploadUpper.dismiss();
                }
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
            case R.id.toggle_upload_pattern:
                isUpload = !isUpload;
                SpUtils.putBoolean(SPConstants.UPLOAD_PATTERN,
                        isUpload);
                tv_pattern_content.setText(isUpload ? getString(R.string.upload_pattern_auto).replace("@",
                        SpUtils.getInt(SPConstants.UPLOAD_UPPER, Constants.UPLOAD_UPPER_DEAFAULT) + "")
                        : getString(R.string.upload_pattern_default));
                break;
            case R.id.toggle_statistics_postion:
                isStatistics = !isStatistics;
                SpUtils.putBoolean(SPConstants.STATISTICS_REPORT_POISTION,
                        isStatistics);
                if (isStatistics) {
                    tv_statistics_postion_counter.setVisibility(View.VISIBLE);
                    tv_statistics_postion_counter.setText(getString(R.string.setting_upload_statistics_num).replace
                            ("@", SpUtils.getInt(SPConstants.STATISTICS_REPORT_NUM, 0) + ""));
                } else {
                    SpUtils.putInt(SPConstants.STATISTICS_REPORT_NUM, 0);
                    tv_statistics_postion_counter.setVisibility(View.GONE);
                    tv_statistics_postion_counter.setText("");
                }
                break;
            case R.id.toggle_rescue_pattern:
                if (BaseUtils.isNetConnected(getContext())) {
                    Dao<HttpPersonInfo, Integer> httpDao = helper.getHttpPerDao();
                    Log.e("MAC", "mac  " + BaseUtils.getMacFromHardware());
                    List<HttpPersonInfo> list = CommonDBOperator.queryByKeys(httpDao, "deviceNumbers", BaseUtils.getMacFromHardware());
                    if (list != null && list.size() > 0) {
                        updateRescueState();
                        if (isRescueFlood) {
                            iniOffset();
                        }
                        syncBindingWatch(list.get(0).getOrg());
                        list.clear();
                    } else {
                        new Handler().postDelayed(() -> {
                            toggle_rescue_pattern.setSliderState(isRescueFlood);
                        }, 500);
                        showToast("该手持MAC未在平台录入");
                    }
                    return;
                } else {
                    List watchs = CommonDBOperator.getList(bindDao);
                    if (watchs != null && watchs.size() > 0) {
                        if (loadingView == null) {
                            loadingView = new LoadingView(getActivity());
                        }
                        loadingView.setLoadingTitle("更换腕表数据...");
                        if (!loadingView.isShowing()) {
                            loadingView.show();
                        }
                        updateRescueState();
                        if (isRescueFlood) {
                            iniOffset();
                        }
                        new Thread(() -> {
                            dealgData(watchs, false);
                            getActivity().runOnUiThread(() -> {
                                loadingView.dismiss();
                                loadingView.cancel();
                                Toast.makeText(getActivity(), "切换成功，应用重启", Toast.LENGTH_SHORT).show();
                                new Handler().postDelayed(() -> {
                                    restartApplication(getActivity());
                                }, 3000);
                                watchs.clear();
                            });
                        }).start();
                    }
                }
                break;
            case R.id.toggle_storage_pattern:
                SpUtils.putBoolean(SPConstants.MAP_SWITCH, !SpUtils.getBoolean(SPConstants.MAP_SWITCH, false));
                isMapState = SpUtils.getBoolean(SPConstants.MAP_SWITCH, false);
                tv_storage_type.setText(getString(isMapState ? R.string.settings_storage_type_inner : R.string.settings_storage_type_outer));
                showToast("地图数据切换为" + getString(isMapState ? R.string.settings_storage_type_inner : R.string.settings_storage_type_outer) + "，应用重启");
                new Handler().postDelayed(() -> {
                    restartApplication(getActivity());
                }, 3000);
                break;
        }
    }

    /**
     * 获取全部绑定腕表信息
     */
    private void downloadBindWatch() {
        List<HttpPersonInfo> list = CommonDBOperator.queryByKeys(httpDao, "deviceNumbers", BaseUtils.getMacFromHardware());
        if (list != null && list.size() > 0) {
            Log.e("BINDING", "------------------------------  org : " + list.get(0).getOrg());
            Observable<BaseBean<List<BindingOfWatchBean>>> observable = SLMRetrofit.getInstance().getApi().getBindingWatchs(Integer.parseInt(list.get(0).getOrg()));
            observable.compose(new ThreadSwitchTransformer<>()) //从数据流中得到原始Observable<T>的操作符
                    .subscribe(new CallbackListObserver<BaseBean<List<BindingOfWatchBean>>>() {

                        @Override
                        protected void onSucceed(BaseBean<List<BindingOfWatchBean>> baseBean) {
                            // 获取数据成功 保存数据
                            try {
                                if ("0".equals(baseBean.getCode())) {
                                    //删除数据表
                                    CommonDBOperator.deleteAllItems(bindDao);
                                    List<BindingOfWatchBean> list = baseBean.getData();
                                    //保存数据
                                    for (BindingOfWatchBean item : list) {
                                        if (item.isBound()) {
                                            item.setState(0);
                                        } else {
                                            item.setState(1);
                                        }
                                        CommonDBOperator.saveToDB(bindDao, item);
                                    }
                                    list.clear();
                                    // 获取绑定腕表
                                    updateBindPerson();
                                }
                            } catch (Exception e) {
                                Log.e("TAG", "解析失败");
                            }
                        }

                        @Override
                        protected void onFailed() {
                            //获取数据失败
                        }
                    });
        } else {
            //删除数据表
            CommonDBOperator.deleteAllItems(bindDao);
        }
    }

    /**
     * 获取该mac下的已绑定腕表信息
     */
    //
    private void updateBindPerson() {
        String mac = BaseUtils.getMacFromHardware();
        Observable<BindingWatchBean> observable = SLMRetrofit.getInstance().getApi().getBindingWatch(mac);
        observable.compose(new ThreadSwitchTransformer<BindingWatchBean>()).subscribe(new CallbackListObserver<BindingWatchBean>() {
            @Override
            protected void onSucceed(BindingWatchBean bean) {
                if (bean.getCode() == 0) {
                    if (bean.getData() != null && bean.getData().size() > 0) {
                        //有绑定数据   插入表中
                        //删除数据
                        CommonDBOperator.deleteAllItems(perdao);
                        CommonDBOperator.deleteAllItems(dao);
                        for (int i = 0; i < bean.getData().size(); i++) {
                            BindingWatchBean.DataBean dataBean = bean.getData().get(i);
                            List<HttpPersonInfo> httpPersonInfo = CommonDBOperator.queryByKeys(httpDao, "deviceNumbers", dataBean.getDeviceNumber());
                            String realName = "perdao";

                            if (httpPersonInfo != null && httpPersonInfo.size() > 0) {
                                realName = TextUtils.isEmpty(httpPersonInfo.get(0).getRealName()) ? "" : httpPersonInfo.get(0).getRealName();
                            } else {
                                LogUtil.e("httpPersonInfo==" + httpPersonInfo);
                            }
                            LocPersonalInfo perInfo = new LocPersonalInfo();
                            perInfo.setNum(dataBean.getDeviceNumber());
                            perInfo.setName(realName);
                            CommonDBOperator.saveToDB(perdao, perInfo);

                            PersonalInfo personalInfo = new PersonalInfo();
                            personalInfo.setName(realName);
                            personalInfo.setNum(dataBean.getDeviceNumber());
                            personalInfo.setSex("0");
                            personalInfo.setState(Constants.PERSON_OFFLINE);
                            personalInfo.setOffset(i);
                            CommonDBOperator.saveToDB(dao, personalInfo);
                        }

                    }
                } else {

                }
            }

            @Override
            protected void onFailed() {

            }
        });
    }

    private void updateRescueState() {
        isRescueFlood = !isRescueFlood;
        SpUtils.putBoolean(SPConstants.RESCUE_PATTERN_FLOOD,
                isRescueFlood);
        setBDType(isRescueFlood ? Constants.CHANNEL_DEF : SpUtils.getInt(SPConstants.CHANNEL_NUM, Constants.CHANNEL_DEF));
        tv_type_pattern.setText(getString(isRescueFlood ? R.string.rescue_pattern_flood : R.string.rescue_pattern_fire));
    }

    private void syncBindingWatch(String org) {
        try {
            int orgnization = Integer.parseInt(org);
            if (loadingView == null) {
                loadingView = new LoadingView(getActivity());
            }
            loadingView.setLoadingTitle("同步腕表数据...");
            if (!loadingView.isShowing()) {
                loadingView.show();
            }

            Observable<BaseBean<List<BindingOfWatchBean>>> observable = SLMRetrofit.getInstance().getApi().getBindingWatchs(orgnization);
            observable.compose(new ThreadSwitchTransformer<>()) //从数据流中得到原始Observable<T>的操作符
                    .subscribe(new CallbackListObserver<BaseBean<List<BindingOfWatchBean>>>() {

                        @Override
                        protected void onSucceed(BaseBean<List<BindingOfWatchBean>> baseBean) {
                            // 获取数据成功 保存数据
                            if ("0".equals(baseBean.getCode())) {
                                //删除数据表
                                List<BindingOfWatchBean> list = baseBean.getData();
                                if (list != null && list.size() > 0) {
                                    dealgData(list, true);
                                }
                            } else {
                                updateRescueState();
                            }
                            loadingView.dismiss();
                            loadingView.cancel();
                            Toast.makeText(getActivity(), "切换成功，开始重启应用...", Toast.LENGTH_SHORT).show();
                            new Handler().postDelayed(() -> {
                                restartApplication(getActivity());
                            }, 3000);
                        }

                        @Override
                        protected void onFailed() {
                            //获取数据失败
                            new Handler().postDelayed(() -> {
                                toggle_rescue_pattern.setSliderState(isRescueFlood);
                            }, 500);
                            showToast("网络异常");
                            loadingView.dismiss();
                            loadingView.cancel();
                        }
                    });
        } catch (Exception e) {
            Log.e("UnBingingWatchFragment", "org parser exception");
            updateRescueState();
        }
    }

    private void dealgData(List<BindingOfWatchBean> list, boolean isUpdate) {
        if (isUpdate) {
            CommonDBOperator.deleteAllItems(bindDao);
        }
        for (BindingOfWatchBean bean : list) {
            // 处理数据 插入数据库
            if (isRescueFlood) {
//                if (!bean.isBound()) {
                // 两个数据库连接
                List<LocPersonalInfo> infos = CommonDBOperator.queryByKeys(perdao,
                        "num", bean.getDeviceNumber());
                if (null != infos && infos.size() > 0) {
                    // 更新
                    infos.get(0).setName(bean.getRealName() + "");
                    CommonDBOperator.updateItem(perdao, infos.get(0));
                    infos.clear();
                    // 更新 personItem
                    List<PersonalInfo> list1 = CommonDBOperator.queryByKeys(dao,
                            "num", bean.getDeviceNumber());
                    list1.get(0).setName(bean.getRealName());
                    CommonDBOperator.updateItem(dao, list1.get(0));
                    list1.clear();
                } else {
                    // 保存
                    int offset = 0;
                    if (offsetList.size() > 0) {
                        offset = offsetList.get(offsetList.size() - 1);
                        offsetList.remove(offsetList.size() - 1);
                    } else {
                        List<LocPersonalInfo> pers = CommonDBOperator.getList(perdao);
                        if (pers == null || pers.size() == 0) {
                            offset = 1;
                        } else {
                            offset = pers.size() + 1;
                        }
                    }
                    LocPersonalInfo perInfo = new LocPersonalInfo();
                    perInfo.setNum(bean.getDeviceNumber());
                    perInfo.setName(bean.getRealName());
                    perInfo.setSex("男");
                    perInfo.setOffset(offset);
                    CommonDBOperator.saveToDB(perdao, perInfo);

                    // 插入到 personItem
                    PersonalInfo item = new PersonalInfo();
                    item.setNum(bean.getDeviceNumber());
                    item.setName(bean.getRealName());
                    item.setSex("男");
                    item.setOffset(offset);
                    item.setState(Constants.PERSON_OFFLINE);
                    CommonDBOperator.saveToDB(dao, item);
                }
//                }
            } else {
                if (!bean.isBound() || (bean.isBound() && !bean.getBelongdeviceNumber().equals(BaseUtils.getMacFromHardware()))) {
                    CommonDBOperator.deleteByKeys(perdao, "num", bean.getDeviceNumber());
                    CommonDBOperator.deleteByKeys(dao, "num", bean.getDeviceNumber());
                }
            }
            if (isUpdate) {
                CommonDBOperator.saveToDB(bindDao, bean);
            }
        }
    }

    private void restartApplication(Context context) {
        final Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }


    private void iniOffset() {
        // 获取期间没有使用的ID
        List<LocPersonalInfo> locList1 = CommonDBOperator.queryByOrderKey(perdao, "offset", true);
        if (locList1 != null && locList1.size() > 0) {
            int offset = 1;
            for (LocPersonalInfo per : locList1) {
                if (offset == per.getOffset()) {
                    offset++;
                } else {
                    while (offset < per.getOffset()) {
                        offsetList.add(offset);
                        offset++;
                    }
                }
            }
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
            Log.e("BINDING", "------------------------------  1");
        }

        @Override
        protected Boolean doInBackground(Object[] params) {
            boolean isSuccess = false;
            Integer[] values = new Integer[2];
            values[0] = mHttpRequstInfos.size() + mLocalBDNums.size();
            publishProgress(values);
            int delay = 20;
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
            downloadBindWatch();
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
            Log.e("BINDING", "------------------------------  2");
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

    private void showUploadUpperDialog() {
        if (uploadUpper == null) {
            uploadUpper = new ShowUploadUpper(getActivity());
        }
        uploadUpper.showDialog();
        uploadUpper.setListener(this);
        uploadUpper.cleanEtContent();
        uploadUpper.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        uploadUpper.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
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

    public void refleshStatistics() {
        if (getActivity() != null && !getActivity().isFinishing()) {
            tv_statistics_postion_counter.setText(getString(R.string.setting_upload_statistics_num).replace
                    ("@", SpUtils.getInt(SPConstants.STATISTICS_REPORT_NUM, 0) + ""));
        }
    }

    private void setBDType(final int type) {
        new Thread(() -> {
            if (loRaManager != null) {
                loRaManager.changeWatchType(type);
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        loRaManager = null;
        loadingView = null;
        offsetList.clear();
        offsetList = null;

        helper = null;
    }
}
