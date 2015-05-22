package org.tangerine;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tangerine.container.ComponentManager;
import org.tangerine.server.GateServer;


public class GateServerApp {

	private static final Log log = LogFactory.getLog(GateServerApp.class);
	
	private String name;
	private GateServer server;
	private ComponentManager componentManager;
	private static volatile boolean running = true;
	
	public GateServerApp(String name) {
		this.name = name;
		componentManager = ComponentManager.instance();
	}
	
	public void run() throws Exception {
		long time = System.currentTimeMillis();
		
		this.server = new GateServer();
		componentManager.addComponent(server);
		componentManager.run();
		
		log.info(" App " + name + " started, cost time:" + (System.currentTimeMillis()-time) + "ms");
		
		synchronized (GateServerApp.class) {
			while (running) {
				try {
					GateServerApp.class.wait();
				} catch (Throwable e) {
				}
			}
		}
	}
	
	public void destroy() throws Exception {
		running = false;
		componentManager.destroy();
	}

	public String getName() {
		return name;
	}

	public GateServer getServer() {
		return server;
	}

	public ComponentManager getComponentManager() {
		return componentManager;
	}

	public static boolean isRunning() {
		return running;
	}
}
