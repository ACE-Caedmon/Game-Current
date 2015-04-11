package com.jcwx.frm.current;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;


/**
 * 任务处理服务接口
 * @author Chenlong
 * */
public interface IActorManager {
	/**
	 * 用来提交延迟任务的缓冲线程池,需要提交延迟处理的任务时，通过该线程池延迟提交到执行任务队列中
	 * */
	ScheduledExecutorService getScheduledExecutorService();
	/**
	 * 创建一个TaskSubmiter
	 * */
	IActor createActor();
	/**
	 * 分配一个RunnableExecutor,多个不同的TaskSubmiter可以共用同一个RunnableExecutor
	 * RunnableExecutor是负责消费执行任务队列的
	 * */
	IActorExecutor assignActorExecutor();
	/**
	 * 获取所有RunnableExecutor
	 * */
	List<IActorExecutor> getActorExecutors();

    void shutdown();

    IActor getActor(String actorName);
}
