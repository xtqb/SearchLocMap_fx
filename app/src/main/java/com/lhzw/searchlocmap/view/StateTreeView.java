package com.lhzw.searchlocmap.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.lhzw.searchlocmap.R;

/**
 * Created by xtqb on 2019/4/9.
 */
public class StateTreeView extends View{
    private float with;
    private float lineW;
    private float lineH_0;
    private float lineH_1;
    private int searchState;
    private int sendState;
    private int completeState;
    private final int STATE_0 = 0;
    private final int STATE_1 = 1;
    private final int STATE_2 = 2;
    private final int STATE_3 = 3;
    private Paint lineSuceess;
    private Paint lineFail;
    private Bitmap searchFail;
    private Bitmap searchBeing;
    private Bitmap searchSuccess;
    private Bitmap sendFail;
    private Bitmap sendBeing;
    private Bitmap sendSuccess;
    private Bitmap uploadFail;
    private Bitmap uploadBeing;
    private Bitmap uploadSuccess;
    private Paint textPaintUp;
    private Paint textPaintDown;
    private Rect rect;
    private String searchTimeUp;
    private String searchTimeDown;
    private String sendTimeUp;
    private String sendTimeDown;
    private String uploadTimeUp;
    private String uploadTimeDown;
    private String searchNote_being;
    private String searchNote_success;
    private Paint textPaint_success;
    private Paint textPaint_fail;
    private Paint textPaint_fail1;
    private Paint textPaint_being;
    private String search_data;
    private String send_data;
    private String upload_data;
    private Paint textPaint_def;
    private String upload_faile;
    private String upload_being;
    private String upload_success;
    private float centre;
    private float radius;
    private Paint circlePaint;
    private String num;
    private Bitmap searchDef;
    private Bitmap sendDef;
    private Bitmap uploadDef;
    private String searchNote_faile;
    private String send_success;
    private String send_being;
    private String send_fail;

    private RectF oval;
    private Paint arcP;
    private Paint circelP;
    private int startAngle;
    private int sweepAngle;
    private int pRadius;


    public StateTreeView(Context context) {
        super(context);
        init();
    }

    public StateTreeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StateTreeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public StateTreeView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        textPaintUp = new Paint();
        textPaintUp.setColor(getResources().getColor(R.color.text_fail));
        textPaintUp.setTextSize(36);
        textPaintDown = new Paint();
        textPaintDown.setColor(getResources().getColor(R.color.line_fail));
        textPaintDown.setTextSize(30);
        textPaint_def = new Paint();
        textPaint_def.setColor(getResources().getColor(R.color.line_fail));
        textPaint_def.setTextSize(48);
        textPaint_def.setTypeface(Typeface.DEFAULT_BOLD);
        textPaint_fail1 = new Paint();
        textPaint_fail1.setColor(getResources().getColor(R.color.text_success));
        textPaint_fail1.setTextSize(48);
        textPaint_fail1.setTypeface(Typeface.DEFAULT_BOLD);
        textPaint_being = new Paint();
        textPaint_being.setColor(getResources().getColor(R.color.text_being));
        textPaint_being.setTextSize(48);
        textPaint_being.setTypeface(Typeface.DEFAULT_BOLD);
        textPaint_success = new Paint();
        textPaint_success.setColor(getResources().getColor(R.color.text_fail));
        textPaint_success.setTextSize(48);
        textPaint_success.setTypeface(Typeface.DEFAULT_BOLD);
        rect = new Rect();
        lineSuceess = new Paint();
        lineSuceess.setColor(getResources().getColor(R.color.line_success));
        lineFail = new Paint();
        lineFail.setColor(getResources().getColor(R.color.line_fail));

        circlePaint = new Paint();
//        给画笔设置颜色
        circlePaint.setColor(getResources().getColor(R.color.line_success));
//        设置画笔属性
        circlePaint.setStyle(Paint.Style.FILL);//画笔属性是实心圆
//        circlePaint.setStyle(Paint.Style.STROKE);//画笔属性是空心圆
//        circlePaint.setStrokeWidth(8);//设置画笔粗细

        circelP = new Paint();
        circelP.setColor(getResources().getColor(R.color.bg_progressbar));
        circelP.setFlags(Paint.ANTI_ALIAS_FLAG);
        circelP.setStyle(Paint.Style.STROKE);
        circelP.setStrokeWidth(10);
        pRadius = 25;
        arcP = new Paint();
        startAngle = 1;
        SweepGradient lg=new SweepGradient(pRadius/2, pRadius/2, getResources().getColor(R.color.progressbar), getResources().getColor(R.color.progressbar));
        arcP.setShader(lg);
        arcP.setColor(getResources().getColor(R.color.progressbar));
        arcP.setFlags(Paint.ANTI_ALIAS_FLAG);
        arcP.setStyle(Paint.Style.STROKE);
        arcP.setStrokeWidth(10);
        oval = new RectF();

        lineW = 6;
        lineH_0 = 70;
        lineH_1 = 90;
        radius = 10;
        searchState = STATE_0;
        sendState = STATE_0;
        completeState = STATE_0;
        num = "1";
        sweepAngle = 120;

        searchDef = BitmapFactory.decodeResource(getResources(), R.drawable.icon_search_default);
        searchBeing = BitmapFactory.decodeResource(getResources(), R.drawable.icon_search_being);
        searchSuccess = BitmapFactory.decodeResource(getResources(), R.drawable.icon_search_success);
        searchFail = BitmapFactory.decodeResource(getResources(), R.drawable.icon_search_fail);

        sendDef = BitmapFactory.decodeResource(getResources(), R.drawable.icon_send_default);
        sendBeing = BitmapFactory.decodeResource(getResources(), R.drawable.icon_send_being);
        sendSuccess = BitmapFactory.decodeResource(getResources(), R.drawable.icon_send_success);
        sendFail = BitmapFactory.decodeResource(getResources(), R.drawable.icon_send_fail);

        uploadDef = BitmapFactory.decodeResource(getResources(), R.drawable.icon_upload_default);
        uploadBeing = BitmapFactory.decodeResource(getResources(), R.drawable.icon_upload_being);
        uploadSuccess = BitmapFactory.decodeResource(getResources(), R.drawable.icon_upload_success);
        uploadFail = BitmapFactory.decodeResource(getResources(), R.drawable.icon_upload_fail);

        searchTimeUp = "12月21";
        searchTimeDown = "21:40";

        sendTimeUp = "12月21";
        sendTimeDown = "21:40";

        uploadTimeUp = "12月21";
        uploadTimeDown = "21:40";

        searchNote_being = getResources().getString(R.string.search_being);
        searchNote_faile = getResources().getString(R.string.search_fail);
        searchNote_success = getResources().getString(R.string.search_already);

        search_data = getResources().getString(R.string.search_data);
        send_data = getResources().getString(R.string.send_data);
        upload_data = getResources().getString(R.string.upload_data);

        upload_faile = getResources().getString(R.string.upload_faile);
        upload_being = getResources().getString(R.string.upload_being);
        upload_success = getResources().getString(R.string.upload_success_1);

        send_success = getResources().getString(R.string.send_success);
        send_being = getResources().getString(R.string.send_being);
        send_fail = getResources().getString(R.string.send_fail);
        post(animator);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        with = getWidth();
        centre = with * 0.6f;
        canvas.drawCircle(centre/2, radius, radius,circlePaint);
        drawSearch(canvas);
        drawSend(canvas);
        drawComplete(canvas);
    }

    private void drawProgessBar(Canvas canvas, float x, float y){
        canvas.drawCircle(x, y, pRadius, circelP);
        oval.set(x - pRadius, y - pRadius, x + pRadius, y + pRadius);
        canvas.drawArc(oval,startAngle,sweepAngle,false, arcP);
    }

    private void drawSearch(Canvas canvas) {
        switch (searchState) {
            case STATE_0:
                canvas.drawRect((centre  - lineW)/2, 0, (centre + lineW)/2, lineH_0, lineSuceess);
                canvas.drawBitmap(searchDef, centre/2 - searchFail.getWidth()/2, lineH_0, null);
                canvas.drawText(search_data, centre/2 + searchFail.getWidth()/2 + 16 , lineH_0 + 36  + 8, textPaint_def);
                break;
            case STATE_1:
                canvas.drawRect((centre - lineW)/2, 0, (centre + lineW)/2, lineH_0, lineSuceess);
                canvas.drawBitmap(searchBeing, centre/2 - searchBeing.getWidth()/2, lineH_0, null);
                canvas.drawText(searchNote_being.replace("@", num), centre/2 + searchBeing.getWidth()/2 + 16 , lineH_0 + 36  + 8, textPaint_being);
                drawProgessBar(canvas, 800, 100);
                break;
            case STATE_2:
                canvas.drawRect((centre - lineW)/2, 0, (centre + lineW)/2, lineH_0, lineSuceess);
                canvas.drawBitmap(searchSuccess, centre/2 - searchSuccess.getWidth()/2, lineH_0, null);
                canvas.drawText(searchNote_success, centre/2 + searchSuccess.getWidth()/2 + 16 , lineH_0 + 36  + 8, textPaint_success);
                break;
            case STATE_3:
                canvas.drawRect((centre  - lineW)/2, 0, (centre + lineW)/2, lineH_0, lineSuceess);
                canvas.drawBitmap(searchFail, centre/2 - searchFail.getWidth()/2, lineH_0, null);
                canvas.drawText(searchNote_faile, centre/2 + searchSuccess.getWidth()/2 + 16 , lineH_0 + 36  + 8, textPaint_fail1);
                textPaint_fail1.getTextBounds(searchNote_faile, 0, searchNote_faile.length(), rect);
                break;
        }

        textPaintUp.getTextBounds(searchTimeUp, 0, searchTimeUp.length(), rect);
        canvas.drawText(searchTimeUp, centre/2 - rect.width() - searchFail.getWidth()/2 - 16, lineH_0 + 36, textPaintUp);
        textPaintDown.getTextBounds(searchTimeDown, 0, searchTimeDown.length(), rect);
        canvas.drawText(searchTimeDown, centre/2 - rect.width() - searchFail.getWidth()/2 - 16 , lineH_0 + 36 + 30 + 8, textPaintDown);
    }

    private void drawSend(Canvas canvas) {
        switch (sendState) {
            case STATE_0:
                canvas.drawRect((centre - lineW)/2, lineH_0 + searchFail.getHeight(), (centre + lineW)/2, lineH_0 + lineH_1 + searchFail.getHeight(), lineFail);
                canvas.drawBitmap(sendDef, centre/2 - sendFail.getWidth()/2, lineH_0 + lineH_1 + searchFail.getHeight(), null);
                canvas.drawText(send_data, centre/2 + searchFail.getWidth()/2 + 16 , lineH_0 + lineH_1 + searchFail.getHeight() + 36  + 8, textPaint_def);
                break;
            case STATE_1:
                canvas.drawRect((centre - lineW)/2, lineH_0 + searchFail.getHeight(), (centre + lineW)/2, lineH_0 + lineH_1 + searchFail.getHeight(), lineSuceess);
                canvas.drawBitmap(sendBeing, centre/2 - sendBeing.getWidth()/2, lineH_0 + lineH_1 + searchFail.getHeight(), null);
                canvas.drawText(send_being, centre/2 + searchFail.getWidth()/2 + 16 , lineH_0 + lineH_1 + searchFail.getHeight() + 36  + 8, textPaint_being);
                drawProgessBar(canvas, 576, 236);
                break;
            case STATE_2:
                canvas.drawRect((centre - lineW)/2, lineH_0 + searchFail.getHeight(), (centre + lineW)/2, lineH_0 + lineH_1 + searchFail.getHeight(), lineSuceess);
                canvas.drawBitmap(sendSuccess, centre/2 - sendSuccess.getWidth()/2, lineH_0 + lineH_1 + searchFail.getHeight(), null);
                canvas.drawText(send_success, centre/2 + searchFail.getWidth()/2 + 16 , lineH_0 + lineH_1 + searchFail.getHeight() + 36  + 8, textPaint_success);
                break;
            case STATE_3:
                canvas.drawRect((centre - lineW)/2, lineH_0 + searchFail.getHeight(), (centre + lineW)/2, lineH_0 + lineH_1 + searchFail.getHeight(), lineSuceess);
                canvas.drawBitmap(sendFail, centre/2 - sendSuccess.getWidth()/2, lineH_0 + lineH_1 + searchFail.getHeight(), null);
                canvas.drawText(send_fail, centre/2 + searchFail.getWidth()/2 + 16 , lineH_0 + lineH_1 + searchFail.getHeight() + 36  + 8, textPaint_fail1);
                break;
        }
        textPaintUp.getTextBounds(sendTimeUp, 0, sendTimeUp.length(), rect);
        canvas.drawText(sendTimeUp, centre/2 - rect.width() - searchFail.getWidth()/2 - 16, lineH_0 + lineH_1 + searchFail.getHeight() +  36, textPaintUp);
        textPaintDown.getTextBounds(sendTimeDown, 0, sendTimeDown.length(), rect);
        canvas.drawText(sendTimeDown, centre/2 - rect.width() - searchFail.getWidth()/2 - 16 , lineH_0 + lineH_1 + searchFail.getHeight() + 36 + 30 + 8, textPaintDown);
    }

    private void drawComplete(Canvas canvas) {
        switch (completeState) {
            case STATE_0:
                canvas.drawRect((centre - lineW)/2, lineH_0 + lineH_1 + 2 * searchFail.getHeight(), (centre + lineW)/2, lineH_0 + 2 * (lineH_1 + searchFail.getHeight()), lineFail);
                canvas.drawBitmap(uploadDef, centre/2 - sendFail.getWidth()/2, lineH_0 + 2 * (lineH_1 + searchFail.getHeight()), null);
                canvas.drawText(upload_data, centre/2 + searchFail.getWidth()/2 + 16 , lineH_0 + 2 * (lineH_1 + searchFail.getHeight()) + 36  + 8, textPaint_def);
                break;
            case STATE_1:
                canvas.drawRect((centre - lineW)/2, lineH_0 + lineH_1 + 2 * searchFail.getHeight(), (centre + lineW)/2, lineH_0 + 2 * (lineH_1 + searchFail.getHeight()), lineSuceess);
                canvas.drawBitmap(uploadBeing, centre/2 - sendFail.getWidth()/2, lineH_0 + 2 * (lineH_1 + searchFail.getHeight()), null);
                canvas.drawText(upload_being, centre/2 + searchFail.getWidth()/2 + 16 , lineH_0 + 2 * (lineH_1 + searchFail.getHeight()) + 36  + 8, textPaint_being);
                drawProgessBar(canvas, 530, 372);
                break;
            case STATE_2:
                canvas.drawRect((centre - lineW)/2, lineH_0 + + lineH_1 + 2 * searchFail.getHeight(), (centre + lineW)/2, lineH_0 + 2 * (lineH_1 + searchFail.getHeight()), lineSuceess);
                canvas.drawBitmap(uploadSuccess, centre/2 - sendFail.getWidth()/2, lineH_0 + 2 * (lineH_1 + searchFail.getHeight()), null);
                canvas.drawText(upload_success, centre/2 + searchFail.getWidth()/2 + 16 , lineH_0 + 2 * (lineH_1 + searchFail.getHeight()) + 36  + 8, textPaint_success);
                break;
            case STATE_3:
                canvas.drawRect((centre - lineW)/2, lineH_0 + + lineH_1 + 2 * searchFail.getHeight(), (centre + lineW)/2, lineH_0 + 2 * (lineH_1 + searchFail.getHeight()), lineSuceess);
                canvas.drawBitmap(uploadFail, centre/2 - sendFail.getWidth()/2, lineH_0 + 2 * (lineH_1 + searchFail.getHeight()), null);
                canvas.drawText(upload_faile, centre/2 + searchFail.getWidth()/2 + 16 , lineH_0 + 2 * (lineH_1 + searchFail.getHeight()) + 36  + 8, textPaint_fail1);
                break;
        }
        textPaintUp.getTextBounds(uploadTimeUp, 0, uploadTimeUp.length(), rect);
        canvas.drawText(uploadTimeUp, centre/2 - rect.width() - searchFail.getWidth()/2 - 16, lineH_0 + 2 * (lineH_1 + searchFail.getHeight()) +  36, textPaintUp);
        textPaintDown.getTextBounds(uploadTimeDown, 0, uploadTimeDown.length(), rect);
        canvas.drawText(uploadTimeDown, centre/2 - rect.width() - searchFail.getWidth()/2 - 16 , lineH_0 + 2 * (lineH_1 + searchFail.getHeight()) + 36 + 30 + 8, textPaintDown);
    }

    public void refleshView(String[] content){
        if(content != null && content.length == 10) {
            searchTimeUp = content[0];
            searchTimeDown = content[1];
            sendTimeUp = content[2];
            sendTimeDown = content[3];
            uploadTimeUp = content[4];
            uploadTimeDown = content[5];
            searchState = Integer.valueOf(content[6]);
            num = content[7];
            sendState = Integer.valueOf(content[8]);
            completeState = Integer.valueOf(content[9]);
//            invalidate();
        }
    }


    private Runnable animator = new Runnable() {
        @Override
        public void run() {
            if (startAngle <= 360) {
                startAngle += 10;
            } else {
                startAngle = 1;
            }

            invalidate();
            postDelayed(this, 30);
        }
    };
}
