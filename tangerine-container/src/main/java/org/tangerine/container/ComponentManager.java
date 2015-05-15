package org.tangerine.container;

import java.util.Map;

import org.tangerine.exception.ComponentNoFoundException;


public class ComponentManager {

	private static final ComponentManager instance = new ComponentManager();
	
	private Map<Class<? extends Component>, Component> componentsMap;
	
	private ComponentManager() {
	}
	
	public static ComponentManager instance() {
		return instance;
	}
	
	public synchronized void addComponent(Component component) {
		if (componentsMap.containsKey(component.getClass())) {
			//TODO
			System.err.println("duplicate add component" + component.getClass().getName() + ", will ignore it.");
		}
		componentsMap.put(component.getClass(), component);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Component> T get(Class<T> clz) {
		Component component = componentsMap.get(clz);
		if (component == null) {
			throw new ComponentNoFoundException("component["+component+"] not found in currnet context.");
		}
		return (T) component;
	}
}
