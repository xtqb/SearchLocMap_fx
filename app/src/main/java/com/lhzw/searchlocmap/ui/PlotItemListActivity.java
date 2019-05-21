package com.lhzw.searchlocmap.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.lhzw.searchlocmap.R;
import com.lhzw.searchlocmap.adapter.PlotItemAdapter;
import com.lhzw.searchlocmap.bean.PlotItemInfo;
import com.lhzw.searchlocmap.constants.Constants;
import com.lhzw.searchlocmap.constants.SPConstants;
import com.lhzw.searchlocmap.db.dao.CommonDBOperator;
import com.lhzw.searchlocmap.db.dao.DatabaseHelper;
import com.lhzw.searchlocmap.utils.BDUtils;
import com.lhzw.searchlocmap.utils.BaseUtils;
import com.lhzw.searchlocmap.utils.LogWrite;
import com.lhzw.searchlocmap.utils.PlotCheckBox;
import com.lhzw.searchlocmap.utils.SpUtils;
import com.lhzw.searchlocmap.view.ShowPlotDialog;
import com.lhzw.uploadmms.UploadInfoBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlotItemListActivity extends PlotBaseActivity implements OnClickListener, TextWatcher,
        OnItemLongClickListener, PlotItemAdapter.OnItemSelectedPlotListener {
    private TextView per_back;
    private EditText plot_keyWord;
    private ListView plot_list;
    private TextView tv_delete;
    private TextView tv_load;
    private PlotItemAdapter adapter;
    private Dao<PlotItemInfo, Integer> dao;
    private List<PlotItemInfo> plotList;
    private ShowPlotDialog dialog;
    private int currentP = -1;
    private boolean isChange;
    private BDUtils bdUtils;
    private ImageView im_select;
    private boolean isAllSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plotitem);
        initView();
        initData();
        setListener();
        initWriter();

    }

    private void initWriter() {
    }

    private void initView() {
        per_back = (TextView) findViewById(R.id.per_back);
        plot_keyWord = (EditText) findViewById(R.id.plot_keyWord);
        plot_list = (ListView) findViewById(R.id.plot_list);
        tv_delete = (TextView) findViewById(R.id.tv_delete);
        tv_load = (TextView) findViewById(R.id.tv_load);
        im_select = (ImageView) findViewById(R.id.im_select);
    }

    private void initData() {
        // TODO Auto-generated method stub
        DatabaseHelper<?> helper = DatabaseHelper.getHelper(PlotItemListActivity.this);
        dao = helper.getPlotItemDao();
        bdUtils = BDUtils.getInstance();
        plotList = CommonDBOperator.getList(dao);
        adapter = new PlotItemAdapter(PlotItemListActivity.this, plotList, R.layout.item_plot_list);
        adapter.setOnItemSelectedPlotListener(this);
        plot_list.setAdapter(adapter);
//        plot_list.setOnItemLongClickListener(this);
        isChange = false;
        isAllSelected = false;
    }

    private void setListener() {
        // TODO Auto-generated method stub
        per_back.setOnClickListener(this);
        tv_delete.setOnClickListener(this);
        tv_load.setOnClickListener(this);
        plot_keyWord.addTextChangedListener(this);
        im_select.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        // TODO Auto-generated method stub
        switch (view.getId()) {
            case R.id.per_back:
                Intent intent = new Intent();
                intent.putExtra("state", isChange);
                setResult(RESULT_OK, intent);
                PlotItemListActivity.this.finish();
                break;
            case R.id.tv_delete:
                List<PlotCheckBox> checkList = adapter.getCheckBoxList();
                int counter = 0;
                for (PlotCheckBox item : checkList) {
                    if (item.isCheck()) {
                        CommonDBOperator.deleteByKeys(dao, "time", item.getTime() + "");
                        counter++;
                    }
                }
                checkList = null;
                if (counter > 0) {
                    plotList.clear();
                    plotList = CommonDBOperator.getList(dao);
                    adapter.setPlotList(plotList);
                    adapter.notifyDataSetChanged();
                    isChange = true;
                    if(isAllSelected) {
                        isAllSelected = false;
                        im_select.setBackgroundResource(R.drawable.icon_selected_def);
                    }
                } else {
                    showToast(getString(R.string.select_note));
                }
                break;
            case R.id.tv_load:
                if (getDBManager() == null) {
                    return;
                }
                List<PlotCheckBox> checkList1 = adapter.getCheckBoxList();
                List<PlotItemInfo> uploadList = new ArrayList<PlotItemInfo>();
                int counter1 = 0;
                int counter2 = 0;
                for (PlotCheckBox item : checkList1) {
                    if (item.isCheck()) {
                        if (plotList.get(item.getID()).getUpload_state() == Constants.UPLOAD_STATE_OFF) {
                            counter1++;
                            uploadList.add(plotList.get(item.getID()));
                        } else {
                            counter2 ++;
                        }
                        // 写入日志
                        try {
                            LogWrite writer = LogWrite.open();
                            String log = LogWrite.df.format(System.currentTimeMillis()) + "\t data_type = " + plotList.get(item.getID()).getData_type() + "\t "
                                    + "latLon = " + plotList.get(item.getID()).getPaths();
                            writer.writeLog(log);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (!isAllSelected && counter2 > 0) {
                    showToast(getString(R.string.upload_state_on_note));
                    return;
                }

                if (counter1 == 0) {
                    showToast(getString(R.string.dipper_upload_plot_note));
                } else {
                    List<UploadInfoBean> list = BaseUtils.getUploadInfoList(uploadList);
                    if (list != null && list.size() > 0) {
                        bdUtils.uploadBena(list);
                        if (SpUtils.getInt(SPConstants.SP_BD_MODE, Constants.UOLOAD_STATE_0) == Constants.UOLOAD_STATE_1) {
                            for (int i = 0; i < list.size(); i++) {
                                list.get(i).setTx_type(Constants.TX_QZH);
                                list.get(i).setNum(SpUtils.getString(Constants.UPLOAD_QZH_NUM, Constants.BD_NUM_DEF));
                            }
                            bdUtils.uploadBena(list);
                        }
                        for (PlotItemInfo item : uploadList) {
                            item.setUpload_state(Constants.UPLOAD_STATE_ON);
                            CommonDBOperator.updateItem(dao, item);
                        }
                        uploadList.clear();
                    }
                    Toast.makeText(this, getString(R.string.uploading_note_successful), Toast.LENGTH_LONG).show();
                    adapter.initCheckList();
                    adapter.notifyDataSetChanged();
                    if(isAllSelected) {
                        isAllSelected = false;
                        im_select.setBackgroundResource(R.drawable.icon_selected_def);
                    }
                }
                break;
            case R.id.bt_plot_save:
                String name = dialog.getPlotName();
                if (dialog != null && !BaseUtils.isStringEmpty(name)) {
                    Log.e("Tag", "dsf : " + name + "  " + currentP);
                    plotList.get(currentP).setPlotName(name);
                    CommonDBOperator.updateItem(dao, plotList.get(currentP));
                    adapter.notifyDataSetChanged();

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(dialog.getPlotEditext().getApplicationWindowToken(), 0);
                    }
                    dialog.cancelDialog();
                } else {
                    showToast(getString(R.string.plot_name_note_fail));
                }
                break;
            case R.id.bt_plot_cancle:
                if (dialog != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(dialog.getPlotEditext().getApplicationWindowToken(), 0);
                    }
                    dialog.cancelDialog();
                }
                break;
            case R.id.im_select:
                if (isAllSelected) {
                    im_select.setBackgroundResource(R.drawable.icon_selected_def);
                } else {
                    im_select.setBackgroundResource(R.drawable.icon_selected_pre);
                }
                isAllSelected = !isAllSelected;
                adapter.selectAll(isAllSelected);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent();
            intent.putExtra("state", isChange);
            setResult(RESULT_OK, intent);
            this.finish();
            return true;
        }
        return false;
    }

    @Override
    public void afterTextChanged(Editable s) {
        // TODO Auto-generated method stub
        if (plotList != null) {
            plotList.clear();
        }
        if (!BaseUtils.isStringEmpty(s.toString())) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("plotName", s.toString());
            map.put("paths", s.toString());
            plotList = CommonDBOperator.queryByMultiKeysFuzzy(dao, map);
            map = null;
        } else {
            plotList = CommonDBOperator.getList(dao);
        }
        adapter.setPlotList(plotList);
        adapter.notifyDataSetChanged();

    }

    @Override
    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                  int arg3) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View child, int position,
                                   long id) {
        // TODO Auto-generated method stub
        dialog = new ShowPlotDialog(PlotItemListActivity.this);
        dialog.showDialog();
        dialog.setListener(this);
        currentP = position;
//		dialog.getPlotEditext().setFocusable(true);
//		dialog.getPlotEditext().setFocusableInTouchMode(true);
//		dialog.getPlotEditext().requestFocus();
//		InputMethodManager inputManager = (InputMethodManager) dialog.getPlotEditext().getContext().getSystemService(Context.INPUT_METHOD_SERVICE);	
//		inputManager.showSoftInput(dialog.getPlotEditext(), 0);

        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return false;
    }

    @Override
    public void onItemPlot() {
        boolean state = true;
        List<PlotCheckBox> stateList = adapter.getCheckBoxList();
        for (int pos = 0; pos < stateList.size(); pos++) {
           if(!stateList.get(pos).isCheck()) {
               state = false;
               break;
           }
        }
        im_select.setBackgroundResource(state ? R.drawable.icon_selected_pre : R.drawable.icon_selected_def);
        isAllSelected = state;
    }
}
