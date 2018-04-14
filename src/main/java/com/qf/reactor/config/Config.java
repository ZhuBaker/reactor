package com.qf.reactor.config;

import com.qf.reactor.IConfig;
import com.qf.reactor.model.EventModel;
import com.qf.reactor.model.EventType;
import com.qf.reactor.model.IUnblockStrategy;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;

public class Config implements IConfig {

	 String name;
	
	EventType dispatcherType;
	
	ProducerType producerType;
	
	int bufferSize;
	
	int corePoolSize;
	
	int queueSize;
	
	int parallel;
	
	Class<? extends WaitStrategy>  waitStrategy;
	
	 EventModel eventModel;
	
	 Class<? extends IUnblockStrategy> unblockStrategy;

	public Config() {
		
	}

	public Config(String name, EventType dispatcherType, ProducerType producerType, int bufferSize, int corePoolSize,
			int queueSize, int parallel, Class<? extends WaitStrategy> waitStrategy, EventModel runModel,
			Class<? extends IUnblockStrategy> unblockStrategy) {
		super();
		this.name = name;
		this.dispatcherType = dispatcherType;
		this.producerType = producerType;
		this.bufferSize = bufferSize;
		this.corePoolSize = corePoolSize;
		this.queueSize = queueSize;
		this.parallel = parallel;
		this.waitStrategy = waitStrategy;
		this.eventModel = runModel;
		this.unblockStrategy = unblockStrategy;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setEventModel(EventModel eventModel) {
		this.eventModel = eventModel;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public EventType getDispatcherType() {
		return dispatcherType;
	}

	@Override
	public ProducerType getProducerType() {
		return producerType;
	}

	@Override
	public int getBufferSize() {
		return bufferSize;
	}

	@Override
	public int getCorePoolSize() {
		return corePoolSize;
	}

	@Override
	public int getQueueSize() {
		return queueSize;
	}

	@Override
	public int getParallel() {
		return parallel;
	}

	@Override
	public Class<? extends WaitStrategy> getWaitStrategy() {
		return waitStrategy;
	}

	@Override
	public EventModel getEventModel() {
		return eventModel;
	}

	@Override
	public Class<? extends IUnblockStrategy> getUnblockStrategy() {
		return unblockStrategy;
	}

	public void setDispatcherType(EventType dispatcherType) {
		this.dispatcherType = dispatcherType;
	}

	public void setProducerType(ProducerType producerType) {
		this.producerType = producerType;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	public void setCorePoolSize(int corePoolSize) {
		this.corePoolSize = corePoolSize;
	}

	public void setQueueSize(int queueSize) {
		this.queueSize = queueSize;
	}

	public void setParallel(int parallel) {
		this.parallel = parallel;
	}

	public void setWaitStrategy(Class<? extends WaitStrategy> waitStrategy) {
		this.waitStrategy = waitStrategy;
	}
	public void setUnblockStrategy(Class<? extends IUnblockStrategy> unblockStrategy) {
		this.unblockStrategy = unblockStrategy;
	}

	public final boolean isUnblockModel(){
		return this.getEventModel().equals(EventModel.UNBLOCK);
	}
	
}
