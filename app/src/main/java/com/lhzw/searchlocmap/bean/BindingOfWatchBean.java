package com.lhzw.searchlocmap.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * 腕表绑定状态  服务器同步信息
 * Created by xtqb on 2019/7/3
 */
@DatabaseTable(tableName = "BindingOfWatchBean")
public class BindingOfWatchBean implements Serializable {
    @DatabaseField(generatedId = true)
    private int _Id;
    @DatabaseField(columnName = "deviceNumber")  // 手表编号
    private String deviceNumber;
    @DatabaseField(columnName = "loginName")      // 登录姓名
    private String loginName;
    @DatabaseField(columnName = "deviceType")     // 设备类型
    private String deviceType;
    @DatabaseField(columnName = "state")
    private int state;                             // 绑定状态 0 代表绑定 1 代表未绑定
    @DatabaseField(columnName = "bound")
    private boolean bound;                        // 是否绑定
    @DatabaseField(columnName = "org")            // 机构编号
    private int org;
    @DatabaseField(columnName = "realName")       // 真实姓名
    private String realName;
    @DatabaseField(columnName = "belongdeviceNumber")   // 手持mac地址
    private String belongdeviceNumber;

    public BindingOfWatchBean() {
        super();
    }

    public BindingOfWatchBean(String deviceNumber, String loginName, String deviceType, int state, int org, String realName, String belongdeviceNumber) {
        this.deviceNumber = deviceNumber;
        this.loginName = loginName;
        this.deviceType = deviceType;
        this.state = state;
        this.org = org;
        this.realName = realName;
        this.belongdeviceNumber = belongdeviceNumber;
    }

    public BindingOfWatchBean(String deviceNumber, String loginName, String deviceType, boolean bound, int org, String realName, String belongdeviceNumber) {
        this.deviceNumber = deviceNumber;
        this.loginName = loginName;
        this.deviceType = deviceType;
        this.bound = bound;
        this.org = org;
        this.realName = realName;
        this.belongdeviceNumber = belongdeviceNumber;
    }

    public String getDeviceNumber() {
        return deviceNumber;
    }

    public void setDeviceNumber(String deviceNumber) {
        this.deviceNumber = deviceNumber;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public boolean isBound() {
        return bound;
    }

    public void setBound(boolean bound) {
        this.bound = bound;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getOrg() {
        return org;
    }

    public void setOrg(int org) {
        this.org = org;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getBelongdeviceNumber() {
        return belongdeviceNumber;
    }

    public void setBelongdeviceNumber(String belongdeviceNumber) {
        this.belongdeviceNumber = belongdeviceNumber;
    }
}
