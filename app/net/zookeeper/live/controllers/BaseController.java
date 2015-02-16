package net.zookeeper.live.controllers;

import java.util.Map;

import play.mvc.Controller;
import play.mvc.Result;

/**
 * 
 * @author zpf.073@gmail.com
 * 
 */
public class BaseController extends Controller {
	
	public static Result index() {
		return ok();
	}

	public static String TransformParam(String key, Map<String, String[]> params) {
		String temp = "";
		String[] str = params.get(key);
		if (str != null && str.length > 0) {
			temp = str[0];
		}
		return temp;
	}

	public static String getQueryString(String key) {
		return request().getQueryString(key);
	}

	public static long getQueryLong(String key) {
		String s = request().getQueryString(key);
		if (s != null) {
			try {
				return Long.parseLong(s);
			} catch (Exception e) {
			}
		}
		return 0;
	}

	public static int getQueryInt(String key) {
		String s = request().getQueryString(key);
		if (s != null) {
			try {
				return Integer.parseInt(s);
			} catch (Exception e) {
			}
		}
		return 0;
	}
}
