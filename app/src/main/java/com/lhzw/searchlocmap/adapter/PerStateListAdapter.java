package com.lhzw.searchlocmap.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.lhzw.searchlocmap.R;
import com.lhzw.searchlocmap.bean.PersonalInfo;
import com.lhzw.searchlocmap.constants.Constants;
import com.lhzw.searchlocmap.utils.BaseUtils;

/*
 * 人员列表adapter
 */
public class PerStateListAdapter extends BaseExpandableListAdapter implements
		OnGroupCollapseListener, OnGroupExpandListener {

	Map<String, List<PersonalInfo>> dataset = new HashMap<String, List<PersonalInfo>>();
	String[] parentList = {};// 离线、在线
	private int resource_group;// group_layout
	private int resource_child;// child_layout
	Context mcontext;
	private boolean[] isOpen;// group展开标志 0/1
	private String state;

	// public PerListAdapter() {
	// super();
	// // TODO Auto-generated constructor stub
	// }

	public PerStateListAdapter(Map<String, List<PersonalInfo>> dataset,
			String[] parentList, int resource_group, int resource_child,
			Context mcontext) {
		super();
		this.dataset = dataset;
		this.parentList = parentList;
		this.resource_group = resource_group;
		this.resource_child = resource_child;
		this.mcontext = mcontext;
		isOpen = new boolean[parentList.length];
	}

	// 某状态某人
	@Override
	public Object getChild(int groupPosition, int childPosition) {

		return dataset.get(parentList[groupPosition]).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		TextView child_name;
		TextView child_address;
		ImageView child_image;

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) mcontext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(resource_child, null);
		}
		child_name = (TextView) convertView.findViewById(R.id.name_child);
		child_image = (ImageView) convertView.findViewById(R.id.sos);
		child_address = (TextView) convertView.findViewById(R.id.address_child);
		if (dataset != null) {
			child_name.setText(dataset.get(parentList[groupPosition])
					.get(childPosition).getName()
					+ "("
					+ dataset.get(parentList[groupPosition]).get(childPosition)
							.getNum() + ")");
			state = dataset.get(parentList[groupPosition]).get(childPosition)
					.getState();
			switch (Integer.parseInt(state)) {
			case 0:// 离线
				child_image.setBackgroundResource(R.drawable.icon_unlocation);
				break;
			case 1:// 普通
				child_image.setBackgroundResource(R.drawable.icon_qita2);
				break;
			case 2:// sos
				child_image.setBackgroundResource(R.drawable.icon_sos2);
				break;
			case -1:// 待定
				String state = dataset.get(parentList[groupPosition])
						.get(childPosition).getState1();
				if (Constants.PERSON_SOS.equals(state)) {
					child_image.setBackgroundResource(R.drawable.icon_sos2);
				} else if (Constants.PERSON_COMMON.equals(state)) {
					child_image.setBackgroundResource(R.drawable.icon_qita2);
				}
				break;
			}
			// 普通
			if (!BaseUtils.isStringEmpty(dataset.get(parentList[groupPosition])
					.get(childPosition).getLatitude())
					&& !BaseUtils.isStringEmpty(dataset
							.get(parentList[groupPosition]).get(childPosition)
							.getLongitude())) {
				// 经纬度间应用逗号隔开
				// child_address.setText("("+dataset.get(parentList[groupPosition]).get(childPosition).getLatitude()+dataset.get(parentList[groupPosition]).get(childPosition).getLongitude()+")");
				String latitude = BaseUtils.translateLonOrLat(dataset
						.get(parentList[groupPosition]).get(childPosition)
						.getLatitude());
				String longitude = BaseUtils.translateLonOrLat(dataset
						.get(parentList[groupPosition]).get(childPosition)
						.getLongitude());
				child_address.setText("【" + latitude + "," + longitude + "】");
			} else {// 待定
				child_address.setText("【离线】");
			}
		}
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		List<PersonalInfo> list = dataset.get(parentList[groupPosition]);
		if (list != null && list.size() > 0) {
			return list.size();
		}
		return 0;
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return dataset.get(parentList[groupPosition]);
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return dataset.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		TextView group_text;
		TextView group_sum;
		ImageView group_image;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) mcontext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(resource_group, null);
		}
		group_text = (TextView) convertView.findViewById(R.id.group_name);
		group_text.setText(parentList[groupPosition]);
		group_image = (ImageView) convertView.findViewById(R.id.list_image);
		group_sum = (TextView) convertView.findViewById(R.id.group_sum);
		String sumText = calsum(groupPosition);
		group_sum.setText(sumText);

		if (isOpen[groupPosition] == true) {
			group_image.setImageDrawable(mcontext.getResources().getDrawable(
					R.drawable.icon_open_32));
		} else {
			group_image.setImageDrawable(mcontext.getResources().getDrawable(
					R.drawable.icon_close_32));
		}

		return convertView;
	}

	// 计算 当前组内人数/总人数
	private String calsum(int groupPosition) {
		String resultstr = " ";
		List<PersonalInfo> list = null;
		if (dataset != null) {
			Set<String> dataset_set = dataset.keySet();
			int sum = 0;
			for (String groupname : dataset_set) {
				list = dataset.get(groupname);
				if (list != null && list.size() > 0) {
					int size = list.size();
					sum += size;
				}
			}
			list = dataset.get(parentList[groupPosition]);
			if (list != null && list.size() > 0) {
				int current_people = list.size();
				resultstr = current_people + "/" + sum;
			}
		}
		// return null; //原先未传值
		return resultstr;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}

	// 写错位置，下方已有要重写的方法
	// 且逻辑写反,展开时应为true,收回为false
	// public void omGroupCollapse(int groupPosition) {
	// isOpen[groupPosition]=false;
	//
	// }
	// public void omGroupExpand(int groupPosition) {
	// isOpen[groupPosition]=true;
	//
	// }

	// 展开和回收监听

	@Override
	public void onGroupExpand(int groupPosition) {
		// TODO Auto-generated method stub
		isOpen[groupPosition] = true;
	}

	@Override
	public void onGroupCollapse(int groupPosition) {
		// TODO Auto-generated method stub
		isOpen[groupPosition] = false;
	}

	public void setMap(Map<String, List<PersonalInfo>> dataset) {
		this.dataset = dataset;
		notifyDataSetChanged();
	}

}
