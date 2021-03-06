package org.tangerine.net.connector;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tangerine.common.ConfigUtil;
import org.tangerine.common.json.JsonUtil;
import org.tangerine.container.ComponentDef;
import org.tangerine.container.ComponentManager;
import org.tangerine.container.impl.RouteDictionary;
import org.tangerine.exception.DecodeException;
import org.tangerine.net.conn.ConnectionManager;
import org.tangerine.protocol.Message;
import org.tangerine.protocol.Packet;
import org.tangerine.server.Server;

@ComponentDef("nettyConnector")
public class NettyConnector extends Connector {
	
	private static final Log log = LogFactory.getLog(NettyConnector.class);
	
	private Channel channel;
	protected NettyIOSocket ioSocket;
	
	protected Thread connectorThread;
	
	@Override
	public void initialize() throws Exception {
		
		this.host = ConfigUtil.get("tangerine.connector.host", "localhost");
		this.port = ConfigUtil.getInt("tangerine.connector.port", 9770);
		
		ioSocket = NettyIOSocket.instance();
		connectorThread = new Thread(run());
		connectorThread.setDaemon(true);
		connectorThread.setName(name() + " Thread.");
		
		if (connectionManager == null) {
			connectionManager = new ConnectionManager();
			ComponentManager.instance().addComponent(connectionManager);
		}
	}
	
	protected Runnable run() throws Exception {
		return new Runnable() {
			@Override
			public void run() {
				NioEventLoopGroup bossGroup = new NioEventLoopGroup();
				NioEventLoopGroup workerGroup = new NioEventLoopGroup();
				
				try {
					ServerBootstrap bootstrap = new ServerBootstrap();
					bootstrap.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class) 
					.handler(new ChannelInitializer<ServerSocketChannel>(){
						@Override
						protected void initChannel(ServerSocketChannel ch) throws Exception {
//							ch.pipeline().addLast("log", new LoggingHandler(LogLevel.INFO));
						}
						
					}).childHandler(new ServerChannelInitializer())
					.option(ChannelOption.SO_BACKLOG, 1024);
					
					channel = bootstrap.bind(host, port).sync().channel();
					channel.closeFuture().sync();
				} catch (InterruptedException e) {
					log.warn("Interrupted + "+name()+" + Thread.");
				} finally {
					bossGroup.shutdownGracefully();
					workerGroup.shutdownGracefully();
				}
			}
		};
	}
	
	@Override
	public void start() throws Exception {
		connectorThread.start();
	}

	@Override
	public void stop() {
		channel.close();
	}
	
	class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {

		@Override
		public void initChannel(SocketChannel ch) throws Exception {
			ch.pipeline().addLast("packetDecoder", _decode());
			ch.pipeline().addLast("packetEncoder", _encode());
			ch.pipeline().addLast("ping", new IdleStateHandler(0, 0,
					ComponentManager.instance().get(Server.class).getHeartbeat()*2));
			ch.pipeline().addLast("ioSocket",  ioSocket);
		}
	}
	
	@Override
	public Object decode(ByteBuf in) {
		
		in.markReaderIndex();
		
		Packet packet = Packet.decode(in);
		
		if (packet == null) {
			in.resetReaderIndex();
			return null;
		}

		/**
		 * 数据包
		 */
		if (packet.getType().equals(Packet.Type.PCK_DATA)) {
			Message message = Message.decode(packet.getPayload());
			packet.getPayload().release();
			return message;
		}
		
		return packet;
	}
	
	@Override
	public ByteBuf encode(Integer messageId, String route, Object msg) {
		
		Server server = ComponentManager.instance().get(Server.class);
		
		Message message = new Message();
		
		if (messageId == null) {
			message.setMessageType(Message.Type.MSG_PUSH);
		} else {
			message.setMessageType(Message.Type.MSG_RESPONSE);
			message.setMessageId(messageId);
		}
		
		if (route == null) {
			message.setRouteFlag(server.getUseDict());
		} else {
			
			String routePath = route;
			boolean routeFlag = false;
			
			if (server.getUseDict()) {
				Short routeId = ComponentManager.instance().get(RouteDictionary.class).getRouteId(route);
				if (routeId != null) {
					routePath = routeId.toString();
					routeFlag = true;
				}
			}
			message.setRouteFlag(routeFlag);
			message.setRoutePath(routePath);
		}
		message.setBody(Unpooled.wrappedBuffer(JsonUtil.toJsonBytes(msg)));
		
		Packet packet = new Packet(Packet.Type.PCK_DATA);
		packet.setPayload(Message.encode(message));
		packet.setLength(packet.getPayload().readableBytes());
		
		return Packet.encode(packet);
	}
	
	private ByteToMessageDecoder _decode() {
		return new ByteToMessageDecoder(){
			@Override
			protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
				Object message = null;
				try {
					message = NettyConnector.this.decode(in);
				} catch (Exception e) {
					throw new DecodeException(e);
				}
				if (message != null) {
					out.add(message);
				} 
			}
		};
	}
	
	private MessageToByteEncoder<Packet> _encode() {
		return new MessageToByteEncoder<Packet>(){
			@Override
			protected void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf out) throws Exception {
				ByteBuf encode = Packet.encode(packet);
				out.writeBytes(encode);
				encode.release();
			}
		};
	}
	
//	class PacketDecoder extends ChannelInboundHandlerAdapter {
//		
//		private final Log log = LogFactory.getLog(PacketDecoder.class);
//		
//		private ByteBuf cumulation;
//		
//		@Override
//		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//			if (msg instanceof ByteBuf) {
//				try {
//					ByteBuf data = (ByteBuf) msg;
//					if (cumulation == null) {
//					    cumulation = data;
//					} else {
//						if (cumulation.writerIndex() > cumulation.maxCapacity() - data.readableBytes()) {
//							log.info("expandCumulation...");
//							expandCumulation(ctx, data.readableBytes());
//						}
//						cumulation.writeBytes(data);
//						data.release();
//					}
//					Object message = NettyConnector.this.decode(cumulation);
//					if (message != null) {
//						ctx.fireChannelRead(message);
//					}
//				} catch (Exception e) {
//					throw new DecodeException(e);
//				}finally {
//	                if (cumulation != null && !cumulation.isReadable()) {
//	                    cumulation.release();
//	                    cumulation = null;
//	                }
//				}
//			} else {
//				ctx.fireChannelRead(msg);
//			}
//		}
//		
//	    private void expandCumulation(ChannelHandlerContext ctx, int readable) {
//	        ByteBuf oldCumulation = cumulation;
//	        cumulation = ctx.alloc().buffer(oldCumulation.readableBytes() + readable);
//	        cumulation.writeBytes(oldCumulation);
//	        oldCumulation.release();
//	    }
//	}
}
