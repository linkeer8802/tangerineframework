package org.tangerine.im;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.tangerine.Constant.PacketType;
import org.tangerine.protocol.Message;
import org.tangerine.protocol.Packet;
import org.tangerine.protocol.PacketHead;

public class PacketWriter {

	private IMConnection connection;
	
	private Thread writerThread;
	
	volatile boolean done;
	
	private ArrayBlockingQueue<Packet> queue = new ArrayBlockingQueue<Packet>(1024);
	
	public PacketWriter(IMConnection imConnection) {
		this.connection = imConnection;
		init();
	}

	public void init() {
		done = false;
		writerThread = new Thread(){
			@Override
			public void run() {
				writePackets(this);
			}
		};
		writerThread.setName("PacketWriter Thread.");
		writerThread.setDaemon(true);
	}
	
	public void startup() {
		writerThread.start();
	}
	
	public void shutdown() {
		done = true;
		if (writerThread.isAlive()) {
			writerThread.interrupt();
		}
	}
	
	public void writePackets(Thread t) {
		try {
			while (!done) {
				Packet packet = queue.take();
				writeAndFlushIfNecessary(packet);
			}
		} catch (Exception e) {
			 if (!(done || connection.isSocketClosed())) {
                shutdown();
                System.err.println("write err.");
                connection.notifyConnectionError(e);
	         }
		}
	}

	private void writeAndFlushIfNecessary(Packet packet) throws Exception {
		if (!done) {
			connection.getSocketOutput().write(encodePacket(packet));
			connection.getSocketOutput().flush();
			//触发写事件
			connection.getIdleStateHandler().onWrite(connection);
		} else {
			throw new Exception("Not Connected Exception.");
		}
//		//心跳超时检测
//		handleHeartbeatTimeout(packet);
	}

//	private void handleHeartbeatTimeout(Packet packet) {
//		if (packet.getPacketHead().getType().equals(PacketType.PCK_HEARTBEAT)) {
//			
////			System.out.println("client send Heartbeat packet at:" + new Date());
//			final int timeout = connection.getConfig().getHeartbeat() * 2 * 1000;
//			
//			connection.getScheduledExecutorService().schedule(new Runnable() {
//				@Override
//				public void run() {
//					
//					long diffTime = System.currentTimeMillis() - connection.getHeartbeatLastTime();
//					
////					System.out.println("diffTime=" + diffTime);
//					if (diffTime > timeout) {
//						System.out.println("timeout in:" + diffTime);
//						connection.notifyConnectionError(new TimeoutException("heartbeat timeOut."));
//					}
//				}
//			}, timeout, TimeUnit.MILLISECONDS);
//		}
//	}
	
	public void send(Packet packet) throws Exception {
		if (done) {
			throw new Exception("Not Connected Exception.");
		}
		try {
			queue.put(packet);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void delaySend(final Packet packet, long delay) {
		connection.getScheduledExecutorService().schedule(new Runnable() {
			public void run() {
				try {
					writeAndFlushIfNecessary(packet);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, delay, TimeUnit.MILLISECONDS);
	}
	
	public byte[] encodePacket(Packet packet) {
		ByteBuf body = null;
		ByteBuf out = Unpooled.buffer();
		
		if (packet.getPacketHead().getType().equals(PacketType.PCK_DATA) &&
				packet.getBody() instanceof Message) {
			
			body = Message.encode((Message) packet.getBody());
			
		} else {
			if (packet.getBody() != null) {
				body = (ByteBuf) packet.getBody();
			}
		}
		
		/**
		 * 数据包
		 */
		if (body != null) {
			packet.getPacketHead().setLength(body.readableBytes());
			out.writeBytes(PacketHead.encode(packet.getPacketHead()));
			out.writeBytes(body);
		} else {
			packet.getPacketHead().setLength(0);
			out.writeBytes(PacketHead.encode(packet.getPacketHead()));
		}
		byte[] buf = new byte[out.readableBytes()];
		out.readBytes(buf);
		out.release();
		return buf;
	}
}
