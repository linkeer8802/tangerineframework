package org.tangerine;

import java.util.Calendar;

public final class Constant {

	public static class Config {
		
		public static final String DEFAULT_CHARTSET = "utf-8";
	}
	
	public static class PacketType {
		/**客户端到服务器的握手请求以及服务器到客户端的握手响应**/
		public static final byte PCK_HANDSHAKE = 0x1;
		/** 客户端到服务器的握手ack */
		public static final byte PCK_SHAKE_ACK = 0x2;
		/***心跳包 **/
		public static final byte PCK_HEARTBEAT = 0x3;
		/**数据包**/
		public static final byte PCK_DATA = 0x4;
		/**服务器主动断开连接通知**/
		public static final byte PCK_DISCONNECT = 0x5;
		
		public static final byte PCK_AUTH = 0x6;
	}
	
	public static class MSGType {
		
		public static final byte MSG_REQUEST = 0x0;
		
		public static final byte MSG_NOTIFY = 0x1;
		
		public static final byte MSG_RESPONSE = 0x2;
		
		public static final byte MSG_PUSH = 0x3;
	}
}
