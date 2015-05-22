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
	 * Handler直接子类的的泛型类型
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public final Class getSuperHandlerGenricType() {
		Class handlerClz = getClass();
		while(handlerClz.getSuperclass() != null 
				&& !handlerClz.getSuperclass().isAssignableFrom(Handler.class)) {
			handlerClz = handlerClz.getSuperclass();
		}
		return ParameterUtil.getSuperClassGenricType(handlerClz);
	}
}
