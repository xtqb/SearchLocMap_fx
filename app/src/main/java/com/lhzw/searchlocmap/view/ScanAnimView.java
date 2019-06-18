package com.lhzw.searchlocmap.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.SweepGradient;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.lhzw.searchlocmap.R;

public class ScanAnimView extends View {

    private static final float DEFAULT_CIRCLE_RADIUS = 16;
    private Paint mCirclePaint;
    private Paint mSectorPaint;
    private int mTotalWidth;
    private int mTotalHeight;
    private Matrix matrix;
    private float mRotateDegree = 0;
    private Paint mThirdPaint;
    private float mInnerCircleRadius;
    private Paint textPaint;

    public ScanAnimView(@NonNull Context context) {
        this(context, null);
    }

    public ScanAnimView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScanAnimView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        //init circle paint
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(getResources().getColor(R.color.white));
        textPaint.setTextSize(48);

        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setColor(getResources().getColor(R.color.green_dark));
        //init sector paint
        mSectorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSectorPaint.setStrokeWidth(6);
        mSectorPaint.setStyle(Paint.Style.STROKE);

        //最外层的paint
        mThirdPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mThirdPaint.setStyle(Paint.Style.STROKE);//只画线
        mThirdPaint.setStrokeWidth(3);
        mThirdPaint.setColor(getResources().getColor(R.color.colorPrimary));
        matrix = new Matrix();

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //int size = MeasureSpec.getSize(widthMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mTotalHeight = getMeasuredHeight();
        mTotalWidth = getMeasuredWidth();
        mInnerCircleRadius = mTotalWidth/2 - 20;
        canvas.drawCircle(mTotalWidth/2, mTotalHeight/2, mInnerCircleRadius, mCirclePaint);
        SweepGradient sweepGradient = new SweepGradient(mTotalWidth / 2, mTotalHeight / 2,
                new int[]{Color.TRANSPARENT, Color.TRANSPARENT, Color.parseColor("#aa1DF6FE")},
                new float[]{0f, 0.66f, 1f});

        SweepGradient sweepGradient2 = new SweepGradient(mTotalWidth / 2, mTotalHeight / 2,
                new int[]{Color.TRANSPARENT,Color.TRANSPARENT,Color.parseColor("#ffff3800"),},//颜色渐变从前到后
                new float[]{0f,0.78f, 1f});//渐变度

        mSectorPaint.setShader(sweepGradient);
        mThirdPaint.setShader(sweepGradient2);

        int sectorRadius = Math.min(mTotalHeight, mTotalWidth) / 2 - 4;
        int thirdRadius = Math.min(mTotalHeight, mTotalWidth) / 2-12;

        matrix.setRotate(mRotateDegree, mTotalWidth/2, mTotalHeight/2);
        canvas.concat(matrix);
        canvas.drawCircle(mTotalWidth/2, mTotalHeight/2, sectorRadius, mSectorPaint);
        canvas.drawCircle(mTotalWidth/2, mTotalHeight/2, thirdRadius, mThirdPaint);
        postDelayed(runnable, 50);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            mRotateDegree += 15;
            if(mRotateDegree == 360) {
                mRotateDegree = 0;
            }
            invalidate();
        }
    };

    public static int px2dip(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue/scale + 0.5f);
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
