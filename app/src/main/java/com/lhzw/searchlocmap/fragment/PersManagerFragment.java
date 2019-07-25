package com.lhzw.searchlocmap.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
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
import com.lhzw.searchlocmap.R;
import com.lhzw.searchlocmap.adapter.PerManageAdapter;
import com.lhzw.searchlocmap.bean.BaseBean;
import com.lhzw.searchlocmap.bean.LocPersonalInfo;
import com.lhzw.searchlocmap.bean.PersonalInfo;
import com.lhzw.searchlocmap.constants.SPConstants;
import com.lhzw.searchlocmap.db.dao.CommonDBOperator;
import com.lhzw.searchlocmap.db.dao.DatabaseHelper;
import com.lhzw.searchlocmap.net.CallbackListObserver;
import com.lhzw.searchlocmap.net.SLMRetrofit;
import com.lhzw.searchlocmap.net.ThreadSwitchTransformer;
import com.lhzw.searchlocmap.ui.PerDetailsActivity;
import com.lhzw.searchlocmap.ui.PerItemAddActivity;
import com.lhzw.searchlocmap.utils.BaseUtils;
import com.lhzw.searchlocmap.utils.CheckBoxState;
import com.lhzw.searchlocmap.utils.LogUtil;
import com.lhzw.searchlocmap.utils.SpUtils;
import com.lhzw.searchlocmap.view.LoadingView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;

import static com.lhzw.searchlocmap.utils.ToastUtil.showToast;

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
    private ArrayList<CheckBoxState> mSelectedList = new ArrayList<>();//选中的人

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
                 //将选择的加入list
                for (int i = 0; i < peradapter.getCheckStateList().size(); i++) {
                    if (peradapter.getCheckStateList().get(i).isCheck()) {
                        mSelectedList.add(peradapter.getCheckStateList().get(i));
                    }
                }
                LogUtil.e("选中的个数为"+mSelectedList.size());
                //拼接参数
                if (mSelectedList.size() > 0) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < mSelectedList.size(); i++) {
                        if (i == mSelectedList.size() - 1) {
                            sb.append(mSelectedList.get(i).getNum());
                        } else {
                            sb.append(mSelectedList.get(i).getNum());
                            sb.append(",");
                        }
                    }

                    String deviceNums = sb.toString();
                    //todo 解绑前  先去请求服务器  服务器反馈后解绑
                    AskServerToUnbind(BaseUtils.getDipperNum(getActivity()), deviceNums);
                } else {
                    showToast("请选择删除的对象");
                }


                break;
        }
    }

    /**
     * 手持机解绑手表
     *
     * @param bdNum
     * @param deviceNums
     */
    private void AskServerToUnbind(String bdNum, String deviceNums) {
        if (TextUtils.isEmpty(bdNum)) {
            showToast("北斗卡未安装,请安装北斗卡后重试");
            return;
        }
        final LoadingView loadingView = new LoadingView(getActivity());
        loadingView.setLoadingTitle("解绑中...");
        loadingView.show();
        Observable<BaseBean> observable = SLMRetrofit.getInstance().getApi().deleteBinding(bdNum, deviceNums);
        observable.compose(new ThreadSwitchTransformer<BaseBean>()) //从数据流中得到原始Observable<T>的操作符
                .subscribe(new CallbackListObserver<BaseBean>() {
                    @Override
                    protected void onSucceed(BaseBean bean) {
                        loadingView.dismiss();
                        if ("0".equals(bean.getCode())) {
                            showToast("解绑成功");
                            LogUtil.d("解绑成功");

                            for (CheckBoxState item : mSelectedList) {
                                    CommonDBOperator.deleteByKeys(persondao, "num",
                                            item.getNum());
                                    // 删除本地表

                                    CommonDBOperator.deleteByKeys(dao, "num", item.getNum());
                            }

                                perList = (ArrayList<LocPersonalInfo>) CommonDBOperator
                                        .getList(persondao);
                                peradapter.setList(perList);
                                peradapter.initList();
                                perlistview.setOnItemClickListener(PersManagerFragment.this);

                                SpUtils.putBoolean(SPConstants.CHECKBOX_ISSHOW, false);
                                relativeLayout1.setVisibility(View.INVISIBLE);
                                ani_state = !ani_state;

                                peradapter.notifyDataSetChanged();

                                // 通知地图更新界面
                                Intent intent1 = new Intent("com.lhzw.soildersos.change");
                                intent1.putExtra("has_new", false);
                                getActivity().sendBroadcast(intent1);

                            mSelectedList.clear();

                        } else {
                            mSelectedList.clear();
                            LogUtil.d("解绑失败");
                            showToast(bean.getMessage() + "");
                        }

                    }

                    @Override
                    protected void onFailed() {
                        mSelectedList.clear();
                        loadingView.dismiss();
                        showToast("网络错误");
                    }

                });

    }

    /**
     * 隐藏输入法
     */
    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
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
            peradapter.initList();
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
        // 单项选
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

