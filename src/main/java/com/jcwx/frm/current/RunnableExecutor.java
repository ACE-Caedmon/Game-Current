package com.jcwx.frm.current;

/**
 * 任务执行Executor接口
 * @author Chenlong
 * */
public interface RunnableExecutor extends Runnable,Comparable<RunnableExecutor>{
	/**
	 * 提交任务给Executor
	 * @param task 提交的任务
	 * */
	void submit(Runnable task);
	/**
	 * @return  当前等待完成的任务数
	 * */
	int getUndoneTaskSize();
	/**
     * @return 获取Executor的工作线程
     * */
	Thread workThread();
}
