package com.lhzw.searchlocmap.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

//取消viewpager的滑动功能
public class PageViewChange extends ViewPager {
	private boolean isCanScroll = true;

	public PageViewChange(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public PageViewChange(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public void setScanScroll(boolean isCanScroll) {
		this.isCanScroll = isCanScroll;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		return isCanScroll && super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		return isCanScroll && super.onTouchEvent(ev);
	}

}
