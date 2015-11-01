package org.tangerine.im.listener;

import java.io.IOException;
import java.net.ConnectException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.tangerine.im.IMConnection;

public class ReConnectionListener extends ConnectionListenerAdapter {

//	private ScheduledExecutorService schedule;
	
	private IMConnection connection;
	
//	boolean done = false;
	
	public ReConnectionListener(IMConnection connection) {
		this.connection = connection;
	}
	
//	static {
//		IMConnection.addConnectionListener(new ConnectionListenerAdapter(){
//			@Override
//			public void connected(IMConnection connection) {
//				IMConnection.addConnectionListener(new ReConnectionListener(connection));
//			}
//		});
//	}
	
    private boolean isReconnectionAllowed() {
        return /*!done &&*/ !connection.isConnected();
    }
	
	public synchronized void reconnect() {
		if (!this.isReconnectionAllowed()) {
			return;
		}
		
//		if (schedule == null || schedule.isShutdown()) {
//			schedule = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
//				public Thread newThread(Runnable runnable) {
//					Thread thread = new Thread(runnable);
//					thread.setDaemon(true);
//					thread.setName("Reconnection Daemon Thread.");
//					return thread;
//				}
//			});
//		}
		
//		System.out.println("等待5秒后重连...");
		
		connection.getScheduledExecutorService().schedule(new Runnable() {
			public void run() {
				if (isReconnectionAllowed()) {
//					System.out.println("正在尝试重连...");
					try {
						connection.connect();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}, 5, TimeUnit.SECONDS);
	}
	
	@Override
	public void connectionClosedOnError(Exception e) {
		if (e instanceof IOException 
				|| e instanceof ConnectException 
				|| e instanceof TimeoutException) {
			
			reconnect();
		} else {
			e.printStackTrace();
		}
	}
}
