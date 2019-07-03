package com.lhzw.searchlocmap.bean;

public class FirePoint {
    private int index;  //该关键点所在列表的顺序号
    private double lat;             // 手持机位置纬度
    private double lng;             // 手持机位置经度

    public FirePoint(int index, double lat, double lng) {
        this.index = index;
        this.lat = lat;
        this.lng = lng;
    }

    public int getIndex() {
        return index;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
