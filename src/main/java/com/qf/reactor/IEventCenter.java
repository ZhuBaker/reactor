package com.qf.reactor;

import reactor.core.Environment;
import reactor.core.Reactor;
import reactor.core.dynamic.DynamicReactor;

/**
 * 事件中心
 *
 *
 */
public interface IEventCenter extends DynamicReactor {

	<T,R>   void register(IEventHandler<T, R> handler);
	
	
	<T> void notice(T model, Class<?>... keys);
	
	
	<T> IEventChain<T>    eventChain(T model);
	
	
	Environment getEnv();
	
	
	Reactor nextReactor();

}
