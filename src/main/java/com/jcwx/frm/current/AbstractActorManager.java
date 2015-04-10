package com.jcwx.frm.current;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public abstract class AbstractActorManager implements IActorManager {
	protected int threadSize;
	protected ExecutorService threadPool;
	private static final ThreadFactory DEFAULT_THREAD_FACTORY=CurrentUtils.createThreadFactory("Message-Task-Pool-");
	
	public AbstractActorManager(int threadSize, ThreadFactory factory){
		this.threadSize=threadSize;
		if(factory==null){
			factory=DEFAULT_THREAD_FACTORY;
		}
		initThreadPool(factory);
	}
	private ExecutorService initThreadPool(ThreadFactory factory){
		if(threadPool==null){
			this.threadPool=Executors.newFixedThreadPool(this.threadSize,factory);
		}
		return threadPool;
	}
    public void shutdown(){
        threadPool.shutdown();
    }
}
