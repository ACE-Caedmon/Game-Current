package com.jcwx.frm.current;

import org.omg.CORBA.Current;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Administrator on 2015/4/29.
 */
public class ActorTest {
    public static void main(String[] args) throws Exception{
        IActorManager actorManager=new QueueActorManager(2, CurrentUtils.createThreadFactory("Actor-Test-"));
        IActor service=actorManager.createActor();

        //ExecutorService service=Executors.newFixedThreadPool(1);
        long start=System.nanoTime();
        Future<?> future=null;
        for(int i=0;i<100000;i++){
            future=service.execute(new Task());

        }

        future.get();
        long end=System.nanoTime();
        System.out.println(end-start);
    }
    public static class Task implements Runnable{
        public static long totalTime=0;
        @Override
        public void run()  {
            totalTime=System.currentTimeMillis();
        }
    }
}
