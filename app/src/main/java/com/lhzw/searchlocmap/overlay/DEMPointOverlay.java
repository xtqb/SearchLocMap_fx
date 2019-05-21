package com.lhzw.searchlocmap.overlay;

import java.io.InputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.view.MotionEvent;

import com.egt.gis.bean.PointD;
import com.egt.gis.interfaces.Analyse;
import com.gtmap.api.IGeoPoint;
import com.gtmap.views.MapView;
import com.gtmap.views.MapView.Projection;
import com.lhzw.searchlocmap.R;

public class DEMPointOverlay extends OverlayController{
	private Context context;
	private List<IGeoPoint> points = new ArrayList<IGeoPoint>(); 
	public DEMPointOverlay(Context ctx) {
		super(ctx);
		this.context = ctx;
	}

	@Override
	public void clearOverlay() {
		if(points.size() != 0){
			points.clear();
		}
	}

	@Override
	public void repealLastPoint() {
		if(points.size() != 0){
			points.remove(points.size()-1);
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
		
		if(points.size() != 0){
			for(int i=0;i<points.size();i++){
				//将经纬度坐标转换为屏幕坐标
				Projection proj = mapView.getProjection();
				Point mPoint = new Point();
				proj.CGCS2000toPixels(points.get(i), mPoint);
				//画点
				drawPoint(canvas,mPoint);
				//画信息
				
				PointD pd = new PointD();
		    	pd.y = points.get(i).getLatitudeE6()/1000000.0;
		    	pd.x = points.get(i).getLongitudeE6()/1000000.0;
		    	//高程
		    	double height = Analyse.getPointHeight(pd, false);
				String ht="";
				if(height<0||height==10000){
					ht = "该处没有高程值";
				} else{
					ht = formatShow(height)+"米";
				}
		    	//坡向
		    	double aspect = Analyse.getPointAspect(pd, false);
				String apt="";
				if(aspect<0){
					apt = "该处没有坡向";
				}else{
					apt = formatShow(aspect);
				}
		    	//坡度
		    	double slope = Analyse.getPointSlope(pd, false);
		    	String slp = formatShow(slope);
		    	//两点通视
//		    	Analyse.p2pViewAnalysis(param, bIfDraw2Scrn)
		    	//面积坡向
//		    	Analyse.aspectAnalysis(pts, bIfDraw2Scrn)
		    	//剖面分析
//		    	Analyse.sectionAnalysis(start, end, count, bIfDraw2Scrn)
		    	//坡度分析
//		    	Analyse.slopeAnalysis(pts, bIfDraw2Scrn)
				String heightText = "高程 " + ht;
				String aspectText = "坡向 " + apt;
				String slopeText = "坡度 " + slp+"°";
				drawDEMMess(canvas,mPoint,heightText,aspectText,slopeText);
			}
		}
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e, MapView mapView) {
		if(!open){
			return super.onSingleTapUp(e, mapView);
		}
		Projection proj = mapView.getProjection();
		Point mPoint = new Point();
		proj.fromScreenCoordsToMapPixels((int)e.getX(), (int)e.getY(), mPoint);
		IGeoPoint touchCoordinate = proj.CGCS2000fromPixels(mPoint.x, mPoint.y);
		points.add(touchCoordinate);
		return super.onSingleTapUp(e, mapView);
	}

	private void drawPoint(Canvas canvas, Point mPoint) {
		//创建画笔
		Paint paint = new Paint();//画笔 
		paint.setStrokeWidth((float)6.0);
		paint.setColor(Color.BLUE);
		paint.setAlpha(255);
		paint.setAntiAlias(true);
		@SuppressLint("ResourceType") InputStream ioStream = context.getResources().openRawResource(R.drawable.check_true);
		Bitmap pic = BitmapFactory.decodeStream(ioStream);
		canvas.drawBitmap(pic, mPoint.x-pic.getWidth()/2, mPoint.y-pic.getHeight()/2,paint);//根据图片，坐标和画笔进行绘图
	}
	
	private void drawDEMMess(Canvas canvas, Point mPoint, String heightText, String aspectText, String slopeText) {
		Paint paint = new Paint();//画笔 用来画面积信息标注
		paint.setStrokeWidth((float)3.0);
		paint.setColor(Color.YELLOW);
		paint.setAlpha(180);
		paint.setAntiAlias(true);
		
		Paint paint2 = new Paint();//画笔 用来画面积信息标注
		paint2.setStrokeWidth((float)3.0);
		paint2.setColor(Color.BLACK);
		paint2.setAlpha(255);
		paint2.setAntiAlias(true);
		paint2.setTextSize(30);
		
		int heightLen = getTextWidth(paint2, heightText); 
		int aspectLen = getTextWidth(paint2, aspectText);
		int slopeLen = getTextWidth(paint2, slopeText);
		int max = Math.max(heightLen, aspectLen);
		max = Math.max(max, slopeLen);
		RectF rect = new RectF();
		rect.top = mPoint.y - 120;
		rect.bottom = mPoint.y;
		rect.left = mPoint.x;
		rect.right = mPoint.x + max + 40;
		canvas.drawRoundRect(rect, 10, 10, paint);
		canvas.drawText(heightText, mPoint.x + 10, mPoint.y - 85, paint2);
		canvas.drawText(aspectText, mPoint.x + 10, mPoint.y - 50, paint2);
		canvas.drawText(slopeText, mPoint.x + 10, mPoint.y - 15, paint2);
	}

	private int getTextWidth(Paint paint, String str) {
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
		return nf.format(d);
	}

}
