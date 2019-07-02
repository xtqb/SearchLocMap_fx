package com.lhzw.searchlocmap.bean;

/**
 * Created by hecuncun on 2019/7/2
 *
 * 短消息上传json
 */
public class RequestMMSBean {

    public RequestMMSBean(String cmd, String sessionidbd, String registerid, String time, String handsetnumber, String smscontent) {
        this.cmd = cmd;
        this.sessionidbd = sessionidbd;
        this.registerid = registerid;
        this.time = time;
        this.handsetnumber = handsetnumber;
        this.smscontent = smscontent;
    }

    /**
     * cmd : receivesmsindication
     * sessionidbd : nnnnnnn
     * registerid : XXX
     * time : 2019-01-17 14:45:30
     * handsetnumber : xxxxxx
     * smscontent : 北斗短消息，中文不超过44个汉字，用UTF-8编码
     */

    private String cmd;
    private String sessionidbd;
    private String registerid;
    private String time;
    private String handsetnumber;
    private String smscontent;

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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getHandsetnumber() {
        return handsetnumber;
    }

    public void setHandsetnumber(String handsetnumber) {
        this.handsetnumber = handsetnumber;
    }

    public String getSmscontent() {
        return smscontent;
    }

    public void setSmscontent(String smscontent) {
        this.smscontent = smscontent;
    }
}
