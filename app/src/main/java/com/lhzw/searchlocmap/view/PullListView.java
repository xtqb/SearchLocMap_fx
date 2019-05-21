package com.lhzw.searchlocmap.view;

/**
 * Created by xtqb on 2019/4/12.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * 和下拉刷新配合的listview
 */
public class PullListView extends ListView {
    public PullListView(Context context) {
        super(context);
    }

    public PullListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        if (getFirstVisiblePosition() == 0 && getChildAt(0).getTop() == 0) {//到头部了
            getParent().requestDisallowInterceptTouchEvent(false);//放行
        } else {
            getParent().requestDisallowInterceptTouchEvent(true);//拦截
        }
        return super.onInterceptTouchEvent(ev);
    }
}
