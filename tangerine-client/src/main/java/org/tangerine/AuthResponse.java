package org.tangerine;

import java.util.Map;

public class AuthResponse {

	private int code;
	
	private String msg;
	
	private Map<String, Object> extras;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Map<String, Object> getExtras() {
		return extras;
	}

	public void setExtras(Map<String, Object> extras) {
		this.extras = extras;
	}

	@Override
	public String toString() {
		return "AuthResponse [code=" + code + ", msg=" + msg + ", extras=" + extras + "]";
	}
}
