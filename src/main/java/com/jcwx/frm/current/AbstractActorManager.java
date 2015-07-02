package com.jcwx.frm.current;

import java.util.concurrent.*;

public abstract class AbstractActorManager implements IActorManager {
	protected int threadSize;
	protected ExecutorService threadPool;
	private static final ThreadFactory DEFAULT_THREAD_FACTORY=CurrentUtils.createThreadFactory("Actor-Thread-Pool-");
	
	public AbstractActorManager(int threadSize, ThreadFactory factory){
		this.threadSize=threadSize;
		if(factory==null){
			factory=DEFAULT_THREAD_FACTORY;
		}
		initThreadPool(factory);
	}
	private ExecutorService initThreadPool(ThreadFactory factory){
		if(threadPool==null){
			this.threadPool=new ThreadPoolExecutor(this.threadSize, threadSize,
					0L, TimeUnit.MILLISECONDS,
					new LinkedBlockingQueue<Runnable>(),
					factory){
				@Override
				protected void afterExecute(Runnable r, Throwable t) {
					System.out.println("Complete");
					t.printStackTrace();
				}
			};
			//this.threadPool=Executors.newFixedThreadPool(this.threadSize,factory);
		}
		return threadPool;
	}
    public void shutdown(){
        threadPool.shutdown();
    }
}
