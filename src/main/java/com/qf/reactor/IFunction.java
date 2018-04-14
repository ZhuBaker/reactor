package com.qf.reactor;

import reactor.function.Function;


/**
 *  注册事件处理器
 *
 *
 * @param <T>
 */
public interface IFunction<E,R> extends Function<E,R> {

	/**
	 * 并行执行时，返回值的key.
	 * @return
	 */
	String getResultKey();
}
