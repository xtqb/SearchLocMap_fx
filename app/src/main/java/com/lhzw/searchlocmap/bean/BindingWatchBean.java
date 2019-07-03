package com.lhzw.searchlocmap.bean;

import java.util.List;

/**
 * Created by hecuncun on 2019/7/3
 */
public class BindingWatchBean {


    /**
     * code : 0
     * message : ok
     * data : [{"deviceNumber":"0529000002","deviceName":"","deviceType":"2","lng":"","lat":"","positionTime":"","createTime":"","bound":true,"deleted":false}]
     */

    private int code;
    private String message;
    private List<DataBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * deviceNumber : 0529000002
         * deviceName :
         * deviceType : 2
         * lng :
         * lat :
         * positionTime :
         * createTime :
         * bound : true
         * deleted : false
         */

        private String deviceNumber;
        private String deviceName;
        private String deviceType;
        private String lng;
        private String lat;
        private String positionTime;
        private String createTime;
        private boolean bound;
        private boolean deleted;

        public String getDeviceNumber() {
            return deviceNumber;
        }

        public void setDeviceNumber(String deviceNumber) {
            this.deviceNumber = deviceNumber;
        }

        public String getDeviceName() {
            return deviceName;
        }

        public void setDeviceName(String deviceName) {
            this.deviceName = deviceName;
        }

        public String getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(String deviceType) {
            this.deviceType = deviceType;
        }

        public String getLng() {
            return lng;
        }

        public void setLng(String lng) {
            this.lng = lng;
        }

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public String getPositionTime() {
            return positionTime;
        }

        public void setPositionTime(String positionTime) {
            this.positionTime = positionTime;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public boolean isBound() {
            return bound;
        }

        public void setBound(boolean bound) {
            this.bound = bound;
        }

        public boolean isDeleted() {
            return deleted;
        }

        public void setDeleted(boolean deleted) {
            this.deleted = deleted;
        }
    }
}
