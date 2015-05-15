package org.tangerine.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigUtil {

	private static Properties properties = new Properties();
	
	protected static void load() {

		try {
			InputStream inputStream = ConfigUtil.class.getClassLoader().getResourceAsStream("conf.properties");
			properties.load(inputStream);
		} catch (IOException e1) {
			e1.printStackTrace();
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
