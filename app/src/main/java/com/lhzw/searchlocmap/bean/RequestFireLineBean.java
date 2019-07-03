package com.lhzw.searchlocmap.bean;

import java.util.List;

public class RequestFireLineBean {
    private String cmd;             //"cmd":"locationindication"
    private String sessionidbd;    // 手持机的会话ID，当前填“handsetsession”
    private String registerid;     // 项目ID，当前填“HANDSET”
    private String handsetnumber;  // 手持机本机北斗号
    private double lat;             // 手持机位置纬度
    private double lng;             // 手持机位置经度
    private String handsettime;     // 手持机位置记录时间
    private String time;             // 火线标绘时间
    private List<FirePoint> pointslist;  //手表位置列表

    public RequestFireLineBean(String cmd, String sessionidbd, String registerid, String handsetnumber, double lat, double lng, String handsettime, String time, List<FirePoint> pointslist) {
        this.cmd = cmd;
        this.sessionidbd = sessionidbd;
        this.registerid = registerid;
        this.handsetnumber = handsetnumber;
        this.lat = lat;
        this.lng = lng;
        this.handsettime = handsettime;
        this.time = time;
        this.pointslist = pointslist;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public void setSessionidbd(String sessionidbd) {
        this.sessionidbd = sessionidbd;
    }

    public void setRegisterid(String registerid) {
        this.registerid = registerid;
    }

    public void setHandsetnumber(String handsetnumber) {
        this.handsetnumber = handsetnumber;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public void setHandsettime(String handsettime) {
        this.handsettime = handsettime;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setPointslist(List<FirePoint> pointslist) {
        this.pointslist = pointslist;
    }

    public String getCmd() {
        return cmd;
    }

    public String getSessionidbd() {
        return sessionidbd;
    }

    public String getRegisterid() {
        return registerid;
    }

    public String getHandsetnumber() {
        return handsetnumber;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getHandsettime() {
        return handsettime;
    }

    public String getTime() {
        return time;
    }

    public List<FirePoint> getPointslist() {
        return pointslist;
    }
}
