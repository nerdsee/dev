package org.stoevesand.findow.provider.finapi.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSONUtils {

	private static Logger log = LoggerFactory.getLogger(JSONUtils.class);

	public static JSONObject getJSONObject(JSONObject jo, String key) {
		JSONObject ret = null;
		try {
			ret = jo.getJSONObject(key);
		} catch (JSONException e) {
			// System.out.println("Cannot read from JSON: " + key);
		}
		return ret;
	}

	public static double getDouble(JSONObject jo, String key) {
		double ret = 0;
		try {
			ret = jo.getDouble(key);
		} catch (JSONException e) {
			// System.out.println("Cannot read from JSON: " + key);
		}
		return ret;
	}

	public static int getInt(JSONObject jo, String key) {
		int ret = 0;
		try {
			ret = jo.getInt(key);
		} catch (JSONException e) {
			// System.out.println("Cannot read from JSON: " + key);
		}
		return ret;
	}

	public static String getString(JSONObject jo, String key) {
		String ret = "";
		try {
			ret = jo.getString(key);
		} catch (JSONException e) {
			// System.out.println("Cannot read from JSON: " + key);
		}
		return ret;
	}

	public static long getLong(JSONObject jo, String key) {
		long ret = 0L;
		try {
			ret = jo.getLong(key);
		} catch (JSONException e) {
			// System.out.println("Cannot read from JSON: " + key);
		}
		return ret;
	}

	public static Date getDate(JSONObject jo, String key, String dateFormat) {
		Date ret = null;
		String ds = null;
		try {
			ds = jo.getString(key);
			DateFormat df = new SimpleDateFormat(dateFormat);
			ret = df.parse(ds);
		} catch (ParseException e) {
			log.error("Failed to parse date: " + ds);
		} catch (JSONException e) {
			log.error("Cannot read from JSON: " + key);
		}
		return ret;
	}

}
