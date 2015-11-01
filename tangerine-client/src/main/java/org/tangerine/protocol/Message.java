package org.tangerine.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.UnsupportedEncodingException;

import org.tangerine.Constant.Config;
import org.tangerine.Constant.MSGType;

public class Message {

	private Byte messageType;
	
	private Boolean routeFlag;
	
	private Integer messageId;
	
	private String routePath;
	
	private byte[] body;

	public Byte getMessageType() {
		return messageType;
	}

	public void setMessageType(Byte messageType) {
		this.messageType = messageType;
	}

	public Boolean getRouteFlag() {
		return routeFlag;
	}

	public void setRouteFlag(Boolean routeFlag) {
		this.routeFlag = routeFlag;
	}

	public Integer getMessageId() {
		return messageId;
	}

	public void setMessageId(Integer messageId) {
		this.messageId = messageId;
	}

	public String getRoutePath() {
		return routePath;
	}

	public void setRoutePath(String routePath) {
		this.routePath = routePath;
	}
	
	public byte[] getBody() {
		return body;
	}
	
	public void setBody(byte[] body) {
		this.body = body;
	}
	
	/**
	 * flag 的第5~7位
	 * @param flag
	 * @return
	 */
	public Byte getMessageType(byte flag) {
		byte value = 0;
		value = (byte) (flag & 0x0e);
		value = (byte) (value >> 1);
		return value;
	}
	/**
	 * flag 最后一位
	 * @param flag
	 * @return
	 */
	public Boolean getRouteFlag(byte flag) {
		return (flag & 0x01) != 0;
	}
	
	public byte getFlag() {
		byte flag = 0;
		
		flag = (byte) (this.messageType << 1);
		flag = (byte) (flag & 0xfe);
		
		if (this.routeFlag) {
			flag = (byte) (flag | 0x01);
		}
		
		return flag;
	}
	
	public static Message decode(ByteBuf buf) {
		
		Message message = new Message();
		
		byte flag = buf.readByte();
		message.setMessageType(message.getMessageType(flag));
		message.setRouteFlag(message.getRouteFlag(flag));
		
		/**只有REQUEST和NOTIFY和RESPONSE才需要MessageId **/
		if (message.getMessageType().equals(MSGType.MSG_REQUEST)
				|| message.getMessageType().equals(MSGType.MSG_RESPONSE)) {
			message.setMessageId(buf.readInt());
		}
		
		/**只有REQUEST和NOTIFY和PUSH才需要route **/
		if (message.getMessageType().equals(MSGType.MSG_REQUEST)
				|| message.getMessageType().equals(MSGType.MSG_NOTIFY)
				|| message.getMessageType().equals(MSGType.MSG_PUSH)) {
			
			if (!message.getRouteFlag()) {
				byte routePathLength = buf.readByte();
				byte[] dst = new byte[routePathLength];
				buf.readBytes(dst);
				try {
					message.setRoutePath(new String(dst, Config.DEFAULT_CHARTSET));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				
			} else {
				Short routeId = buf.readShort();
				message.setRoutePath(routeId.toString());
			}
		}
		
		byte[] body = new byte[buf.readableBytes()];
		buf.readBytes(body);
		message.setBody(body);
		
		return message;
	}
	
	public static ByteBuf encode(Message message) {
		
		ByteBuf out = Unpooled.buffer();
		
		out.writeByte(message.getFlag());
		
		/**只有REQUEST和RESPONSE才需要messageId**/
		if (message.getMessageType().equals(MSGType.MSG_REQUEST)
				|| message.getMessageType().equals(MSGType.MSG_RESPONSE)) {
			out.writeInt(message.getMessageId());
		}
		
		/**只有REQUEST和NOTIFY和PUSH才需要route **/
		if (message.getMessageType().equals(MSGType.MSG_REQUEST)
				|| message.getMessageType().equals(MSGType.MSG_NOTIFY)
				|| message.getMessageType().equals(MSGType.MSG_PUSH)) {
			
			if (!message.getRouteFlag()) {
				out.writeByte(message.getRoutePath().getBytes().length);
				out.writeBytes(message.getRoutePath().getBytes());
				
			} else {
				Short routeId = Short.valueOf(message.getRoutePath());
				out.writeShort(routeId);
			}
		}
		
		/**message body**/
		out.writeBytes(message.getBody());
		
		return out;
	}
}
