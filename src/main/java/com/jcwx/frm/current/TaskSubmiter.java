package com.jcwx.frm.current;

import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 并发包中核心的类，负责处理所有任务，并且保证同一Submiter提交的任务
 * 都在同一线程中顺序执行,每个TaskSubmiter对应一个
 * RunnableExecutor但是同一个RunnableExecutor 可以为多个Submiter服务
 * Submiter可以切换RunnableExecutor 切换时为了保证线程安全一致性
 * 会改变Submiter状态为SubmiterState.TRANSITIVE 处于此状态时，
 * 任务不会马上提交执行，而是会先放入缓冲队列中等待状态恢复正常时下次任务激活执行
 * @author Chenlong
 * */
public class TaskSubmiter implements ITaskSubmiter{
	protected volatile RunnableExecutor executor;
	private BlockingQueue<Runnable> waitQueue=new LinkedBlockingQueue<Runnable>();
	private volatile SubmiterState state=SubmiterState.NORMAL;
	private Lock lock=new ReentrantLock(true);
	private TaskSubmiterService parent;
	private static Logger logger =LoggerFactory.getLogger(TaskSubmiter.class);
	private ScheduledExecutorService scheduledExecutorService;
	public TaskSubmiter(TaskSubmiterService parent){
		this.parent=parent;
		this.scheduledExecutorService=parent.getScheduledExecutorService();
	}
	public RunnableExecutor getExecutor(){
		return executor;
	}
	/**
	 * 如果是切换Executor,在切换Executor之前，必须将State设置为SubmiterState.TRANSITIVE
	 * */
	public void setExecutor(RunnableExecutor executor){
		lock.lock();
		try {
			if(executor==null){
				throw new NullPointerException("任务处理器executor不能为空,否则提交的任务不能执行");
			}
			if(this.executor!=null&&this.executor.workThread()!=null&&this.executor!=executor){//切换Executor
				logger.debug("玩家切换线程( oldThread = "+this.executor.workThread()+",newThread ="+executor.workThread()+")");
				state=SubmiterState.TRANSITIVE;
				transfer(executor);
				state=SubmiterState.NORMAL;
			}
			this.executor=executor;
		}finally{
			lock.unlock();
		}

		
	}
    /**
     * 将缓冲队列中的任务提交到executor的执行队列中
     * */
	private void transfer(RunnableExecutor executor) {
		if(state==SubmiterState.TRANSITIVE){//必须是过渡状态才允许此操作
			for(Runnable task:waitQueue){
				executor.submit(task);
			}
		}else{
			throw new IllegalStateException("非"+SubmiterState.TRANSITIVE+"状态不能执行此操作");
		}
	}
	@Override
	public SubmiterState getSubmiterState() {
		// TODO Auto-generated method stub
		return state;
	}
    /**
     * @param  task 构造的Future
     * @return true 过渡期任务，提交到缓冲waitQueue中 false 任务会直接提交到执行队列中,走executor.submit()流程
     * */
	private boolean executeTransTask(Runnable task) {
        if(state==SubmiterState.TRANSITIVE){
            lock.lock();
            try{
                //双重检查，避免多余锁开销，先判定为过渡状态加一次锁，然后再判断状态
                if(state==SubmiterState.TRANSITIVE){
                    logger.debug("Submiter "+this.toString()+"在过渡期有新任务");
                    waitQueue.put(task);
                    return true;
                }else{
                    return  false;
                }
            }catch (Exception e){
                logger.error("处理过渡期任务异常",e);
            }finally {
                lock.unlock();
            }

        }

		return false;
	}
	/**
	 * 保证同一Session中的任务必须在同一线程中顺序执行
	 * 判断Submiter是否为过渡状态，如果是过渡状态
	 * 则任务不直接提交到Executor执行队列中，而且是提交到过渡任务队列中
	 * 当submiter切换Executor完毕后，将过渡任务队列中的内容提交到新的Executor执行。
	 * */
	@Override
	public Future<?> execute(Runnable task) {
		FutureTask<Object> future=new FutureTask<Object>(task, null);
		if(executor==null){
			synchronized (parent.getTaskExecutors()) {
				executor=parent.assignTaskExecutor();
			}
		}
		if(executeTransTask(future)){
			
		}else{
			executor.submit(future);
		}
		return future;
	}
	@Override
	public Future<?> scheduledTask(final Runnable task,
			long delay, TimeUnit unit) {
		if(delay<=0){
			return execute(task);
		}
		return scheduledExecutorService.schedule(new Runnable() {			
			@Override
			public void run() {
				execute(task);
			}
		}, delay, unit);
	}
	@Override
	public Future<?> scheduleAtFixedRateTask(final Runnable task, long delay, long period, TimeUnit unit) {
		return scheduledExecutorService.scheduleAtFixedRate(new Runnable() {			
			@Override
			public void run() {
				execute(task);
			}
		}, delay,period, unit);
	}
	@Override
	public Future<?> scheduleWithFixedDelayTask(final Runnable task, long delay, long period, TimeUnit unit) {
		return scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {			
			@Override
			public void run() {
				execute(task);
			}
		}, delay,period, unit);
	}

    @Override
    public Future<?> execute(Callable task) {
        FutureTask<Object> future=new FutureTask<Object>(task);
        if(executor==null){
            synchronized (parent.getTaskExecutors()) {
                executor=parent.assignTaskExecutor();
            }
        }
        if(executeTransTask(future)){

        }else{
            executor.submit(future);
        }
        return future;
    }

    @Override
    public Future<?> scheduledTask(Callable task, long delay, TimeUnit unit) {
        if(delay<=0){
            return execute(task);
        }
        return scheduledExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                execute(task);
            }
        }, delay, unit);
    }

}
