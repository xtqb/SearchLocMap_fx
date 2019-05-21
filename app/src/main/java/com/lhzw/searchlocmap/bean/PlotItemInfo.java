package com.lhzw.searchlocmap.bean;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "PlotItemInfo")
public class PlotItemInfo implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    @DatabaseField(generatedId = true)
    private int _Id;
    @DatabaseField(columnName = "plotName")
    private String plotName;
    @DatabaseField(columnName = "makerId")
    private int makerId;
    @DatabaseField(columnName = "time")
    private long time;
    @DatabaseField(columnName = "paths")
    private String paths;
    @DatabaseField(columnName = "data_type")
    private int data_type;
    @DatabaseField(columnName = "state")
    private int state;
    @DatabaseField(columnName = "tx_type")
    private int tx_type;
    @DatabaseField(columnName = "upload_state")
    private int upload_state;

    public PlotItemInfo() {
        super();
        // TODO Auto-generated constructor stub
    }

    public PlotItemInfo(String plotName, int makerId, long time, String paths, int data_type, int state, int tx_type, int upload_state) {
        super();
        this.plotName = plotName;
        this.makerId = makerId;
        this.time = time;
        this.paths = paths;
        this.data_type = data_type;
        this.state = state;
        this.tx_type = tx_type;
        this.upload_state = upload_state;
    }

    public String getPlotName() {
        return plotName;
    }

    public long getTime() {
        return time;
    }

    public String getPaths() {
        return paths;
    }

    public int getData_type() {
        return data_type;
    }

    public int getState() {
        return state;
    }

    public int getTx_type() {
        return tx_type;
    }

    public void setPlotName(String plotName) {
        this.plotName = plotName;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setPaths(String paths) {
        this.paths = paths;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setData_type(int data_type) {
        this.data_type = data_type;
    }

    public void setTx_type(int tx_type) {
        this.tx_type = tx_type;
    }

    public int getMakerId() {
        return makerId;
    }

    public void setMakerId(int makerId) {
        this.makerId = makerId;
    }

    public int getUpload_state() {
        return upload_state;
    }

    public void setUpload_state(int upload_state) {
        this.upload_state = upload_state;
    }
}
