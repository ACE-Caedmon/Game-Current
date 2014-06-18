package com.jcwx.frm.current;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 任务提交者接口,同一个Submiter提交的所有任务，能确保在同一个线程中顺序执行
 * @author Chenlong
 * */
public interface ITaskSubmiter {
	/**
	 * 获取当前Submiter状态
	 * */
	SubmiterState getSubmiterState();
	/**
	 * 获取任务执行Executor
	 * */
	RunnableExecutor getExecutor();
	/**
	 * 设置提交者的任务执行Executor
	 * */
	void setExecutor(RunnableExecutor executor);
	/**
	 * 立即将任务提交到队列中
	 * */
	Future<?> execute(Runnable task);
	/**
	 * 延时将任务提交到队列中
	 * */
	Future<?> scheduledTask(Runnable task,long delay,TimeUnit unit);
	/**
	 * @see java.util.concurrent.ScheduledExecutorService#scheduleAtFixedRate(Runnable, long, long, java.util.concurrent.TimeUnit)
	 * */
	Future<?> scheduleAtFixedRateTask(Runnable task,long delay,long period,TimeUnit unit);
	/**
	 * @see java.util.concurrent.ScheduledExecutorService#scheduleWithFixedDelay(Runnable, long, long, java.util.concurrent.TimeUnit)
     * */
	Future<?> scheduleWithFixedDelayTask(Runnable task,long delay,long period,TimeUnit unit);
    /**
     * 立即将任务提交到队列中
     * */
    <T> Future<T> execute(Callable<T> task);

    /**
     * 延时将任务提交到队列中
     * */
    Future<?> scheduledTask(Callable task,long delay,TimeUnit unit);

}
