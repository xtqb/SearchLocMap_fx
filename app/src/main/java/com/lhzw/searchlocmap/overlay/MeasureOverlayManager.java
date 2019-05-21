package com.lhzw.searchlocmap.overlay;

import com.gtmap.views.overlay.OverlayManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jiangyang on 2019/3/6.
 */
public class MeasureOverlayManager {
    private static HashMap<Integer,OverlayController> overlays = new HashMap<>();
    private int type = -1;
    public OverlayController getOverlay(int type){
        return overlays.get(type);
    }

    public void setOverlays(int type) {
        this.type = type;
        for(Map.Entry<Integer,OverlayController>e:overlays.entrySet()){
            if(e.getKey() == type){
                e.getValue().openOverlay();
            }else {
                e.getValue().closeOverlay();
                e.getValue().clearOverlay();
            }
        }
    }

    public int getType() {
        return type;
    }

    public void addOverlay(int type, OverlayController overlay){
        overlays.put(type,overlay);
    }

    public void removeOverlays(OverlayManager manager){
        for(Map.Entry<Integer,OverlayController>e:overlays.entrySet()){
            manager.remove(e.getValue());
        }
        overlays.clear();
    }

    public void clearMeasureOverlays(){
        for(Map.Entry<Integer,OverlayController>e:overlays.entrySet()){
            e.getValue().clearOverlay();
        }
    }
}
