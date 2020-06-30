package com.lhzw.searchlocmap.utils;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.BDManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;

import com.gtmap.util.GeoPoint;
import com.lhzw.searchlocmap.R;
import com.lhzw.searchlocmap.application.SearchLocMapApplication;
import com.lhzw.searchlocmap.bean.FirePoint;
import com.lhzw.searchlocmap.bean.PlotItemInfo;
import com.lhzw.searchlocmap.bean.RequestFirePointBean;
import com.lhzw.searchlocmap.bean.WatchLocBean;
import com.lhzw.searchlocmap.constants.Constants;
import com.lhzw.searchlocmap.constants.SPConstants;
import com.lhzw.uploadmms.UploadInfoBean;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.NetworkInterface;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class BaseUtils {

    public static double pi = 3.1415926535897932384626;
    public static double ee = 0.00669342162296594323;
    public static double a = 6378245.0;
    private static DecimalFormat df = new DecimalFormat("######0.0");
    public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static boolean isStringEmpty(String str) {
        if (null == str || str.length() == 0 || "".equals(str.trim()))
            return true;
        else
            return false;
    }

    public static double transformLat(double x, double y) {
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y
                + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * pi) + 40.0 * Math.sin(y / 3.0 * pi)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * pi) + 320 * Math.sin(y * pi / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    public static double transformLon(double x, double y) {
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1
                * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * pi) + 40.0 * Math.sin(x / 3.0 * pi)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * pi) + 300.0 * Math.sin(x / 30.0
                * pi)) * 2.0 / 3.0;
        return ret;
    }

    // 全国的地图范围
    public static boolean outOfChina(double lat, double lon) {
        if (lon < 72.004 || lon > 137.8347)
            return true;
        if (lat < 0.8293 || lat > 55.8271)
            return true;
        return false;
    }

    // 北京市地图范围
    public static boolean outOfBeiJing(double lat, double lon) {
        if (lon < 115.7 || lon > 117.4)
            return true;
        if (lat < 39.4 || lat > 41.6)
            return true;
        return false;
    }

    /**
     * 84 to 火星坐标系 (GCJ-02) World Geodetic System ==> Mars Geodetic System
     *
     * @param lat
     * @param lon
     * @return
     */
    public static double[] gps84_To_Gcj02(double lat, double lon) {
        if (outOfChina(lat, lon)) {
            return new double[]{lat, lon};
        }

        double dLat = transformLat(lon - 105.0, lat - 35.0);
        double dLon = transformLon(lon - 105.0, lat - 35.0);
        double radLat = lat / 180.0 * pi;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);
        double mgLat = lat + dLat;
        double mgLon = lon + dLon;
        return new double[]{mgLat, mgLon};
    }

    public static String translateLonOrLat(String str) {
        double lonOrlat = 0;
        try {
            lonOrlat = Double.valueOf(str);
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            return "";
        }

        int zhengShu = (int) (lonOrlat / 1);
        double xiaoShu = lonOrlat - zhengShu;

        int fen = (int) (xiaoShu * 60);
        int miao = (int) (xiaoShu * 3600) - fen * 60;

        return (int) zhengShu + "°" + fen + "\'" + miao + "\"";
    }

    // 将byte 字节转化为字符串
    public static String traslation(byte[] tag_data) {
        String result = new String();
        for (int i = 0; i < tag_data.length; i++) {
            if ((tag_data[i] < 16) && (tag_data[i] >= 0)) {
                result += ("0" + Integer.toHexString(tag_data[i] & 0xFF));
            } else {
                result += Integer.toHexString(tag_data[i] & 0xFF);
            }
        }
        return result;
    }

    /**
     * 将获取的4字节定位信息转化为字符串
     */

    public static double ByteToStringForLocInfo(byte[] data) {
        int floatLocInfo = 0;

        if (data == null) {
            return floatLocInfo;
        }
        for (int i = 0; i < data.length; i++) {
            floatLocInfo <<= 8;
            floatLocInfo |= (data[i] & 0xff);
        }
        double doubleLocInfo = (floatLocInfo / 10000000.0);
        return doubleLocInfo;

    }

    /**
     * @param data String translate byte[]
     * @return
     */
    public static byte[] stringtoByteArr(String data) {
        char[] stringtochar = new char[data.length()];
        byte[] translat = new byte[data.length()];
        byte[] result = new byte[data.length() / 2];
        data.getChars(0, data.length(), stringtochar, 0);

        for (int j = 0; j < data.length(); j++) {
            if (stringtochar[j] >= '0' && stringtochar[j] <= '9') {
                translat[j] = (byte) (stringtochar[j] - '0');
            } else if (stringtochar[j] >= 'A' && stringtochar[j] <= 'F') {
                translat[j] = (byte) (stringtochar[j] - 'A' + 10);
            } else if (stringtochar[j] >= 'a' && stringtochar[j] <= 'f') {
                translat[j] = (byte) (stringtochar[j] - 'a' + 10);
            } else {
                return null;
            }
        }

        for (int i = 0; i < data.length() / 2; i++) {
            result[i] = (byte) ((translat[2 * i] << 4) + translat[2 * i + 1]);
        }
        return result;
    }

    public static byte[] getPerRegisterByteArr(String num) {
        byte[] byteArr = new byte[11];
        System.arraycopy(stringtoByteArr(num), 0, byteArr, 0, 5);
        return byteArr;

    }

    /**
     * BD短信发送
     *
     * @param comType :0 ~ 普通 1 ~ 紧急 0
     * @param comFont :0 ~ 代码 1 ~ 汉字 2 ~ 混发 2
     * @param des     ：收信方SIM卡地址 458893
     * @param body    ：信息发送内容 输出：$BDRev,46,250287,test 0xA4 body
     */
    public static void sendMMS(Context mContext, BDManager mBDManager,
                               int comType, int comFont, String des, String body) {
        String command = "$BDSnd";
        int len = 0;
        if (!isStringEmpty(body)) {
            try {
                byte[] data = body.getBytes("gb2312");
                len = data.length;
            } catch (UnsupportedEncodingException e) {
                Log.e("Tag", "sendMMS body.getBytes fail");
                e.printStackTrace();
            }
        }
        command += "," + String.valueOf(comType) + ","
                + String.valueOf(comFont) + "," + des + "," + len + "," + body;
        body = "01" + "" +
                mBDManager.sendBDCommand(command);

    }

    /**
     * 获取当前时间
     * <p/>
     * 返回 年、月、日、时、分、秒
     */
    public static String getDateStr(long time) {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String timeStr = sdf.format(time);
        return timeStr;
    }

    public static String formatTime(long time) {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd  HH:mm:ss");
        String timeStr = sdf.format(time);
        return timeStr;
    }

    public static String[] obtainDate(Context mContext) {
        String[] date = new String[2];
        try {
            StringBuilder builder = new StringBuilder();
            long time = System.currentTimeMillis();
            final SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
            date = new String[2];
            String[] str = sdf.format(time).split(" ");
            date[1] = str[1];
            String[] month = str[0].split("-");
            builder.append(month[0]);
            builder.append(mContext.getString(R.string.month));
            builder.append(month[1]);
            builder.append(mContext.getString(R.string.day));
            date[0] = builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    /*
     * 写文件
     */
    public static void writeFileStr(String fileName, String content) {
        try {
            // 打开一个随机访问文件流，按读写方式
            final RandomAccessFile randomFile = new RandomAccessFile(fileName,
                    "rw");
            // 文件长度，字节数
            long fileLength = randomFile.length();
            // 将写文件指针移到文件尾。
            randomFile.seek(fileLength);
            randomFile.writeBytes(content);
            randomFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * 查北斗卡号
     */
    public static String getDipperNum(Context mContext) {
        @SuppressLint("WrongConstant") BDManager bd = (BDManager) mContext
                .getSystemService(Context.BD_SERVICE);
        return bd.getBDCardNumber();
    }

    public static BDManager getDBManager(Context mContext) {
        //检查北斗开关
        if (Settings.Global.getInt(mContext.getContentResolver(),
                Settings.Global.BD_MODE_ON, 0) != 1) {// 北斗定位没开
            Toast.makeText(mContext, mContext.getString(R.string.dipper_switch_check_note), Toast.LENGTH_LONG).show();
            return null;
        }
        @SuppressLint("WrongConstant") final BDManager mBDManager = (BDManager) mContext.getSystemService(Context.BD_SERVICE);
        //检查北斗卡
        if (BaseUtils.isStringEmpty(mBDManager.getBDCardNumber())) {
            Toast.makeText(mContext, mContext.getString(R.string.dipper_card_check_note), Toast.LENGTH_LONG).show();
            return null;
        }
        return mBDManager;
    }
//	public static String mesureDistance(double latA, double lonA, double latB, double lonB){
//		return df.format(SpatialCalculator.distance(latA, lonA, 0, latB, lonB, 0));
//	}

    public static List<UploadInfoBean> getUploadInfoList(List<PlotItemInfo> infoList) {
        if (infoList == null || infoList.size() == 0) {
            return null;
        }
        List<UploadInfoBean> list = new ArrayList<UploadInfoBean>();
        for (PlotItemInfo item : infoList) {
            list.add(getUploadInfo(item));
        }
        return list;
    }

    public static UploadInfoBean getUploadInfo(PlotItemInfo item) {
        if (item == null) return null;
        String latLonStr = SpUtils.getFloat(SPConstants.LAT_ADDR, Constants.CENTRE_LAT) + ","
                + SpUtils.getFloat(SPConstants.LON_ADDR, Constants.CENTRE_LON);
        UploadInfoBean bean = new UploadInfoBean(item.getTx_type(), item.getData_type(), item.getTime(), item.getPaths(), item.getTime() + "", "01",
                latLonStr, SpUtils.getLong(SPConstants.LOC_TIME, System.currentTimeMillis()), 1 + "", SpUtils.getString(Constants.UPLOAD_JZH_NUM, Constants.BD_NUM_DEF), 0, -1, null);
        return bean;
    }

    /**
     * 将获取的4字节定位信息转化为字符串
     */

    public static long byteArrToTime(byte[] data) {
        long floatLocInfo = 0;
        String log = "";
        if (data == null) {
            return floatLocInfo;
        }
        for (int i = 0; i < data.length; i++) {
            log += Integer.toHexString(data[i] & 0xff) + " ";
            floatLocInfo <<= 8;
            floatLocInfo |= (data[i] & 0xff);
        }
        Log.e("Tag", "locTime : " + log);
        return floatLocInfo * 1000;
    }

    public static String geoPointToString(List<GeoPoint> list) {
        String paths = "";
        int counter = 1;
        for (GeoPoint gPoint : list) {
            if (counter == list.size()) {
                paths += gPoint.getLatitudeE6() / 1000000.0d + "," + gPoint.getLongitudeE6() / 1000000.0d;
            } else {
                paths += gPoint.getLatitudeE6() / 1000000.0d + "," + gPoint.getLongitudeE6() / 1000000.0d + "-";
            }
            counter++;
        }
        return paths;
    }

    public static String doubleToDegree(double value) {
        String res = "";
        int integer = (int) value;
        double temp = (value - integer) * 60;
        int minute = (int) (temp);
        int second = (int) ((temp - minute) * 60);
        res = integer + "°" + minute + "′" + second + "″";
        return res;
    }

    public static GeoPoint averagePoint(String paths) {
        String[] points = paths.split("-");
        double x = 0;
        double y = 0;
        for (int i = 0; i < points.length; i++) {
            String[] latLon = points[i].split(",");
            x += Double.valueOf(latLon[0]);
            y += Double.valueOf(latLon[1]);
        }
        return new GeoPoint(x / points.length, y / points.length);

    }


    public static GeoPoint[] stringToGeoPoint(String paths) {
        GeoPoint[] geoPoints = null;
        if (paths != null && !"".equals(paths)) {
            String[] pointArr = paths.split("-");
            geoPoints = new GeoPoint[pointArr.length];
            int counter = 0;
            for (String point : pointArr) {
                String[] latLon = point.split(",");
                GeoPoint gPoint = new GeoPoint(Double.valueOf(latLon[0]), Double.valueOf(latLon[1]));
                geoPoints[counter] = gPoint;
                counter++;
            }
        }
        return geoPoints;
    }

    public static GeoPoint[] listToGeoPointArr(List<GeoPoint> list) {
        GeoPoint[] gPointArr = null;
        if (list.size() > 0) {
            gPointArr = new GeoPoint[list.size()];
            for (int i = 0; i < list.size(); i++) {
                gPointArr[i] = list.get(i);
            }
        }
        return gPointArr;
    }

    public static void increaseID() {
        int ID = SpUtils.getInt(SPConstants.SEND_ID, Constants.SEND_ID_DEF);
        if (ID == Integer.MAX_VALUE) {
            SpUtils.putInt(SPConstants.SEND_ID, Constants.SEND_ID_DEF);
        } else {
            ID++;
            SpUtils.putInt(SPConstants.SEND_ID, ID);
        }
    }

    public static int getSendID() {
        return SpUtils.getInt(SPConstants.SEND_ID, Constants.SEND_ID_DEF);
    }

    public static boolean isNetConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static String getStoragePath() {
        String path = getFilesAllName() + "/gtmap";
        return path;
    }

    public static String getFilesAllName() {
        File file = new File("/storage");
        File[] files = file.listFiles();
        String paths = "";
        if (files == null) {
            Log.e("error", "空目录");
            return null;
        }
        for (int i = 0; i < files.length; i++) {
            if (!files[i].getAbsolutePath().equals("/storage/emulated") && !files[i].getAbsolutePath().equals("/storage/self")) {
                paths = files[i].getAbsolutePath();
                break;
            }
        }
        return paths;
    }

    public static boolean isPathExist(String path) {
        File mFile = new File(path);
        return mFile.exists();
    }

    public static void installApk(Context mContext, File file) {
        // TODO Auto-generated method stub
        Log.e("OpenFile", file.getName());
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        mContext.startActivity(intent);
    }

    public static int getPackageCode(Context context) {
        PackageManager manager = context.getPackageManager();
        int code = 0;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            code = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return code;
    }

    public static String getPackageName(Context context) {
        PackageManager manager = context.getPackageManager();
        String name = null;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            name = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return name;
    }

    public static void flipAnimatorXViewShow(final View oldView, final View newView, final long time) {
        final ObjectAnimator animator1 = ObjectAnimator.ofFloat(oldView, "rotationX", 0, 90);
        final ObjectAnimator animator2 = ObjectAnimator.ofFloat(newView, "rotationX", -90, 0);
        animator2.setInterpolator(new OvershootInterpolator(2.0f));
        animator1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                oldView.setVisibility(View.INVISIBLE);
                oldView.clearAnimation();
                animator2.setDuration(time).start();
                newView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator1.setDuration(time).start();
    }

    /**
     * 将对象转化为json作为请求体 请求接口
     *
     * @param object
     * @return
     */
    public static RequestBody getRequestBody(Object object) {
        RequestBody requestBody =
                RequestBody.create(MediaType.parse("application/json; charset=utf-8"),
                        ToJsonUtil.getInstance().toJson(object));
        return requestBody;
    }

    public static List<RequestFirePointBean.FirepointlistBean> getFirePointList(UploadInfoBean infoBean) {
        List<RequestFirePointBean.FirepointlistBean> list = null;
        if (infoBean.getBody() == null) {
            return list;
        }
        list = new ArrayList<>();
        String[] locTimes = infoBean.getLocTimes().split("-");
        // String[] idArr = infoBean.getOffsets().split("-");
        String[] latLngArr = infoBean.getBody().split("-");
        for (int pos = 0; pos < latLngArr.length; pos++) {
            String status = "small";
            double lat = 0.0;
            double lng = 0.0;
            String time = "";
            String[] latLng = latLngArr[pos].split(",");
            if (latLng != null && latLng.length == 2 && latLng[0] != null && latLng[1] != null && !"".equals(latLng[0]) && !"".equals(latLng[1]) && !"null".equals(latLng[0]) && !"null".equals(latLng[1])) {
                lat = Double.valueOf(latLng[0]);
                lng = Double.valueOf(latLng[1]);
                time = sdf.format(Double.valueOf(locTimes[pos]));
            }
            RequestFirePointBean.FirepointlistBean bean = new RequestFirePointBean.FirepointlistBean(status, time, lng + "", lat + "");
            list.add(bean);
        }
        return list;
    }

    public static List<WatchLocBean> getWatchLocList(UploadInfoBean infoBean, String type) {
        List<WatchLocBean> list = null;
        if (infoBean.getBody() == null) {
            return list;
        }
        list = new ArrayList<>();
        String[] locTimes = infoBean.getLocTimes().split("-");
        String[] idArr = infoBean.getBdNum().split("-");//手表的设备号
        String[] latLngArr = infoBean.getBody().split("-");
        String[] data_state = infoBean.getData_state().split("-");
        for (int pos = 0; pos < idArr.length; pos++) {
            String watchstatus = "offline";
            WatchLocBean bean = null;
            double lat = 0.0;
            double lng = 0.0;
            String time = "";
            String[] latLng = latLngArr[pos].split(",");
            if (latLng != null && latLng.length == 2 && latLng[0] != null && latLng[1] != null && !"".equals(latLng[0]) && !"".equals(latLng[1]) && !"null".equals(latLng[0]) && !"null".equals(latLng[1])) {
                watchstatus = data_state[pos].equals("0") ? "normal" : "sosing";
                lat = Double.valueOf(latLng[0]);
                lng = Double.valueOf(latLng[1]);
                time = sdf.format(Double.valueOf(locTimes[pos]));
            }
            if (lat == 0.0) {
                bean = new WatchLocBean("watch", idArr[pos], "", "", watchstatus, "", null, null, time);
            } else {
                bean = new WatchLocBean("watch", idArr[pos], "", "", watchstatus, "", lat + "", lng + "", time);
            }

            list.add(bean);
        }
        return list;
    }

    public static List<FirePoint> getFirePoint(UploadInfoBean infoBean) {
        List<FirePoint> list = null;
        if (infoBean.getBody() == null) {
            return list;
        }
        list = new ArrayList<>();
        String[] latLngArr = infoBean.getBody().split("-");
        for (int pos = 0; pos < latLngArr.length; pos++) {
            String[] latLng = latLngArr[pos].split(",");
            double lat = 0.0;
            double lng = 0.0;
            if (latLng != null && latLng.length == 2 && !BaseUtils.isStringEmpty(latLng[0]) && !BaseUtils.isStringEmpty(latLng[1])) {
                lat = Double.valueOf(latLng[0]);
                lng = Double.valueOf(latLng[1]);
            }
            FirePoint bean = new FirePoint(pos + 1, lat, lng);
            list.add(bean);
        }
        return list;
    }

    public static String getBaseIP() {
        StringBuilder builder = new StringBuilder();
        builder.append(SpUtils.getString(SPConstants.NET_BASE_IP, Constants.BASE_IP_DEF));
        builder.append(":");
        builder.append(SpUtils.getString(SPConstants.NET_BASE_COM, Constants.BASE_COM_DEF));
        builder.append("/");
        return builder.toString();
    }

    /**
     * 获取手持机的Mac地址
     *
     * @return
     */
//    private static boolean state = true;
    public static String getMacFromHardware() {
//        if (state)
//            return "A4D4B255B58C";
        if (!TextUtils.isEmpty(SpUtils.getString("MAC", ""))) {
            return SpUtils.getString("MAC", "");
        }

        try {
            WifiManager manager = (WifiManager) SearchLocMapApplication.getInstance().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (!manager.isWifiEnabled()) {
                // 设置为开启状态
                manager.setWifiEnabled(true);
            }
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;
                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                //取消：
                String[] mac = res1.toString().split(":");
                StringBuilder builder = new StringBuilder();
                for (String str : mac) {
                    builder.append(str);
                }
                all.clear();
                if (builder.toString().length() == 12) {
                    SpUtils.putString("MAC", builder.toString());
                    return builder.toString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getMacFromHardware();
    }

    @SuppressLint("WrongConstant")
    public static byte[] obtainSearchBytes() {
        String numStr = SpUtils.getString(SPConstants.BINDING_NUM, "");
        if ("".equals(numStr) || numStr.length() != 8) {
            numStr = getMacFromHardware().substring(4);
            SpUtils.putString(SPConstants.BINDING_NUM, numStr);
        }
        int CHANNEL = SpUtils.getInt(SPConstants.CHANNEL_NUM, 0);
        String channel;
        if (CHANNEL == 10) {
            channel = "0" + "a";
        } else {
            channel = "0" + CHANNEL + "";
        }
        numStr = numStr + channel;
        return BaseUtils.getPerRegisterByteArr(numStr);
    }

}

