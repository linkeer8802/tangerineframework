package org.tangerine.server;

import org.tangerine.container.Component;
import org.tangerine.container.ComponentDef;
import org.tangerine.container.ComponentManager;
import org.tangerine.handle.interceptor.CoInterceptor;
import org.tangerine.handle.interceptor.CoSpringBeanInterceptor;
import org.tangerine.handle.interceptor.HandlerInterceptor;
import org.tangerine.net.connector.Connector;
import org.tangerine.net.gate.NettyGateAcceptor;

@ComponentDef("gateServer")
public class GateServer extends Component {

	private Connector connector;
	
	private CoInterceptor coInterceptor;
	
	public void initialize() {
		if (connector == null) {
			connector = new NettyGateAcceptor();
			ComponentManager.instance().addComponent(connector);
		}
		if (coInterceptor == null) {
			coInterceptor = new CoSpringBeanInterceptor();
			ComponentManager.instance().addComponent(coInterceptor);
		}
	}
	
	public GateServer addInterceptor(HandlerInterceptor interceptor) {
		coInterceptor.add(interceptor);
		return this;
	}
	
	public void start() throws Exception {

	}

	public void afterStart() throws Exception {
	}

	public void stop() throws Exception {
	}

	public Connector getConnector() {
		return connector;
	}

	public void setConnector(Connector connector) {
		this.connector = connector;
	}
}
