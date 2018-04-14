package com.qf.reactor;

import java.util.concurrent.TimeUnit;

public interface IEventChain<T> {

	/*
	 * 
	 */
	IEventChain<T> consume(IConsumer<T> consumer);

	/**
	 * 添加顺序执行业务函数,可以有返回值
	 */
	<R> IEventChain<T> map(IFunction<T, R>... iFunctions);

	/**
	 * 添加并发执行消费函数，没有返回值
	 * @param clone
	 * @param consumers
	 * @return
	 */
	IEventChain<T> concurrent(boolean clone, IConsumer<T>... consumers);

	<R> IEventChain<T> concurrent(boolean clone, IFunction<T, R>... functions);
	
	<R> IEventChain<T> concurrent(IFunction<T, R>... functions);

	IEventChain<T> concurrent(IConsumer<T>... consumers);

	IEventChain<T> then(IConsumer<T> onSuccess, IConsumer<Throwable> onError);

	IEventChain<T> then(IConsumer<T> onSuccess);

	<E extends Throwable> IEventChain<T> when(Class<E> exceptionType, IConsumer<E> onError);

	/**
	 * 执行并立即返回结果
	 * @return
	 */
	<R> R execute();

	/**
	 * 执行并等待timeout毫秒返回结果
	 * @param timeout
	 * @return
	 * @throws InterruptedException
	 */
	<R> R execute(long timeout) throws InterruptedException;

	<R> R execute(long timeout, TimeUnit unit) throws InterruptedException;

	<R> R get(long timeout);

	<R> R get(long timeout, TimeUnit unit);

	//IEventChain<T> setClone(boolean clone);
}
