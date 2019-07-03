package com.lhzw.searchlocmap.bean;

import java.util.List;

/**
 * Created by hecuncun on 2019/7/3
 */
public class RequestFirePointBean {

    /**
     * cmd : receivefirepointindication
     * sessionidbd : nnnnnnn
     * registerid : XXX
     * handsetnumber : xxxxxx
     * lat : 44.111111
     * lng : 145.111111
     * time : 2018-10-29 15:22:30
     * firepointlist : [{"state":"small","time":"2019-01-17 14:45:30","lng":"115.111111","lat":"44.5555555"},{"state":"big","time":"2019-01-17 14:46:30","lng":"115.111111","lat":"44.5555555"}]
     */

    private String cmd;
    private String sessionidbd;
    private String registerid;
    private String handsetnumber;
    private double lat;

    public RequestFirePointBean(String cmd, String sessionidbd, String registerid, String handsetnumber, double lat, double lng, String time, List<FirepointlistBean> firepointlist) {
        this.cmd = cmd;
        this.sessionidbd = sessionidbd;
        this.registerid = registerid;
        this.handsetnumber = handsetnumber;
        this.lat = lat;
        this.lng = lng;
        this.time = time;
        this.firepointlist = firepointlist;
    }

    private double lng;
    private String time;
    private List<FirepointlistBean> firepointlist;

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getSessionidbd() {
        return sessionidbd;
    }

    public void setSessionidbd(String sessionidbd) {
        this.sessionidbd = sessionidbd;
    }

    public String getRegisterid() {
        return registerid;
    }

    public void setRegisterid(String registerid) {
        this.registerid = registerid;
    }

    public String getHandsetnumber() {
        return handsetnumber;
    }

    public void setHandsetnumber(String handsetnumber) {
        this.handsetnumber = handsetnumber;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<FirepointlistBean> getFirepointlist() {
        return firepointlist;
    }

    public void setFirepointlist(List<FirepointlistBean> firepointlist) {
        this.firepointlist = firepointlist;
    }

    public static class FirepointlistBean {

        public FirepointlistBean(String state, String time, String lng, String lat) {
            this.state = state;
            this.time = time;
            this.lng = lng;
            this.lat = lat;
        }

        /**
         * state : small
         * time : 2019-01-17 14:45:30
         * lng : 115.111111
         * lat : 44.5555555
         */


        private String state;
        private String time;
        private String lng;
        private String lat;

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
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
    }
}
