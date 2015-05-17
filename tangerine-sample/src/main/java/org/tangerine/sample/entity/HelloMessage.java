package org.tangerine.sample.entity;

import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;

public class HelloMessage {

	@Protobuf(fieldType = FieldType.INT64)
	private Long time;
	
	@Protobuf(fieldType = FieldType.STRING)
	private String msg;
	
	@Protobuf(fieldType = FieldType.STRING)
	private String from;
	
	@Protobuf(fieldType = FieldType.STRING)
	private String to;

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	@Override
	public String toString() {
		return "HelloMessage [time=" + time + ", msg=" + msg + ", from=" + from + ", to=" + to + "]";
	}
}
