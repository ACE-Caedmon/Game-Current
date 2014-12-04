package com.jcwx.frm.current;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 顺序执行的RunnableExecutor
 * 确保先提交的任务一定先执行
 * @author Chenlong
 * */
public class QueueRunnableExecutor  implements RunnableExecutor{
	private BlockingQueue<Runnable> queue=new LinkedBlockingQueue<Runnable>();
	private Logger logger=LoggerFactory.getLogger(QueueRunnableExecutor.class);
	private Thread thread;
	@Override
	public void run() {
		this.thread=Thread.currentThread();
		for(;;){
			try {
				Runnable task=queue.take();
				task.run();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	public void submit(Runnable task){
		if(thread==Thread.currentThread()){
			task.run();
		}else{
			try {
				queue.put(task);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public int getUndoneTaskSize(){
		return queue.size();
	}
	/**
	 * 根据RunnableExecutor中等待完成任务的数量比较优先级
	 * */
	@Override
	public int compareTo(RunnableExecutor o) {
		// TODO Auto-generated method stub
		return getUndoneTaskSize()-o.getUndoneTaskSize();
	}
	@Override
	public Thread workThread() {
		// TODO Auto-generated method stub
		return thread;
	}
}
