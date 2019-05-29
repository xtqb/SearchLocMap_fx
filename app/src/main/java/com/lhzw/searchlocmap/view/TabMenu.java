package com.lhzw.searchlocmap.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lhzw.searchlocmap.R;

import java.util.ArrayList;

/**
 * Created by hecuncun on 2019/5/22
 */
public class TabMenu extends LinearLayout{

    private Context mContext;
    private ArrayList<ImageView> mImageViews;
    private ArrayList<TextView> mTextViews;
    private int mItemCount;//子布局个数
    private int mTextSize;
    private int mTextColor;//未选中颜色
    private int mTextSelectColor;//选中颜色
    private int mItemBg;//背景色
    private int mSelectedItem;//已选中的item

    private int[] mIcons = new int[10];
    private int[] mSelectIcons = new int[10];
    private String[] mTexts = new String[10];

    private ViewPager mViewPager;

    /**
     * 初始化  底部选中未选中图标
     * @param icons
     * @param selectIcons
     */
    public void setImageViews(int[] icons,int[] selectIcons){
        if(icons != null && selectIcons != null){
            for (int i = 0; i < mItemCount; i++) {
                if(i > mIcons.length-1){
                    break;
                }
                mIcons[i] = icons[i];
                mSelectIcons[i] = selectIcons[i];

            }
        }
    }

    /**
     * 初始化  底部文字
     * @param texts
     */
    public void setTexts(String[] texts){
        if(texts!=null){
            for (int i = 0; i < mItemCount; i++) {
                if(i>mTexts.length-1){
                    break;
                }
                mTexts[i]=texts[i];
                mTextViews.get(i).setText(mTexts[i]);
            }
        }
    }


    public TabMenu(Context context) {
        this(context,null);
    }

    public TabMenu(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TabMenu(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setOrientation(LinearLayout.HORIZONTAL);
        mContext = context;
        mImageViews = new ArrayList<>();
        mTextViews = new ArrayList<>();
        //获取自定义属性
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TabMenu, defStyleAttr, 0);
        mItemCount = array.getInt(R.styleable.TabMenu_itemCount, 2);
        mTextSize = array.getDimensionPixelSize(R.styleable.TabMenu_android_textSize, 0);
        mTextColor = array.getColor(R.styleable.TabMenu_text_color, 0x777777);
        mTextSelectColor = array.getColor(R.styleable.TabMenu_text_select_color,0x222222);
        mItemBg = array.getColor(R.styleable.TabMenu_item_bg, 0xE0E0E0);
        mSelectedItem = array.getInt(R.styleable.TabMenu_selected, 0);
        array.recycle();

        initItemView();


    }

    private void initItemView() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        for (int i = 0; i <mItemCount ; i++) {
            View mView = inflater.inflate(R.layout.tab_menu_item, this, false);
            mView.setClickable(true);
            mView.setBackgroundColor(mItemBg);
            ImageView imageView = (ImageView) mView.findViewById(R.id.tab_item_image);
            TextView textView = (TextView) mView.findViewById(R.id.tab_item_text);
            textView.setTextSize(mTextSize);
            textView.setTextColor(mTextColor);
          //  mView.setBackgroundColor(mItemBg);

            //item通过LayoutParams 向父布局传递自己的意愿,设置布局属性
            LayoutParams layoutParams = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.weight = 1;
            mView.setLayoutParams(layoutParams);
            addView(mView,i);
           mImageViews.add(imageView);
           mTextViews.add(textView);
           final int j = i;
           mView.setOnClickListener(new OnClickListener() {
               @Override
               public void onClick(View v) {
                   setCurrentSelectedItem(j);
                   mViewPager.setCurrentItem(j);
               }
           });
        }
    }

    private void setItemTextColor() {
        for (int i = 0; i < mItemCount; i++) {
            if(i > mTexts.length-1){
                break;
            }
            if(mSelectedItem == i){
                mTextViews.get(i).setTextColor(mTextSelectColor);
            }else {
                mTextViews.get(i).setTextColor(mTextColor);
            }
        }
    }

    private void setItemImageBg() {
        for (int i = 0; i < mItemCount; i++) {
            if( i > mIcons.length-1){
                break;
            }
            if(mSelectedItem == i){
                mImageViews.get(i).setBackgroundResource(mSelectIcons[i]);
            }else {
                mImageViews.get(i).setBackgroundResource(mIcons[i]);
            }
            
        }
    }

    private void setSelectedItem(int selected) {
        mSelectedItem = selected ;
    }

    public void setViewPager(ViewPager viewPager){
        mViewPager = viewPager;
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setCurrentSelectedItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void setCurrentSelectedItem(int position) {
        setSelectedItem(position);
        setItemImageBg();
        setItemTextColor();
    }
}
