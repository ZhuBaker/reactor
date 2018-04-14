package com.qf.reactor.eventchain;

import com.qf.reactor.IConsumer;
import com.qf.reactor.IEventCenter;
import com.qf.reactor.IEventChain;
import com.qf.reactor.IFunction;
import com.qf.reactor.core.EventChainPromises;
import reactor.core.composable.Composable;
import reactor.core.composable.Deferred;
import reactor.core.composable.Promise;
import reactor.util.Assert;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class EventChain<T> implements IEventChain<T> {


	final Deferred<T, Promise<T>> deferred;
	
	Promise<T> promise;
	
	final T model;
	
	final IEventCenter eventCenter;
	
	/**
	 * 该属性标示 并行运行时，消息对象是否共享(是否需要拷贝)。
	 */
	boolean clone; 
	
	static final CloneFunction cloneFunction=new CloneFunction();
	
	boolean assertFix;
	
	CountLock lock;
	
	public EventChain(IEventCenter eventCenter,T model){
		this.eventCenter =eventCenter;
		this.deferred  =EventChainPromises.defer(eventCenter.getEnv(), eventCenter.nextReactor().getDispatcher());
		this.promise = deferred.compose();
		this.model = model;
	}
	
	public   EventChain<T> consume(IConsumer<T> consumer){
		Assert.isTrue(!assertFix, "调用concurrent(..)方法后，不能调用该方法。");
		promise=this.promise.consume(consumer);
		return this;
	}
	
	public <R> EventChain<T> map(IFunction<T,R> ...iFunctions){
		Assert.isTrue(!assertFix, "调用concurrent(..)方法后，不能调用该方法。");
		for(IFunction f:iFunctions){
			promise =this.promise.map(f);
		}
		return this;//concurrent
	}
	public  EventChain<T> setClone(boolean clone){
		this.clone =clone;
		return this;
	}

	private  EventChain<T> connect(final Composable<T> composable) {
		promise.connect(composable);
		return this;
	}

	/**
	 * 
	 * @param clone 消息对象是否共享(是否需要拷贝)。值为:false 是共享。true是不共享，需要拷贝，默认为浅拷贝。
	 * @param consumers
	 * @return
	 */
	public EventChain<T> concurrent(boolean clone,IConsumer<T> ...consumers){
		for(IConsumer<T> consumer : consumers){
			this.connect(newComposable(clone,consumer));
		}
		assertFix=true;
		return this;
	}
	/**
	 * 并行执行后，在等待指定时间后获取返回值
	 * @param clone 消息对象是否共享(是否需要拷贝)。值为:false 是共享。true是不共享，需要拷贝，默认为浅拷贝。
	 * @param consumers
	 * @return
	 */
	public <R> EventChain<T> concurrent(boolean clone,IFunction<T,R> ...functions){
		for(IFunction<T,R> function : functions){
			this.connect(newComposable(clone,function));
		}
		assertFix=true;
		return this;
	}
	/**
	 * 并行执行后，在等待指定时间后获取返回值
	 * @param clone 消息对象是否共享(是否需要拷贝)。值为:false 是共享。true是不共享，需要拷贝，默认为浅拷贝。
	 * @param consumers
	 * @return
	 */
	public <R> EventChain<T> concurrent(IFunction<T,R> ...functions){
		return concurrent(clone,functions);
	}
	/**
	 * 
	 * @param clone 消息对象是否共享(是否需要拷贝)。值为:false 是共享。true是不共享，需要拷贝，默认为浅拷贝。
	 * @return
	 */
	public EventChain<T> concurrent(IConsumer<T> ...consumers){
		return concurrent(clone,consumers);
	}
	/**
	 * 
	 * @param clone 消息对象是否共享(是否需要拷贝)。值为:false 是共享。true是不共享，需要拷贝，默认为浅拷贝。
	 * @return
	 */
	private Composable<T>  newComposable(boolean clone,IConsumer<T> consumer){
		Composable<T> able= newComposable();
		 DecorateConsumer<T> c=this.newDecorateConsumer(consumer);
		if(clone){
			c.setCloneFunction(cloneFunction);
		}
		able.consume(c);
		return able;
	}

	private  DecorateConsumer<T> newDecorateConsumer(IConsumer<T> consumer){
		return  new DecorateConsumer<T>(consumer);
	}
	private <R> DecorateFunction<T,R> newDecorateFunction(IFunction<T,R> function){
		return  new DecorateFunction<T,R>(function);
	}
	private <R> Composable<T>  newComposable(boolean clone,IFunction<T,R> function){
		Composable<T> able= newComposable();
		DecorateFunction<T,R> f=newDecorateFunction(function);
		f.setLock(this.getLock(1));
		if(clone)
			f.setCloneFunction(cloneFunction);
		able.map(f);
		return able;
	}
	public Composable<T>  newComposable(){
		return new Promise(eventCenter.nextReactor(), eventCenter.getEnv(), null);
	}
	

	private CountLock getLock() {
		if(lock==null)
			lock=new CountLock(0);
		return lock;
	}
	private CountLock getLock(int add) {
		return getLock().addCount(add);
	}
	public EventChain<T> then(IConsumer<T> onSuccess,IConsumer<Throwable> onError){
		this.promise = this.promise.then(onSuccess, onError);
		return this;
	}
	public EventChain<T> then(IConsumer<T> onSuccess){
		return this.then(onSuccess, null);
	}
	public <E extends Throwable>  EventChain<T> when( Class<E> exceptionType,  IConsumer<E> onError){
		this.promise = this.promise.when(exceptionType, onError);
		return this;
	}
	
	public <R> R execute(){
		try{
			return this.execute(-1);
		}catch(Exception g){
			
		}
		return null;
	}
	public <R> R execute(long timeout)throws InterruptedException{
		return this.execute(timeout, TimeUnit.MILLISECONDS);
	}
	public <R> R execute(long timeout,TimeUnit unit)throws InterruptedException{
		deferred.accept(model);
		if(timeout >-1){
			return await(timeout,unit);
		}
		return null;
	}
	public <R> R get(long timeout){
		return this.get(timeout, TimeUnit.MILLISECONDS);
	}
	public <R> R get(long timeout,TimeUnit unit){
		if(this.promise.isError()){
			return (R)this.promise.reason();
		}else if(assertFix){
			
		}else if(this.promise.isSuccess()){
		
			return (R)this.promise.get();
		}
		try {
			return await(timeout,unit);
		} catch (InterruptedException e) {
		}
		return null;
	}
	private <R> R await(long timeout,TimeUnit unit)throws InterruptedException{
		if(assertFix){
			this.getLock().park(unit, timeout);
			 Map<String,Object> r=getLock().getConcurrentResults();
			 getLock().clearResults();
			return (R)r;
		}
		return (R)promise.await(timeout, unit);
	}

}
