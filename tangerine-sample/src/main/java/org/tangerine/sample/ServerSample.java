package org.tangerine.sample;

import org.tangerine.ServerApp;
import org.tangerine.container.impl.CoSimpleAuthManager;

public class ServerSample {

	public static void main(String[] args) throws Exception {
		ServerApp app = new ServerApp("chart server");
		app.getComponentManager().addComponent(new CoSimpleAuthManager());
		app.run();
	}
}
