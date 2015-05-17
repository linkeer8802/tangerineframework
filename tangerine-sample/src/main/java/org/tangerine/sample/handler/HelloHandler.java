package org.tangerine.sample.handler;

import org.tangerine.handle.router.RuoteMapping;
import org.tangerine.handler.annotation.Handler;
import org.tangerine.net.conn.Connection;
import org.tangerine.protocol.Message;
import org.tangerine.sample.entity.HelloMessage;

@Handler("hello")
public class HelloHandler {

	@RuoteMapping(value="sayHello")
	public void sayHello(Connection connection, String message) {
		System.out.println(message);
		connection.deliver(null, "onSayHello", "pull sayHello from server.");
	}
	
	@RuoteMapping(value="sayHello2")
	public String sayHello2(Connection connection, Message message, HelloMessage msg) {
		System.out.println(msg);
		return msg.getMsg();
	}
}
