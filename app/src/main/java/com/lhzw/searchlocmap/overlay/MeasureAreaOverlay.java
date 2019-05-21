package com.lhzw.searchlocmap.overlay;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.view.MotionEvent;

import com.gtmap.api.IGeoPoint;
import com.gtmap.common.GT_GeoArithmetic;
import com.gtmap.util.GeoPoint;
import com.gtmap.views.MapView;
import com.lhzw.searchlocmap.R;

import java.io.InputStream;
import java.text.NumberFormat;

/**
 * Created by jiangyang on 2019/3/6.
 */
public class MeasureAreaOverlay extends OverlayController{
    private Context context;
    private double area;
    private int maxWidth;
    private int maxHeight;
    private int minWidth;
    private int minHeight;

    public MeasureAreaOverlay(Context ctx) {
        super(ctx);
        this.context = ctx;
    }

    @Override
    public void clearOverlay() {
        if(touchGps.size() != 0){
            touchGps.clear();
        }
    }

    @Override
    public void repealLastPoint() {
        if(touchGps.size() != 0){
            touchGps.remove(touchGps.size()-1);
        }
    }

    @Override
    public boolean isHasData() {
        return false;
    }

    @Override
    protected void draw(Canvas canvas, MapView mapView, boolean shadow) {
        if(shadow){
            return;
        }

        Paint paint1 = new Paint();//画笔 画空心多边形
        paint1.setStrokeWidth((float)4.0);
        paint1.setStyle(Paint.Style.STROKE);
        paint1.setColor(Color.BLUE);
        paint1.setAlpha(255);
        paint1.setAntiAlias(true);

        Paint paint2 = new Paint();//画笔 填充多边形
        paint2.setStrokeWidth((float)3.0);
        paint2.setColor(Color.GRAY);
        paint2.setAlpha(100);
        paint2.setAntiAlias(true);

        MapView.Projection proj = mapView.getProjection();
        if(touchGps.size() > 0){
            Path path = new Path();
            Point shapePoint = new Point();
            proj.CGCS2000toPixels(touchGps.get(0),shapePoint);
            path.moveTo(shapePoint.x,shapePoint.y);
            maxWidth = minWidth = shapePoint.x;//初始化最小值，不然最小值肯定为0
            maxHeight = minHeight = shapePoint.y;

            for(int i = 0;i<touchGps.size();i++){
                Point mPoint = new Point();
                //将经纬度转换成收集屏幕上坐标系的像素坐标，存储在Point对象中
                proj.toPixels(touchGps.get(i), mPoint);
                maxWidth = Math.max(maxWidth, mPoint.x);
                maxHeight = Math.max(maxHeight, mPoint.y);
                minWidth = Math.min(minWidth, mPoint.x);
                minHeight = Math.min(minHeight, mPoint.y);
                //得到Bitmap对象
                @SuppressLint("ResourceType")
                InputStream ioStream = context.getResources().openRawResource(R.drawable.check_true);
                Bitmap pic = BitmapFactory.decodeStream(ioStream);

                canvas.drawBitmap(pic, mPoint.x-pic.getWidth()/2, mPoint.y-pic.getHeight()/2,paint1);//画点
                if(i > 0){//如果大于两个点则画出闭合多边形
                    proj.CGCS2000toPixels(touchGps.get(i), shapePoint);
                    path.lineTo(shapePoint.x, shapePoint.y);
                }
            }

            path.close();

            canvas.drawPath(path, paint1);//画空心图形
            canvas.drawPath(path, paint2);//画实心图形
            //绘制面积信息
            if(touchGps.size()>2){
                //先判断多边形是否自相交
                boolean isCross = isCross(touchGps, proj);
                //计算面积
                area = GT_GeoArithmetic.ComputeSphereArea(touchGps.size(),(GeoPoint[]) touchGps.toArray(new GeoPoint[touchGps.size()]));
                Paint paint4 = new Paint();//画笔 用来画面积信息标注
                paint4.setStrokeWidth((float)3.0);
                paint4.setColor(Color.BLACK);
                paint4.setAlpha(255);
                paint4.setAntiAlias(true);
                paint4.setTextSize(20);

                String areaText = "面积："+formatShow(area);
                if(isCross){//自交叉则面积计算错误
                    areaText = "该区域不适合计算面积";
                }
                int len = getTextWidth(paint4,areaText);

                Paint paint3 = new Paint();//画笔 用来画面积信息标注
                paint3.setStrokeWidth((float)3.0);
                paint3.setColor(Color.YELLOW);
                paint3.setAlpha(180);
                paint3.setAntiAlias(true);
                RectF rect = new RectF();
                int widthX = (maxWidth+minWidth)/2;
                int heightY = (minHeight+maxHeight)/2;
                rect.left = widthX - len/2 - 20;
                rect.right = widthX + len/2 + 20;
                rect.top = heightY - 25;
                rect.bottom = heightY + 25;
                canvas.drawRoundRect(rect, 10, 10, paint3);
                canvas.drawText(areaText, widthX-len/2, heightY+10, paint4);
            }
        }

    }

    private int getTextWidth(Paint paint,String str){
        int iRet = 0;
        if(str != null && str.length()>0){
            int len = str.length();
            float[] widths = new float[len];
            paint.getTextWidths(str, widths);
            for(int j = 0; j<len;j++){
                iRet+=(int)Math.ceil(widths[j]);
            }
        }
        return iRet;
    }
    private String formatShow(double d){
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(3);
        if(d>1000){
            String str = nf.format(d/1000/1000)+"平方千米";
            return str;
        }
        return nf.format(d)+"平方米";
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e, MapView mapView) {
        if(!open){
            return super.onSingleTapUp(e, mapView);
        }
        MapView.Projection proj = mapView.getProjection();
        Point mPoint = new Point();
        proj.fromScreenCoordsToMapPixels((int)e.getX(), (int)e.getY(), mPoint);
        IGeoPoint touchCoordinate = proj.CGCS2000fromPixels(mPoint.x, mPoint.y);
        touchGps.add(touchCoordinate);
        return super.onSingleTapUp(e, mapView);
    }

}
