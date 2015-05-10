package org.tangerine.container;

public interface Component {

	public void start();
	
	public void afterStart();
	
	public void stop();
}
