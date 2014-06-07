package com.jcwx.frm.current.scheduled;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Future;

import org.slf4j.LoggerFactory;

import com.jcwx.frm.current.ITaskSubmiter;
/**
 * 定时执行任务的管理类，提供根据唯一标示名取消任务的功能
 * @author Chenlong
 * */
public class ScheduledTaskManager {
	private Map<String, Future<?>> futures;
	private org.slf4j.Logger logger=LoggerFactory.getLogger(ScheduledTaskManager.class);
	private ITaskSubmiter submiter;
	public ScheduledTaskManager(ITaskSubmiter taskSubmiter){
		this.futures=new TreeMap<String, Future<?>>();
		this.submiter=taskSubmiter;
	}
	/**
	 * @param task 添加定时循环执行的任务
	 * */
	public Future<?> addLoopTask(LoopScheduledTask  task){
		if(!futures.containsKey(task.getName())){
			Future<?> future=null;
				if(task.isFixRate()){
					future=submiter.scheduleAtFixedRateTask(task, task.getDelay(), task.getPeriod(), task.getUnit());
				}else{
					future=submiter.scheduleWithFixedDelayTask(task, task.getDelay(), task.getPeriod(), task.getUnit());
				}
			futures.put(task.getName(), future);
			return futures.get(task.getName());
		}else{
			logger.error("定时任务已存在( name = "+task.getName()+")");
			return null;
		}
	}
	/**
	 * @param  task 添加延迟执行一次的任务
	 * */
	public Future<?> addTask(ScheduledTask task){
		if(!futures.containsKey(task.getName())){
			task.addListener(new DefaultTaskFutureListener());
			Future<?> future=submiter.scheduledTask(task, task.getDelay(), task.getUnit());
			futures.put(task.getName(), future);
			return futures.get(task.getName());
		}else{
			logger.error("定时任务已存在( name = "+task.getName()+")");
			return null;
		}

	}
	/**
	 * 取消指定名称的任务
	 * @param name 任务唯一标示名 @see com.jcwx.frm.current.scheduled.ScheduledTask#getName()
	 * @param mayInterruptIfRunning 是否允许任务线程正在执行时中断
	 * */
	public boolean cancelTask(String name,boolean mayInterruptIfRunning){
		Future<?> future=futures.get(name);
		if(future!=null){
			boolean b=future.cancel(mayInterruptIfRunning);
			if(b){
				futures.remove(name);
			}
			return b;
		}else{
			logger.warn("任务不存在( taskName = "+name+" )");
			return false;
		}
		
	}
    /**
     * 任务执行完毕后，会从缓存中移除保存的Future对象，只针对ScheduledTask
     * */
	private class DefaultTaskFutureListener implements TaskFutureListener{

		@Override
		public void completed(ScheduledTask task) {
			cancelTask(task.getName(), true);
		}
		
	}
}
