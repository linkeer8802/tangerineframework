package org.tangerine.container;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class Component {

	Log log = LogFactory.getLog(Component.class);
	
	public Component() {
		try {
			initialize();
		} catch (Exception e) {
			log.error("initialize component[" + name() + "] failure", e);
		}
	}
	/**
	 * 组件的名称
	 * @return
	 */
	protected String name() {
		ComponentDef def = this.getClass().getAnnotation(ComponentDef.class);
		return def == null ? this.getClass().getSimpleName() : def.value();
	}
	
	/**
	 * 依赖的组件
	 * @return
	 */
	protected Class<? extends Component>[] depends() {
		ComponentDef def = this.getClass().getAnnotation(ComponentDef.class);
		return def == null ? null : def.depends();
	}
	
	protected abstract void initialize() throws Exception;
	
	public abstract void start() throws Exception;
	
	public void afterStart() throws Exception {};
	
	public abstract void stop() throws Exception;
}
