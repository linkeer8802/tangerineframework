package org.tangerine.im;

public class Configuration {

	private String host;
	
	private int port;
	
	private int connectTimeout;
	
	private Boolean routeFlag = false;
	
	private Integer heartbeat = 3;
	
	private Boolean useProtobuf = false;
	
	public Configuration(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public Boolean getRouteFlag() {
		return routeFlag;
	}

	public void setRouteFlag(Boolean routeFlag) {
		this.routeFlag = routeFlag;
	}

	public Integer getHeartbeat() {
		return heartbeat;
	}

	public void setHeartbeat(Integer heartbeat) {
		this.heartbeat = heartbeat;
	}

	public Boolean getUseProtobuf() {
		return useProtobuf;
	}

	public void setUseProtobuf(Boolean useProtobuf) {
		this.useProtobuf = useProtobuf;
	}

}
