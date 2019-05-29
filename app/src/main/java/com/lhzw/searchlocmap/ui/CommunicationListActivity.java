package com.lhzw.searchlocmap.ui;


import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.lhzw.searchlocmap.R;
import com.lhzw.searchlocmap.adapter.CommunicationViewPagerAdapter;
import com.lhzw.searchlocmap.fragment.ContactFragment;
import com.lhzw.searchlocmap.fragment.ShortMessageFragment;
import com.lhzw.searchlocmap.view.TabMenu;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by hecuncun on 2019/5/22
 * 联系人  最近联系人+联系人列表
 */
public class CommunicationListActivity extends BaseActivity {
    @BindView(R.id.viewpager)
    ViewPager mViewpager;
    @BindView(R.id.tab_menu)
    TabMenu mTabMenu;

    private List<Fragment> mFragments=new ArrayList<>();

    @Override
    protected int initView() {
        return R.layout.activity_communication_list;
    }

    @Override
    protected void initData() {
        int[] icons = {R.drawable.short_msg_normal, R.drawable.contact_normal};
        int[] selectIcons = {R.drawable.short_msg_selected, R.drawable.contact_select};
        String[] tabString ={"短消息","联系人"};
        mTabMenu.setImageViews(icons,selectIcons);
        mTabMenu.setTexts(tabString);
        mFragments.add(new ShortMessageFragment());
        mFragments.add(new ContactFragment());
        mViewpager.setAdapter(new CommunicationViewPagerAdapter(getSupportFragmentManager(),mFragments));
        mTabMenu.setViewPager(mViewpager);
        mTabMenu.setCurrentSelectedItem(0);

    }

}
