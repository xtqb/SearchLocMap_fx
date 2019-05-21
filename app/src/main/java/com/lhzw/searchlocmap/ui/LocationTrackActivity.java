package com.lhzw.searchlocmap.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.j256.ormlite.dao.Dao;
import com.lhzw.searchlocmap.R;
import com.lhzw.searchlocmap.adapter.LocTrackAdapter;
import com.lhzw.searchlocmap.bean.LocTrackBean;
import com.lhzw.searchlocmap.db.dao.CommonDBOperator;
import com.lhzw.searchlocmap.db.dao.DatabaseHelper;
import com.lhzw.searchlocmap.utils.CheckBoxTrack;
import java.util.ArrayList;
import java.util.List;

public class LocationTrackActivity extends Activity implements OnClickListener, OnCheckedChangeListener, LocTrackAdapter.OnSeletedAllListner,
        OnTouchListener, OnItemClickListener {
    private Button per_back;
    private ListView listview;
    private CheckBox select;
    private TextView delete;
    private DatabaseHelper<?> helper;
    private Dao<LocTrackBean, Integer> locTrackDao;
    private List<LocTrackBean> trackList = new ArrayList<LocTrackBean>();
    private LocTrackAdapter adapter;
    private boolean isOnClick = false;
    private final int RESULTCODE = 0x673435;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locationtrack);
        initView();
        initData();
        setListener();
    }

    private void initView() {
        per_back = (Button) findViewById(R.id.per_back);
        listview = (ListView) findViewById(R.id.listview);
        select = (CheckBox) findViewById(R.id.select);
        delete = (TextView) findViewById(R.id.delete);
    }

    private void initData() {
        // TODO Auto-generated method stub
        helper = DatabaseHelper.getHelper(LocationTrackActivity.this);
        locTrackDao = helper.getLocTrackDao();
        select.setVisibility(View.INVISIBLE);
        List<LocTrackBean> list = CommonDBOperator.getList(locTrackDao);
        if (list != null && list.size() > 0) {
            trackList.addAll(list);
        }
        Log.e("Tag", "len : " + trackList.size());
        adapter = new LocTrackAdapter(LocationTrackActivity.this, trackList);
        adapter.setOnSelectedAllListener(this);
        listview.setOnItemClickListener(this);
        listview.setAdapter(adapter);
    }

    private void setListener() {
        // TODO Auto-generated method stub
        per_back.setOnClickListener(this);
        delete.setOnClickListener(this);
        select.setOnCheckedChangeListener(this);

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.per_back:
                finish();
                break;
            case R.id.delete:
                if (trackList != null && trackList.size() > 0) {
                    List<CheckBoxTrack> list = adapter.getCheckBoxList();
                    int counter = 0;
                    for (CheckBoxTrack item : list) {
                        if (item.isCheck()) {
                            counter++;
                            CommonDBOperator.deleteByKeys(locTrackDao, "time", item.getTime() + "");
                        }
                    }

                    if (counter > 0) {
                        trackList.clear();
                        List<LocTrackBean> list2 = CommonDBOperator.getList(locTrackDao);
                        if (list2 != null && list2.size() > 0) {
                            trackList.addAll(list2);
                        }
                        adapter.refleshView();

                        if (adapter.getCheckBoxList().size() > 0) {
                            select.setChecked(false);
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // TODO Auto-generated method stub
        switch (buttonView.getId()) {
            case R.id.select:
                if (isOnClick) {
                    isOnClick = false;
                    Log.e("Tag", "selected all");
                    if (adapter.getCheckBoxList().size() == 0) {
                        Toast.makeText(LocationTrackActivity.this,
                                getString(R.string.track_num_zero_note), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    int counter = 0;
                    for (CheckBoxTrack item : adapter.getCheckBoxList()) {
                        if (item.isCheck() != isChecked) {
                            item.setCheck(isChecked);
                            counter++;
                        }
                    }
                    if (counter > 0) {
                        adapter.notifyDataSetChanged();
                    }
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onSeletedAll(boolean isChecked) {
        // TODO Auto-generated method stub
        if (select != null && select.isChecked() != isChecked) {
            select.setChecked(isChecked);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.select:
                isOnClick = true;
                break;
        }
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        // TODO Auto-generated method stub
        Log.e("Tag", "position = " + position);
        Intent intent = new Intent();
        intent.putExtra("path", trackList.get(position).getPaths());
        setResult(RESULTCODE, intent);
        finish();
    }
}
