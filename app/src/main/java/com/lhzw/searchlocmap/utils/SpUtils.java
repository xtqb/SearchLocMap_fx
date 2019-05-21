package com.lhzw.searchlocmap.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.lhzw.searchlocmap.application.SearchLocMapApplication;
import com.lhzw.searchlocmap.constants.Constants;

public class SpUtils {
	public static void putString(String key, String value) {
		SharedPreferences sp = SearchLocMapApplication.getInstance()
				.getSharedPreferences(Constants.SP_NAME, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static String getString(String key, String defaultValue) {
		SharedPreferences sp = SearchLocMapApplication.getInstance()
				.getSharedPreferences(Constants.SP_NAME, Context.MODE_PRIVATE);
		return sp.getString(key, defaultValue);
	}

	public static void putBoolean(String key, boolean value) {
		SharedPreferences sp = SearchLocMapApplication.getInstance()
				.getSharedPreferences(Constants.SP_NAME, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public static boolean getBoolean(String key, boolean defaultValue) {
		SharedPreferences sp = SearchLocMapApplication.getInstance()
				.getSharedPreferences(Constants.SP_NAME, Context.MODE_PRIVATE);
		return sp.getBoolean(key, defaultValue);
	}

	public static void putInt(String key, int value) {
		SharedPreferences sp = SearchLocMapApplication.getInstance()
				.getSharedPreferences(Constants.SP_NAME, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public static int getInt(String key, int defaultValue) {
		SharedPreferences sp = SearchLocMapApplication.getInstance()
				.getSharedPreferences(Constants.SP_NAME, Context.MODE_PRIVATE);
		return sp.getInt(key, defaultValue);
	}

	public static void putFloat(String key, float value) {
		SharedPreferences sp = SearchLocMapApplication.getInstance()
				.getSharedPreferences(Constants.SP_NAME, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putFloat(key, value);
		editor.commit();
	}

	public static float getFloat(String key, float defaultValue) {
		SharedPreferences sp = SearchLocMapApplication.getInstance()
				.getSharedPreferences(Constants.SP_NAME, Context.MODE_PRIVATE);
		return sp.getFloat(key, defaultValue);
	}
	
	public static void putLong(String key, long value) {
		SharedPreferences sp = SearchLocMapApplication.getInstance()
				.getSharedPreferences(Constants.SP_NAME, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putLong(key, value);
		editor.commit();
	}

	public static long getLong(String key, long defaultValue) {
		SharedPreferences sp = SearchLocMapApplication.getInstance()
				.getSharedPreferences(Constants.SP_NAME, Context.MODE_PRIVATE);
		return sp.getLong(key, defaultValue);
	}
}
