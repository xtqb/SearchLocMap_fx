package com.lhzw.searchlocmap.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by xtqb on 2019/3/11.
 */
@DatabaseTable(tableName = "SyncFireLine")
public class SyncFireLine implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    @DatabaseField(columnName = "time")
    private long time;
    @DatabaseField(columnName = "tx_type")
    private int tx_type;
    @DatabaseField(columnName = "paths")
    private String paths;
    @DatabaseField(columnName = "num")
    private String num;

    public SyncFireLine() {
        super();
    }

    public SyncFireLine(long time, int tx_type, String paths, String num) {
        this.time = time;
        this.tx_type = tx_type;
        this.paths = paths;
        this.num = num;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getTx_type() {
        return tx_type;
    }

    public void setTx_type(int tx_type) {
        this.tx_type = tx_type;
    }

    public String getPaths() {
        return paths;
    }

    public void setPaths(String paths) {
        this.paths = paths;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }
}
