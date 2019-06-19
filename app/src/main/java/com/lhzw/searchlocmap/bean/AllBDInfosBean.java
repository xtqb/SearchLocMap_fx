package com.lhzw.searchlocmap.bean;

import java.util.List;

/**
 * Created by hecuncun on 2019/6/17
 */
public class AllBDInfosBean {

    /**
     * code : 0
     * message : ok
     * data : [{"bdNumber":"0402081","send":"1","receive":"0"},{"bdNumber":"0402082","send":"1","receive":"0"},{"bdNumber":"0402083","send":"1","receive":"0"},{"bdNumber":"0402084","send":"1","receive":"0"},{"bdNumber":"0402085","send":"1","receive":"0"},{"bdNumber":"0402096","send":"0","receive":"1"}]
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
         * bdNumber : 0402081
         * send : 1
         * receive : 0
         */

        private String bdNumber;
        private String send;
        private String receive;

        public String getBdNumber() {
            return bdNumber;
        }

        public void setBdNumber(String bdNumber) {
            this.bdNumber = bdNumber;
        }

        public String getSend() {
            return send;
        }

        public void setSend(String send) {
            this.send = send;
        }

        public String getReceive() {
            return receive;
        }

        public void setReceive(String receive) {
            this.receive = receive;
        }
    }
}
