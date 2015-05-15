package org.tangerine.handler;

import org.tangerine.common.ParameterUtil;
import org.tangerine.net.conn.Connection;

/**
 * 消息处理抽象类
 * @author weird
 * @param <T> 具体消息
 */
public abstract class Handler<T> {
	/**
	 * 处理消息
	 * @param conn TODO
	 * @param msg
	 * @throws Exception
	 */
	public abstract void handle(Connection conn, T msg) throws Exception;
	
	/**
	 * 获得Handler超类的泛型类型
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public final Class getSuperHandlerGenricType() {
		return ParameterUtil.getSuperClassGenricType(getChildHandlerClass(this));
	}
	
	/**
	 * 上一级Handler子类的Class
	 * @param handler
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Class getChildHandlerClass(Handler handler) {
		
		Class handlerClz = handler.getClass();
		while(handlerClz.getSuperclass() != null 
				&& !handlerClz.getSuperclass().isAssignableFrom(Handler.class)) {
			handlerClz = handlerClz.getSuperclass();
		}
		
		return handlerClz;
	}
}
