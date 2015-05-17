package org.tangerine.container;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tangerine.exception.ComponentNoFoundException;


public final class ComponentManager {

	private static final Log log = LogFactory.getLog(ComponentManager.class);
	
	protected static ComponentManager instance;
	
	private Map<Class<? extends Component>, Component> componentsMap;
	
	protected ComponentManager() {
		componentsMap = new ConcurrentHashMap<Class<? extends Component>, Component>();
	}
	
	public static ComponentManager instance() {
		if (instance == null) {
			instance = new ComponentManager();
		}
		return instance;
	}
	
	public void run() throws Exception {
		//加载依赖的组件
		for (Component component : componentsMap.values()) {
			
			Class<? extends Component>[] depends = component.depends();
			if (depends != null) {
				for (Class<? extends Component> dependComponent : depends) {
					if (componentsMap.get(dependComponent) == null) {
						Component co = dependComponent.newInstance();
						addComponent(co);
						log.info("Initialize Depend Component " + co.name());
					}
				}
			}
        }
		
		//启动组件
		for (Component component : componentsMap.values()) {
			component.start();
            log.info("Component " + component.name() + " started.");
        }
		for (Component component : componentsMap.values()) {
			component.afterStart();
        }
	}
	
	public void destroy() throws Exception {
		for (Component component : componentsMap.values()) {
			component.stop();
            log.info("Component " + component.name() + " stoped.");
        }
	}
	
	public synchronized void addComponent(Component component) {
		if (componentsMap.containsKey(component.getClass())) {
			log.warn("duplicate add component" + component.getClass().getName() + ", will ignore it.");
		}
		componentsMap.put(component.getClass(), component);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Component> T get(Class<T> clz) {
		Component component = componentsMap.get(clz);
		
		if (component == null) {
			for (Component co : componentsMap.values()) {
				if (clz.isInstance(co)) {
					return (T) co;
				}
			}
		} else {
			return (T) component;
		}
		
		throw new ComponentNoFoundException("component["+component+"] not found in currnet context.");
	}
	
	public Component get(String name) {
		for (Component component : componentsMap.values()) {
			if (component.name().equals(name)) {
				return component;
			}
		}
		return null;
	}
}
