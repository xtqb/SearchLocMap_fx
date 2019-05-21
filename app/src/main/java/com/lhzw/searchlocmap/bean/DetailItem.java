package com.lhzw.searchlocmap.bean;

/**
 * Created by xtqb on 2019/4/22.
 */
public class DetailItem {
    private String name;
    private String num;
    private String state;

    public DetailItem(String name, String num, String state) {
        this.name = name;
        this.num = num;
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public String getNum() {
        return num;
    }

    public String getState() {
        return state;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public void setState(String state) {
        this.state = state;
    }
}
