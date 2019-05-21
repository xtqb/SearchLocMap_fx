package com.lhzw.searchlocmap.view;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by xtqb on 2019/1/2.
 */
public class MyDrawerLayout extends DrawerLayout implements View.OnTouchListener {
    public MyDrawerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MyDrawerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyDrawerLayout(Context context) {
        super(context);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isDrawerOpen(Gravity.LEFT)) {
                closeDrawer(Gravity.LEFT);
            } else if (isDrawerOpen(Gravity.RIGHT)) {
                closeDrawer(Gravity.RIGHT);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.e("Tag", "123");
        return false;
    }
}
