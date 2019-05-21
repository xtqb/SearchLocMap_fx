package com.lhzw.searchlocmap.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by xtqb on 2019/4/18.
 */
@DatabaseTable(tableName = "TreeStateBean")
public class TreeStateBean implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    @DatabaseField(generatedId = true)
    private int _Id;
    @DatabaseField(columnName = "time")
    private long time;
    @DatabaseField(columnName = "sMonth")
    private String sMonth;
    @DatabaseField(columnName = "sTime")
    private String sTime;
    @DatabaseField(columnName = "tMonth")
    private String tMonth;
    @DatabaseField(columnName = "tTime")
    private String tTime;
    @DatabaseField(columnName = "cMonth")
    private String cMonth;
    @DatabaseField(columnName = "cTime")
    private String cTime;
    @DatabaseField(columnName = "sState")
    private String sState;
    @DatabaseField(columnName = "num")
    private String num;
    @DatabaseField(columnName = "tState")
    private String tState;
    @DatabaseField(columnName = "cState")
    private String cState;
    @DatabaseField(columnName = "SEND_ID")
    private int SEND_ID;
    @DatabaseField(columnName = "total")
    private int total;
    @DatabaseField(columnName = "successNum")
    private int successNum;
    @DatabaseField(columnName = "failNum")
    private int failNum;
    @DatabaseField(columnName = "content")
    private String content;

    public TreeStateBean() {
    }

    public TreeStateBean(long time, String sMonth, String sTime, String tMonth, String tTime, String cMonth, String cTime, String sState, String num, String tState, String cState, int SEND_ID, int total, int successNum, int failNum, String content) {
        this.time = time;
        this.sMonth = sMonth;
        this.sTime = sTime;
        this.tMonth = tMonth;
        this.tTime = tTime;
        this.cMonth = cMonth;
        this.cTime = cTime;
        this.sState = sState;
        this.num = num;
        this.tState = tState;
        this.cState = cState;
        this.SEND_ID = SEND_ID;
        this.total = total;
        this.successNum = successNum;
        this.failNum = failNum;
        this.content = content;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getsMonth() {
        return sMonth;
    }

    public String getsTime() {
        return sTime;
    }

    public String gettMonth() {
        return tMonth;
    }

    public String gettTime() {
        return tTime;
    }

    public String getcMonth() {
        return cMonth;
    }

    public String getcTime() {
        return cTime;
    }

    public String getsState() {
        return sState;
    }

    public String getNum() {
        return num;
    }

    public String gettState() {
        return tState;
    }

    public String getcState() {
        return cState;
    }

    public int getSEND_ID() {
        return SEND_ID;
    }

    public void setsMonth(String sMonth) {
        this.sMonth = sMonth;
    }

    public void setsTime(String sTime) {
        this.sTime = sTime;
    }

    public void settMonth(String tMonth) {
        this.tMonth = tMonth;
    }

    public void settTime(String tTime) {
        this.tTime = tTime;
    }

    public void setcMonth(String cMonth) {
        this.cMonth = cMonth;
    }

    public void setcTime(String cTime) {
        this.cTime = cTime;
    }

    public void setsState(String sState) {
        this.sState = sState;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public void settState(String tState) {
        this.tState = tState;
    }

    public void setcState(String cState) {
        this.cState = cState;
    }

    public void setSEND_ID(int SEND_ID) {
        this.SEND_ID = SEND_ID;
    }

    public int getTotal() {
        return total;
    }

    public int getSuccessNum() {
        return successNum;
    }

    public int getFailNum() {
        return failNum;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void setSuccessNum(int successNum) {
        this.successNum = successNum;
    }

    public void setFailNum(int failNum) {
        this.failNum = failNum;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
