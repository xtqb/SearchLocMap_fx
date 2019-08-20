package com.lhzw.searchlocmap.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.LoRaManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.lhzw.searchlocmap.R;
import com.lhzw.searchlocmap.bean.PersonalInfo;
import com.lhzw.searchlocmap.bean.WatchLastLocTime;
import com.lhzw.searchlocmap.constants.Constants;
import com.lhzw.searchlocmap.db.dao.CommonDBOperator;
import com.lhzw.searchlocmap.db.dao.DatabaseHelper;
import com.lhzw.searchlocmap.utils.BaseUtils;
import com.lhzw.searchlocmap.utils.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SrollAdapter extends BaseAdapter implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private Context mContext;
    private List<PersonalInfo> sosList;
    private List<PersonalInfo> commonList;
    private List<PersonalInfo> undetermined_List;
    private List<Bean> list = new ArrayList<>();
    private final Dao<WatchLastLocTime, Integer> locTimeDao;
    private TextView tv_per_num;
    private TextView tv_update_num;
    private TextView tv_sos_num;
    private int per_num;
    private int sos_num;
    private int update_num;
    protected ViewHolder holder;
    private OnClickScrollItemListener listener;

    public SrollAdapter(Context mContext, List<PersonalInfo> sosList, List<PersonalInfo> commonList, List<PersonalInfo> undetermined_List, TextView tv_per_num, TextView tv_update_num, TextView tv_sos_num){
        this.mContext = mContext;
        this.sosList = sosList;
        this.commonList = commonList;
        this.undetermined_List = undetermined_List;
        this.tv_per_num = tv_per_num;
        this.tv_sos_num = tv_sos_num;
        this.tv_update_num = tv_update_num;
        locTimeDao = DatabaseHelper.getHelper(mContext).getLastLocTimeDao();
        initData();
    }


    private void initData(){
        if(list.size() > 0) {
            list.clear();
        }
        per_num = sosList.size() + commonList.size() + undetermined_List.size();
        sos_num = sosList.size();
        update_num = 0;
        List<WatchLastLocTime> locTimelist = CommonDBOperator.getList(locTimeDao);
        Map<String, Long> locTimeMap = null;
        if(locTimelist != null && locTimelist.size() > 0) {
            locTimeMap = new HashMap<>();
            for(WatchLastLocTime bean : locTimelist) {
                locTimeMap.put(bean.getNum(), bean.getLocTime());
            }
        }
        int pos = 0;
        for(PersonalInfo item : sosList) {
            Bean bean = null;
            if(locTimeMap != null) {
                Long locTime = locTimeMap.get(item.getNum());
                if(locTime != null) {
                    bean = new Bean(item.getName(), item.getNum(), BaseUtils.formatTime(item.getLocTime()), true, item.getLocTime() > locTime);
                    if(item.getLocTime() > locTime) {
                        update_num += 1;
                    }
                } else {
                    bean = new Bean(item.getName(), item.getNum(), BaseUtils.formatTime(item.getLocTime()), true, item.getLocTime() > 0);
                    if(item.getLocTime() > 0) {
                        update_num += 1;
                    }
                }
            } else {
                bean = new Bean(item.getName(), item.getNum(), BaseUtils.formatTime(item.getLocTime()), true, item.getLocTime() > 0);
                if(item.getLocTime() > 0) {
                    update_num += 1;
                }
            }
            bean.setState(Constants.PERSON_SOS);
            if(item.getState().equals(Constants.PERSON_OFFLINE)){
                bean.setOnline(false);
            } else {
                bean.setOnline(true);
            }
            bean.setPos(pos);
            list.add(bean);
            pos ++;
        }
        pos = 0;
        for(PersonalInfo item : commonList) {
            Bean bean = null;
            if(locTimeMap != null) {
                Long locTime = locTimeMap.get(item.getNum());
                if(locTime != null) {
                    bean = new Bean(item.getName(), item.getNum(), BaseUtils.formatTime(item.getLocTime()), false, item.getLocTime() > locTime);
                    if(item.getLocTime() > locTime) {
                        update_num += 1;
                    }
                } else {
                    bean = new Bean(item.getName(), item.getNum(), BaseUtils.formatTime(item.getLocTime()), false, item.getLocTime() > 0);
                    if(item.getLocTime() > 0) {
                        update_num += 1;
                    }
                }
            } else {
                bean = new Bean(item.getName(), item.getNum(), BaseUtils.formatTime(item.getLocTime()), false, item.getLocTime() > 0);
                if(item.getLocTime() > 0) {
                    update_num += 1;
                }
            }
            bean.setState(Constants.PERSON_COMMON);
            if(item.getState().equals(Constants.PERSON_OFFLINE)){
                bean.setOnline(false);
            } else {
                bean.setOnline(true);
            }
            bean.setPos(pos);
            list.add(bean);
            pos ++;
        }
        pos = 0;
        for(PersonalInfo item : undetermined_List) {
            Bean bean = null;
            boolean state = false;
            if(item.getState1() != null) {
                state =  item.getState1().equals(Constants.PERSON_SOS);
            }
            bean = new Bean(item.getName(), item.getNum(), BaseUtils.formatTime(item.getLocTime()), state, false);
            if(state) {
                sos_num += 1;
            }
            bean.setState(Constants.PERSON_UNDETERMINED);
            if(item.getState().equals(Constants.PERSON_OFFLINE)){
                bean.setOnline(false);
            } else {
                bean.setOnline(true);
            }
            LogUtil.e(item.getState1() + ",  " + item.getState());
            bean.setPos(pos);
            list.add(bean);
            pos ++;
        }
        if(locTimelist != null && locTimelist.size() > 0) {
            locTimelist.clear();
            locTimeMap.clear();
        }
        tv_per_num.setText(per_num + "");
        tv_sos_num.setText(sos_num + "");
        tv_update_num.setText(update_num + "");
    }

    public void refleshView(){
        initData();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_srollview, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_num.setText(position + 1 + "");
        if(list.get(position).isOnline){
             holder.tv_num.setBackgroundResource(R.drawable.bg_round_scrollview_rect);
        } else {
            holder.tv_num.setBackgroundResource(R.drawable.bg_round_scrollview_rect_offline);
        }
        if(position < 9) {
            holder.tv_num.setPadding(17,1,17,1);
        } else {
            holder.tv_num.setPadding(6,1,6,1);

        }
        holder.tv_name.setText(list.get(position).getName());
        holder.tv_bdnum.setText(list.get(position).getNum());
        holder.tv_time.setText(list.get(position).getTime());
        holder.im_sos.setVisibility(list.get(position).isSos() ? View.VISIBLE: View.INVISIBLE);
        holder.im_update.setBackgroundResource(list.get(position).isUpdate() ? R.drawable.icon_update_yes : R.drawable.icon_update_no) ;
        return convertView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(listener != null) {
            listener.onSrollItemClick(list.get(position).getState(), list.get(position).getPos());
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        LogUtil.e("search watch ...");
        sendCMDSearch(BaseUtils.stringtoByteArr(list.get(position).getNum()));
        Toast.makeText(mContext, "发送成功", Toast.LENGTH_SHORT).show();
        return true;
    }

    protected static class ViewHolder{
        private ViewHolder(View view){
            ButterKnife.bind(this, view);
        }
        @BindView(R.id.tv_num) TextView tv_num;
        @BindView(R.id.tv_name) TextView tv_name;
        @BindView(R.id.tv_bdnum) TextView tv_bdnum;
        @BindView(R.id.tv_time)  TextView tv_time;
        @BindView(R.id.im_sos) ImageView im_sos;
        @BindView(R.id.im_update) ImageView im_update;
    }

    private class Bean{
        private String name;
        private String num;
        private String time;
        private boolean isSos;
        private boolean isUpdate;
        private String state;
        private int pos;
        private boolean isOnline;

        public Bean(String name, String num, String time, boolean isSos, boolean isUpdate) {
            this.name = name;
            this.num = num;
            this.time = time;
            this.isSos = isSos;
            this.isUpdate = isUpdate;
        }

        public boolean isOnline() {
            return isOnline;
        }

        public void setOnline(boolean online) {
            isOnline = online;
        }

        public String getState() {
            return state;
        }

        public int getPos() {
            return pos;
        }

        public void setState(String state) {
            this.state = state;
        }

        public void setPos(int pos) {
            this.pos = pos;
        }

        public String getName() {
            return name;
        }

        public String getNum() {
            return num;
        }

        public String getTime() {
            return time;
        }

        public boolean isSos() {
            return isSos;
        }

        public boolean isUpdate() {
            return isUpdate;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setNum(String num) {
            this.num = num;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public void setSos(boolean sos) {
            isSos = sos;
        }

        public void setUpdate(boolean update) {
            isUpdate = update;
        }
    }

    public interface OnClickScrollItemListener{
        public void onSrollItemClick(String state, int pos);
    }

    public void setOnClickScrollItemListener(OnClickScrollItemListener listener){
        this.listener = listener;
    }

    private boolean sendCMDSearch(final byte[] num) {
        @SuppressLint("WrongConstant") final LoRaManager loRaManager = (LoRaManager) mContext.getSystemService(Context.LORA_SERVICE);
        new Thread(new Runnable() {

            @Override
            public void run() {
                loRaManager.searchCard(num);
            }
        }).start();
        return true;
    }

}
