package com.lhzw.searchlocmap.bean;

/**
 * Created by hecuncun on 2019/7/2
 */
public class NetResponseBean {

    /**
     * cmd :
     * sessionid :
     * status : OK
     * reason :
     * registerid :
     */

    private String cmd;
    private String sessionid;
    private String status;
    private String reason;
    private String registerid;

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getSessionid() {
        return sessionid;
    }

    public void setSessionid(String sessionid) {
        this.sessionid = sessionid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getRegisterid() {
        return registerid;
    }

    public void setRegisterid(String registerid) {
        this.registerid = registerid;
    }
}
