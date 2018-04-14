package com.qf.reactor.model;

/*
 * Dispatcher的枚举类型
 */
public  enum EventType {

	/*RINGBUFFER("ringBuffer"),WORKQUEUE("workQueue"),EVENTLOOP("eventLoop"),
	THREADPOOLEXECUTOR("threadPoolExecutor"),SYNC("sync"),*/
	RINGBUFFERPOOL("ringBufferPool"),RINGBUFFER("ringBuffer");
	private EventType(String type){
		this.type =type;
	}
	public final String getType(){
		return type;
	}
  final String type;
}
