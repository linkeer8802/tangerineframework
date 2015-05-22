package org.tangerine.protocol.model;

import java.util.HashMap;
import java.util.Map;

public class AuthResponse {

	private int code;
	
	private String msg;
	
	private Map<String, Object> extras = new HashMap<String, Object>();

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

	public void addExtra(String name, Object value) {
		this.extras.put(name, value);
	}
}
