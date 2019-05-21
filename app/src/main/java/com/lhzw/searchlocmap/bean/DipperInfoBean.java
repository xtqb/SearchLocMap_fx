package com.lhzw.searchlocmap.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by xtqb on 2019/1/7.
 */
@DatabaseTable(tableName = "DipperInfoBean")
public class DipperInfoBean implements Serializable{
    private static final long serialVersionUID = 1L;

    @DatabaseField(generatedId = true)
    private int _Id;
    @DatabaseField(columnName = "num")
    private String num;
    @DatabaseField(columnName = "type")
    private int type;
    @DatabaseField(columnName = "name")
    private String name;
    @DatabaseField(columnName = "remark")
    private String remark;

    public DipperInfoBean() {
        super();
    }

    public DipperInfoBean(String num, int type, String name, String remark) {
        this.num = num;
        this.type = type;
        this.name = name;
        this.remark = remark;
    }

    public String getNum() {
        return num;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getRemark() {
        return remark;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setNum(String num) {
        this.num = num;
    }
}
