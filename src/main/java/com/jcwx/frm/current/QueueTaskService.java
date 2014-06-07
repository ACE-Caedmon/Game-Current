package com.jcwx.frm.current;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
/**
 * 线程并发包管理类，负责为Submiter分配RunnableExecutor
 *@author Chenlong
 * */
public class QueueTaskService extends ThreadTaskService{
	private List<RunnableExecutor> executors;
	private static final ScheduledExecutorService scheduledExecutorService=Executors.newSingleThreadScheduledExecutor();
	public QueueTaskService(int threadSize, ThreadFactory factory) {
		super(threadSize, factory);
		executors=new ArrayList<RunnableExecutor>(threadSize);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 得到一个MessageTaskExecutor
	 * */
	public RunnableExecutor assignTaskExecutor(){
		//判断集合中Executor是否已达到配置上限
		if(executors.size()<threadSize){
			//直接取出空闲的Executor
			for(RunnableExecutor executor:executors){
				if(executor.getUndoneTaskSize()==0){
					return executor;
				}
			}
			//如果没有空闲的Executor则创建新的到集合中，并提交到线程池
			RunnableExecutor executor=new QueueRunnableExecutor();
			executors.add(executor);
			threadPool.execute(executor);
			return executor;
		}else{//如果集合中元素已满，则取出一个任务最少的
			Collections.sort(executors);
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
	public ITaskSubmiter createSubmiter() {
		// TODO Auto-generated method stub
		ITaskSubmiter submiter=new TaskSubmiter(this);
		submiter.setExecutor(assignTaskExecutor());
		return submiter;
	}
    /**
     * @return  获取所有用来处理任务的RunnableExecutor
     * */
	@Override
	public List<RunnableExecutor> getTaskExecutors() {
		// TODO Auto-generated method stub
		return executors;
	}


}
