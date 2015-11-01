package org.tangerine.im.listener;

public abstract class DataCallBack<T> {

	public abstract void onResponse(T msg) throws Exception;
}
