package org.tangerine.common.json;

import java.io.UnsupportedEncodingException;

import org.tangerine.common.Constant.Config;

import com.google.gson.GsonBuilder;

public class JsonUtil {
	
	private static GsonBuilder gsonBuilder = new GsonBuilder();
	
	public static String toHtmlPrettyJson(Object object) {
		String json = gsonBuilder.setPrettyPrinting().create().toJson(object);
		json = json.replace("\n", "<br/>");
		json = json.replace(" ", "&nbsp;");
		return json;
	}
	
	public static <T> T fromJson(String json, Class<T> classOfT) {
		return gsonBuilder.create().fromJson(json, classOfT);
	}
	
	public static String toJson(Object src) {
		return gsonBuilder.create().toJson(src);
	}
	
	public static byte[] toJsonBytes(Object src) {
		try {
			return gsonBuilder.create().toJson(src).getBytes(Config.DEFAULT_CHARTSET);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return new byte[0];
	}
}
