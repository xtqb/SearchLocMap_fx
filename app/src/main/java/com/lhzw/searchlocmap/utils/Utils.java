package com.lhzw.searchlocmap.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

public class Utils {
	private static final String TAG = "Utils";

	/**
	 * 字符串判空
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isStringEmpty(String str) {
		if (null == str || str.length() == 0 || "".equals(str.trim()))
			return true;
		else
			return false;
	}

	/**
	 * 列表判空
	 * 
	 * @param list
	 * @return
	 */
	public static boolean isListEmpty(List<?> list) {
		if (list != null && list.size() > 0)
			return false;
		else
			return true;
	}

	/**
	 * 将一个int数据,转换为4字节byte数组
	 * 
	 * @param intValue
	 * @return
	 */
	public static byte[] Int2Bytes(int intValue) {
		byte[] result = new byte[4];
		result[0] = (byte) ((intValue & 0xFF000000) >> 24);
		result[1] = (byte) ((intValue & 0x00FF0000) >> 16);
		result[2] = (byte) ((intValue & 0x0000FF00) >> 8);
		result[3] = (byte) ((intValue & 0x000000FF));
		return result;
	}

	/**
	 * 将4字节byte数组的数据,转换成int值
	 * 
	 * @param byteVal
	 * @return
	 */
	public static int Bytes2Int(byte[] byteVal) {
		int result = 0;
		for (int i = 0; i < byteVal.length; i++) {
			int tmpVal = (byteVal[i] << (8 * (3 - i)));
			switch (i) {
			case 0:
				tmpVal = tmpVal & 0xFF000000;
				break;
			case 1:
				tmpVal = tmpVal & 0x00FF0000;
				break;
			case 2:
				tmpVal = tmpVal & 0x0000FF00;
				break;
			case 3:
				tmpVal = tmpVal & 0x000000FF;
				break;
			}
			result = result | tmpVal;
		}
		return result;
	}

	/*
	 * 关闭软键盘
	 */
	public static void closeSoftInput(Context context) {
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		boolean isOpen = imm.isActive();
		if (isOpen) {
			imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	/**
	 * 时间格式化为 yyyy-MM-dd HH:mm:ss
	 * 
	 * @param current
	 * @return
	 */
	public static String getFormatDateTime(long current) {

		SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd@HH:mm:ss", Locale.CHINA);
		String mDateStr = mSimpleDateFormat.format(new Date(current));
		return mDateStr;
	}

	/**
	 * 获得格式化的日期时间字符串之后指定分钟的日期时间
	 * 
	 * @param dateStr
	 * @param minute
	 * @return
	 */
	public static String formatDateStrAfterMinute(String dateStr, String minute) {
		String minuteInt;
		if (minute.contains(".")) {
			String[] minuteArr = minute.split("\\.");
			minuteInt = minuteArr[0];
		} else {
			minuteInt = minute;
		}
		long time = Integer.parseInt(minuteInt) * 60 * 1000;
		SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss", Locale.CHINA);

		Date afterDate;
		String afterDateStr = null;
		try {
			afterDate = new Date(mSimpleDateFormat.parse(dateStr).getTime()
					+ time);
			afterDateStr = mSimpleDateFormat.format(afterDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return afterDateStr;
	}

	/**
	 * 获取当前时间
	 * 
	 * 返回 年、月、日
	 */
	public static String getDateStr(Calendar mCalendar) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
		String timeStr = sdf.format(mCalendar.getTime());
		return timeStr;
	}

	/**
	 * 检查当前是否有可用网络
	 * 
	 * @param context
	 * @return
	 */
	public static boolean checkNetWorkConnect(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm != null) {
			NetworkInfo[] networkInfos = cm.getAllNetworkInfo();
			if (networkInfos != null && networkInfos.length > 0) {
				for (NetworkInfo info : networkInfos) {
					if (info.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 获取软件版本号
	 * 
	 * @param context
	 * @return
	 */
	public static String getVersionName(Context context) {
		// 获取packagemanager的实例
		PackageManager packageManager = context.getPackageManager();
		// 0代表是获取版本信息
		PackageInfo packInfo = null;
		String version = "";
		try {
			packInfo = packageManager.getPackageInfo(context.getPackageName(),
					0);
			version = packInfo.versionName;

		} catch (NameNotFoundException e) {
			if (e != null) {
				Log.e(TAG, e.getMessage() + "");
			}
		}

		return version;
	}

	/**
	 * 隐藏进度框
	 */
	public static void dissmissProgressDialog() {
		ProgressDialog progDialog = null;// 搜索时进度条
		if (progDialog != null) {
			progDialog.dismiss();
		}
	}

	/**
	 * 显示进度框
	 */
	public static void showProgressDialog(Context context) {
		ProgressDialog progDialog = null;// 搜索时进度条
		if (progDialog == null)
			progDialog = new ProgressDialog(context);
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(false);
		progDialog.setMessage("正在搜索:\n" + "");
		progDialog.show();
	}

}
