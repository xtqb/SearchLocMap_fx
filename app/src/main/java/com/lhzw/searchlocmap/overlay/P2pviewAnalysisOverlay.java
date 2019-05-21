package com.lhzw.searchlocmap.overlay;

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

import com.egt.gis.bean.P2PViewParam;
import com.egt.gis.bean.PointD;
import com.egt.gis.interfaces.Analyse;
import com.gtmap.api.IGeoPoint;
import com.gtmap.common.GT_GeoArithmetic;
import com.gtmap.util.GeoPoint;
import com.gtmap.views.MapView;
import com.gtmap.views.MapView.Projection;
import com.lhzw.searchlocmap.R;
import com.lhzw.searchlocmap.bean.SectionChartPoint;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class P2pviewAnalysisOverlay extends OverlayController{
	private Context context;
	private IGeoPoint start;
	private IGeoPoint end;
	private boolean analysize = true;
	private boolean largeDistance = false;
	private List<SectionChartPoint> points;
	
	public List<SectionChartPoint> getPoints() {
		return points;
	}
	
	public P2pviewAnalysisOverlay(Context ctx) {
		super(ctx);
		this.context = ctx;
	}

	public boolean isAnalysize() {
		return analysize;
	}

	public void setAnalysize(boolean analysize) {
		this.analysize = analysize;
	}

	public boolean isLargeDistance() {
		return largeDistance;
	}

	@Override
	public void clearOverlay() {
		start = null;
		end = null;
		if(points!=null){
			points.clear();
		}
	}

	@Override
	public void repealLastPoint() {
		start = null;
		end = null;
	}

	@Override
	public boolean isHasData() {
		if(start != null && end !=null){
			return true;
		}else{
			return false;
		}
	}

	@Override
	protected void draw(Canvas canvas, MapView mapView, boolean shadow) {
		if(shadow){
			return;
		}
		Paint paint = new Paint();
		paint.setStrokeWidth((float)4.0);
		paint.setColor(Color.YELLOW);
		paint.setAlpha(255);
		paint.setAntiAlias(true);
		Projection proj = mapView.getProjection();
		Point startPoint = null;
		Point endPoint = null;
		if(end != null){
			endPoint = proj.CGCS2000toPixels(end, null);
			//终点
			@SuppressLint("ResourceType")
			InputStream ioStreamEnd = context.getResources().openRawResource(R.drawable.check_true);
			Bitmap picEnd = BitmapFactory.decodeStream(ioStreamEnd);
			canvas.drawBitmap(picEnd, endPoint.x, endPoint.y-picEnd.getHeight(),paint);//画终点
			
		}
		if(start != null){
			startPoint = proj.CGCS2000toPixels(start, null);
			//起点
			@SuppressLint("ResourceType")
			InputStream ioStreamStart = context.getResources().openRawResource(R.drawable.check_true);
			Bitmap picStart = BitmapFactory.decodeStream(ioStreamStart);			
			canvas.drawBitmap(picStart, startPoint.x-picStart.getWidth()/2, startPoint.y-picStart.getHeight()/2,paint);//画起点
		}
		if(end != null && start != null){//均不为空则画线
			canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y, paint);
			double distance = GT_GeoArithmetic.ComputeDistanceOfTwoPoints((GeoPoint) start,(GeoPoint) end);
			if(distance>2*100000){//如果大于200千米则提示距离太长
				int lat = (start.getLatitudeE6()+end.getLatitudeE6())/2;
				int lon = (start.getLongitudeE6()+end.getLongitudeE6())/2;
				GeoPoint middlePoint = new GeoPoint(lat,lon);
				Point point = proj.CGCS2000toPixels(middlePoint, null);
				drawToast(canvas,point);
				largeDistance = true;//设置计算距离超出范围
				return;
			}
			largeDistance = false;
			if(analysize){
				drawAnalysize(canvas,mapView);
			}
		}
	}

	private void drawToast(Canvas canvas,Point point){
		Paint paint1 = new Paint();//画笔 用来画面积信息标注
		paint1.setStrokeWidth((float)3.0);
		paint1.setColor(Color.BLACK);
		paint1.setAlpha(255);
		paint1.setAntiAlias(true);
		paint1.setTextSize(30);

		String areaText = "该计算距离太大";
		int len = getTextWidth(paint1,areaText);

		Paint paint3 = new Paint();//画笔 用来画面积信息标注
		paint3.setStrokeWidth((float)3.0);
		paint3.setColor(Color.YELLOW);
		paint3.setAlpha(180);
		paint3.setAntiAlias(true);
		RectF rect = new RectF();
		int widthX = point.x;
		int heightY = point.y;
		rect.left = widthX - len/2 - 20;
		rect.right = widthX + len/2 + 20;
		rect.top = heightY - 25;
		rect.bottom = heightY + 25;
		canvas.drawRoundRect(rect, 10, 10, paint3);
		canvas.drawText(areaText, widthX-len/2, heightY+10, paint1);
	}

	private void drawAnalysize(Canvas canvas, MapView mapView) {
		PointD s = new PointD();
		s.x = start.getLongitudeE6()/1000000.0;
		s.y = start.getLatitudeE6()/1000000.0;
		PointD e = new PointD();
		e.x = end.getLongitudeE6()/1000000.0;
		e.y = end.getLatitudeE6()/1000000.0;
		//获取分析点前3位为最高点x,y, height
		P2PViewParam param = new P2PViewParam();
//		param.setD_observerHeight(2);
//		param.setD_targetheight(0);
		param.setPos_observer(s);
		param.setPos_target(e);
		double[] result = Analyse.p2pViewAnalysis(param, true);
		initResultPoints(result);
		if(result[0] == 0){//不可视
			GeoPoint shelter = new GeoPoint(result[2]/360000,result[1]/360000);
            String la = "N";
            String lon = "E";
            if(shelter.getLatitudeE6()<0){
                la="S";
            }
            if(shelter.getLongitudeE6()<0){
                lon="W";
            }
			String text = "高程值:"+result[3]+"米";
			String coordinate = "坐标:"+la+shelter.getLatitudeE6()/1000000.0+"°,"+lon+shelter.getLongitudeE6()/1000000.0+"°";
			drawPoint(canvas, mapView, shelter, text, coordinate);
			drawLine(canvas,mapView,shelter);
		}
		else{//可视
			drawLine(canvas,mapView);
		}
	}
	
	private void initResultPoints(double[] result) {
		points = new ArrayList<SectionChartPoint>();
		for(int i=0;i<(result.length-1)/3;i++){//从第1个点开始 第一个值位是否可视
			GeoPoint gp = new GeoPoint(result[i*3+2]/360000,result[i*3+1]/360000);
			double distance = GT_GeoArithmetic.ComputeDistanceOfTwoPoints((GeoPoint)start,gp);
			double d = formatShow(distance);
			double height = result[i*3+3];
			SectionChartPoint point = new SectionChartPoint(gp.getLatitudeE6()/1000000,gp.getLongitudeE6()/1000000,d,height);
			points.add(point);
		}
	}

	private double formatShow(double distance) {
		DecimalFormat df = new DecimalFormat("#.000");
		return Double.valueOf(df.format(distance/1000));
	}

	private void drawLine(Canvas canvas, MapView mapView) {
		Paint paint1 = new Paint();
		paint1.setStrokeWidth((float)3.0);
		paint1.setColor(Color.GREEN);
		paint1.setAlpha(255);
		paint1.setAntiAlias(true);
		Point startPoint = mapView.getProjection().CGCS2000toPixels(start, null);
		Point endPoint = mapView.getProjection().CGCS2000toPixels(end, null);
		canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y, paint1);
	}

	private void drawLine(Canvas canvas, MapView mapView, GeoPoint shelter) {
		Paint paint1 = new Paint();
		paint1.setStrokeWidth((float)3.0);
		paint1.setColor(Color.GREEN);
		paint1.setAlpha(255);
		paint1.setAntiAlias(true);
		Paint paint2 = new Paint();
		paint2.setStrokeWidth((float)3.0);
		paint2.setColor(Color.RED);
		paint2.setAlpha(255);
		paint2.setAntiAlias(true);
		Point startPoint = mapView.getProjection().CGCS2000toPixels(start, null);
		Point shelterPoint = mapView.getProjection().CGCS2000toPixels(shelter, null);
		Point endPoint = mapView.getProjection().CGCS2000toPixels(end, null);
		canvas.drawLine(startPoint.x, startPoint.y, shelterPoint.x, shelterPoint.y, paint1);
		canvas.drawLine(shelterPoint.x, shelterPoint.y, endPoint.x, endPoint.y, paint2);
	}

	private void drawPoint(Canvas canvas, MapView mapView,GeoPoint gp,String text, String coordinate) {
		Paint paint = new Paint();
		paint.setStrokeWidth((float)3.0);
		paint.setColor(Color.RED);
		paint.setAlpha(255);
		paint.setAntiAlias(true);
		Projection proj = mapView.getProjection();
		Point point = proj.CGCS2000toPixels(gp, null);
		
		@SuppressLint("ResourceType") InputStream ioStreamEnd = context.getResources().openRawResource(R.drawable.check_true);
		Bitmap picEnd = BitmapFactory.decodeStream(ioStreamEnd);
		canvas.drawBitmap(picEnd, point.x-picEnd.getWidth()/2, point.y-picEnd.getHeight(),paint);//画特殊点
		drawRectText(canvas,point,text,coordinate);
	}

	private void drawRectText(Canvas canvas, Point point, String text,
			String coordinate) {
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
		
		int len1 = getTextWidth(paint2, text); 
		int len2 = getTextWidth(paint2, coordinate);
		int len = Math.max(len1, len2);
		RectF rect = new RectF();
		rect.top = point.y - 90;
		rect.bottom = point.y;
		rect.left = point.x;
		rect.right = point.x + len + 40;
		canvas.drawRoundRect(rect, 10, 10, paint);
		canvas.drawText(text, point.x + 20, point.y - 60, paint2);
		canvas.drawText(coordinate, point.x + 20, point.y - 10, paint2);
		
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
	
	@Override
	public boolean onSingleTapUp(MotionEvent e, MapView mapView) {
		if(!open){
			return super.onSingleTapUp(e, mapView);
		}
		Projection proj = mapView.getProjection();
		Point mPoint = new Point();
		proj.fromScreenCoordsToMapPixels((int)e.getX(), (int)e.getY(), mPoint);
		IGeoPoint touchCoordinate = proj.CGCS2000fromPixels(mPoint.x, mPoint.y);
		if(start == null){
			start = touchCoordinate;
		}else if(end == null){
			end = touchCoordinate;
		}
		return super.onSingleTapUp(e, mapView);
	}

}
