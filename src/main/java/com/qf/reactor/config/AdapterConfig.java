package com.qf.reactor.config;

import com.qf.reactor.model.EventModel;
import com.qf.reactor.model.EventType;
import com.qf.reactor.model.IUnblockStrategy;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;

public final class AdapterConfig extends Config {

	final com.qf.reactor.annotation.Reactor reactor;
	public AdapterConfig(/*String name,EventModel eventModel,*/com.qf.reactor.annotation.Reactor reactor) {
		super();
		
		this.reactor =reactor;
		
	}

	@Override
	public EventType getDispatcherType() {
		return reactor.eventType();
	}

	@Override
	public ProducerType getProducerType() {
		return reactor.producerType();
	}

	@Override
	public int getBufferSize() {
		return reactor.bufferSize();
	}

	@Override
	public int getCorePoolSize() {
		return reactor.corePoolSize();
	}

	@Override
	public int getQueueSize() {
		return reactor.queueSize();
	}

	@Override
	public int getParallel() {
		return reactor.parallel();
	}

	@Override
	public Class<? extends WaitStrategy> getWaitStrategy() {
		return reactor.waitStrategy();
	}


	@Override
	public Class<? extends IUnblockStrategy> getUnblockStrategy() {
		return reactor.unblockStrategy();
	}

	

	@Override
	public void setDispatcherType(EventType dispatcherType) {
	}

	@Override
	public void setProducerType(ProducerType producerType) {
	}

	@Override
	public void setBufferSize(int bufferSize) {
	}

	@Override
	public void setCorePoolSize(int corePoolSize) {
	}

	@Override
	public void setQueueSize(int queueSize) {
	}

	@Override
	public void setParallel(int parallel) {
	}

	@Override
	public void setWaitStrategy(Class<? extends WaitStrategy> waitStrategy) {
	}


	@Override
	public void setUnblockStrategy(Class<? extends IUnblockStrategy> unblockStrategy) {
	}

	
	@Override
	public void setName(String name) {
	}

	@Override
	public void setEventModel(EventModel eventModel) {
	}

	@Override
	public String getName() {
		return reactor.name();
	}

	@Override
	public EventModel getEventModel() {
		return reactor.model();
	}


}
