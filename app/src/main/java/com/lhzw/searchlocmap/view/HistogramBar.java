package com.lhzw.searchlocmap.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.lhzw.searchlocmap.R;
/**
 * Created by xtqb on 2019/4/9.
 */
public class HistogramBar extends View {

    private int high;
    private int with;
    private int num;
    private Paint paint_L;
    private Paint paint_h;
    private float inteval;
    private float barW;
    private float[] values;
    private Paint paint_bg;

    public HistogramBar(Context context) {
        super(context);
        init();
    }

    private void init() {
        paint_h = new Paint();
        paint_h.setColor(getResources().getColor(R.color.green));
        paint_L = new Paint();
        paint_L.setColor(getResources().getColor(R.color.red));

        paint_bg = new Paint();
        paint_bg.setColor(getResources().getColor(R.color.hisbar_bg));

        values = new float[10];
        num = 10;
        inteval = 10;
        barW = 16;
    }

    public HistogramBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HistogramBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public HistogramBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        high = getHeight();
        with = getWidth();
        for (int pos = 0; pos < num; pos++) {
            canvas.drawRect((inteval + barW) * pos, 0, (inteval + barW) * pos + barW, high, paint_bg);
        }

        for (int pos = 0; pos < num; pos++) {
            if(values[pos] > 0.5) {
                canvas.drawRect((inteval + barW) * pos, high - high * values[pos], (inteval + barW) * pos + barW, high, paint_h);
            } else {
                canvas.drawRect((inteval + barW) * pos, high - high * values[pos], (inteval + barW) * pos + barW, high, paint_L);
            }

        }
    }

    public void refleshView(float[] values) {
        this.values = values;
        invalidate();
    }
}
