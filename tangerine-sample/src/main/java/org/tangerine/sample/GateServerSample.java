package org.tangerine.sample;

import org.tangerine.GateServerApp;
import org.tangerine.container.impl.CoSimpleAuthManager;

public class GateServerSample {

	public static void main(String[] args) throws Exception {
		GateServerApp app = new GateServerApp("gate server");
		app.getComponentManager().addComponent(new CoSimpleAuthManager());
		app.run();
	}

}
