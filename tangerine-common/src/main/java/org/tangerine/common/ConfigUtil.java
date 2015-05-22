package org.tangerine.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigUtil {

	private static Properties properties = new Properties();
	
	public static void load(boolean reload) {

		if (properties.isEmpty() || reload) {
			try {
				InputStream inputStream = ConfigUtil.class.getClassLoader().getResourceAsStream("conf/tangerine.properties");
				properties.load(inputStream);
				
				properties.list(System.out);
				
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public static String get(String name) {
		return properties.getProperty(name);
	}
	
	public static String get(String name, String defaultValue) {
		return properties.getProperty(name, defaultValue);
	}
	
	public static Integer getInt(String name, Integer defaultValue) {
		String value = get(name);
		return value == null ? defaultValue : Integer.valueOf(value);
	}
	
	public static Boolean getBool(String name, Boolean defaultValue) {
		String value = get(name);
		return value == null ? defaultValue : Boolean.valueOf(value);
	}
}
