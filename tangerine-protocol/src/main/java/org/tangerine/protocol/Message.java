package org.tangerine.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.UnsupportedEncodingException;

import org.tangerine.common.StringUtil;
/**
 * 普通IM消息
 * @author weird
 *
 */
public class Message {
	
	/**
	 * 消息类型
	 * @author weird
	 *
	 */
	public static class Type {
		/**客户端或服务器端请求消息**/
		public static final byte MSG_REQUEST = 0x0;
		/**客户端向服务器端发送的通知消息**/
		public static final byte MSG_NOTIFY = 0x1;
		/**服务器端响应客户端的消息**/
		public static final byte MSG_RESPONSE = 0x2;
		/**服务器端向客户端推送的消息**/
		public static final byte MSG_PUSH = 0x3;
	}
	
	/**消息类型**/
	private Byte messageType;
	/**路由压缩标志**/
	private Boolean routeFlag;
	/**消息Id**/
	private Integer messageId;
	/**消息路由路径**/
	private String routePath;
	/**消息体**/
	private ByteBuf body;

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
	
	public ByteBuf getBody() {
		return body;
	}
	
	public void setBody(ByteBuf body) {
		this.body = body;
	}
	
	/**
	 * flag 的第5~7位
	 * @param flag
	 * @return
	 */
	public static byte getMessageType(byte flag) {
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
	public static Boolean getRouteFlag(byte flag) {
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
	
	public static Message decode(ByteBuf buffer) {
		
		Message message = new Message();
		
		byte flag = buffer.readByte();
		message.setMessageType(Message.getMessageType(flag));
		message.setRouteFlag(Message.getRouteFlag(flag));
		
		/**只有REQUEST和NOTIFY和RESPONSE才需要MessageId **/
		if (message.getMessageType().equals(Type.MSG_REQUEST)
				|| message.getMessageType().equals(Type.MSG_RESPONSE)) {
			message.setMessageId(buffer.readInt());
		}
		
		/**只有REQUEST和NOTIFY和PUSH才需要route **/
		if (message.getMessageType().equals(Type.MSG_REQUEST)
				|| message.getMessageType().equals(Type.MSG_NOTIFY)
				|| message.getMessageType().equals(Type.MSG_PUSH)) {
			
			//没有使用路由压缩
			if (!message.getRouteFlag()) {
				byte lenRoutePath = buffer.readByte();
				message.setRoutePath(StringUtil.decode(buffer.readSlice(lenRoutePath)));
				//使用路由压缩
			} else { 
				Short routeId = buffer.readShort();
				message.setRoutePath(routeId.toString());
			}
		}
		// import! 要保证 buffer为一个且仅为一个完整message包数据
		message.setBody(buffer.readSlice(buffer.readableBytes()).retain());
		
		return message;
	}
	
	public static ByteBuf encode(Message message) {
		
		ByteBuf hMessageBuf = Unpooled.buffer();
		
		hMessageBuf.writeByte(message.getFlag());
		
		/**只有REQUEST和RESPONSE才需要messageId**/
		if (message.getMessageType().equals(Type.MSG_REQUEST)
				|| message.getMessageType().equals(Type.MSG_RESPONSE)) {
			hMessageBuf.writeInt(message.getMessageId());
		}
		
		/**只有REQUEST和NOTIFY和PUSH才需要route **/
		if (message.getMessageType().equals(Type.MSG_REQUEST)
				|| message.getMessageType().equals(Type.MSG_NOTIFY)
				|| message.getMessageType().equals(Type.MSG_PUSH)) {
			
			if (!message.getRouteFlag()) {
				try {
					hMessageBuf.writeByte(message.getRoutePath().getBytes("utf-8").length);
					hMessageBuf.writeBytes(message.getRoutePath().getBytes("utf-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				
			} else {
				Short routeId = Short.valueOf(message.getRoutePath());
				hMessageBuf.writeShort(routeId);
			}
		}
		
		return Unpooled.wrappedBuffer(hMessageBuf, message.getBody());
	}
	
//	public static void main(String[] args) throws Exception {
//		ByteBuf buf = Unpooled.buffer(32);
//		buf.writeByte(3);
//		byte[] bytes = "this is a test buf demo.".getBytes("utf-8");
//		buf.writeByte(bytes.length);
//		buf.writeBytes(bytes);
//		buf.writeByte(55);
//		
//		buf.readByte();
//		int len = buf.readByte();
//		ByteBuf strbuf = buf.readSlice(len);
//		
//		System.out.println(StringUtil.decode(strbuf));
//	}
}
