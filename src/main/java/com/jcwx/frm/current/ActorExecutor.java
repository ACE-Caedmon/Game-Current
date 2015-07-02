package com.jcwx.frm.current;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 顺序执行的RunnableExecutor
 * 确保先提交的任务一定先执行
 * @author Chenlong
 * */
public class ActorExecutor implements IActorExecutor {
	private BlockingQueue<FutureTask> queue=new LinkedBlockingQueue<FutureTask>();
	private Thread thread;
	private AtomicInteger actorCount=new AtomicInteger(0);
	private long totalTime;
	@Override
	public void run() {
		this.thread=Thread.currentThread();
		for(;;){
			try {
				FutureTask task=queue.take();
				runTask(task);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}
	public void submit(FutureTask task){
		try {
			if(thread==Thread.currentThread()){
				runTask(task);
			}else{
				queue.put(task);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	private void runTask(FutureTask task) throws Throwable{
		task.run();
		task.get();
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

	public long getTotalTime() {
		return totalTime;
	}
}
