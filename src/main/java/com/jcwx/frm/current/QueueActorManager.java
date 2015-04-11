package com.jcwx.frm.current;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 线程并发包管理类，负责为Actor分配Actorxecutor
 *@author Chenlong
 * */
public class QueueActorManager extends AbstractActorManager {
	private List<IActorExecutor> executors;
	private ScheduledExecutorService scheduledExecutorService=Executors.newSingleThreadScheduledExecutor();
    private Map<String,IActor> actorMap=new ConcurrentHashMap<String, IActor>();
	public QueueActorManager(int actorThreadSize, ThreadFactory actorThreadFactory) {
		super(actorThreadSize, actorThreadFactory);
		executors=new ArrayList<IActorExecutor>(actorThreadSize);
		// TODO Auto-generated constructor stub
	}
	public QueueActorManager(int actorThreadSize,ThreadFactory actorThreadFactory,int scheduleThreadSize,ThreadFactory scheduleThreadFactory){
		this(actorThreadSize,actorThreadFactory);
		this.scheduledExecutorService=Executors.newScheduledThreadPool(scheduleThreadSize,scheduleThreadFactory);
	}
	
	/**
	 * 得到一个IActorExecutor
	 * */
	public IActorExecutor assignActorExecutor(){
		//判断集合中Executor是否已达到配置上限
		if(executors.size()<threadSize){
			//直接取出空闲的Executor
			for(IActorExecutor executor:executors){
				if(executor.getUndoneTaskSize()==0&&executor.getActorCount()==0){
					return executor;
				}
			}
			//如果没有空闲的Executor则创建新的到集合中，并提交到线程池
			IActorExecutor executor=new ActorExecutor();
			executors.add(executor);
			threadPool.execute(executor);
			return executor;
		}else{//如果集合中元素已满，则取出一个任务最少的
			JDK6Sorts.sort(executors);
			return executors.get(0);
		}
		
	}
    /**
     * 用来提交延时任务到队列中的线程池,该线程池几乎不处理任何运算，默认采用单个线程的线程池
     * @see Executors#newSingleThreadScheduledExecutor()
     * */
	@Override
	public ScheduledExecutorService getScheduledExecutorService() {
		// TODO Auto-generated method stub
		return scheduledExecutorService;
	}
    /**
     * 创建一个已分配好执行任务Executor的Submiter
     * */
	@Override
	public IActor createActor() {
		// TODO Auto-generated method stub
		IActor actor=new Actor(this);
		actor.setExecutor(assignActorExecutor());
		return actor;
	}
    /**
     * @return  获取所有用来处理任务的RunnableExecutor
     * */
	@Override
	public List<IActorExecutor> getActorExecutors() {
		// TODO Auto-generated method stub
		return executors;
	}


    @Override
    public IActor getActor(String actorName) {
        return actorMap.get(actorName);
    }
}
