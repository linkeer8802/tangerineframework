package org.tangerine;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tangerine.container.ComponentManager;
import org.tangerine.server.Server;


public class ServerApp {

	private static final Log log = LogFactory.getLog(ServerApp.class);
	
	private String name;
	private Server server;
	private ComponentManager componentManager;
	private static volatile boolean running = true;
	
	public ServerApp(String name) {
		this.name = name;
		componentManager = ComponentManager.instance();
	}
	
	public void run() throws Exception {
		long time = System.currentTimeMillis();
		
		this.server = new Server();
		componentManager.addComponent(server);
		
		componentManager.run();
		
		log.info(" App " + name + " started, cost time:" + (System.currentTimeMillis()-time) + "ms");
		
		synchronized (ServerApp.class) {
			while (running) {
				try {
					ServerApp.class.wait();
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

	public Server getServer() {
		return server;
	}

	public ComponentManager getComponentManager() {
		return componentManager;
	}

	public static boolean isRunning() {
		return running;
	}
}
