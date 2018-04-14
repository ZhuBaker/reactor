package com.qf.reactor.eventchain;

import com.qf.reactor.IConsumer;
import com.qf.reactor.IFunction;

/**
 * 并行消费者的装饰类
 *
 *
 * @param <T>
 */
public final class DecorateConsumer<T> implements IConsumer<T> {
	IFunction<T,T> cloneFunction;
	IConsumer<T> target;
	public DecorateConsumer(IConsumer<T> target){
		this.target = target;
	}
	@Override
	public void accept(T t) {
		if(cloneFunction!=null)
			target.accept(cloneFunction.apply(t));
		else
			target.accept(t);
		this.target = null;
		this.cloneFunction =null;
	}
	public final DecorateConsumer<T> setCloneFunction(final IFunction cloneFunction){
		this.cloneFunction =cloneFunction;
		return this;
	}
}
