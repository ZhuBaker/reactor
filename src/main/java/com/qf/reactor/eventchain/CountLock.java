package com.qf.reactor.eventchain;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

public final class CountLock {

	 int count;
	 
	final AtomicInteger index=new AtomicInteger(1);
	
	Thread thread;
	
	Map<String,Object>  concurrentResults=new HashMap<String,Object>(4);
	
	public CountLock(int count){
		this.count =count;
	}
	public CountLock addCount(int count){
		this.count+=count;
		return this;
	}
	/**
	 * 毫秒
	 * @param time
	 */
	public void park(TimeUnit unit,long time){
		if(count<1)
			return;
		thread =Thread.currentThread();
		LockSupport.parkNanos(unit.toNanos(time));
	}
	public final void tryUnPark(String key,Object concurrentResult){
		if(concurrentResults!=null)
			concurrentResults.put(key, concurrentResult);
		if(index.getAndIncrement() >= count){
			LockSupport.unpark(thread);
			thread=null;
		}
	}
	
	public final Map<String,Object> getConcurrentResults(){
		return concurrentResults;
	}
	public void clearResults(){
		concurrentResults=null;
		thread=null;
	}
}
