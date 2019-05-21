package com.lhzw.searchlocmap.utils;

/**
 * Created by xtqb on 2019/1/4.
 */
public class PlotCheckBox {
    private int ID;
    private long time;
    private boolean isCheck;
    public PlotCheckBox() {
        super();
        // TODO Auto-generated constructor stub
    }
    public PlotCheckBox(int ID, long time, boolean isCheck) {
        super();
        this.ID = ID;
        this.time = time;
        this.isCheck = isCheck;
    }

    public int getID() {
        return ID;
    }
    public void setID(int iD) {
        ID = iD;
    }
    public long getTime() {
        return time;
    }
    public void setTime(long time) {
        this.time = time;
    }
    public boolean isCheck() {
        return isCheck;
    }
    public void setCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }

}
