package com.lhzw.searchlocmap.event;

/**
 * Created by hecuncun on 2019/5/13
 */
public class EventBusBean {

    //eventbus的 code值
    private int code;
    //携带信息int类型标记
    private int intTag;
    //携带信息的string类型标记
    private String stringTag;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getIntTag() {
        return intTag;
    }

    public void setIntTag(int intTag) {
        this.intTag = intTag;
    }

    public String getStringTag() {
        return stringTag;
    }

    public void setStringTag(String stringTag) {
        this.stringTag = stringTag;
    }
}
