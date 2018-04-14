package com.qf.reactor.model;


/**
 * 非阻塞运行模型下，对积压事件的处理策略
 *
 *
 */
public interface IUnblockStrategy<T>{

	void handle(T d);
	
}
