package org.tangerine.handle.interceptor;

import java.util.Map;

import org.tangerine.container.ComponentDef;
import org.tangerine.container.ComponentManager;
import org.tangerine.container.impl.CoSpringSupport;
import org.tangerine.exception.ComponentNoFoundException;

@ComponentDef(value="springBeanInterceptor", depends={CoSpringSupport.class})
public class CoSpringBeanInterceptor extends CoInterceptor{

	@Override
	public void initialize() throws Exception {}
	
	@Override
	public void afterStart() throws Exception {
		try {
			Map<String, HandlerInterceptor> interceptorsMap 
				= ComponentManager.instance().get(CoSpringSupport.class)
				.getContext().getBeansOfType(HandlerInterceptor.class);
			if (interceptors != null) {
				for (HandlerInterceptor interceptor : interceptorsMap.values()) {
					add(interceptor);
				}
			}
		} catch (ComponentNoFoundException e) {
			throw new IllegalStateException("depend SpringSupportComponent, please add the component to the current context.");
		}
	}

	@Override
	public void stop() throws Exception {
	}
}
