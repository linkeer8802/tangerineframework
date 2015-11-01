package org.tangerine;

import java.util.Map;

public class HandShakeRequest {

	private String version;
	
	private Map<String, Object> extras;

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Map<String, Object> getExtras() {
		return extras;
	}

	public void setExtras(Map<String, Object> extras) {
		this.extras = extras;
	}
}
