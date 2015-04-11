Game-Current
=========

The simple ['Actor Model'](http://en.wikipedia.org/wiki/Actor_model)<br /> based on JDK ThreadPool
一.What can this framework do?
-----------------------------------
Submitted to the same actor task, is orderly and is thread safe
二.How to use?
-----------------------------------
### Create ActorManager
IActorManager actorManager=new QueueActorManager(5,CurrentUtils.createThreadFactory("Test-Thread-Pool-"))
### Create Actor
IActor actor=actorManager.createActor()
### The specific function
See all method of actor，they are something like java.util.concurrent.ScheduledThreadPoolExecutor
### The matters needing attention
Use the JDK1.6 version of the above


