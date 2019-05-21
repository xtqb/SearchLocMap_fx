package com.lhzw.searchlocmap.bean;

/**
 * Created by xtqb on 2019/4/29.
 */
public class SectionChartPoint implements Comparable<SectionChartPoint>{
    //纬度
    private double latitude;
    //经度
    private double longitude;
    //距离起始点距离
    private double distance;
    //高程值
    private double height;

    public SectionChartPoint(double latitude, double longitude, double distance, double height) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
        this.height = height;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    @Override
    public int compareTo(SectionChartPoint another) {
        return (this.distance - another.getDistance()) > 0 ? 1 : -1;
    }
}
