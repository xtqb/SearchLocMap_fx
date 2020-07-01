package com.lhzw.searchlocmap.overlay;

import android.content.Context;

import com.gtmap.views.overlay.Overlay;
import com.lhzw.searchlocmap.MeasureOriginDistanceOverlay;

/**
 * Created by jiangyang on 2019/3/6.
 */
public class OverlayFactory {
    public static final int MEASUREDISTANCE = 0;
    public static final int MEASUREAREA = 1;
    public static final int MEASUREDEM = 2;
    public static final int P2PVIEWANALYSIS = 3;
    public static final int MEASUREORIGINDISTANCE = 4;
    private OverlayFactory(){}

    public static Overlay createOverlayInstance(int type,Context ctx){
        switch (type){
            case MEASUREDISTANCE:
                MeasureDistanceOverlay mdOverlay = new MeasureDistanceOverlay(ctx);
                return mdOverlay;
            case MEASUREAREA:
                MeasureAreaOverlay maOverlay = new MeasureAreaOverlay(ctx);
                return maOverlay;
            case MEASUREDEM:
                DEMPointOverlay demOverlay = new DEMPointOverlay(ctx);
                return demOverlay;
            case P2PVIEWANALYSIS:
                P2pviewAnalysisOverlay p2pOverlay = new P2pviewAnalysisOverlay(ctx);
                return p2pOverlay;
            case MEASUREORIGINDISTANCE:
                MeasureOriginDistanceOverlay modOverlay = new MeasureOriginDistanceOverlay(ctx);
                return modOverlay;
            default:
                return null;
        }
    }
}
