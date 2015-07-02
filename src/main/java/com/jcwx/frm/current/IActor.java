package com.jcwx.frm.current;

import com.jcwx.frm.current.scheduled.LoopScheduledTask;
import com.jcwx.frm.current.scheduled.ScheduledTask;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 任务提交者接口,同一个Submiter提交的所有任务，能确保在同一个线程中顺序执行
 * @author Chenlong
 * */
public interface IActor {
	/**
	 * @return 当前Actor状态
	 * */
	ActorState getActorState();
	/**
	 * 获取任务执行Executor
	 * */
	IActorExecutor getExecutor();
	/**
	 * 设置提交者的任务执行Executor
	 * */
	void setExecutor(IActorExecutor executor);
	/**
	 * 立即将任务提交到队列中
	 * 调用该方法时,有异常会立即抛出
	 * */
	Future execute(Runnable task);
	/**
	 * 延时将任务提交到队列中
	 * @param task 任务
	 * @param delay 延迟时间
	 * @param unit 时间单位
	 * @return future
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
     * 立即将任务提交到队列中,调用该方法时,如果Callable执行中有异常，
	 * 需要通过actor.submit(task).get()来捕获异常
	 * @param task 任务
	 * @return future
     * */
    <T> Future<T> submit(Callable<T> task);

    /**
     * 延时将任务提交到队列中
     * */
    Future<?> scheduledTask(Callable task,long delay,TimeUnit unit);
	/**
	 * @param task 添加定时循环执行的任务
	 * */
	Future<?> addLoopTask(LoopScheduledTask task);
	/**
	 * @param  task 添加延迟执行一次的任务
	 * @return  future
	 * */
	Future<?> addTask(ScheduledTask task);
	/**
	 * 取消指定名称的任务
	 * @param name 任务唯一标示名 @see com.jcwx.frm.current.scheduled.ScheduledTask#getName()
	 * @param mayInterruptIfRunning 是否允许任务线程正在执行时中断
	 * @return true 成功取消 false 取消失败或者已经被执行
	 * */
	boolean cancelTask(String name,boolean mayInterruptIfRunning);
	/**
	 * 释放持有的ActorExecutor，当确信Actor不会再被使用时,记得调用此方法
	 * 否则只能等到垃圾回收时才会调用
	 * @return true 成功释放 false 已经被释放过
	 * */
	boolean releaseExecutor();
}
