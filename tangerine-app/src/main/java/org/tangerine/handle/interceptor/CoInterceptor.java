package org.tangerine.handle.interceptor;

import java.util.LinkedHashSet;
import java.util.Set;

import org.tangerine.container.Component;

public abstract class CoInterceptor extends Component {

	protected Set<HandlerInterceptor> interceptors;
	
	public void add(HandlerInterceptor interceptor) {
		interceptors.add(interceptor);
	}
	
	public Set<HandlerInterceptor> getAll() {
		return interceptors;
	}
	
	@Override
	public void start() throws Exception {
		interceptors = new LinkedHashSet<HandlerInterceptor>();
	}
}
