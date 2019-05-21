package com.lhzw.searchlocmap.overlay;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;

import com.gtmap.api.IGeoPoint;
import com.gtmap.common.GT_GeoArithmetic;
import com.gtmap.views.MapView;
import com.gtmap.views.overlay.Overlay;

import java.util.ArrayList;

/**
 * Created by jiangyang on 2019/3/6.
 */
public abstract class OverlayController extends Overlay {
    protected ArrayList<IGeoPoint> touchGps= new ArrayList<>();
    boolean open = false;
    public OverlayController(Context ctx) {
        super(ctx);
    }

    public void openOverlay(){
        this.open = true;
    }

    public void closeOverlay(){
        this.open = false;
    }
    //清除图层中的点
    public abstract void clearOverlay();
    //回撤上一步
    public abstract void repealLastPoint();
    //判断是否有数据
    public abstract boolean isHasData();
    //判断是否是自交叉多边形
    public boolean isCross(ArrayList<IGeoPoint> touchGps,MapView.Projection proj){
        boolean isCross = false;
        if(touchGps.size()<3){//三点不可能自交叉
            return isCross;
        }
        //先判断起点和终点连线与其他线是否相交
        Point linelpt1 = new Point();
        proj.CGCS2000toPixels(touchGps.get(0),linelpt1);
        Point linelpt2 = new Point();
        proj.CGCS2000toPixels(touchGps.get(touchGps.size()-1),linelpt2);
        for(int i=1;i<touchGps.size()-2;i++){
            Point line2pt1 = new Point();
            proj.CGCS2000toPixels(touchGps.get(i),line2pt1);
            Point line2pt2 = new Point();
            proj.CGCS2000toPixels(touchGps.get(i+1),line2pt2);
            isCross = GT_GeoArithmetic.is_line_interset(linelpt1,linelpt2,line2pt1,line2pt2);
            if(isCross){
                return isCross;
            }
        }
        //再判断终点与倒数第二个点的连线与其他线是否相交
        proj.CGCS2000toPixels(touchGps.get(touchGps.size()-1),linelpt1);
        proj.CGCS2000toPixels(touchGps.get(touchGps.size()-2),linelpt2);
        for(int i=0;i<touchGps.size()-3;i++){
            Point line2pt1 = new Point();
            proj.CGCS2000toPixels(touchGps.get(i),line2pt1);

            Point line2pt2 = new Point();
            proj.CGCS2000toPixels(touchGps.get(i+1),line2pt2);
            isCross = GT_GeoArithmetic.is_line_interset(linelpt1,linelpt2,line2pt1,line2pt2);
            if(isCross){
                return isCross;
            }
        }
        return isCross;
    }

}
