package com.lhzw.searchlocmap.bean;

public class WatchLocBean {
    private String watchtype;          // 设备类型，watch手表
    private String watchid;            // 手表（信标）唯一编号
    private String personname;        // 使用者姓名
    private String identitycardid;   // 身份证号
    private String watchstatus;      // 状态，normal正常，sosing求救，sosstarted开始救援，sosfinished救援完成，offline离线
    private String sostype;           // 搜救类型，包含主动求救seekhelp、落水overboard、摔倒falldown、体征异常abnormalsigns watchstatus为正常时sostype为”“
    private double lat;               // 纬度
    private double lng;                // 经度
    private String time;                // 手表定位时间

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
