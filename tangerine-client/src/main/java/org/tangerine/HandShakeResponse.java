package org.tangerine;

import java.io.Serializable;
import java.util.Map;

public class HandShakeResponse implements Serializable {

	private static final long serialVersionUID = -4749390994802689730L;

	private int code;
	
	private int heartbeat;
	
	private Map<String, Object> dict;
	
	private Map<String, Object> extras;

	
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public int getHeartbeat() {
		return heartbeat;
	}

	public void setHeartbeat(int heartbeat) {
		this.heartbeat = heartbeat;
	}

	public Map<String, Object> getDict() {
		return dict;
	}

	public void setDict(Map<String, Object> dict) {
		this.dict = dict;
	}

	public Map<String, Object> getExtras() {
		return extras;
	}

	public void setExtras(Map<String, Object> extras) {
		this.extras = extras;
	}
}
