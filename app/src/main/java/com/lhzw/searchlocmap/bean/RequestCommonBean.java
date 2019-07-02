package com.lhzw.searchlocmap.bean;

import java.util.List;

public class RequestCommonBean {
    private String cmd;
    private String sessionidbd;
    private String registerid;
    private String handsetnumber;
    private double lat;
    private double lng;
    private String time;
    private List<WatchLocBean> watchloclist;

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
