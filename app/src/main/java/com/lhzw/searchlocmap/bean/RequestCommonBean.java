package com.lhzw.searchlocmap.bean;

import java.util.List;


public class RequestCommonBean {
    private String cmd;             //"cmd":"locationindication"
    private String sessionidbd;    // 手持机的会话ID，当前填“handsetsession”
    private String registerid;     // 项目ID，当前填“HANDSET”
    private String handsetnumber;  // 手持机本机北斗号
    private double lat;             // 手持机位置纬度
    private double lng;             // 手持机位置经度
    private String time;             // 手持机位置记录时间
    private List<WatchLocBean> watchloclist;  //手表位置列表

    public RequestCommonBean(String cmd, String sessionidbd, String registerid, String handsetnumber, double lat, double lng, String time, List<WatchLocBean> watchloclist) {
        this.cmd = cmd;
        this.sessionidbd = sessionidbd;
        this.registerid = registerid;
        this.handsetnumber = handsetnumber;
        this.lat = lat;
        this.lng = lng;
        this.time = time;
        this.watchloclist = watchloclist;
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

    public void setTime(String time) {
        this.time = time;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public void setWatchloclist(List<WatchLocBean> watchloclist) {
        this.watchloclist = watchloclist;
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

    public String getTime() {
        return time;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public List<WatchLocBean> getWatchloclist() {
        return watchloclist;
    }
}
