package org.tangerine.common;

import java.io.UnsupportedEncodingException;

import org.tangerine.common.Constant.Config;

public class StringUtil {

	public static String decode(byte[] bytes) {
		try {
			return new String(bytes, Config.DEFAULT_CHARTSET);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}
}
