package com.lhzw.searchlocmap.bean;

/**
 * Created by xtqb on 2019/4/10.
 */
public class DeviceInfo {
    private int deviceType;

    public DeviceInfo(int deviceType) {
        this.deviceType = deviceType;
    }

    public DeviceInfo() {
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }
}
