package org.tangerine.sample;

import org.tangerine.ServerApp;

public class ServerSample {

	public static void main(String[] args) throws Exception {
		ServerApp app = new ServerApp("chart server");
		app.run();
	}

}
