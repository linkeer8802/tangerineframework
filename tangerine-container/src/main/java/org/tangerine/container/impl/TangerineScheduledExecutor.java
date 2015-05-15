package org.tangerine.container.impl;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.tangerine.common.ConfigUtil;
import org.tangerine.container.Component;
import org.tangerine.container.ComponentDef;
/**
 * 定时执行任务组件
 * @author weird
 *
 */
@ComponentDef("scheduledExecutor")
public class TangerineScheduledExecutor extends Component {

	private int corePoolSize;
	private ScheduledExecutorService executorService;
	
	@Override
	public void initialize() throws Exception {
		executorService = Executors.newScheduledThreadPool(
				ConfigUtil.getInt("tangerine.scheduledExecutor.corePoolSize", Runtime.getRuntime().availableProcessors()) , 
				new ThreadFactory() {
					public Thread newThread(Runnable r) {
						Thread thread = new Thread(r);
						thread.setName("ScheduledExecutor Worker Thread");
						return thread;
					}
				});
	}
	
	public void start() throws Exception {

	}

	public void stop() throws Exception {
		if (executorService != null) {
			executorService.shutdown();
		}
	}

	public int getCorePoolSize() {
		return corePoolSize;
	}

	public void setCorePoolSize(int corePoolSize) {
		this.corePoolSize = corePoolSize;
	}
	
    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        return executorService.schedule(command, delay, unit);
    }
    
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        return executorService.schedule(callable, delay, unit);
    }
    
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        return executorService.scheduleAtFixedRate(command, initialDelay, period, unit);
    }
    
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        return executorService.scheduleWithFixedDelay(command, initialDelay, delay, unit);
    }
}
