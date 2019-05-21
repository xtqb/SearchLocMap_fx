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
import com.lhzw.searchlocmap.bean.LocPersonalInfo;
import com.lhzw.searchlocmap.constants.SPConstants;
import com.lhzw.searchlocmap.utils.CheckBoxState;
import com.lhzw.searchlocmap.utils.SpUtils;

public class PerManageAdapter extends BaseAdapter {
	private int resourceId;
	private List<CheckBoxState> list = new ArrayList<CheckBoxState>();
	private TextView tv_num;
	private List<LocPersonalInfo> listPer;
	private Context context;
	private onCheckBoxChangeListener checkListener;

	public PerManageAdapter() {
		super();
		// TODO Auto-generated constructor stub
	}

	public PerManageAdapter(List<LocPersonalInfo> listPer, Context context,
			int textViewResourceId, TextView tv_num) {
		super();
		this.listPer = listPer;
		this.context = context;
		resourceId = textViewResourceId;
		this.tv_num = tv_num;
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

	public List<LocPersonalInfo> getListPer() {
		return listPer;
	}

	public void setListPer(List<LocPersonalInfo> listPer) {
		this.listPer = listPer;
	}

	public void updateItem(List<LocPersonalInfo> updateData) {
		listPer = updateData;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (listPer != null) {
			tv_num.setText(context.getResources().getString(R.string.total)
					.replace("@", listPer.size() + ""));
			return listPer.size();
		} else {
			tv_num.setText(context.getResources().getString(R.string.total)
					.replace("@", 0 + ""));
			return 0;
		}
	}

	@Override
	public LocPersonalInfo getItem(int position) {
		// TODO Auto-generated method stub
		return listPer.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setList(ArrayList<LocPersonalInfo> list) {
		this.listPer = list;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LocPersonalInfo personInfo = getItem(position);
		ViewHolder holder;
		View view;
		if (convertView == null) {
			view = LayoutInflater.from(context).inflate(resourceId, null);
			holder = new ViewHolder();
			holder.address = (TextView) view.findViewById(R.id.address_tv);
			holder.name = (TextView) view.findViewById(R.id.name_tv);
			holder.spread = (ImageView) view.findViewById(R.id.spread);
			holder.choose = (CheckBox) view.findViewById(R.id.choose);
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}
		holder.choose.setOnCheckedChangeListener(listener);
		if (SpUtils.getBoolean(SPConstants.CHECKBOX_ISSHOW, false)) {
			holder.choose.setVisibility(View.VISIBLE);
		} else {
			holder.choose.setVisibility(View.GONE);
		}
		holder.choose.setTag(position);
		holder.choose.setChecked(list.get(position).isCheck());
		holder.name.setText(personInfo.getName() + "(" + personInfo.getNum()
				+ ")");

		return view;
	}

	static class ViewHolder {
		TextView address;
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
				checkListener.itemSelected(pos);
			}
		}
	};

	public interface onCheckBoxChangeListener {
		public void itemSelected(int pos);
	}

	public List<CheckBoxState> getCheckStateList() {
		return list;
	}

	public void setItemcheckListener(onCheckBoxChangeListener checkListener) {
		this.checkListener = checkListener;
	}
}
