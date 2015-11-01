package org.tangerine;

import org.tangerine.im.TangerineClient;

public class Main {

	public static void main(String[] args) throws Exception {
		String host = "localhost";
//		String host = "121.199.65.49";
		final TangerineClient client = new TangerineClient(host, 9770);
		client.connect();
		
		client.login("linkeer", "111111");
		
//		client.sendRequest("chart.userAuth.login", auth, new DataCallBack<Boolean>() {
//			public void onResponse(Boolean msg) throws Exception {
//				System.out.println(msg);
//			}
//		});
//		
//		client.on("onNotify", new DataListener<NotifyMessage>() {
//			public void onData(NotifyMessage data) {
//				System.out.println(data.getContent());
//			}
//		});
		
//		client.waitForClose();
		Thread.sleep(Integer.MAX_VALUE);
	}
}
