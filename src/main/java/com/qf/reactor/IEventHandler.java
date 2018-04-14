package com.qf.reactor;

import com.qf.reactor.model.IEvent;

/**
 * 异步事件 逻辑处理接口
 *
 *
 * @param <T>
 * @param <R>
 */
public interface IEventHandler<T extends Object,R> extends IEvent {

	
	//Key getKey();  //注册的Key
	
	R handler(T t);
	
}
