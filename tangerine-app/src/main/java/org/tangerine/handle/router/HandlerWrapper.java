package org.tangerine.handle.router;

public class HandlerWrapper {
	
	private Object handler;

	public HandlerWrapper() {}
	
	public HandlerWrapper(Object handler) {
		super();
		this.handler = handler;
	}

	public Object getHandler() {
		return handler;
	}

	public void setHandler(Object handler) {
		this.handler = handler;
	}
}
