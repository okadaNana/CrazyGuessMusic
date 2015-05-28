package com.owen.crazygussmusic.util;

import android.util.Log;

public class MyLog {
	
	public static final boolean DEBUG = true;
	
	/**
	 * DEBUG
	 */
	public static void d(String tag, String message) {
		if (DEBUG) {
			Log.d(tag, message);
		}
	}
	
	/**
	 * WARN
	 */
	public static void w(String tag, String message) {
		if (DEBUG) {
			Log.w(tag, message);
		}
	}

	/**
	 * ERROR
	 */
	public static void e(String tag, String message) {
		if (DEBUG) {
			Log.e(tag, message);
		}
	}
	
	/**
	 * INFO
	 */
	public static void i(String tag, String message) {
		if (DEBUG) {
			Log.i(tag, message);
		}
	}
}
