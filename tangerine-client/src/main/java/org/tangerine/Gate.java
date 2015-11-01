package org.tangerine;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.IOException;
import java.net.Socket;

import org.tangerine.Constant.Config;
import org.tangerine.Constant.PacketType;
import org.tangerine.protocol.PacketHead;
import org.tangerine.util.JsonUtil;

public class Gate {

	public static void main(String[] args) throws Exception {
		Socket soc = new Socket("localhost", 8770);
		
		for (int i = 0; i < 10; i++) {
			System.out.println("write " + (i+1));
			net(soc);
			Thread.sleep(500);
		}
		
		Thread.sleep(1000 * 15);
		soc.close();
	}

	private static void net(Socket soc) throws IOException, Exception {
		AuthRequest req = new AuthRequest();
		req.setUname("linkeer");
		req.setPassword("111111");
		soc.getOutputStream().write(encode(req));
		
		byte[] bytes = new byte[1024];
		soc.getInputStream().read(bytes);
		AuthResponse authResponse = decode(bytes);
		System.out.println(authResponse);
	}
	
	private static byte[] encode(AuthRequest req) {
		
		ByteBuf data = Unpooled.wrappedBuffer(JsonUtil.toJsonBytes(req));
		
		ByteBuf hBuffer = Unpooled.buffer(PacketHead.getHeadLength() + data.readableBytes());
		hBuffer.writeByte(PacketType.PCK_AUTH);
		hBuffer.writeMedium(data.readableBytes());
		hBuffer.writeBytes(data);
		
		byte[] result = new byte[hBuffer.readableBytes()];
		hBuffer.readBytes(result);
		return result;
	}
	
	private static AuthResponse decode(byte[] bytes) throws Exception {
		ByteBuf hBuffer = Unpooled.wrappedBuffer(bytes);
		hBuffer.readByte();
		int size = hBuffer.readMedium();
		byte[] data = new byte[size];
		hBuffer.readBytes(data);
		return JsonUtil.fromJson(new String(data, Config.DEFAULT_CHARTSET), AuthResponse.class);
	}
}
