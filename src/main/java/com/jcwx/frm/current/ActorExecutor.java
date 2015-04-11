package com.jcwx.frm.current;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 顺序执行的RunnableExecutor
 * 确保先提交的任务一定先执行
 * @author Chenlong
 * */
public class ActorExecutor implements IActorExecutor {
	private BlockingQueue<Runnable> queue=new LinkedBlockingQueue<Runnable>();
	private Thread thread;
	private AtomicInteger actorCount=new AtomicInteger(0);
	@Override
	public void run() {
		this.thread=Thread.currentThread();
		for(;;){
			try {
				Runnable task=queue.take();
				task.run();
			} catch (Exception e) {
				System.err.println("执行任务异常");
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
				System.err.println("提交任务异常");
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
	public int compareTo(IActorExecutor o) {
		// TODO Auto-generated method stub
		int compare=getUndoneTaskSize()-o.getUndoneTaskSize();
		if(compare==0){
			compare=actorCount.get()-o.getActorCount();
		}
		return compare;
	}
	@Override
	public Thread workThread() {
		// TODO Auto-generated method stub
		return thread;
	}

	@Override
	public void incrActorCount() {
		actorCount.getAndIncrement();
	}

	@Override
	public void decrActorCount() {
		actorCount.getAndDecrement();
	}

	@Override
	public int getActorCount() {
		return actorCount.get();
	}

}