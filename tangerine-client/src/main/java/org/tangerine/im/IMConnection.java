package org.tangerine.im;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeoutException;

import org.tangerine.im.IdleStateHandler.IdleState;
import org.tangerine.im.listener.ConnectionListener;
import org.tangerine.im.listener.DataCallBack;
import org.tangerine.im.listener.DataListener;
import org.tangerine.protocol.Packet;

public class IMConnection {

	Socket socket;
	
	private String host;
	
	private int port;
	
	PacketWriter packetWriter;
	
    PacketReader packetReader;
    
    private volatile boolean socketClosed = true;
    
    private volatile boolean connected = false;
    
    protected final Configuration config;
    
    private ScheduledExecutorService scheduler;
    
    private volatile long heartbeatLastTime;
    
    private IdleStateHandler idleStateHandler = new IdleStateHandler();
    
    protected final Collection<ConnectionListener> connectionListeners =
            					new CopyOnWriteArrayList<ConnectionListener>();
    
    protected final Map<Integer, DataCallBack<?>> dataCallBacks =
								new ConcurrentHashMap<Integer, DataCallBack<?>>();
    
    protected final Map<String, List<DataListener<?>>> dataListeners =
            					new ConcurrentHashMap<String, List<DataListener<?>>>();
    
    
    public IMConnection(String host, int port) {
    	this(new Configuration(host, port));
	}
    
    public IMConnection(Configuration config) {
    	this.config = config;
    	scheduler = Executors.newSingleThreadScheduledExecutor();
	}
    
    public void connect() throws Exception {
    	try {
			this.socket = new Socket(config.getHost(), config.getPort());
			socketClosed = false;
			initConect();
			synchronized (this) {
				this.wait(3000);
			}
			if (!isConnected()) {
				throw new TimeoutException("connect timeout.");
			} else {
//				System.out.println("连接成功");
			}
		} catch (SocketException e) {
			System.out.println("connect异常.");
			callConnectionClosedOnErrorListener(e);
			throw new Exception(e);
		} catch (Exception e) {
			e.printStackTrace();
		} 
    }

	private void initConect() throws Exception {
		 boolean isFirstInitialization = packetReader == null || packetWriter == null;
		 
		 if (isFirstInitialization) {
			 packetReader = new PacketReader(this);
			 packetWriter = new PacketWriter(this);
		 } else {
			 packetWriter.init();
			 packetReader.init();
		 }
		 
         packetWriter.startup();
         packetReader.startup();

         connected = true;
         
         callConnectionConnectedListener();
	}
	
	public void close() {
		synchronized(this) {
			connected = false;
			socketClosed = true;
			
			try {
				socket.close();
				packetReader.shutdown();
				packetWriter.shutdown();
				
				packetReader = null;
				packetWriter = null;
				socket = null;
				
			} catch (IOException e) {
				e.printStackTrace();
				
			}
			finally {
				idleStateHandler.destroy();
			}
		}
	}
	/**
	 * 空闲检测
	 * @param conn
	 * @param state
	 * @throws Exception
	 */
	public void onIdle(IMConnection conn, IdleState state) throws Exception {
		
        if (state == IdleState.FIRST_READER_IDLE_STATE || state == IdleState.READER_IDLE_STATE ) {
//        	System.out.println("读空闲超时，断开重连...");
        	notifyConnectionError(new TimeoutException("heartbeat timeOut."));
        	
        } else if (state == IdleState.FIRST_WRITER_IDLE_STATE || state == IdleState.WRITER_IDLE_STATE) {
//        	System.out.println("发送心跳包...");
        	getPacketWriter().send(Packet.buildHeartbeatPacket());
        }
	}
	
   public synchronized void notifyConnectionError(Exception e) {
        
	   if ((packetReader == null || packetReader.done) &&
                (packetWriter == null || packetWriter.done)) return;

        close();

        callConnectionClosedOnErrorListener(e);
    }
	
	public void callConnectionConnectedListener() throws Exception {
		for (ConnectionListener connectionListener : connectionListeners) {
			connectionListener.connected(this);
		}
	}
	
    void callConnectionClosedListener() {
		for (ConnectionListener connectionListener : connectionListeners) {
			connectionListener.connectionClosed();
		}
    }

    protected void callConnectionClosedOnErrorListener(Exception e) {
		for (ConnectionListener connectionListener : connectionListeners) {
			connectionListener.connectionClosedOnError(e);
		}
    }
	
	public void addConnectionListener(ConnectionListener listener) {
		connectionListeners.add(listener);
	}
	
	public void removeConnectionListener(ConnectionListener listener) {
		connectionListeners.remove(listener);
	}
	
	public Map<Integer, DataCallBack<?>> getDataCallBack() {
		return dataCallBacks;
	}
	
	public void addDataCallBack(Integer messageId, DataCallBack<?> dataCallBack) {
		dataCallBacks.put(messageId, dataCallBack);
	}
	
	public void removeDataCallBack(Integer messageId) {
		dataCallBacks.remove(messageId);
	}
	
	public List<DataListener<?>> getDataListeners(String route) {
		if (dataListeners.isEmpty()) {
			return null;
		} 
		return dataListeners.get(route);
	}
	
	public void addDataListener(String route, DataListener<?> listener) {
		List<DataListener<?>> listeners = dataListeners.get(route);
		if (listeners == null) {
			listeners = new ArrayList<DataListener<?>>();
		}
		listeners.add(listener);
		dataListeners.put(route, listeners);
	}
	
//	public void removeDataListener(DataListener listener) {
//		dataListeners.remove(listener);
//	}
	
	public InputStream getSocketInput() throws IOException {
		return this.socket.getInputStream();
	}
	
	public OutputStream getSocketOutput() throws IOException {
		return this.socket.getOutputStream();
	}

	public PacketWriter getPacketWriter() {
		return packetWriter;
	}

	public void setPacketWriter(PacketWriter packetWriter) {
		this.packetWriter = packetWriter;
	}

	public PacketReader getPacketReader() {
		return packetReader;
	}

	public void setPacketReader(PacketReader packetReader) {
		this.packetReader = packetReader;
	}

	public Configuration getConfig() {
		return config;
	}

	public boolean isSocketClosed() {
		return socketClosed;
	}

	public boolean isConnected() {
		return connected;
	}
	
	public ScheduledExecutorService getScheduledExecutorService() {
		return scheduler;
	}

	public long getHeartbeatLastTime() {
		return heartbeatLastTime;
	}

	public void setHeartbeatLastTime(long heartbeatLastTime) {
		this.heartbeatLastTime = heartbeatLastTime;
	}

	public IdleStateHandler getIdleStateHandler() {
		return idleStateHandler;
	}
}
