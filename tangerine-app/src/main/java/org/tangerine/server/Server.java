package org.tangerine.server;

import org.tangerine.common.ConfigUtil;
import org.tangerine.container.Component;
import org.tangerine.container.ComponentDef;
import org.tangerine.container.ComponentManager;
import org.tangerine.container.impl.TangerineScheduledExecutor;
import org.tangerine.handle.interceptor.CoInterceptor;
import org.tangerine.handle.interceptor.CoSpringBeanInterceptor;
import org.tangerine.handle.interceptor.HandlerInterceptor;
import org.tangerine.handle.router.AnnotationHandlerRouter;
import org.tangerine.handle.router.HandlerRouter;
import org.tangerine.handler.Handler;
import org.tangerine.net.connector.Connector;
import org.tangerine.net.connector.NettyConnector;

@ComponentDef("server")
public class Server extends Component {

	private Boolean useDict;
	
	private Integer heartbeat;
	
	private Boolean useProtobuf = false;
	
	private Connector connector;
	
	private HandlerRouter router;
	
	private TangerineScheduledExecutor scheduledExecutor;
	
	private CoInterceptor coInterceptor;
	
	public void initialize() {
		if (useDict == null) {
			useDict = ConfigUtil.getBool("tangerine.server.useDict", false);
		}
		if (heartbeat == null) {
			heartbeat = ConfigUtil.getInt("tangerine.server.heartbeat", 3);
		}
		if (useProtobuf == null) {
			useProtobuf = ConfigUtil.getBool("tangerine.server.useDict", false);
		}
		if (connector == null) {
			connector = new NettyConnector();
			ComponentManager.instance().addComponent(connector);
		}
		if (router == null) {
			router = new AnnotationHandlerRouter();
			ComponentManager.instance().addComponent(router);
		}
		if (coInterceptor == null) {
			coInterceptor = new CoSpringBeanInterceptor();
			ComponentManager.instance().addComponent(coInterceptor);
		}
		if (scheduledExecutor == null) {
			scheduledExecutor = new TangerineScheduledExecutor();
			ComponentManager.instance().addComponent(scheduledExecutor);
		}
	}
	
	public Server addInterceptor(HandlerInterceptor interceptor) {
		coInterceptor.add(interceptor);
		return this;
	}
	
	public Server bindHandler(Handler<?> handler) {
		connector.bindHandler(handler);
		return this;
	}
	
	public Server addMessageHandler(String route, Object handler) {
		router.register(route, handler);
		return this;
	}
	
	public void start() throws Exception {

	}

	public void afterStart() throws Exception {
	}

	public void stop() throws Exception {
	}

	public Boolean getUseDict() {
		return useDict;
	}

	public void setUseDict(Boolean useDict) {
		this.useDict = useDict;
	}

	public Integer getHeartbeat() {
		return heartbeat;
	}

	public void setHeartbeat(Integer heartbeat) {
		this.heartbeat = heartbeat;
	}

	public Boolean getUseProtobuf() {
		return useProtobuf;
	}

	public void setUseProtobuf(Boolean useProtobuf) {
		this.useProtobuf = useProtobuf;
	}

	public Connector getConnector() {
		return connector;
	}

	public void setConnector(Connector connector) {
		this.connector = connector;
	}

	public TangerineScheduledExecutor getScheduledExecutor() {
		return scheduledExecutor;
	}

	public void setScheduledExecutor(TangerineScheduledExecutor scheduledExecutor) {
		this.scheduledExecutor = scheduledExecutor;
	}

	public HandlerRouter getRouter() {
		return router;
	}

	public void setRouter(HandlerRouter router) {
		this.router = router;
	}
}
