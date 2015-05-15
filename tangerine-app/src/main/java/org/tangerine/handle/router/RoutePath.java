package org.tangerine.handle.router;

public class RoutePath {

	private String server;
	
	private String handler;
	
	private String action;

	public RoutePath(String path) {
		String[] paths = path.split("\\.");
		this.server = paths[0];
		this.handler = paths[1];
		this.action = paths[2];
	}
	
	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getHandler() {
		return handler;
	}

	public void setHandler(String handler) {
		this.handler = handler;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
}
