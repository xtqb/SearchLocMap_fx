package com.lhzw.searchlocmap.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "WatchLastLocTime")
public class WatchLastLocTime implements Serializable {
    private static final long serialVersionUID = 1L;
    @DatabaseField(generatedId = true)
    private int Id;
    @DatabaseField(columnName = "num")
    private String num;
    @DatabaseField(columnName = "locTime")
    private long locTime;

    public WatchLastLocTime() {
    }

    public WatchLastLocTime(String num, long locTime) {
        this.num = num;
        this.locTime = locTime;
    }

    public String getNum() {
        return num;
    }

    public long getLocTime() {
        return locTime;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public void setLocTime(long locTime) {
        this.locTime = locTime;
    }
}
