package com.lhzw.searchlocmap.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;
import com.lhzw.searchlocmap.R;
import com.lhzw.searchlocmap.adapter.SortAdapter;
import com.lhzw.searchlocmap.bean.HttpPersonInfo;
import com.lhzw.searchlocmap.db.dao.CommonDBOperator;
import com.lhzw.searchlocmap.db.dao.DatabaseHelper;
import com.lhzw.searchlocmap.utils.LogUtil;
import com.lhzw.searchlocmap.utils.PinyinComparator;
import com.lhzw.searchlocmap.utils.PinyinUtils;
import com.lhzw.searchlocmap.view.ClearEditText;
import com.lhzw.searchlocmap.view.SideBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;

/**
 * Created by hecuncun on 2019/5/22
 */
public class ContactFragment extends BaseLazyFragment {
    @BindView(R.id.filter_edit)
    ClearEditText mClearEditText;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.dialog)
    TextView mDialog;
    @BindView(R.id.sideBar)
    SideBar mSideBar;
    private DatabaseHelper mHelper;
    private Dao mHttpPerDao;
    private List<String> values;
    private SortAdapter adapter;
    LinearLayoutManager manager;
    private List<HttpPersonInfo> SourceDateList;//查询到的所有联系人
    /**
     * 根据拼音来排列RecyclerView里面的数据类
     */
    private PinyinComparator pinyinComparator;


    @Override
    protected int initView() {
        return R.layout.fragment_contact;
    }

    @Override
    protected void initData() {
        mHelper = DatabaseHelper.getHelper(getActivity());
        mHttpPerDao = mHelper.getHttpPerDao();
        values = new ArrayList<>();
        values.add("0");
        values.add("1");
        SourceDateList = CommonDBOperator.queryByMultiKeysEqual(mHttpPerDao, "deviceType", values);//查询到的所有联系人deviceType 为0和1的
        values.clear();
        LogUtil.d("所有的联系人 mList.size()==" + SourceDateList.size());

        pinyinComparator = new PinyinComparator();
        mSideBar.setTextView(mDialog);

        //设置右侧SideBar触摸监听
        mSideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    manager.scrollToPositionWithOffset(position, 0);
                }

            }
        });

        List<HttpPersonInfo> personInfos = filledData(SourceDateList);//将所有人的名字首字母设置出来

        // 根据a-z进行排序源数据
        Collections.sort(personInfos, pinyinComparator);
        //RecyclerView社置manager
        manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        adapter = new SortAdapter(getActivity(), personInfos);
        mRecyclerView.setAdapter(adapter);


        //根据输入框输入值的改变来过滤搜索
        mClearEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    protected void onReflesh() {

    }


    /**
     * 为RecyclerView的bean 设置首字符数据
     *
     * @param list
     * @return
     */
    private List<HttpPersonInfo> filledData(List<HttpPersonInfo> list) {


        for (int i = 0; i <list.size(); i++) {
            HttpPersonInfo personInfo = list.get(i);
            //汉字转换成拼音
            String pinyin = PinyinUtils.getPingYin(personInfo.getRealName());
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                personInfo.setLetters(sortString.toUpperCase());
            } else {
                personInfo.setLetters("#");
            }
        }
        return list;

    }

    /**
     * 根据输入框中的值来过滤数据并更新RecyclerView
     *
     * @param filterStr
     */
    private void  filterData(String filterStr) {
        List<HttpPersonInfo> filterDateList = new ArrayList<>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = SourceDateList;
        } else {
            filterDateList.clear();
            for (HttpPersonInfo httpPersonInfo : SourceDateList) {
                String name = httpPersonInfo.getRealName();
                if (name.indexOf(filterStr.toString()) != -1 ||
                        PinyinUtils.getFirstSpell(name).startsWith(filterStr.toString())
                        //不区分大小写
                        || PinyinUtils.getFirstSpell(name).toLowerCase().startsWith(filterStr.toString())
                        || PinyinUtils.getFirstSpell(name).toUpperCase().startsWith(filterStr.toString())
                        ) {
                    filterDateList.add(httpPersonInfo);
                }
            }
        }

        // 根据a-z进行排序
        Collections.sort(filterDateList, pinyinComparator);
        adapter.updateList(filterDateList);
    }



}
