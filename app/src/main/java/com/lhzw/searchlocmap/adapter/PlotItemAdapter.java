package com.lhzw.searchlocmap.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import com.lhzw.searchlocmap.bean.PlotItemInfo;
import com.lhzw.searchlocmap.constants.Constants;
import com.lhzw.searchlocmap.utils.BaseUtils;
import com.lhzw.searchlocmap.utils.PlotCheckBox;
import com.lhzw.searchlocmap.R;

public class PlotItemAdapter extends BaseAdapter implements OnCheckedChangeListener, View.OnTouchListener {
	private List<PlotItemInfo> plotList;
	private int layout;
	private Context mContext;
	private List<PlotCheckBox> checkList;
	private ViewHolder holder;
	private boolean isChange;
	private OnItemSelectedPlotListener listener;

	public PlotItemAdapter(Context mContext, List<PlotItemInfo> plotList, int layout) {
		this.mContext = mContext;
		this.plotList = plotList;
		this.layout = layout;
		checkList = new ArrayList<>();
		isChange = false;
		initCheckList();
	}
	
	public void initCheckList(){
		if(checkList != null) {
			checkList.clear();
		}
		
		if(plotList == null || plotList.size() ==0){
			return;
		}
		for(int pos = 0; pos < plotList.size(); pos ++) {
			PlotCheckBox checkbox = new PlotCheckBox(pos, plotList.get(pos).getTime(), false);
			checkList.add(checkbox);
		}
	}
	
	public List<PlotCheckBox> getCheckBoxList(){
		return checkList;
	}
	
	public synchronized void setPlotList(List<PlotItemInfo> plotList){
		if(this.plotList != null && this.plotList.size() > 0) {
			this.plotList.clear();
		}
		this.plotList = plotList;
		initCheckList();
	}

	public void selectAll(boolean state) {
		if(checkList != null && checkList.size() > 0) {
			for(int pos = 0; pos < checkList.size(); pos ++) {
				checkList.get(pos).setCheck(state);
			}
			notifyDataSetChanged();
		}
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(plotList == null){
			return 0;
		}
		return plotList.size();
	}

	@Override
	public Object getItem(int itemId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View view, ViewGroup group) {
		// TODO Auto-generated method stub
		if(view == null) {
			view = LayoutInflater.from(mContext).inflate(layout, null);
			holder = new ViewHolder();
			holder.checkBox = (CheckBox) view.findViewById(R.id.choose);
			holder.tv_plotName = (TextView) view.findViewById(R.id.tv_name);
			holder.tv_time = (TextView) view.findViewById(R.id.tv_time);
			holder.tv_state = (TextView) view.findViewById(R.id.tv_state);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		holder.checkBox.setChecked(checkList.get(position).isCheck());
		holder.checkBox.setTag(position);
		holder.checkBox.setOnCheckedChangeListener(this);
		holder.checkBox.setOnTouchListener(this);
		holder.tv_plotName.setText((position + 1) + "\t" +(plotList.get(position).getData_type() ==
				Constants.TX_FIREPOIT ? "火点" : "火线")+ "\t" + plotList.get(position).getPlotName());
		holder.tv_time.setText(BaseUtils.getDateStr(plotList.get(position).getTime()));
		holder.tv_state.setText(mContext.getString(plotList.get(position).getUpload_state() ==
				Constants.UPLOAD_STATE_ON ? R.string.upload_state_on : R.string.upload_state_off));
		return view;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if(MotionEvent.ACTION_UP == event.getAction()) {
			isChange = true;
		}
		return false;
	}

	private class ViewHolder {
		private CheckBox checkBox;
		private TextView tv_plotName;
		private TextView tv_time;
		private TextView tv_state;
	}

	@Override
	public void onCheckedChanged(CompoundButton view, boolean isCheck) {
		// TODO Auto-generated method stub
		Log.e("Tag", "sdfaf");
		try {
			if(isChange && view != null) {
				int pos = (Integer) view.getTag();
				checkList.get(pos).setCheck(isCheck);
				isChange = false;
				if(listener != null) {
					listener.onItemPlot();
				}
			}
		} catch (IndexOutOfBoundsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public interface OnItemSelectedPlotListener{
		public void onItemPlot();
	}

	public void setOnItemSelectedPlotListener(OnItemSelectedPlotListener listener){
		this.listener = listener;
	}

}
