package net.zookeeper.live.controllers;

import java.util.Map;

import play.mvc.Controller;

/**
 * 
 * @author zpf.073@gmail.com
 * 
 */
public class BaseController extends Controller {
	
	public static String TransformParam(String key, Map<String, String[]> params) {
		String temp = "";
		String[] str = params.get(key);
		if (str != null && str.length > 0) {
			temp = str[0];
		}
		return temp;
	}

	public static String getQueryString(String key) {
		return getQueryString(key, "");
	}

	public static String getQueryString(String key, String defaultValue) {
		String s = request().getQueryString(key);
		if (s == null) {
			return defaultValue;
		}
		return s;
	}

	public static long getQueryLong(String key) {

		return getQueryLong(key, 0L);
	}

	public static int getQueryInt(String key) {
		return getQueryInt(key, 0);
	}

	public static int getQueryInt(String key, int defaultVal) {
		String s = request().getQueryString(key);
		if (s != null) {
			try {
				return Integer.parseInt(s);
			} catch (Exception e) {
			}
		}
		return defaultVal;
	}

	public static Boolean getQueryBoolean(String key) {
		return getQueryBoolean(key, false);
	}

	public static Boolean getQueryBoolean(String key, Boolean defaultVal) {
		Boolean showProfile = defaultVal;
		try {
			String s = request().getQueryString(key);
			showProfile = Boolean.parseBoolean(s);
		} catch (Exception e) {
		}
		return showProfile;
	}

	public static long getQueryLong(String key, long defaultVal) {
		String s = request().getQueryString(key);
		if (s != null) {
			try {
				return Long.parseLong(s);
			} catch (Exception e) {
			}
		}
		return defaultVal;
	}
}
