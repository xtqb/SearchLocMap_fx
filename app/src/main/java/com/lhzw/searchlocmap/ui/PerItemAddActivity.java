package com.lhzw.searchlocmap.ui;

import android.app.Activity;
import android.content.BDManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.LoRaManager;
import android.content.ProtocolParser;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.lhzw.searchlocmap.R;
import com.lhzw.searchlocmap.adapter.SelectChannelAdapter;
import com.lhzw.searchlocmap.bean.BaseBean;
import com.lhzw.searchlocmap.bean.HttpPersonInfo;
import com.lhzw.searchlocmap.bean.LocPersonalInfo;
import com.lhzw.searchlocmap.bean.PersonalInfo;
import com.lhzw.searchlocmap.constants.Constants;
import com.lhzw.searchlocmap.constants.SPConstants;
import com.lhzw.searchlocmap.db.dao.CommonDBOperator;
import com.lhzw.searchlocmap.db.dao.DatabaseHelper;
import com.lhzw.searchlocmap.net.CallbackListObserver;
import com.lhzw.searchlocmap.net.SLMRetrofit;
import com.lhzw.searchlocmap.net.ThreadSwitchTransformer;
import com.lhzw.searchlocmap.utils.BaseUtils;
import com.lhzw.searchlocmap.utils.LogUtil;
import com.lhzw.searchlocmap.utils.SpUtils;
import com.lhzw.searchlocmap.utils.Utils;
import com.lhzw.searchlocmap.view.ShowAlertDialog;
import com.lhzw.searchlocmap.view.ShowSelectChanelDialog;

import java.util.List;

import io.reactivex.Observable;

public class PerItemAddActivity extends Activity implements OnClickListener,
		OnGlobalLayoutListener, ShowSelectChanelDialog.onChannelItemClickListener {
	private TextView save_btn;
	private TextView addback_btn;
	private LocPersonalInfo perInfo;
	private DatabaseHelper<?> helper;
	private TextView add_num_et;
	private EditText add_name_et;
	private RadioGroup add_sex_et;
	private EditText add_phone_et;
	private EditText add_phone1_et;
	private EditText add_phone2_et;
	private EditText add_blood_et;
	private EditText add_allergy_et;
	private Dao<LocPersonalInfo, Integer> perdao;
	private Dao<PersonalInfo, Integer> dao;
	private String person_sex;
	private WatchSignalReceiver receiver;
	private ShowAlertDialog alertDialog;
	private ImageView icon_watch;
	private View decorView;
	private ScrollView loginBinding;
	private Toast mGlobalToast;
	private boolean isCurrentApp;
	private LoRaManager loRaManager;
	private byte[] bdByteArr;
	private BDManager mBDManager;
	private TextView tv_local_identify;
	private boolean isSelectChannel;
	private int CHANNEL;
	private ShowSelectChanelDialog dialog;
	private TextView tv_channel;
	private ImageView im_channel_edit;
	private boolean isFinish;
	private Dao<HttpPersonInfo, Integer> mHttpPerDao;

	private boolean canSave;

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.per_add_details);
		initView();
		initData();
		setListener();
	}

	private void initData() {
		isCurrentApp = true;
		person_sex = getString(R.string.person_sex_man);
		isSelectChannel = false;
		SpUtils.putBoolean(SPConstants.PERSON_ENTER, true);
		registerBroadcastReceiver();
		loRaManager = (LoRaManager) getSystemService(Context.LORA_SERVICE);
		mBDManager = (BDManager) getSystemService(Context.BD_SERVICE);
		setBDType(Constants.CHANNEL_DEF);
		decorView = getWindow().getDecorView();
		decorView.getViewTreeObserver().addOnGlobalLayoutListener(this);
		String num = mBDManager.getBDCardNumber();
		if(BaseUtils.isStringEmpty(num)) {
			tv_local_identify.setText("00000000");
		} else {
			tv_local_identify.setText("00" + num);
		}
		CHANNEL = SpUtils.getInt(SPConstants.CHANNEL_NUM, Constants.CHANNEL_DEF);
		if(SpUtils.getInt(SPConstants.CHANNEL_NUM, Constants.CHANNEL_DEF) == Constants.CHANNEL_DEF) {
			dialog = new ShowSelectChanelDialog(this);
			dialog.show();
			dialog.setAdapter(new SelectChannelAdapter(PerItemAddActivity.this, getResources().getStringArray(R.array.signal_channel)), this);
			dialog.setOnSelecteChannelClickListener(this);
		} else {
			isSelectChannel = true;
		}
		tv_channel.setText(CHANNEL + "");
		isFinish = true;
	}

	private void initView() {
		save_btn = (TextView) findViewById(R.id.save);
		save_btn.setOnClickListener(this);
		addback_btn = (TextView) findViewById(R.id.add_per_back);
		addback_btn.setOnClickListener(this);
		add_num_et = (TextView) findViewById(R.id.add_coding_input);
		add_name_et = (EditText) findViewById(R.id.add_name);
		add_sex_et = (RadioGroup) findViewById(R.id.rg_two);
		add_phone_et = (EditText) findViewById(R.id.add_phone);
		add_phone1_et = (EditText) findViewById(R.id.add_phone1);
		add_phone2_et = (EditText) findViewById(R.id.add_phone2);
		add_blood_et = (EditText) findViewById(R.id.add_blood);
		add_allergy_et = (EditText) findViewById(R.id.add_allergy);
		icon_watch = (ImageView) findViewById(R.id.icon_watch);
		helper = DatabaseHelper.getHelper(PerItemAddActivity.this);
		perdao = helper.getLocPersonDao();
		dao = helper.getPersonalInfoDao();
		mHttpPerDao = helper.getHttpPerDao();
		loginBinding = (ScrollView) findViewById(R.id.pre_add_details_scrollview);
		tv_local_identify = (TextView) findViewById(R.id.tv_local_identify);
		tv_channel = (TextView) findViewById(R.id.tv_channel);
		im_channel_edit = (ImageView) findViewById(R.id.im_channel_edit);
	}

	private void setListener() {
		add_sex_et.setOnCheckedChangeListener(listener);
		im_channel_edit.setOnClickListener(this);
	}

	private OnCheckedChangeListener listener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch (checkedId) {
			case R.id.btn1:
				person_sex = getString(R.string.person_sex_man);
				break;
			case R.id.btn2:
				person_sex = getString(R.string.person_sex_women);
				break;

			default:
				break;
			}

		}

	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.save:
			String person_code = add_num_et.getText().toString();
			if (BaseUtils.isStringEmpty(person_code)) {
				showToast(getString(R.string.person_detail_save_note));
				return;
			}
			if (BaseUtils
					.isStringEmpty(add_name_et.getText().toString().trim())) {
				showToast(getString(R.string.person_info_requried_name));
				return;
			}

			if(!canSave){//已保存过了  就不再保存了
				showToast(getString(R.string.person_info_repeat_note));
				return;
			}

	          AskServerToBind(BaseUtils.getDipperNum(this),person_code);

			break;

		case R.id.add_per_back:
			if (!BaseUtils.isStringEmpty(add_num_et.getText().toString())) {
				showAlertDialog();
				return;
			}
			setResult(300);
			finish();
			Utils.closeSoftInput(PerItemAddActivity.this);
			setBDType(CHANNEL);
			break;
		case R.id.tv_cancel:
			alertDialog.cancel();
			break;
		case R.id.tv_sure:
			alertDialog.clear();
			setBDType(CHANNEL);
			this.finish();
			break;
		case R.id.bt_back:
			dialog.dismiss();
			if(isFinish) {
				this.finish();
			}
			break;
		case R.id.im_channel_edit:
			if(dialog == null) {
				dialog = new ShowSelectChanelDialog(this);
				dialog.show();
				dialog.setAdapter(new SelectChannelAdapter(PerItemAddActivity.this, getResources().getStringArray(R.array.signal_channel)), this);
				dialog.setOnSelecteChannelClickListener(this);
				dialog.setConten(getString(R.string.dialog_cancel));
			} else {
				dialog.show();
				dialog.setConten(getString(R.string.dialog_cancel));
			}
			isFinish = false;
			break;
		}
	}


	/**
	 * 向服务器请求是否可以绑定
	 * @param bdNum  本机北斗号
	 * @param deviceNum  手表的固话注册码
	 */

	private void AskServerToBind(String bdNum ,String deviceNum) {
		LogUtil.d("bdNum=="+bdNum+"<=====>deviceNum=="+deviceNum);
		Observable<BaseBean> observable = SLMRetrofit.getInstance().getApi().canBinding(bdNum, deviceNum);
		observable.compose(new ThreadSwitchTransformer<BaseBean>())
				.subscribe(new CallbackListObserver<BaseBean>() {
					@Override
					protected void onSucceed(BaseBean bean) {
					if (bean!=null){
						if("0".equals(bean.getCode())){
							LogUtil.d("请求成功可以绑定");
							// 两个数据库连接
							List<LocPersonalInfo> list = CommonDBOperator.queryByKeys(perdao,
									"num", add_num_et.getText().toString());
							if (null != list && list.size() > 0) {
								// 更新
								list.get(0).setName(add_name_et.getText() + "");
								list.get(0).setSex(person_sex);
								list.get(0).setPhone(add_phone_et.getText() + "");
								list.get(0).setContact1(add_phone1_et.getText() + "");
								list.get(0).setContact2(add_phone2_et.getText() + "");
								list.get(0).setBloodtype(add_blood_et.getText() + "");
								list.get(0).setAllergy(add_allergy_et.getText() + "");
								CommonDBOperator.updateItem(perdao, list.get(0));
								list.clear();
								list = null;

								// 更新 personItem
								List<PersonalInfo> list1 = CommonDBOperator.queryByKeys(dao,
										"num", add_num_et.getText().toString());
								list1.get(0).setName(add_name_et.getText() + "");
								list1.get(0).setSex(person_sex);
								list1.get(0).setPhone(add_phone_et.getText() + "");
								list1.get(0).setContact1(add_phone1_et.getText() + "");
								list1.get(0).setContact2(add_phone2_et.getText() + "");
								list1.get(0).setBloodtype(add_blood_et.getText() + "");
								list1.get(0).setAllergy(add_allergy_et.getText() + "");
								CommonDBOperator.updateItem(dao, list1.get(0));
								list1.clear();
								list1 = null;

							} else {
								// 保存
								perInfo = new LocPersonalInfo();
								perInfo.setNum(add_num_et.getText() + "");
								perInfo.setName(add_name_et.getText() + "");
								perInfo.setSex(person_sex);
								perInfo.setPhone(add_phone_et.getText() + "");
								perInfo.setContact1(add_phone1_et.getText() + "");
								perInfo.setContact2(add_phone2_et.getText() + "");
								perInfo.setBloodtype(add_blood_et.getText() + "");
								perInfo.setAllergy(add_allergy_et.getText() + "");
								CommonDBOperator.saveToDB(perdao, perInfo);
								perInfo = null;

								// 插入到 personItem
								PersonalInfo item = new PersonalInfo();
								item.setNum(add_num_et.getText() + "");
								item.setName(add_name_et.getText() + "");
								item.setSex(person_sex);
								item.setPhone(add_phone_et.getText() + "");
								item.setContact1(add_phone1_et.getText() + "");
								item.setContact2(add_phone2_et.getText() + "");
								item.setBloodtype(add_blood_et.getText() + "");
								item.setAllergy(add_allergy_et.getText() + "");
								item.setState(Constants.PERSON_OFFLINE);
								CommonDBOperator.saveToDB(dao, item);
								uploadOffset();
								setBDType(CHANNEL);
							}
							// 通知地图更新界面
							Intent intent = new Intent("com.lhzw.soildersos.change");
							intent.putExtra("has_new", false);
							sendBroadcast(intent);
							finish();
						}else {
							showToast(bean.getMessage()+"");
							LogUtil.d("请求成功不可绑定");
						}
					}else {
						LogUtil.d("返回bean为空");
					}
					}

					@Override
					protected void onFailed() {
						showToast("网络连接失败");
						LogUtil.e("请求失败");
					}
				});

	}

	/**
	 * 重新设置ID值
	 */
	private void uploadOffset() {
		List<PersonalInfo> rxList = CommonDBOperator.queryByOrderKey(dao, "num");
		for(int offset = 0; offset < rxList.size(); offset ++) {
			rxList.get(offset).setOffset(offset);
			CommonDBOperator.updateItem(dao, rxList.get(offset));
		}
	}

	@Override
	public void onChannelClick(int pos) {

		CHANNEL = pos + 1;
		SpUtils.putInt(SPConstants.CHANNEL_NUM, CHANNEL);
		isSelectChannel = true;
		tv_channel.setText(CHANNEL + "");
		dialog.dismiss();
	}

	private class WatchSignalReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			try {
				//
				if (isCurrentApp && isSelectChannel) {
                    ProtocolParser parser = intent.getParcelableExtra("result");
                    byte[] typeKey = parser.getCmdKey();
                    if (typeKey[0] == (byte) 0x11) {// 普通人员不接收
                        return;
                    }
                    String register_num = BaseUtils.traslation(parser.getPersonNum()).substring(0, 10);//注册码

					//根据固话注册码  查此人的信息  显示在页面上
					List<HttpPersonInfo> personInfos = CommonDBOperator.queryByKeys(mHttpPerDao, "deviceNumbers", register_num);
					if(personInfos!=null && personInfos.size()>0){
						//查到了此人的信息
						HttpPersonInfo httpPersonInfo = personInfos.get(0);
						//显示在页面上
						add_name_et.setText(httpPersonInfo.getRealName());//名字
						add_sex_et.check(httpPersonInfo.getGender()==1 ? R.id.btn1 : R.id.btn2);//性别
					}

					icon_watch.setVisibility(View.GONE);
					add_num_et.setText(register_num);

					List<LocPersonalInfo> list = CommonDBOperator.queryByKeys(
                            perdao, "num", register_num);
                    if (list != null && list.size() > 0) {
                        list.clear();
                        list = null;
						canSave=false;//已录入不再进行入库
                        showToast(getString(R.string.person_info_repeat_note));
                        return;
                    }

					canSave=true;
               byte[] sndBytes = BaseUtils.getPerRegisterByteArr(add_num_et.getText().toString());
                    byte[] numByte1 = obtainBDNum();
                    for(int j = 0; j < 5; j ++) {
                        sndBytes[5 + j] = numByte1[j];
                    }
                    sendCMDSearch(sndBytes);
                }
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private void registerBroadcastReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.bspsos");
		filter.addAction("android.intent.action.SOS_RECEIVE");
		receiver = new WatchSignalReceiver();
		registerReceiver(receiver, filter);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		isCurrentApp = false;
		mGlobalToast = null;
		SpUtils.putBoolean(SPConstants.PERSON_ENTER, false);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (receiver != null) {
			unregisterReceiver(receiver);
		}
		receiver = null;
		alertDialog = null;
		dialog = null;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (!BaseUtils.isStringEmpty(add_num_et.getText().toString())) {
				showAlertDialog();
			} else {
				finish();
				setBDType(CHANNEL);
			}
		}
		return false;
	}

	private void showAlertDialog() {
		alertDialog = new ShowAlertDialog(PerItemAddActivity.this);
		alertDialog.showDialog();
		alertDialog.setContent(getString(R.string.dialog_note_back));
		alertDialog.setListener(this);
	}

	@Override
	public void onGlobalLayout() {
		Rect rect = new Rect();
		decorView.getWindowVisibleDisplayFrame(rect);
		int screenHeight = decorView.getRootView().getHeight();
		int heightDifference = screenHeight - rect.bottom;
		LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) loginBinding
				.getLayoutParams();
		layoutParams.setMargins(0, 0, 0, heightDifference);
		loginBinding.requestLayout();

	}

	public void showToast(String text) {

		if (mGlobalToast == null) {
			mGlobalToast = Toast.makeText(PerItemAddActivity.this, text,
					Toast.LENGTH_SHORT);
			mGlobalToast.show();
		} else {
			mGlobalToast.setText(text);
			mGlobalToast.setDuration(Toast.LENGTH_SHORT);
			mGlobalToast.show();
		}
	}

	private boolean sendCMDSearch(final byte[] num) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				loRaManager.searchCard(num);
			}
		}).start();
		return true;
	}

	private byte[] obtainBDNum() {
		if(bdByteArr == null) {
			String numStr = mBDManager.getBDCardNumber();
			if(BaseUtils.isStringEmpty(numStr)) {
				numStr = "000000";
			}
			String channel = null ;
			if(CHANNEL == 10) {
				channel = "a";
			} else {
				channel = CHANNEL + "";
			}
			if(numStr.length() == 6) {
				numStr = "00" + numStr + "0" + channel;
			} else if (numStr.length() == 7) {
				numStr = "0" + numStr + "0" + channel;
			}
			bdByteArr = BaseUtils.getPerRegisterByteArr(numStr);
		}
		return bdByteArr;
	}

	private void setBDType(final int type) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				loRaManager.changeWatchType(type);
			}
		}).start();
	}

}
