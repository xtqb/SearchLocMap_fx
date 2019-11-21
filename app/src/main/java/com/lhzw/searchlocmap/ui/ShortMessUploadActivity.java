package com.lhzw.searchlocmap.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BDManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.lhzw.searchlocmap.R;
import com.lhzw.searchlocmap.bdsignal.BDSignal;
import com.lhzw.searchlocmap.bean.HttpPersonInfo;
import com.lhzw.searchlocmap.bean.MessageInfoIBean;
import com.lhzw.searchlocmap.constants.Constants;
import com.lhzw.searchlocmap.constants.SPConstants;
import com.lhzw.searchlocmap.db.dao.CommonDBOperator;
import com.lhzw.searchlocmap.db.dao.DatabaseHelper;
import com.lhzw.searchlocmap.event.EventBusBean;
import com.lhzw.searchlocmap.utils.BaseUtils;
import com.lhzw.searchlocmap.utils.ComUtils;
import com.lhzw.searchlocmap.utils.LogUtil;
import com.lhzw.searchlocmap.utils.LogWrite;
import com.lhzw.searchlocmap.utils.SpUtils;
import com.lhzw.uploadmms.UploadInfoBean;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShortMessUploadActivity extends Activity implements
		OnClickListener {
	private ImageView im_mess_upload_back;
	private ListView mess_listview;
	private EditText ed_content;
	private TextView tv_send_mes;
	private DatabaseHelper<?> helper;
	private Dao<MessageInfoIBean, Integer> mesDao;
	private List<MessageInfoIBean> mesgList;
	private Toast mGlobalToast;
	public static final int MESSAGE_RECEIVE = 1; // 接收消息
	private static final int MESSAGE_SEND = 0; // 发送消息
	private String body;
	private ImageView im_bd_signal;
	public static final int TX_MMS = 3;
	private String dipper_num;
	private TextView tv_mess_upload;
	private ComUtils mComUtils;
	private boolean isCurrentActivity;
	private int ID;
	private String head;
	private String realName;
	private String bdNum;
	private boolean isDestroy;
	private Dao<HttpPersonInfo, Integer> mHttpPerDao;


	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_short_message);
		initView();
		initData();
		setListener();
	}

	private void initView() {
		im_mess_upload_back = (ImageView) findViewById(R.id.im_mess_upload_back);
		mess_listview = (ListView) findViewById(R.id.mess_listview);
		ed_content = (EditText) findViewById(R.id.ed_content);
		tv_send_mes = (TextView) findViewById(R.id.tv_send_mes);
		im_bd_signal = (ImageView) findViewById(R.id.im_bd_signal);
		tv_mess_upload = (TextView) findViewById(R.id.tv_mess_upload);
	}
	@SuppressLint("WrongConstant")
	private void initData() {
		initTitle();
		BDManager mBDManager = (BDManager) this.getSystemService(Context.BD_SERVICE);
		mBDManager.systemCheck(2+"");
		helper = DatabaseHelper.getHelper(ShortMessUploadActivity.this);
		mesDao = helper.getMesgInfoDao();
		mHttpPerDao = helper.getHttpPerDao();
		mComUtils = ComUtils.getInstance();

		isCurrentActivity = true;
		registerBroadcastReceiver();
		im_bd_signal.setImageLevel(0);
		isDestroy = false;
		mess_listview.setAdapter(adapter);
		new Thread(new Runnable() {
			@Override
			public void run() {
				updateState();
                mesgList = CommonDBOperator.queryByKeys(mesDao,"ID", ID +"" );
                runOnUiThread(new Runnable() {
					@Override
					public void run() {
						adapter.notifyDataSetChanged();
					}
				});
			}
		}).start();
	}

	private void initTitle() {
		bdNum = getIntent().getStringExtra("bdNum");
		String org = getIntent().getStringExtra("org");
		ID = getIntent().getIntExtra("msg_Id", -1);
		realName = getIntent().getStringExtra("realName");
		head = ID + "";
		if(head.length() == 1) {
			head = "000" + head;
		} else if(head.length() == 2) {
			head = "00" + head;
		} else if(head.length() == 3) {
			head = "0" + head;
		}
		String title = realName;
		title += "(" + org + ")";
		tv_mess_upload.setText(title);
	}

	private void setListener() {
		// TODO Auto-generated method stub
		im_mess_upload_back.setOnClickListener(this);
		tv_send_mes.setOnClickListener(this);
		ed_content.addTextChangedListener(watcher);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.im_mess_upload_back:
			setResult(7);
			this.finish();
			break;
		case R.id.tv_send_mes:
//			if(BDSignal.value <= 2) {
//				showToast(getString(R.string.upload_signal_strength_low_note));
//				return;
//			}
			body = ed_content.getText().toString();
			if (BaseUtils.isStringEmpty(body.trim())) {
				showToast(getString(R.string.short_mess_send_fail_note));
				return;
			}
			List<HttpPersonInfo> list = CommonDBOperator.queryByKeys(mHttpPerDao, "loginName", SpUtils.getString(SPConstants.LOGIN_NAME, ""));
			if (list!=null && list.size()>0 && list.get(0).getId()==ID){
				showToast("不能给自己发消息!!!");
				return;
			}
			sendMesgToServer();
			break;
		}
	}

	private void sendMesgToServer() {
		// TODO Auto-generated method stub
		String latLon = SpUtils.getFloat(SPConstants.LAT_ADDR, Constants.CENTRE_LAT) + ","
				+ SpUtils.getFloat(SPConstants.LON_ADDR, Constants.CENTRE_LON);
		MessageInfoIBean item = new MessageInfoIBean(dipper_num, System.currentTimeMillis(), body, MESSAGE_SEND, Constants.MESSAGE_READ, ID);//ID为表中接收人ID
		CommonDBOperator.saveToDB(mesDao, item);
		body  = head + body;
		UploadInfoBean item1 = new UploadInfoBean(Constants.TX_JZH, TX_MMS,
				System.currentTimeMillis(), body, System.currentTimeMillis()+"", 1 + "", latLon, SpUtils.getLong(
						SPConstants.LOC_TIME, System.currentTimeMillis()), 1, SpUtils.getString(Constants.UPLOAD_JZH_NUM, Constants.BD_NUM_DEF), 2, -1, null);
		mComUtils.uploadBena(item1);
		item1 = null;
		if(SpUtils.getInt(SPConstants.SP_BD_MODE, Constants.UOLOAD_STATE_0) == Constants.UOLOAD_STATE_1) {
			UploadInfoBean item2 = new UploadInfoBean(Constants.TX_QZH, TX_MMS,
					System.currentTimeMillis(), body, System.currentTimeMillis()+"", 1 + "", latLon, SpUtils.getLong(
					SPConstants.LOC_TIME, System.currentTimeMillis()), 1, SpUtils.getString(Constants.UPLOAD_QZH_NUM, Constants.BD_NUM_DEF), 2, -1, null);
			mComUtils.uploadBena(item2);
			item2 = null;
		}
//		if(bdNum != null) {
//			UploadInfoBean item3 = new UploadInfoBean(Constants.TX_BJ, TX_MMS,
//					System.currentTimeMillis(), body, System.currentTimeMillis()+"", 1 + "", latLon, SpUtils.getLong(
//					SPConstants.LOC_TIME, System.currentTimeMillis()), 1, bdNum, 2, -1, null);//接收者的北斗号
//			mComUtils.uploadBena(item3);
//			item3 = null;
//		}

		//showToast(getString(R.string.mms_send_data));
		try {
			LogWrite writer = LogWrite.open();
			String log = LogWrite.df.format(System.currentTimeMillis()) + " \t data_type = " + Constants.TX_MMS + "latLons : " + body;
			writer.writeLog(log);
		} catch (Exception e) {
			e.printStackTrace();
		}
		body = null;
		ed_content.setText("");
		updataList();
	}

	private synchronized void updataList() {
		updateState();
		if (mesgList != null && mesgList.size() > 0) {
			mesgList.clear();
		}
		mesgList = CommonDBOperator.queryByKeys(mesDao,"ID", ID + "");
		adapter.notifyDataSetChanged();

	}

	private void updateState() {
		/**
		 * 将此人的未读消息全部改为已读状态
		 */
		Map<String, String> map = new HashMap<>();
		map.put("type", ShortMessUploadActivity.MESSAGE_RECEIVE + "");
		map.put("state", Constants.MESSAGE_UNREAD + "");
		map.put("ID",ID + "");
		List<MessageInfoIBean> msgList = CommonDBOperator.queryByMultiKeys(mesDao,map);
		for(MessageInfoIBean bean : msgList) {
			bean.setState(Constants.MESSAGE_READ);
			CommonDBOperator.updateItem(mesDao, bean);
		}
		map.clear();
		if(msgList != null && msgList.size() > 0) {
			msgList.clear();
		}

		// 刷新安全监管页未读数据
        EventBusBean eventBusBean = new EventBusBean(); // 刷新安全监管页未读数据
		eventBusBean.setCode(Constants.EVENT_CODE_REFRESH_MSG_NUM);
		EventBus.getDefault().post(eventBusBean);

		//通知最近联系人列表刷新
        EventBusBean eventBusBean1 = new EventBusBean(); //通知最近联系人列表刷新
		eventBusBean1.setCode(Constants.EVENT_CODE_REFRESH_MSG_LIST);
		eventBusBean1.setStringTag("refresh");
		EventBus.getDefault().post(eventBusBean1);
		LogUtil.e("发送刷新最近联系人列表的event");

	}

	private BaseAdapter adapter = new BaseAdapter() {
		private ViewHolder sendHolder;
		private ViewHolder receiveHolder;

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			int type = getItemViewType(position);
			if (convertView == null) {
				switch (type) {
				case MESSAGE_SEND:
					sendHolder = new ViewHolder();
					convertView = LayoutInflater.from(
							ShortMessUploadActivity.this).inflate(
							R.layout.item_short_mes_send, null);
					sendHolder.tv_time = (TextView) convertView
							.findViewById(R.id.tv_receive_time);
					sendHolder.tv_content = (TextView) convertView
							.findViewById(R.id.tv_receive_content);
					convertView.setTag(sendHolder);
					break;
				case MESSAGE_RECEIVE:
					receiveHolder = new ViewHolder();
					convertView = LayoutInflater.from(
							ShortMessUploadActivity.this).inflate(
							R.layout.item_short_mes_receive, null);
					receiveHolder.tv_time = (TextView) convertView
							.findViewById(R.id.tv_send_time);
					receiveHolder.tv_content = (TextView) convertView
							.findViewById(R.id.tv_send_content);
					convertView.setTag(receiveHolder);
					break;
				}
			} else {
				switch (type) {
				case MESSAGE_SEND:
					sendHolder = (ViewHolder) convertView.getTag();
					break;
				case MESSAGE_RECEIVE:
					receiveHolder = (ViewHolder) convertView.getTag();
					break;
				default:
					break;
				}
			}
			switch (type) {
			case MESSAGE_SEND:
				sendHolder.tv_time.setText(BaseUtils.getDateStr(mesgList.get(
						position).getTime()));
				sendHolder.tv_content.setText(mesgList.get(position).getBody());
				break;
			case MESSAGE_RECEIVE:
				receiveHolder.tv_time.setText(BaseUtils.getDateStr(mesgList
						.get(position).getTime()));
				receiveHolder.tv_content.setText(mesgList.get(position)
						.getBody());
				break;

			default:
				break;
			}

			return convertView;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (mesgList == null || mesgList.size() == 0)
				return 0;
			return mesgList.size();
		}

		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return 2;
		}

		@Override
		public int getItemViewType(int position) {
			// TODO Auto-generated method stub
			if (mesgList.get(position).getType() == MESSAGE_SEND) {
				return MESSAGE_SEND;
			} else {
				return MESSAGE_RECEIVE;
			}
		}

	};

	private class ViewHolder {
		private TextView tv_time;
		private TextView tv_content;
	}

	private TextWatcher watcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			if (s.toString().length() == 50) {
				showToast(getString(R.string.send_mess_length_note));
			}
		}
	};

	public void showToast(String text) {

		if (mGlobalToast == null) {
			mGlobalToast = Toast.makeText(ShortMessUploadActivity.this, text,
					Toast.LENGTH_SHORT);
			mGlobalToast.show();
		} else {
			mGlobalToast.setText(text);
			mGlobalToast.setDuration(Toast.LENGTH_SHORT);
			mGlobalToast.show();
		}
	}

	private void registerBroadcastReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.BD_Mms_ACTION);
		filter.addAction(Constants.BD_SIG_ACTION);
		registerReceiver(receiver, filter);
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(isDestroy) return;
			if (intent.getAction().equals(Constants.BD_Mms_ACTION)) {
				if(isCurrentActivity) {
					Map<String, String> map = new HashMap<>();
					map.put("type", MESSAGE_RECEIVE + "");
					map.put("state", Constants.MESSAGE_UNREAD + "");
					map.put("ID",ID + "");
					List<MessageInfoIBean> list = CommonDBOperator.queryByMultiKeys(mesDao, map);
					if(list != null && list.size() > 0) {
						list.get(0).setState(Constants.MESSAGE_READ);
						CommonDBOperator.updateItem(mesDao, list.get(0));
						list.clear();
					}
					map.clear();
				}


				if(intent.getIntExtra("ID", -1) ==  ID) {
					updataList();
					showToast("接收成功");
				}else {
				//	showToast("接收短消息成功,ID不匹配");
					LogUtil.e("收到短消息,但是页面未没刷新,原因为:ID=="+ID+"<====>收到的消息唯一标识ID=="+intent.getIntExtra("ID", -1));
				}
			}else if (intent.getAction().equals(Constants.BD_SIG_ACTION)) {
				switch (BDSignal.value){
					case 4:
						im_bd_signal.setImageLevel(4);
						break;
					case 3:
						im_bd_signal.setImageLevel(3);
						break;
					case 2:
						im_bd_signal.setImageLevel(2);
						break;
					case 0:
					case 1:
						im_bd_signal.setImageLevel(0);
						break;
				}

			}
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			setResult(7);
			this.finish();


		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onStop() {
		super.onStop();
		isDestroy = true;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		isCurrentActivity = false;
		mGlobalToast = null;
		if (receiver != null) {
			unregisterReceiver(receiver);
		}
		if (mesgList != null && mesgList.size() > 0) {
			mesgList.clear();
			mesgList = null;
		}
	}
}
