package com.lhzw.searchlocmap.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.lhzw.searchlocmap.R;
import com.lhzw.searchlocmap.bean.PersonalInfo;
import com.lhzw.searchlocmap.utils.CheckBoxState;

public class SelectReportSosAdapter extends BaseAdapter {
	private List<CheckBoxState> list = new ArrayList<CheckBoxState>();
	List<PersonalInfo> listPer;
	Context context;
	private int resourceId;

	public SelectReportSosAdapter(List<PersonalInfo> listPer, Context context,
			int textViewResourceId) {
		super();
		this.listPer = listPer;
		this.context = context;
		resourceId = textViewResourceId;
		initList();

	}

	public void initList() {
		list.clear();
		if (null == listPer || listPer.size() == 0)
			return;
		for (int i = 0; i < listPer.size(); i++) {
			CheckBoxState item = new CheckBoxState(false, i, listPer.get(i)
					.getNum());
			list.add(item);
		}
	}

	public void setCheckStateList(boolean isCheck) {
		for (CheckBoxState item : list) {
			item.setCheck(isCheck);
		}
	}

	public List<PersonalInfo> getListPer() {
		return listPer;
	}

	public void setListPer(List<PersonalInfo> listPer) {
		this.listPer = listPer;
	}

	public void updateItem(List<PersonalInfo> updateData) {
		listPer = updateData;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listPer.size();
	}

	public void setList(ArrayList<PersonalInfo> list) {
		this.listPer = list;
	}

	@Override
	public PersonalInfo getItem(int position) {
		// TODO Auto-generated method stub
		return listPer.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		PersonalInfo personInfo = getItem(position);
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context)
					.inflate(resourceId, null);
			holder = new ViewHolder();
			holder.num = (TextView) convertView
					.findViewById(R.id.report_num_tv);
			holder.name = (TextView) convertView
					.findViewById(R.id.report_name_tv);
			holder.spread = (ImageView) convertView.findViewById(R.id.spread);
			holder.choose = (CheckBox) convertView
					.findViewById(R.id.report_choose);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.choose.setTag(position);
		holder.choose.setOnCheckedChangeListener(listener);
		holder.name.setText(personInfo.getName());
		holder.num.setText("编码：" + personInfo.getNum());
		return convertView;
	}

	static class ViewHolder {
		TextView num;
		TextView name;
		ImageView spread;
		CheckBox choose;
	}

	private OnCheckedChangeListener listener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			// TODO Auto-generated method stub
			if (buttonView.getTag() != null) {
				int pos = (Integer) buttonView.getTag();
				list.get(pos).setCheck(isChecked);
				list.get(pos).setID(pos);
			}
		}
	};

	public interface onCheckBoxChangeListener {
		public void itemSelected(int pos);
	}

	public List<CheckBoxState> getCheckStateList() {
		return list;
	}
}
