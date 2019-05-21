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
import android.widget.TextView;

import com.lhzw.searchlocmap.R;
import com.lhzw.searchlocmap.bean.LocTrackBean;
import com.lhzw.searchlocmap.utils.BaseUtils;
import com.lhzw.searchlocmap.utils.CheckBoxTrack;

public class LocTrackAdapter extends BaseAdapter implements OnCheckedChangeListener{
	private List<LocTrackBean> trackList;
	private Context mContext;
	private ViewHolder holder;
	private List<CheckBoxTrack> stateList = new ArrayList<CheckBoxTrack>();
	private OnSeletedAllListner selectedListener;
	public LocTrackAdapter(Context mContext, List<LocTrackBean> trackList){
		this.mContext = mContext;
		this.trackList = trackList;
		initCheckBoxState();
	}
	
	private void initCheckBoxState() {
		if(trackList != null && trackList.size() > 0) {
			stateList.clear();
		}
		int counter = 0;
		for(LocTrackBean item : trackList) {
			stateList.add(new CheckBoxTrack(counter, item.getTime(), false));
		}
	}
	
	public List<CheckBoxTrack> getCheckBoxList(){
		return stateList;
	}
	
	public void refleshView(){
		initCheckBoxState();
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(trackList == null) return 0;
		return trackList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		// TODO Auto-generated method stub
		if(view == null) {
			view = LayoutInflater.from(mContext).inflate(R.layout.item_track_list, null);
			holder = new ViewHolder();
			holder.checkBox = (CheckBox) view.findViewById(R.id.choose);
			holder.tv_plotName = (TextView) view.findViewById(R.id.tv_name);
			holder.tv_time = (TextView) view.findViewById(R.id.tv_time);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		holder.checkBox.setChecked(stateList.get(position).isCheck());
		holder.checkBox.setTag(position);
		holder.checkBox.setOnCheckedChangeListener(this);
		holder.tv_plotName.setText((position + 1) + "\t" + mContext.getString(R.string.record_local_track) + "\t" +trackList.get(position).getTrackName() + "\t " +
				BaseUtils.getDateStr(trackList.get(position).getTime()));
//		holder.tv_time.setText();
		return view;
	}

	
	private class ViewHolder {
		private CheckBox checkBox;
		private TextView tv_plotName;
		private TextView tv_time;
	}


	@Override
	public void onCheckedChanged(CompoundButton view, boolean isChecked) {
		// TODO Auto-generated method stub
		if(view != null) {
			try {
				int pos = (Integer) view.getTag();
				stateList.get(pos).setCheck(isChecked);
				notifyDataSetChanged();

				checkSelectedAll();

			} catch (IndexOutOfBoundsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void checkSelectedAll() {
		boolean isALl = true;
		for(CheckBoxTrack item : stateList) {
			if(!item.isCheck()) {
				isALl = false;
				break;
			}
		}
		if(selectedListener != null) {
			selectedListener.onSeletedAll(isALl);
		}
	}
	
	public interface OnSeletedAllListner{
		public void onSeletedAll(boolean isChecked);
	}
	
	public void setOnSelectedAllListener(OnSeletedAllListner listener){
		selectedListener = listener;
	}

}
