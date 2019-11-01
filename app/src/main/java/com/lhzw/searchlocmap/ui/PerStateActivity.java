package com.lhzw.searchlocmap.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.SQLException;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.lhzw.searchlocmap.R;
import com.lhzw.searchlocmap.adapter.PerStateListAdapter;
import com.lhzw.searchlocmap.bean.PersonalInfo;
import com.lhzw.searchlocmap.constants.Constants;
import com.lhzw.searchlocmap.db.dao.CommonDBOperator;
import com.lhzw.searchlocmap.db.dao.DatabaseHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PerStateActivity extends Activity implements OnClickListener,
		OnChildClickListener, TextWatcher {
	private ExpandableListView exlistview;
	private PerStateListAdapter adapter;
	private Map<String, List<PersonalInfo>> dataset = new HashMap<String, List<PersonalInfo>>();
	private String[] parentList = {};// 离线、在线
	private List<PersonalInfo> onlineList = new ArrayList<PersonalInfo>();
	private DatabaseHelper<?> helper;
	private Dao<PersonalInfo, Integer> persondao;
	private List<PersonalInfo> offlineList;
	private List<PersonalInfo> sosList;
	private List<PersonalInfo> normalList;
	private EditText per_keyWord;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.per_status_list);
		registerBroadcastReceiver();
		// 数据库初始化
		helper = DatabaseHelper.getHelper(PerStateActivity.this);
		persondao = helper.getPersonalInfoDao();
		// 人员状态列表
		exlistview = (ExpandableListView) findViewById(R.id.per_status);
		per_keyWord = (EditText) findViewById(R.id.per_keyWord);
		per_keyWord.addTextChangedListener(this);
		Button per_statu_back = (Button) findViewById(R.id.per_statu_back);
		per_statu_back.setOnClickListener(this);
		initExListViewDataSet();
		adapter = new PerStateListAdapter(dataset, parentList,
				R.layout.pre_status_group_list, R.layout.pre_status_child_list,
				PerStateActivity.this);

		// 列表绑定数据
		exlistview.setAdapter(adapter);// 少了这一句！
		exlistview.setOnGroupCollapseListener(adapter);
		exlistview.setOnGroupExpandListener(adapter);
		exlistview.setOnChildClickListener(this);

	}

	private void initExListViewDataSet() {
		if (offlineList != null && offlineList.size() > 0) {
			offlineList.clear();
		}
		if (onlineList != null && onlineList.size() > 0) {
			onlineList.clear();
		}
		try {
			offlineList = CommonDBOperator.queryByKeys(persondao, "state",
					Constants.PERSON_OFFLINE);// 离线
			sosList = CommonDBOperator.queryByKeys(persondao, "state",
					Constants.PERSON_SOS); // sos

			if (sosList != null && sosList.size() > 0) {
				onlineList.addAll(sosList);
				sosList.clear();
				sosList = null;
			}

			// 初始化待定区域
			Map<String, String> map_sos = new HashMap<String, String>();
			map_sos.put("state", Constants.PERSON_UNDETERMINED);
			map_sos.put("state1", Constants.PERSON_SOS);
			List<PersonalInfo> undermined_sos_list = CommonDBOperator
					.queryByMultiKeys(persondao, map_sos);
			if (undermined_sos_list != null && undermined_sos_list.size() > 0) {
				onlineList.addAll(undermined_sos_list);
				undermined_sos_list.clear();
				undermined_sos_list = null;
			}

			normalList = CommonDBOperator.queryByKeys(persondao, "state",
					Constants.PERSON_COMMON); // 普通
			if (normalList != null && normalList.size() > 0) {
				onlineList.addAll(normalList);
				normalList.clear();
				normalList = null;
			}
			Map<String, String> map_common = new HashMap<String, String>();
			map_common.put("state", Constants.PERSON_UNDETERMINED);
			map_common.put("state1", Constants.PERSON_COMMON);
			List<PersonalInfo> undermined_common_list = CommonDBOperator
					.queryByMultiKeys(persondao, map_common);
			if (undermined_common_list != null
					&& undermined_common_list.size() > 0) {
				onlineList.addAll(undermined_common_list);
				undermined_common_list.clear();
				undermined_common_list = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		parentList = getResources().getStringArray(R.array.per_status);
		dataset.put(parentList[0], offlineList);
		dataset.put(parentList[1], onlineList);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.per_statu_back:
			this.finish();
			break;

		default:
			break;
		}
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {

		PersonalInfo perchild = (PersonalInfo) adapter.getChild(groupPosition,
				childPosition);
		if ((Constants.PERSON_COMMON).equals(perchild.getState())
				|| (Constants.PERSON_SOS).equals(perchild.getState())) {

			Intent intent = new Intent();
			intent.putExtra("markerId", perchild.getMarkerId());
			intent.putExtra("num", perchild.getNum());
			setResult(4, intent);
			finish();
		} else if (Constants.PERSON_UNDETERMINED.equals(perchild.getState())) {
			finish();
			Toast.makeText(PerStateActivity.this, "待定区域人员", Toast.LENGTH_SHORT)
					.show();
		} else {
			Intent intent = new Intent();
			intent.putExtra("markerId", perchild.getMarkerId());
			intent.putExtra("num",perchild.getNum());
			setResult(5, intent);
			finish();
		}

		return false;
	}

	// 广播接收后重新查数据库
	protected void signalChange() {
		// TODO Auto-generated method stub
		initExListViewDataSet();
		adapter.setMap(dataset);
	}

	// 释放空间
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(receiver != null) {
			unregisterReceiver(receiver);
		}
		super.onDestroy();
		adapter = null;
		dataset.clear();
		dataset = null;
		helper = null;
		onlineList.clear();
		onlineList = null;
		persondao = null;
	}

	private void registerBroadcastReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.lhzw.soildersos.change");
		registerReceiver(receiver, filter);
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals("com.lhzw.soildersos.change")){
				initExListViewDataSet();
				adapter.notifyDataSetChanged();
			}
		}
	};

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

	@Override
	public void afterTextChanged(Editable s) {
		Log.e("Tag", "search content :" + s.toString());
		String content = s.toString();
		if (offlineList != null && offlineList.size() > 0) {
			offlineList.clear();
		}
		if (onlineList != null && onlineList.size() > 0) {
			onlineList.clear();
		}
		if(TextUtils.isEmpty(content)){
			try {
				List<PersonalInfo> tmpOfflineList = CommonDBOperator.queryByKeys(persondao, "state",
						Constants.PERSON_OFFLINE);// 离线
				if(tmpOfflineList != null) {
					offlineList.addAll(tmpOfflineList);
					tmpOfflineList.clear();
				}
				sosList = CommonDBOperator.queryByKeys(persondao, "state",
						Constants.PERSON_SOS); // sos

				if (sosList != null && sosList.size() > 0) {
					onlineList.addAll(sosList);
					sosList.clear();
					sosList = null;
				}

				// 初始化待定区域
				Map<String, String> map_sos = new HashMap<String, String>();
				map_sos.put("state", Constants.PERSON_UNDETERMINED);
				map_sos.put("state1", Constants.PERSON_SOS);
				List<PersonalInfo> undermined_sos_list = CommonDBOperator
						.queryByMultiKeys(persondao, map_sos);
				if (undermined_sos_list != null && undermined_sos_list.size() > 0) {
					onlineList.addAll(undermined_sos_list);
					undermined_sos_list.clear();
				}

				normalList = CommonDBOperator.queryByKeys(persondao, "state",
						Constants.PERSON_COMMON); // 普通
				if (normalList != null && normalList.size() > 0) {
					onlineList.addAll(normalList);
					normalList.clear();
					normalList = null;
				}
				Map<String, String> map_common = new HashMap<String, String>();
				map_common.put("state", Constants.PERSON_UNDETERMINED);
				map_common.put("state1", Constants.PERSON_COMMON);
				List<PersonalInfo> undermined_common_list = CommonDBOperator
						.queryByMultiKeys(persondao, map_common);
				if (undermined_common_list != null
						&& undermined_common_list.size() > 0) {
					onlineList.addAll(undermined_common_list);
					undermined_common_list.clear();
					undermined_common_list = null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else{
			Map<String, String> map = new HashMap<>();
			map.put("name", content);
			map.put("num", content);
			Map<String, String> map1 = new HashMap<>();
			map1.put("state", Constants.PERSON_OFFLINE + "");
			List<PersonalInfo> tmpOfflineList = CommonDBOperator.queryByMultiKeysFuzzy("PersonalInfo", map,
					map1);// 离线
			if(tmpOfflineList != null) {
				offlineList.addAll(tmpOfflineList);
				tmpOfflineList.clear();
			}
			map1.put("state", Constants.PERSON_SOS + "");
			sosList = CommonDBOperator.queryByMultiKeysFuzzy("PersonalInfo", map,
					map1); // sos

			if (sosList != null && sosList.size() > 0) {
				onlineList.addAll(sosList);
				sosList.clear();
				sosList = null;
			}

			map1.put("state", Constants.PERSON_UNDETERMINED);
			map1.put("state1", Constants.PERSON_SOS);
			List<PersonalInfo> undermined_sos_list = CommonDBOperator.queryByMultiKeysFuzzy("PersonalInfo", map,
					map1); // sos
			if (undermined_sos_list != null && undermined_sos_list.size() > 0) {
				onlineList.addAll(undermined_sos_list);
				undermined_sos_list.clear();
			}
			map1.remove("state1");
			map1.put("state", Constants.PERSON_COMMON);
			normalList = CommonDBOperator.queryByMultiKeysFuzzy("PersonalInfo", map,
					map1); // sos
			if (normalList != null && normalList.size() > 0) {
				onlineList.addAll(normalList);
				normalList.clear();
				normalList = null;
			}
			map1.put("state", Constants.PERSON_UNDETERMINED);
			map1.put("state1", Constants.PERSON_COMMON);
			List<PersonalInfo> undermined_common_list = CommonDBOperator.queryByMultiKeysFuzzy("PersonalInfo", map,
					map1);
			if (undermined_common_list != null
					&& undermined_common_list.size() > 0) {
				onlineList.addAll(undermined_common_list);
				undermined_common_list.clear();
			}

			map.clear();
			map1.clear();

		}
		adapter.notifyDataSetChanged();
	}
}
