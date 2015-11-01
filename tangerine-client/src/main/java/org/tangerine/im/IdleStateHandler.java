package org.tangerine.im;


import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class IdleStateHandler {

	   private static final long MIN_TIMEOUT_NANOS = TimeUnit.MILLISECONDS.toNanos(1);

	    private long readerIdleTimeNanos;
	    private long writerIdleTimeNanos;
	    private long allIdleTimeNanos;

	    volatile ScheduledFuture<?> readerIdleTimeout;
	    volatile long lastReadTime;
	    private boolean firstReaderIdleEvent;

	    volatile ScheduledFuture<?> writerIdleTimeout;
	    volatile long lastWriteTime;
	    private boolean firstWriterIdleEvent;

	    volatile ScheduledFuture<?> allIdleTimeout;
	    private boolean firstAllIdleEvent;

	    private volatile int state; // 0 - none, 1 - initialized, 2 - destroyed

	    public IdleStateHandler(){
	    	firstReaderIdleEvent = true;
	    	firstWriterIdleEvent = true;
	    	firstAllIdleEvent = true;
	    }

	    private void _init(long readerIdleTime, long writerIdleTime,
	    		long allIdleTime, TimeUnit unit) {
	        if (unit == null) {
	            throw new NullPointerException("unit");
	        }

	        if (readerIdleTime <= 0) {
	            readerIdleTimeNanos = 0;
	        } else {
	            readerIdleTimeNanos = Math.max(unit.toNanos(readerIdleTime), MIN_TIMEOUT_NANOS);
	        }
	        if (writerIdleTime <= 0) {
	            writerIdleTimeNanos = 0;
	        } else {
	            writerIdleTimeNanos = Math.max(unit.toNanos(writerIdleTime), MIN_TIMEOUT_NANOS);
	        }
	        if (allIdleTime <= 0) {
	            allIdleTimeNanos = 0;
	        } else {
	            allIdleTimeNanos = Math.max(unit.toNanos(allIdleTime), MIN_TIMEOUT_NANOS);
	        }
	    }

	    /**
	     * Return the readerIdleTime that was given when instance this class in milliseconds.
	     *
	     */
	    public long getReaderIdleTimeInMillis() {
	        return TimeUnit.NANOSECONDS.toMillis(readerIdleTimeNanos);
	    }

	    /**
	     * Return the writerIdleTime that was given when instance this class in milliseconds.
	     *
	     */
	    public long getWriterIdleTimeInMillis() {
	        return TimeUnit.NANOSECONDS.toMillis(writerIdleTimeNanos);
	    }

	    /**
	     * Return the allIdleTime that was given when instance this class in milliseconds.
	     *
	     */
	    public long getAllIdleTimeInMillis() {
	        return TimeUnit.NANOSECONDS.toMillis(allIdleTimeNanos);
	    }


	    public void onRead(IMConnection conn) throws Exception {
	        lastReadTime = System.nanoTime();
	        firstReaderIdleEvent = firstAllIdleEvent = true;
	    }

	    public void onWrite(IMConnection conn) throws Exception {
	    	lastWriteTime = System.nanoTime();
            firstWriterIdleEvent = firstAllIdleEvent = true;
	    }

	    public void initialize(IMConnection conn,int readerIdleTimeSeconds,
	            int writerIdleTimeSeconds, int allIdleTimeSeconds) {
	    	
	    	_init(readerIdleTimeSeconds, writerIdleTimeSeconds, allIdleTimeSeconds,
            TimeUnit.SECONDS);
	    	
	        switch (state) {
	        case 1:
//	        case 2:
	            return;
	        }

	        state = 1;

	        ScheduledExecutorService loop = conn.getScheduledExecutorService();

	        lastReadTime = lastWriteTime = System.nanoTime();
	        if (readerIdleTimeNanos > 0) {
	            readerIdleTimeout = loop.schedule(
	                    new ReaderIdleTimeoutTask(conn),
	                    readerIdleTimeNanos, TimeUnit.NANOSECONDS);
	        }
	        if (writerIdleTimeNanos > 0) {
	            writerIdleTimeout = loop.schedule(
	                    new WriterIdleTimeoutTask(conn),
	                    writerIdleTimeNanos, TimeUnit.NANOSECONDS);
	        }
	        if (allIdleTimeNanos > 0) {
	            allIdleTimeout = loop.schedule(
	                    new AllIdleTimeoutTask(conn),
	                    allIdleTimeNanos, TimeUnit.NANOSECONDS);
	        }
	    }

	    public void destroy() {
	        state = 2;

	        if (readerIdleTimeout != null) {
	            readerIdleTimeout.cancel(false);
	            readerIdleTimeout = null;
	        }
	        if (writerIdleTimeout != null) {
	            writerIdleTimeout.cancel(false);
	            writerIdleTimeout = null;
	        }
	        if (allIdleTimeout != null) {
	            allIdleTimeout.cancel(false);
	            allIdleTimeout = null;
	        }
	    }

	    protected void onIdle(IMConnection conn, IdleState state) throws Exception {
	    	conn.onIdle(conn, state);
	    }

	    private final class ReaderIdleTimeoutTask implements Runnable {

	        private final IMConnection conn;

	        ReaderIdleTimeoutTask(IMConnection conn) {
	            this.conn = conn;
	        }

	        @Override
	        public void run() {
	            if (!conn.isConnected()) {
	                return;
	            }

	            long currentTime = System.nanoTime();
	            long lastReadTime = IdleStateHandler.this.lastReadTime;
	            long nextDelay = readerIdleTimeNanos - (currentTime - lastReadTime);
	            if (nextDelay <= 0) {
	                // Reader is idle - set a new timeout and notify the callback.
	                readerIdleTimeout =
	                		conn.getScheduledExecutorService().schedule(this, readerIdleTimeNanos, TimeUnit.NANOSECONDS);
	                try {
	                	IdleState iState;
	                    if (firstReaderIdleEvent) {
	                        firstReaderIdleEvent = false;
	                        iState = IdleState.FIRST_READER_IDLE_STATE;
	                    } else {
	                        iState = IdleState.READER_IDLE_STATE;
	                    }
	                    onIdle(conn, iState);
	                } catch (Throwable t) {
	                    //TODO
	                }
	            } else {
	                // Read occurred before the timeout - set a new timeout with shorter delay.
	                readerIdleTimeout = conn.getScheduledExecutorService().schedule(this, nextDelay, TimeUnit.NANOSECONDS);
	            }
	        }
	    }

	    private final class WriterIdleTimeoutTask implements Runnable {

	    	private final IMConnection conn;

	        WriterIdleTimeoutTask(IMConnection conn) {
	            this.conn = conn;
	        }

	        @Override
	        public void run() {
	            if (!conn.isConnected()) {
	                return;
	            }

	            long currentTime = System.nanoTime();
	            long lastWriteTime = IdleStateHandler.this.lastWriteTime;
	            long nextDelay = writerIdleTimeNanos - (currentTime - lastWriteTime);
	            
	            if (nextDelay <= 0) {
	                // Writer is idle - set a new timeout and notify the callback.
	                writerIdleTimeout = conn.getScheduledExecutorService().schedule(
	                        this, writerIdleTimeNanos, TimeUnit.NANOSECONDS);
	                try {
	                	IdleState iState;
	                    if (firstWriterIdleEvent) {
	                        firstWriterIdleEvent = false;
	                        iState = IdleState.FIRST_WRITER_IDLE_STATE;
	                    } else {
	                    	iState = IdleState.WRITER_IDLE_STATE;
	                    }
	                    onIdle(conn, iState);
	                } catch (Throwable t) {
	                   //TODO
	                }
	            } else {
	                // Write occurred before the timeout - set a new timeout with shorter delay.
	                writerIdleTimeout = conn.getScheduledExecutorService().schedule(this, nextDelay, TimeUnit.NANOSECONDS);
	            }
	        }
	    }

	    private final class AllIdleTimeoutTask implements Runnable {

	    	private final IMConnection conn;

	        AllIdleTimeoutTask(IMConnection conn) {
	            this.conn = conn;
	        }

	        @Override
	        public void run() {
	            if (!conn.isConnected()) {
	                return;
	            }

	            long currentTime = System.nanoTime();
	            long lastIoTime = Math.max(lastReadTime, lastWriteTime);
	            long nextDelay = allIdleTimeNanos - (currentTime - lastIoTime);
	            if (nextDelay <= 0) {
	                // Both reader and writer are idle - set a new timeout and
	                // notify the callback.
	                allIdleTimeout = conn.getScheduledExecutorService().schedule(
	                        this, allIdleTimeNanos, TimeUnit.NANOSECONDS);
	                try {
	                	IdleState iState;
	                    if (firstAllIdleEvent) {
	                        firstAllIdleEvent = false;
	                        iState = IdleState.FIRST_ALL_IDLE_STATE;
	                    } else {
	                    	iState = IdleState.ALL_IDLE_STATE;
	                    }
	                    onIdle(conn, iState);
	                } catch (Throwable t) {
	                    //TODO
	                }
	            } else {
	                // Either read or write occurred before the timeout - set a new
	                // timeout with shorter delay.
	                allIdleTimeout = conn.getScheduledExecutorService().schedule(this, nextDelay, TimeUnit.NANOSECONDS);
	            }
	        }
	    }
	    
	    public enum IdleState {
	        FIRST_READER_IDLE_STATE,
	        READER_IDLE_STATE,
	        FIRST_WRITER_IDLE_STATE,
	        WRITER_IDLE_STATE,
	        FIRST_ALL_IDLE_STATE,
	        ALL_IDLE_STATE 
	    }
}
