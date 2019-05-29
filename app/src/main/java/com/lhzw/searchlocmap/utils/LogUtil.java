package com.lhzw.searchlocmap.utils;

import com.orhanobut.logger.Logger;

/**
 * Created by hecuncun on 2018/3/26
 */

public class LogUtil {
    private static boolean isDebug = true;//定义个打印日志的标记，不需要时改为false,关闭整个应用的打印


    /**
     * 错误
     */
    public static void e(String msg) {
        if (isDebug) {
            Logger.e(msg);
        }
    }

    /**
     * 调试
     */
    public static void d(String msg) {
        if (isDebug) {
            Logger.d(msg);
        }
    }

    /**
     * 信息
     */
    public static void i(String msg) {
        if (isDebug) {
            Logger.i(msg);
        }
    }
}
