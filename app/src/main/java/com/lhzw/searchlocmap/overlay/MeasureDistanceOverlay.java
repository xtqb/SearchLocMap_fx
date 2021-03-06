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
public class MeasureDistanceOverlay extends OverlayController {
    private double distance;
    private double totalDistance;
    private Context context;

    public MeasureDistanceOverlay(Context ctx) {
        super(ctx);
        this.context = ctx;
    }

    @Override
    public void clearOverlay() {
        if (touchGps.size() != 0) {
            touchGps.clear();
        }
    }

    @Override
    public void repealLastPoint() {
        if (touchGps.size() != 0) {
            touchGps.remove(touchGps.size() - 1);
        }
    }

    @Override
    public boolean isHasData() {
        return false;
    }

    @Override
    protected void draw(Canvas canvas, MapView mapView, boolean shadow) {
        if (shadow) {
            return;
        }
        Paint paint = new Paint();
        paint.setStrokeWidth((float) 4.0);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAlpha(255);
        paint.setAntiAlias(true);

        if (touchGps.size() > 0) {
            Path path = new Path();
            MapView.Projection proj = mapView.getProjection();
            Point shapePoint = new Point();
            proj.CGCS2000toPixels(touchGps.get(0), shapePoint);
            path.moveTo(shapePoint.x, shapePoint.y);
            //画起点
            String text = "起点";
            drawRectText(canvas, shapePoint, text);
            for (int i = 0; i < touchGps.size(); i++) {
                Point mPoint = new Point();
                proj.CGCS2000toPixels(touchGps.get(i), mPoint);
                //得到bitmap对象
                @SuppressLint("ResourceType")
                InputStream ioStream = context.getResources().openRawResource(R.drawable.check_true);
                Bitmap pic = BitmapFactory.decodeStream(ioStream);
                canvas.drawBitmap(pic, mPoint.x - pic.getWidth() / 2, mPoint.y - pic.getHeight() / 2, paint);

                if (i > 0) {
                    proj.CGCS2000toPixels(touchGps.get(i), shapePoint);
                    path.lineTo(shapePoint.x, shapePoint.y);
                    distance = GT_GeoArithmetic.ComputeDistanceOfTwoPoints((GeoPoint) touchGps.get(i), (GeoPoint) touchGps.get(i - 1));
                    totalDistance = GT_GeoArithmetic.ComputeDistanceOfPoints(touchGps.toArray(new GeoPoint[touchGps.size()]), touchGps.size());//总距离
                    if (i == touchGps.size() - 1) {
                        String distanceText = "直线总长：" + formatShow(totalDistance);
                        drawRectText(canvas, shapePoint, distanceText);
                    } else {
                        String distanceText = "直线：" + formatShow(distance);
                        drawRectText(canvas, shapePoint, distanceText);
                    }
                }
            }
            canvas.drawPath(path, paint);
        }
    }

    private String formatShow(double distance) {
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(3);
        if (distance > 1000) {
            String str = nf.format(distance / 1000) + "千米";
            return str;
        }
        return nf.format(distance) + "米";
    }

    private void drawRectText(Canvas canvas, Point point, String text) {
        Paint paint = new Paint();
        paint.setStrokeWidth((float) 3.0);
        paint.setColor(Color.YELLOW);
        paint.setAlpha(180);
        paint.setAntiAlias(true);

        Paint paint2 = new Paint();
        paint2.setStrokeWidth((float) 3.0);
        paint2.setColor(Color.BLACK);
        paint2.setAlpha(255);
        paint2.setAntiAlias(true);
        paint2.setTextSize(20);
        int len = getTextWidth(text, paint2);

        if (text.equals("起点")) {//如果是起点，则矩形的高度变窄
            RectF rect = new RectF();
            rect.top = point.y - 35;
            rect.bottom = point.y;
            rect.left = point.x;
            rect.right = point.x + len + 40;
            canvas.drawRoundRect(rect, 10, 10, paint);
            canvas.drawText(text, point.x + 20, point.y - 10, paint2);
        } else {
            RectF rect = new RectF();
            rect.top = point.y - 40;
            rect.bottom = point.y;
            rect.left = point.x;
            rect.right = point.x + len + 40;
            canvas.drawRoundRect(rect, 10, 10, paint);
            canvas.drawText(text, point.x + 20, point.y - 10, paint2);
        }
    }

    public int getTextWidth(String text, Paint paint) {
        int iRet = 0;
        if (text != null && text.length() > 0) {
            int len = text.length();
            float[] widths = new float[len];
            paint.getTextWidths(text, widths);
            for (int j = 0; j < len; j++) {
                iRet += Math.ceil(widths[j]);
            }
        }
        return iRet;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e, MapView mapView) {
        if (!open) {
            return super.onSingleTapUp(e, mapView);
        }
        MapView.Projection proj = mapView.getProjection();
        Point mPoint = new Point();
        proj.fromScreenCoordsToMapPixels((int) e.getX(), (int) e.getY(), mPoint);
        IGeoPoint touchCoordinate = proj.CGCS2000fromPixels(mPoint.x, mPoint.y);
        touchGps.add(touchCoordinate);
        return super.onSingleTapUp(e, mapView);
    }
}
