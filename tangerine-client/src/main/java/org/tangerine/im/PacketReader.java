package org.tangerine.im;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.tangerine.Constant.Config;
import org.tangerine.Constant.PacketType;
import org.tangerine.im.listener.DataCallBack;
import org.tangerine.im.listener.DataListener;
import org.tangerine.protocol.Message;
import org.tangerine.protocol.Packet;
import org.tangerine.protocol.PacketHead;
import org.tangerine.util.JsonUtil;
import org.tangerine.util.ParameterUtil;

import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;

public class PacketReader {

	private IMConnection connection;
	
	private Thread readerThread;
	
	private PacketHandler packetHandler;
	
	volatile boolean done;
	
	public PacketReader(IMConnection imConnection) {
		this.connection = imConnection;
		packetHandler = new PacketHandler();
		init();
	}
	
	public void init() {
		done = false;
		
		readerThread = new Thread(){
			@Override
			public void run() {
				readPackets(this);
			}
		};
		readerThread.setName("PacketReader thread.");
		readerThread.setDaemon(true);
	}

	public void startup() {
		readerThread.start();
	}
	
	public void shutdown() {
		done = true;
	}

	private void readPackets(Thread t) {
		int len = 0;
		int bufIndex = 0;
		byte[] buf = new byte[1024];
		ByteBuf byteBuf = Unpooled.buffer();
		
		try {
			while (!done) {
				//读取socket中的数据
				
				len = connection.getSocketInput().read(buf, bufIndex, buf.length-bufIndex);
				bufIndex = len + bufIndex;
				if (len == -1) {
//					System.out.println("remote server has closed the socket connection.");
					throw new IOException("remote server has closed the socket connection");
				}
				//触发读事件
				connection.getIdleStateHandler().onRead(connection);
				
				byteBuf.writeBytes(buf, 0, bufIndex);
				bufIndex = 0;
				//解析数据包
				Packet packet = parserPacket(byteBuf);
				
				if (packet != null) {
					
					dispatch(packet);
					
					bufIndex = byteBuf.readableBytes();
					if (bufIndex > 0) {
//						System.out.println("read " + bufIndex + " bytes to buf" );
						byteBuf.readBytes(buf, 0, bufIndex);
					}
					byteBuf.clear();
				}
			}

		} catch (Exception e) {
			
			System.err.println("read err.");
			
			 if (!(done || connection.isSocketClosed())) {
                shutdown();
                connection.notifyConnectionError(e);
		     }
		}
	}

	private Packet parserPacket(ByteBuf in) {
		//头长度
		if (in.readableBytes() < PacketHead.getHeadLength()) {
			return null;
		}
		
		//读取头
		in.markReaderIndex();
		PacketHead packetHead = PacketHead.decode(in);
		
		//消息体长度
		if (in.readableBytes() < packetHead.getLength()) {
			in.resetReaderIndex();
			return null;
		}
		
		/**
		 * 已读取到完整包数据
		 */
		Packet packet = new Packet();
		packet.setPacketHead(packetHead);
		
		/**
		 * 数据包
		 */
		if (packetHead.getType().equals(PacketType.PCK_DATA)) {
			// fix bug 用slice返回bytebuf的一个分片，可防止粘包
			packet.setBody(Message.decode(in.slice(in.readerIndex(), packetHead.getLength())));
			in.skipBytes(packetHead.getLength());
		} else {
			packet.setBody(in.readBytes(packetHead.getLength()));
		}
		
		return packet;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void dispatch(Packet packet) {
		
		try {
			if (packet.getBody() instanceof Message) {
				Message message = (Message) packet.getBody();
//				System.out.println("packet[" + packet.getPacketHead().getLength() + "]");
//				System.out.println("encode packet[" + Message.encode(message).readableBytes() + "]");
//				System.out.println("message[" + new String(message.getBody(), Config.DEFAULT_CHARTSET) + "]");
				Object data = null;
				
				//listener
				if (message.getRoutePath() != null) {
					List<DataListener<?>> listeners = connection.getDataListeners(message.getRoutePath());
					if (listeners != null) {
						for (DataListener listener : listeners) {
							data = getMessageBody(message, ParameterUtil.getSuperClassGenricType(listener.getClass()));
							listener.onData(data);
						}
					}
				}
				//callback
				if (message.getMessageId() != null) {
					DataCallBack dataCallBack = connection.getDataCallBack().get(message.getMessageId());
					if (dataCallBack != null) {
						data = getMessageBody(message, ParameterUtil.getSuperClassGenricType(dataCallBack.getClass()));
						dataCallBack.onResponse(data);
						connection.removeDataCallBack(message.getMessageId());
					}
				}
				
			} else {
				packetHandler.handle(connection, packet);
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Object getMessageBody(Message message, Class<?> dataClz) throws IOException,
			UnsupportedEncodingException {
		Object data;
		if (connection.getConfig().getUseProtobuf()) {
			//probuf
			@SuppressWarnings("rawtypes")
			Codec codec = ProtobufProxy.create(dataClz);
			data = codec.decode(message.getBody());
		} else {
			//json
			String json = new String(message.getBody(), Config.DEFAULT_CHARTSET);
//			System.out.println("json:[" + json + "]");
			data = JsonUtil.fromJson(json, dataClz);
		}
		return data;
	}
}
