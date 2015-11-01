package org.tangerine.im;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.atomic.AtomicInteger;

import org.tangerine.AuthRequest;
import org.tangerine.Constant.Config;
import org.tangerine.Constant.MSGType;
import org.tangerine.Constant.PacketType;
import org.tangerine.im.listener.DataCallBack;
import org.tangerine.im.listener.DataListener;
import org.tangerine.im.listener.ReConnectionListener;
import org.tangerine.im.listener.RequestHandShakeListener;
import org.tangerine.protocol.Message;
import org.tangerine.protocol.Packet;
import org.tangerine.protocol.PacketHead;
import org.tangerine.util.JsonUtil;

public class TangerineClient {

	private IMConnection connection;
	private RouteDictionary routeDictionary;
	
	private final AtomicInteger messageId = new AtomicInteger(0);
	
	
	public TangerineClient(String host, int port) {
		connection = new IMConnection(host, port);
		routeDictionary = new RouteDictionary();
	}
	
	protected void init() {
		connection.addConnectionListener(new RequestHandShakeListener());
		connection.addConnectionListener(new ReConnectionListener(connection));
	}
	
	public void connect() throws Exception {
		init();
		connection.connect();
	}
	
	public void login(String uname, String password) throws Exception {
		if (connection.isConnected()) {
			AuthRequest req = new AuthRequest();
			req.setUname(uname);
			req.setPassword(password);
			
			ByteBuf data = Unpooled.wrappedBuffer(JsonUtil.toJsonBytes(req));
			Packet packet = new Packet(new PacketHead(PacketType.PCK_AUTH, data.readableBytes()), data);
			connection.getPacketWriter().send(packet);
		} else {
			System.err.println("not connected.");
		}
	}
	
	public void sendRequest(String route, Object msg, DataCallBack<?> callback) {
		int msgId = messageId.incrementAndGet();
		try {
			connection.getPacketWriter().send(getPacket(msgId, route, msg));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		connection.addDataCallBack(msgId, callback);
		
	}
	
    public void waitForClose() {
    	synchronized (connection) {
//    		if (!connection.isSocketClosed()) {
    			try {
    				connection.wait();
    			} catch (InterruptedException e) {
    				e.printStackTrace();
    			}
//    		}
	    	System.out.println("over....");
    	}
    }
	
	public void sendNotify(String route, Object msg) throws Exception {
		
		connection.getPacketWriter().send(getPacket(null, route, msg) );
	}
	
	public void on(String route, DataListener<?> listener) {
		connection.addDataListener(route, listener);
	}
	
	private Packet getPacket(Integer reqId, String route, Object body) {
		
		Message message = new Message();
		
		if (reqId == null) {
			message.setMessageType(MSGType.MSG_NOTIFY);
		} else {
			message.setMessageType(MSGType.MSG_REQUEST);
			message.setMessageId(reqId);
		}
		
		if (route == null) {
			message.setRouteFlag(connection.getConfig().getRouteFlag());
		} else {
			
			String routePath = route;
			boolean routeFlag = false;
			
			if (connection.getConfig().getRouteFlag()) {
				Short routeId = routeDictionary.getRouteId(route);
				if (routeId != null) {
					routePath = routeId.toString();
					routeFlag = true;
				}
			}
			message.setRouteFlag(routeFlag);
			message.setRoutePath(routePath);
		}
		
		try {
			message.setBody(JsonUtil.toJson(body).getBytes(Config.DEFAULT_CHARTSET));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return new Packet(message);
	}

	public IMConnection getConnection() {
		return connection;
	}
}
