package com.lhzw.searchlocmap.utils;

import android.widget.Toast;

import com.lhzw.searchlocmap.application.SearchLocMapApplication;

/**
 * Created by hecuncun on 2018/3/26
 */

public class ToastUtil {
    private static Toast toast;
    /**
     * 强大的可以连续弹的吐司
     * @param text
     */
    public static void showToast(String text){
        if(toast==null){
            //创建吐司对象
            toast = Toast.makeText(SearchLocMapApplication.getContext(), text, Toast.LENGTH_SHORT);
        }else {
            //说明吐司已经存在了，那么则只需要更改当前吐司的文字内容
            toast.setText(text);
        }
        //最后你再show
        toast.show();
    }
}
