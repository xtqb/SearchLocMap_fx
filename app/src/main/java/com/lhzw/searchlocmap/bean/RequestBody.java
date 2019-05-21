package com.lhzw.searchlocmap.bean;

/**
 * Created by xtqb on 2019/3/29.
 */
public class RequestBody {
    private int code;
    private String message;
    private String data;

    public RequestBody(int code, String data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public RequestBody() {
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getData() {
        return data;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(String data) {
        this.data = data;
    }
}
