package com.lhzw.searchlocmap.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lhzw.searchlocmap.R;
import com.lhzw.searchlocmap.bean.PlotItem;
import com.lhzw.searchlocmap.bean.PlotItemInfo;
import com.lhzw.searchlocmap.constants.Constants;

public class PortaitAdapter extends BaseAdapter implements OnItemClickListener {
    private List<PlotItem> list = new ArrayList<>();
    private Context mContext;
    private ViewHolder holder;
    private int currentId = -1;
    private OnItemSelectedListener listener;
    private List<PlotItemInfo> plotList;

    public PortaitAdapter(Context mContext, List<PlotItemInfo> plotList) {
        // TODO Auto-generated constructor stub
        this.mContext = mContext;
        this.plotList = plotList;
        initPlotItem();
    }

    public void initPlotItem() {
        if(list.size() > 0) {
            list.clear();
        }
        if(plotList != null && plotList.size() > 0) {
            for (PlotItemInfo item : plotList) {
                if(item.getData_type() == Constants.TX_FIREPOIT) {
                    PlotItem plot = new PlotItem("", R.drawable.icon_fire);
                    list.add(plot);
                } else if (item.getData_type() == Constants.TX_FIRELINE || item.getData_type() == Constants.TX_SYNCFL) {
                    PlotItem plot = new PlotItem("", R.drawable.icon_line1);
                    list.add(plot);
                }
            }
        }
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.listener = listener;
    }

    public void setList(List<PlotItem> list) {
        if (this.list != null && this.list.size() > 0) {
            this.list.clear();
        }
        currentId = -1;
        this.list = list;
        notifyDataSetChanged();

    }

    public void setSelectID(int position){
        currentId = position;
    }

    public void resetList() {
        if (currentId != -1) {
            currentId = -1;
            notifyDataSetChanged();
        }
    }

    public void refleshList() {
        currentId = -1;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        if (list == null) return 0;
        return list.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        // TODO Auto-generated method stub
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_vplot, null);
            holder = new ViewHolder();
            holder.tv_num = (TextView) view.findViewById(R.id.plot_num);
            holder.im_plot = (ImageView) view.findViewById(R.id.plot_pic);
            holder.im_upload_state = (ImageView) view.findViewById(R.id.im_upload_state);
            holder.im_bg = (ImageView) view.findViewById(R.id.im_bg);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.tv_num.setText((1 + position) + "");
        holder.im_plot.setImageResource(list.get(position).getPicId());
        if(plotList.get(position).getUpload_state() == Constants.UPLOAD_STATE_ON) {
            holder.im_upload_state.setImageResource(R.drawable.icon_ready_upload);
        } else {
            holder.im_upload_state.setImageResource(R.drawable.icon_not_upload);

        }
        if (position == currentId) {
            holder.im_bg.setImageResource(R.drawable.icon_selected_item);
        } else {
            holder.im_bg.setImageResource(R.drawable.icon_no_select);
        }
        return view;
    }

    private class ViewHolder {
        private ImageView im_bg;
        private TextView tv_num;
        private ImageView im_plot;
        private ImageView im_upload_state;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View child, int position,
                            long id) {
        // TODO Auto-generated method stub
        Log.e("Tag", "sdfsafasf");
        notifyDataSetChanged();
        if (listener != null) {
            listener.onItemSelected(position);
        }
    }

    public interface OnItemSelectedListener {
        public void onItemSelected(int pos);
    }

}
