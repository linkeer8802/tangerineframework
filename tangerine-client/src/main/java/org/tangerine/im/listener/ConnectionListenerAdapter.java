package org.tangerine.im.listener;

import org.tangerine.im.IMConnection;

public class ConnectionListenerAdapter implements ConnectionListener {

	public void connected(IMConnection connection) {}

	public void connectionClosed() {}

	public void connectionClosedOnError(Exception e) {}

}
