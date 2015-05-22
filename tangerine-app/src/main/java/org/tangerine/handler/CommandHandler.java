package org.tangerine.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tangerine.common.ParameterUtil;
import org.tangerine.common.StringUtil;
import org.tangerine.common.json.JsonUtil;
import org.tangerine.net.conn.Connection;
import org.tangerine.protocol.Packet;
/**
 * 处理命令类型的消息
 * @author weird
 * @param <T> 命令
 */
public abstract class CommandHandler<T> extends Handler<Packet> {

	private static final Log log =LogFactory.getLog(CommandHandler.class);
	
	@SuppressWarnings("unchecked")
	@Override
	public final void handle(Connection conn, Packet msg) throws Exception {
		Byte type = null;
		try {
			type = getClass().getAnnotation(HandleCommand.class).value();
		} catch (NullPointerException e) {
			//TODO log err
			log.error("CommandHandler[" + this.getClass() + "] must assign annotation org.tangerine.handler.HandleCommand.");
		}
		if (msg.getType().equals(type)) {
			T cmd = null;
			if (msg.getLength() > 0) {
				byte[] payload = new byte[msg.getPayload().readableBytes()];
				msg.getPayload().readBytes(payload).release();
				String decode = StringUtil.decode(payload);
				
				try {
					cmd = (T) JsonUtil.fromJson(decode, getCommandHandlerGenricType());
				} catch (Exception e) {
					log.error("decode string:" + decode);
				}
			}
			handleCmd(conn, cmd);
		}
	}
	
	/**
	 * CommandHandler直接子类的泛型类型
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Class getCommandHandlerGenricType() {
		Class handlerClz = getClass();
		while(handlerClz.getSuperclass() != null 
				&& !handlerClz.getSuperclass().isAssignableFrom(CommandHandler.class)) {
			handlerClz = handlerClz.getSuperclass();
		}
		
		return ParameterUtil.getSuperClassGenricType(handlerClz);
	} 
	
	public abstract void handleCmd(Connection conn, T cmd);
}
