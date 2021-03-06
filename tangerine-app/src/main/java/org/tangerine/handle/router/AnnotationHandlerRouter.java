package org.tangerine.handle.router;

import java.lang.reflect.Method;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.tangerine.container.ComponentDef;
import org.tangerine.container.ComponentManager;
import org.tangerine.container.impl.CoSpringSupport;
import org.tangerine.exception.ComponentNoFoundException;
import org.tangerine.handler.annotation.Handler;
import org.tangerine.net.conn.Connection;
import org.tangerine.protocol.model.PacketMsg;

@ComponentDef(value="annotationHandlerRouter", depends={CoSpringSupport.class})
public class AnnotationHandlerRouter extends HandlerRouter {

	
	private void _register(String name, Object handler) {
		
		for (Method method : handler.getClass().getDeclaredMethods()) {
			
			RuoteMapping mapping = method.getAnnotation(RuoteMapping.class);
			if (mapping == null) {
				continue;
			}
			
			String routePath = StringUtils.uncapitalize(name == "" ?
					handler.getClass().getSimpleName().replace("Handler", "") : name) + "." + mapping.value();
			register(routePath, handler);
		}
	}
	

	@Override
	public Object handle(Connection conn, PacketMsg msg) throws Exception {
		
		RoutePath routePath = new RoutePath(msg.getRoute());
		
		HandlerWrapper handlerWrapper = getHandler(routePath);
		
		if (handlerWrapper != null) {
			Object handler = handlerWrapper.getHandler();
			if (handler != null) {
				for (Method method : handler.getClass().getDeclaredMethods()) {
					
					RuoteMapping messageMapping = method.getAnnotation(RuoteMapping.class);
					if (routePath.getAction().equals(messageMapping.value())) {
						
						return method.invoke(handler, 
								getHandlerMethodArgs(method, conn, msg));
					}
				}
			}
		}
		
		noHandlerFound(conn, msg);
		return null;
	}
	
	protected HandlerWrapper getHandler(RoutePath routePath) {
		String handlerName = routePath.getHandler();
		if (handlerName.endsWith("Handler")) {
			handlerName = handlerName.replace("Handler", "");
		}
		return messageHandlers.get(handlerName + "." + routePath.getAction());
	}
	
	@Override
	public void afterStart() throws Exception {
		
		super.afterStart();
		
		try {
			Map<String, Object> handlers = ComponentManager.instance()
							.get(CoSpringSupport.class)
							.getContext().getBeansWithAnnotation(Handler.class);
			
			for (Object handler : handlers.values()) {
				_register(handler.getClass().getAnnotation(Handler.class).value(), handler);
			}
			
		} catch (ComponentNoFoundException e) {
			throw new IllegalStateException("depend component [CoSpringSupport], please add it to the current context.");
		}
	}
}
