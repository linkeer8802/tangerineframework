package org.tangerine;

import org.tangerine.im.TangerineClient;
import org.tangerine.im.listener.DataCallBack;

public class PressureTest {

	private static final int num = 10000;
	private static final String host = "192.168.1.147";
	
	public static void main(String[] args) throws Exception {
		int success = 0;
		int failure = 0;
		
		for (int i = 1; i <= num; i++) {
			final TangerineClient client = new TangerineClient(host, 9770);
			try {
				client.connect();
				success++;
			} catch (Exception e) {
				e.printStackTrace();
				failure++;
			}
//			request(client, i);
			Thread.sleep((long) (Math.random() * 50));
			System.out.println("exec " + i + ",success=" + success + ",failure=" + failure);
		}
		
		Thread.sleep(Integer.MAX_VALUE);
	}
	
	public static void request(TangerineClient client, int index) {
		client.sendRequest("chart.hello.sayHello2", "hello i'm a new user" + index, new DataCallBack<String>() {
		public void onResponse(String msg) throws Exception {
			System.out.println(msg);
		}
	});
	}
}
