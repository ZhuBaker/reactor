package com.qf.reactor;

import com.qf.reactor.model.EventModel;
import com.qf.reactor.model.EventType;
import com.qf.reactor.model.IUnblockStrategy;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;

public interface IConfig {

	String getName(); //dispatch的名称
	/*
	 */
	EventType getDispatcherType() ;
	/*
	 * 生产者类型
	 */
	ProducerType getProducerType() ;
	
	/*
	 * ringBuffer数组的大小
	 */
	int getBufferSize() ;
	
	/*
	 * 线程池corePoolsize  size
	 * @return
	 */
	int getCorePoolSize();
	
	/*
	 * 线程池列队大小
	 */
	int getQueueSize(); 
	
	/*
	 * 并行数
	 */
	int getParallel();
	
	/*
	 * 同步策略
	 */
	Class<? extends WaitStrategy>  getWaitStrategy();
	
	/*
	 * 运行模型
	 * @return
	 */
	EventModel getEventModel();
	/*
	 * 非阻塞运行模型下，对积压事件的处理策略
	 */
	Class<? extends IUnblockStrategy>  getUnblockStrategy();
	
	boolean isUnblockModel();
}
