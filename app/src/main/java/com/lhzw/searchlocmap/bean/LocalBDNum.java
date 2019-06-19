package com.lhzw.searchlocmap.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 所有平台的北斗号信息
 */
@DatabaseTable(tableName = "LocalBDNum")
public class LocalBDNum {

    @DatabaseField(generatedId = true)
    private int _Id;

    @DatabaseField(columnName = "num")
    private String num;

    @DatabaseField(columnName = "send")
    private String send;

    @DatabaseField(columnName = "receive")
    private String receive;

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
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

    public LocalBDNum() {
    }

    public LocalBDNum(String num, String send, String receive) {
        this.num = num;
        this.send = send;
        this.receive = receive;
    }


}

