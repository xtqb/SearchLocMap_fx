package com.lhzw.searchlocmap.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;

import com.lhzw.searchlocmap.R;
import com.lhzw.searchlocmap.interfaces.OnHoriItemClickListener;

public class OnClickLinearLayout extends LinearLayout implements
        OnTouchListener {
    private OnHoriItemClickListener listener;
    private int pos;
    private int state;
    private long start;

    public OnClickLinearLayout(Context context, AttributeSet attrs,
                               int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        // TODO Auto-generated constructor stub
        state = -1;
        this.setOnTouchListener(this);
    }

    public OnClickLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        state = -1;
        this.setOnTouchListener(this);
    }

    public OnClickLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        state = -1;
        this.setOnTouchListener(this);
    }

    public OnClickLinearLayout(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        state = -1;
        this.setOnTouchListener(this);
    }

    public void setOnHoriItemClickListener(OnHoriItemClickListener listener) {
        this.listener = listener;
    }

    public OnHoriItemClickListener getOnItemtListener() {
        return listener;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // TODO Auto-generated method stub
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                long offset = System.currentTimeMillis() - start;
                if (listener != null && offset > 50 && offset < 130) {
                    listener.onItemClick(pos);
                    Log.e("Tag", "offset = " + offset);
                }
                setBackgroundResource(R.color.awhite);
                state = MotionEvent.ACTION_UP;
                break;
            case MotionEvent.ACTION_DOWN:
                if (state == MotionEvent.ACTION_DOWN) {
                    return false;
                }
                start = System.currentTimeMillis();
                setBackgroundResource(R.color.agray_background);
                state = MotionEvent.ACTION_DOWN;
                break;
        }
        return true;
    }

}
