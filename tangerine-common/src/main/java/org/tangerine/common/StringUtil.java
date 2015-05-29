package org.tangerine.common;

import io.netty.buffer.ByteBuf;

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
	
	public static String decode(ByteBuf buf) {
		try {
			return new String(buf.array(), buf.arrayOffset(), buf.readableBytes(), Config.DEFAULT_CHARTSET);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}
}
