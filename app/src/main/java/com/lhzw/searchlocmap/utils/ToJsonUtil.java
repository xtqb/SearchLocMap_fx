package com.lhzw.searchlocmap.utils;

import com.google.gson.Gson;


public class ToJsonUtil {

    private static ToJsonUtil mToJsonUtil;
    private static Gson gson;


    public static ToJsonUtil getInstance(){
        if (mToJsonUtil == null){
            synchronized (ToJsonUtil.class){
                if (mToJsonUtil == null){
                    mToJsonUtil = new ToJsonUtil();
                    gson = new Gson();
                }
            }
        }

        return mToJsonUtil;
    }

    private ToJsonUtil(){

    }

    public String toJson(Object object){
        String json = gson.toJson(object);
        LogUtil.d("上传的json===>"+json);
        return json;
    }
}
