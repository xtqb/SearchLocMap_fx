package com.lhzw.searchlocmap.bean;

public class WatchLocBean {
    private String watchtype;
    private String watchid;
    private String personname;
    private String identitycardid;
    private String watchstatus;
    private String sostype;
    private double lat;
    private double lng;
    private String time;

    public WatchLocBean(String watchtype, String watchid, String personname, String identitycardid, String watchstatus, String sostype, double lat, double lng, String time) {
        this.watchtype = watchtype;
        this.watchid = watchid;
        this.personname = personname;
        this.identitycardid = identitycardid;
        this.watchstatus = watchstatus;
        this.sostype = sostype;
        this.lat = lat;
        this.lng = lng;
        this.time = time;
    }

    public void setWatchtype(String watchtype) {
        this.watchtype = watchtype;
    }

    public void setWatchid(String watchid) {
        this.watchid = watchid;
    }

    public void setPersonname(String personname) {
        this.personname = personname;
    }

    public void setIdentitycardid(String identitycardid) {
        this.identitycardid = identitycardid;
    }

    public void setWatchstatus(String watchstatus) {
        this.watchstatus = watchstatus;
    }

    public void setSostype(String sostype) {
        this.sostype = sostype;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getWatchtype() {
        return watchtype;
    }

    public String getWatchid() {
        return watchid;
    }

    public String getPersonname() {
        return personname;
    }

    public String getIdentitycardid() {
        return identitycardid;
    }

    public String getWatchstatus() {
        return watchstatus;
    }

    public String getSostype() {
        return sostype;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getTime() {
        return time;
    }
}
