package org.tangerine.handler;

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

	@SuppressWarnings("unchecked")
	@Override
	public final void handle(Connection conn, Packet msg) throws Exception {
		Byte type = null;
		try {
			type = getClass().getAnnotation(HandleCommand.class).value();
		} catch (NullPointerException e) {
			//TODO log err
			System.err.println("CommandHandler[" + this.getClass() + "] must assign annotation org.tangerine.handler.HandleCommand.");
		}
		if (msg.getType().equals(type)) {
			T cmd = null;
			if (msg.getLength() > 0) {
				byte[] payload = new byte[msg.getLength()];
				msg.getPayload().readBytes(payload).release();
				cmd = (T) JsonUtil.fromJson(StringUtil.decode(payload), ParameterUtil.getSuperClassGenricType(getClass()));
			}
			handleCmd(conn, cmd);
		}
	}
	
	public abstract void handleCmd(Connection conn, T cmd);
}
