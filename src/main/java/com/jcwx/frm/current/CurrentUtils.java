package com.jcwx.frm.current;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class CurrentUtils {
	/**
	 * 创建指定线程名前缀的ThreadFactory，后缀ID自增
	 * @param prefix 线程名前缀
	 * @return 指定线程工厂类
	 * */
	public static ThreadFactory createThreadFactory(final String prefix){
		return new ThreadFactory() {
			private AtomicInteger size=new AtomicInteger();
			@Override
			public Thread newThread(Runnable r) {
				Thread thread=new Thread(r);
				thread.setName(prefix+size.incrementAndGet());
				if(thread.isDaemon()){
					thread.setDaemon(false);
				}
				//thread.setPriority(9);
				return thread;
			}
		};
	}
}
