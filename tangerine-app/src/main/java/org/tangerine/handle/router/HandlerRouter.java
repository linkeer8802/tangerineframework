package org.tangerine.handle.router;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tangerine.common.StringUtil;
import org.tangerine.common.json.JsonUtil;
import org.tangerine.container.Component;
import org.tangerine.container.ComponentManager;
import org.tangerine.handle.interceptor.CoInterceptor;
import org.tangerine.handle.interceptor.HandlerInterceptor;
import org.tangerine.net.conn.Connection;
import org.tangerine.protocol.model.PacketMsg;
import org.tangerine.server.Server;

import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;

public abstract class HandlerRouter extends Component {

	private static final Log log = LogFactory.getLog(HandlerRouter.class);
	
	protected Map<String, HandlerWrapper> messageHandlers;
	
	@Override
	public void initialize() throws Exception {
		messageHandlers = new HashMap<String, HandlerWrapper>();
	}
	
	@Override
	public void start() throws Exception {}

	@Override
	public void stop() throws Exception {}
	
	public void register(String route, Object handler) {
		messageHandlers.put(route, new HandlerWrapper(handler));
	}
	
	protected abstract HandlerWrapper getHandler(RoutePath routePath);
	
	protected abstract Object handle(Connection conn, PacketMsg msg) throws Exception;
	
	
	protected void noHandlerFound(Connection connection, PacketMsg msg) {
		//TODO log
		log.warn("Handler No Found for msg:" + msg);
	}
	
	public void route(Connection conn, PacketMsg msg) throws Exception {
		
		List<HandlerInterceptor> interceptors = new ArrayList<HandlerInterceptor>(
						ComponentManager.instance().get(CoInterceptor.class).getAll());
		
		for (int i = 0; i < interceptors.size(); i++) {
			if (!interceptors.get(i).preHandle(conn, msg)) {
				return;
			}
		}
		
		Object result = handle(conn, msg);
		
		for (int i = interceptors.size()-1; i >= 0; i--) {
			interceptors.get(i).postHandle(conn, msg, result);
		}
		
		//需要响应的消息
		if (msg.getMessageId() != null) {
			conn.deliver(msg.getMessageId(), null, result);
		}
	}
	
	protected Object[] getHandlerMethodArgs(Method method, Connection connection, PacketMsg message) throws Exception {
		
		List<Object> args = new ArrayList<Object>();
		Class<?>[] types = method.getParameterTypes();
		for (Class<?> clz : types) {
			
			if (clz.isAssignableFrom(Connection.class)) {
				args.add(connection);
				
			} else if (clz.isAssignableFrom(PacketMsg.class)) {
				args.add(message);
				
			} else {
				try {
					if (ComponentManager.instance().get(Server.class).getUseProtobuf()) {
						//probuf
						@SuppressWarnings("rawtypes")
						Codec codec = ProtobufProxy.create(clz);
						args.add(codec.decode(message.getBody()));
					} else {
						//json
						args.add(JsonUtil.fromJson(StringUtil.decode(message.getBody()), clz));
					}
					
				} catch (Exception e) {
					args.add(null);
				}
			}
		}
		
		return args.toArray();
	}
}
