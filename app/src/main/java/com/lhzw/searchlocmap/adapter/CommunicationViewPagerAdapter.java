package com.lhzw.searchlocmap.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by hecuncun on 2019/5/22
 */
public class CommunicationViewPagerAdapter extends FragmentPagerAdapter{
    private List<Fragment> mFragments;

    public CommunicationViewPagerAdapter(FragmentManager manager, List<Fragment> fragments) {
        super(manager);
        mFragments=fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }
}
