package com.lhzw.searchlocmap.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by xtqb on 2019/3/29.
 */
@DatabaseTable(tableName = "HttpPersonInfo")
public class HttpPersonInfo implements Serializable{
    @DatabaseField(generatedId = true)
    private int _Id;
    @DatabaseField(columnName = "id")
    private int id;
    @DatabaseField(columnName = "loginName")
    private String loginName;
    @DatabaseField(columnName = "realName")
    private String realName;
    @DatabaseField(columnName = "org")
    private String org;
    @DatabaseField(columnName = "gender")
    private int gender;
    @DatabaseField(columnName = "orgName")
    private String orgName;
    @DatabaseField(columnName = "xynumber")
    private String xynumber;
    @DatabaseField(columnName = "deviceNumbers")
    private String deviceNumbers;
    @DatabaseField(columnName = "deviceType")
    private int deviceType;


    public HttpPersonInfo(int id, String loginName, String realName, String org, int gender, String orgName, String xynumber, String deviceNumbers, int deviceType) {
        this.id = id;
        this.loginName = loginName;
        this.realName = realName;
        this.org = org;
        this.gender = gender;
        this.orgName = orgName;
        this.xynumber = xynumber;
        this.deviceNumbers = deviceNumbers;
        this.deviceType = deviceType;
    }

    public HttpPersonInfo() {
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

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }
}
