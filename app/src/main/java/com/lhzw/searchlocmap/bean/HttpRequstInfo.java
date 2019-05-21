package com.lhzw.searchlocmap.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by xtqb on 2019/3/29.
 */
public class HttpRequstInfo implements Serializable{
    private int id;
    private String loginName;
    private String realName;
    private String org;
    private int gender;
    private String orgName;
    private String xynumber;
    private String deviceNumbers;
    private DeviceInfo device;

    public HttpRequstInfo(int id, String loginName, String realName, String org, int gender, String orgName, String xynumber, String deviceNumbers, DeviceInfo device) {
        this.id = id;
        this.loginName = loginName;
        this.realName = realName;
        this.org = org;
        this.gender = gender;
        this.orgName = orgName;
        this.xynumber = xynumber;
        this.deviceNumbers = deviceNumbers;
        this.device = device;
    }

    public HttpRequstInfo() {
        super();
    }

    public int getId() {
        return id;
    }

    public String getLoginName() {
        return loginName;
    }

    public String getRealName() {
        return realName;
    }

    public String getOrg() {
        return org;
    }

    public int getGender() {
        return gender;
    }

    public String getOrgName() {
        return orgName;
    }

    public String getXynumber() {
        return xynumber;
    }

    public String getDeviceNumbers() {
        return deviceNumbers;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public void setXynumber(String xynumber) {
        this.xynumber = xynumber;
    }

    public void setDeviceNumbers(String deviceNumbers) {
        this.deviceNumbers = deviceNumbers;
    }

    public DeviceInfo getDevice() {
        return device;
    }

    public void setDevice(DeviceInfo device) {
        this.device = device;
    }
}
