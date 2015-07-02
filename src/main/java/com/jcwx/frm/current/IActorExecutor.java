package com.jcwx.frm.current;

import java.util.concurrent.FutureTask;

/**
 * 任务执行Executor接口
 * @author Chenlong
 * */
public interface IActorExecutor extends Runnable,Comparable<IActorExecutor>{
	/**
	 * 提交任务给Executor
	 * @param task 提交的任务
	 * */
	void submit(FutureTask task);
	/**
	 * @return  当前等待完成的任务数
	 * */
	int getUndoneTaskSize();
	/**
     * @return 获取Executor的工作线程
     * */
	Thread workThread();

	void incrActorCount();

	void decrActorCount();

	int getActorCount();
}
