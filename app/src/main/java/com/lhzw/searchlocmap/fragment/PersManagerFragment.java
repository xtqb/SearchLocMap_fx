package com.lhzw.searchlocmap.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lhzw.searchlocmap.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.lhzw.searchlocmap.adapter.PerManageAdapter;
import com.lhzw.searchlocmap.bean.LocPersonalInfo;
import com.lhzw.searchlocmap.bean.PersonalInfo;
import com.lhzw.searchlocmap.constants.SPConstants;
import com.lhzw.searchlocmap.db.dao.CommonDBOperator;
import com.lhzw.searchlocmap.db.dao.DatabaseHelper;
import com.lhzw.searchlocmap.ui.PerDetailsActivity;
import com.lhzw.searchlocmap.ui.PerItemAddActivity;
import com.lhzw.searchlocmap.utils.CheckBoxState;
import com.lhzw.searchlocmap.utils.SpUtils;

public class PersManagerFragment extends Fragment implements
        OnClickListener, OnItemClickListener,
        android.view.View.OnClickListener, PerManageAdapter.onCheckBoxChangeListener,
        OnTouchListener {

    private View view;

    private TextView add_btn;
    private String keyWord = "";// 要输入的poi搜索关键字
    private EditText searchText;// 输入搜索关键字
    private int currentPage = 0;// 当前页面，从0开始计数
    private DatabaseHelper helper;
    private PerManageAdapter peradapter;
    private ListView perlistview;
    private ArrayList<LocPersonalInfo> perList;
    private Dao<LocPersonalInfo, Integer> persondao;
    private Dao<PersonalInfo, Integer> dao;
    private boolean ani_state = true;
    private TextView tv_num;
    private CheckBox select;
    private RelativeLayout relativeLayout1;
    private boolean isOnClick = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.pers_manager_fg, container, false);

        init();

        return view;
    }

    private void init() {
        add_btn = (TextView) view.findViewById(R.id.tv_add);
        add_btn.setOnClickListener(this);
        select = (CheckBox) view.findViewById(R.id.select);
        select.setOnCheckedChangeListener(listener);
        select.setOnTouchListener(this);
        ImageView im_delete = (ImageView) view.findViewById(R.id.im_delete);
        im_delete.setOnClickListener(this);
        searchText = (EditText) view.findViewById(R.id.per_keyWord);
        searchText.addTextChangedListener(watcher);
        TextView delete = (TextView) view.findViewById(R.id.delete);
        delete.setOnClickListener(this);
        tv_num = (TextView) view.findViewById(R.id.tv_num);
        relativeLayout1 = (RelativeLayout) view
                .findViewById(R.id.relativeLayout1);
        relativeLayout1.setVisibility(View.INVISIBLE);
        helper = DatabaseHelper.getHelper(getActivity());
        persondao = helper.getLocPersonDao();
        dao = helper.getPersonalInfoDao();

        perList = (ArrayList<LocPersonalInfo>) CommonDBOperator
                .getList(persondao);// 取出来
        perlistview = (ListView) view.findViewById(R.id.personal_listview);
        peradapter = new PerManageAdapter(perList, getActivity(),
                R.layout.item_per_list, tv_num);
        peradapter.setItemcheckListener(this);
        SpUtils.putBoolean(SPConstants.CHECKBOX_ISSHOW, false);// checkbox是否显示
        perlistview.setOnItemClickListener(this);
        perlistview.setAdapter(peradapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_add:
                Intent intent = new Intent(getActivity(), PerItemAddActivity.class);
                startActivityForResult(intent, 250);
                break;
            case R.id.im_delete:
                if (perList == null || perList.size() == 0) {
                    Toast.makeText(getActivity(),
                            getString(R.string.person_manager_delete_note1),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (ani_state) {
                    SpUtils.putBoolean(SPConstants.CHECKBOX_ISSHOW, true);
                    relativeLayout1.setVisibility(View.VISIBLE);
                } else {
                    SpUtils.putBoolean(SPConstants.CHECKBOX_ISSHOW, false);
                    relativeLayout1.setVisibility(View.INVISIBLE);
                }
                peradapter.notifyDataSetChanged();
                ani_state = !ani_state;
                break;

            case R.id.delete:
                List<CheckBoxState> list = peradapter.getCheckStateList();
                int counter = 0;
                for (CheckBoxState item : list) {
                    if (item.isCheck()) {
                        counter++;
                        CommonDBOperator.deleteByKeys(persondao, "num",
                                item.getNum());
                        // 删除本地表
                        CommonDBOperator.deleteByKeys(dao, "num", item.getNum());
                    }
                }
                if (counter > 0) {
                    perList = (ArrayList<LocPersonalInfo>) CommonDBOperator
                            .getList(persondao);
                    peradapter.setList(perList);
                    peradapter.initList();
                    perlistview.setOnItemClickListener(this);

                    SpUtils.putBoolean(SPConstants.CHECKBOX_ISSHOW, false);
                    relativeLayout1.setVisibility(View.INVISIBLE);
                    ani_state = !ani_state;

                    peradapter.notifyDataSetChanged();

                    // 通知地图更新界面
                    Intent intent1 = new Intent("com.lhzw.soildersos.change");
                    intent1.putExtra("has_new", false);
                    getActivity().sendBroadcast(intent1);
                } else {
                    Toast.makeText(getActivity(),
                            getString(R.string.person_manager_delete_note),
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * 隐藏输入法
     */
    private void hideInputMethod() {
        // 隐藏输入法
        InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && imm.isActive()) {
            View v = getActivity().getCurrentFocus();
            if (v != null) {
                IBinder ib = v.getWindowToken();
                if (ib != null) {
                    imm.hideSoftInputFromWindow(ib,
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        }
    }

    // 列表 监听
    @Override
    public void onItemClick(AdapterView<?> parent, View child, int position,
                            long id) {
        Intent intent = new Intent(getActivity(), PerDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("PreID", perList.get(position));
        intent.putExtras(bundle);

        startActivityForResult(intent, 100);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        perList = (ArrayList<LocPersonalInfo>) CommonDBOperator
                .getList(persondao);
        peradapter.setList(perList);
        perlistview.setOnItemClickListener(this);
        peradapter.initList();
        // perlistview.setAdapter(peradapter);
        peradapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        // TODO Auto-generated method stub

    }

    private TextWatcher watcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            Log.e("dayin", s.toString());
            Map<String, String> map = new HashMap<String, String>();
            map.put("num", s.toString());
            map.put("name", s.toString());
            perList = (ArrayList<LocPersonalInfo>) CommonDBOperator
                    .queryByMultiKeysFuzzy(persondao, map);
            peradapter.setList(perList);
            peradapter.notifyDataSetChanged();
        }
    };

    private OnCheckedChangeListener listener = new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            // TODO Auto-generated method stub
            if (isOnClick) {
                isOnClick = false;
                peradapter.setCheckStateList(isChecked);
                peradapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    public void itemSelected(int pos) {
        // TODO Auto-generated method stub
        boolean isAllCheck = true;
        for (CheckBoxState item : peradapter.getCheckStateList()) {
            isAllCheck = isAllCheck && item.isCheck();
        }
        select.setChecked(isAllCheck);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // TODO Auto-generated method stub
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                isOnClick = true;
                break;

            default:
                break;
        }

        return false;
    }

}

