package com.lhzw.searchlocmap.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class LogWrite {
    private static LogWrite mLogWrite;
    private static FileWriter mWriter;
    private final static String LOG_DIR = Environment.getExternalStorageDirectory() + "/searchlogmap/";
    public final static SimpleDateFormat df = new SimpleDateFormat(
            "yy-MM-dd HH:mm:ss");
    private final static String TAG = "LOGWRITE";
    private static String fileName;
    private int counter;
    private final int MAXLOG = 1000;

    public synchronized static LogWrite open() {
        try {
            if (mLogWrite == null) {
                mLogWrite = new LogWrite();
            }
            File mFileDir = new File(LOG_DIR);
            if (!mFileDir.exists()) {
                mFileDir.mkdirs();
            }
            if (fileName == null) {
                fileName = LOG_DIR + df.format(System.currentTimeMillis())
                        + ".log";
                File mFile = new File(fileName);
                if (!mFile.exists()) {
                    mFile.createNewFile();
                }
            }
            mWriter = new FileWriter(fileName, true);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.e(TAG, "LogWriter failed : " + e.getMessage());
        }
        return mLogWrite;
    }

    public synchronized void writeLog(String str) {
        try {
            if (mWriter != null) {
                mWriter.write(counter + "  ");
                mWriter.write(str);
                mWriter.write("\n");
                mWriter.flush();
                close();
            }
            ++counter;
            if (counter == MAXLOG) {
                counter = 0;
                fileName = null;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.e(TAG, "write log failed : " + e.getMessage());
        }
    }

    private void close() {
        try {
            if(mWriter != null) {
                mWriter.close();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.e(TAG, "close Writer failed : " + e.getMessage());
        }
    }

}
