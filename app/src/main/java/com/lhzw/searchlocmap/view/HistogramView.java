package com.lhzw.searchlocmap.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.lhzw.searchlocmap.R;

/**
 * Created by xtqb on 2019/3/27.
 */
public class HistogramView extends View{

    private int hisH_1;
    private int hisH_2;
    private int hisH_3;
    private int hisH_4;
    private int hisW;

    public HistogramView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }
    public HistogramView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    public HistogramView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public HistogramView(Context context) {
        super(context);
        init();
    }

    private void init(){
        //初始化高度
        hisH_1 = 0;
        hisH_2 = 0;
        hisH_3 = 0;
        hisH_4 = 0;
        //初始化宽度
        hisW = 0;

        //背景
        Paint bgPaint = new Paint();
        bgPaint.setColor(getResources().getColor(R.color.his_bg));

        Paint textPaint = new Paint();
        textPaint.setColor(getResources().getColor(R.color.taobao_black));

        Paint hisPaint1 = new Paint();
        hisPaint1.setColor(getResources().getColor(R.color.his_paint));
    }

    @Override
    protected void onDraw(Canvas canvas) {



    }


}
