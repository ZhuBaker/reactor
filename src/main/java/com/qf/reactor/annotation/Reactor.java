package com.qf.reactor.annotation;

import com.qf.reactor.model.EventModel;
import com.qf.reactor.model.EventType;
import com.qf.reactor.model.IUnblockStrategy;
import com.qf.reactor.model.LogUnblockStrategy;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;

/**
 * 异步事件模型标注
 *
 *
 */

@Target({ TYPE,ANNOTATION_TYPE, METHOD, FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented

public @interface Reactor {
	
	String name() default "ringBufferPool" ; //dispatch的名称

	/*
	 * 运行模型
	 * @return
	 */
	EventModel model() default EventModel.BLOCK;
	/*
	 */
	EventType eventType() default EventType.RINGBUFFERPOOL;
	/*
	 * 生产者类型
	 */
	ProducerType producerType() default ProducerType.MULTI;
	
	/*
	 * ringBuffer数组的大小
	 */
	int bufferSize() default 1024;
	
	/*
	 * 线程池corePoolsize  size
	 * @return
	 */
	int corePoolSize()  default 4;
	
	/*
	 * 线程池列队大小
	 */
	int queueSize() default 1024; 
	
	/*
	 * 并行数
	 */
	int parallel()  default 2;
	
	/*
	 * 同步策略
	 */
	Class<? extends WaitStrategy>  waitStrategy() default BlockingWaitStrategy.class;
	
	/*
	 * 非阻塞运行模型下，对积压事件的处理策略
	 */
	Class<? extends IUnblockStrategy>  unblockStrategy() default LogUnblockStrategy.class;
}
