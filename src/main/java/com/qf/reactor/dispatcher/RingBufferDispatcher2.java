package com.qf.reactor.dispatcher;

import com.qf.reactor.IConfig;
import com.qf.reactor.model.IUnblockStrategy;
import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.event.Event;
import reactor.event.dispatch.AbstractSingleThreadDispatcher;
import reactor.event.registry.Registry;
import reactor.event.routing.EventRouter;
import reactor.function.Consumer;
import reactor.support.NamedDaemonThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


@SuppressWarnings({"rawtypes","unchecked"})
public class RingBufferDispatcher2 extends AbstractSingleThreadDispatcher {

	private final Logger log = LoggerFactory.getLogger(getClass());
	private final ExecutorService            executor;
	private final Disruptor<RingBufferTask>  disruptor;
	private final RingBuffer<RingBufferTask> ringBuffer;
	
	private IConfig config;
	
	private  IUnblockStrategy unblockStrategy;
	
	public RingBufferDispatcher2(String name,IConfig config) throws Exception {
		super(config.getBufferSize());
		WaitStrategy wait=config.getWaitStrategy().newInstance();
		this.config =config;
		if(config.isUnblockModel()){
			unblockStrategy = config.getUnblockStrategy().newInstance();
		}
		
		this.executor = Executors.newSingleThreadExecutor(new NamedDaemonThreadFactory(name, getContext()));
		
		this.disruptor = new Disruptor<RingBufferTask>(
				new EventFactory<RingBufferTask>() {
					@Override
					public RingBufferTask newInstance() {
						return new RingBufferTask();
					}
				},
				config.getBufferSize(),
				executor,
				config.getProducerType(),
				wait
		);

		this.disruptor.handleExceptionsWith(new ExceptionHandler() {
			@Override
			public void handleEventException(Throwable ex, long sequence, Object event) {
				handleOnStartException(ex);
			}

			@Override
			public void handleOnStartException(Throwable ex) {
					log.error(ex.getMessage(), ex);
			}

			@Override
			public void handleOnShutdownException(Throwable ex) {
				handleOnStartException(ex);
			}
		});
		this.disruptor.handleEventsWith(new EventHandler<RingBufferTask>() {
			@Override
			public void onEvent(RingBufferTask task, long sequence, boolean endOfBatch) throws Exception {
				final EventRouter eventRouter=task.getEventRouter();
				final Object key=task.getKey();
				final Event event=task.getEvent();
				final Registry registry=task.getRegistry();
				final Consumer consumer=task.getConsumer();
				final Consumer errorConsumer=task.getErrorConsumer();
				task.recycle();
				eventRouter.route(key, event,  null == registry ? null : registry.select(key), consumer, errorConsumer);
				
			}
		});
		this.ringBuffer = disruptor.start();
	}

	@Override
	public boolean awaitAndShutdown(long timeout, TimeUnit timeUnit) {
		shutdown();
		try {
			executor.awaitTermination(timeout, timeUnit);
			disruptor.shutdown();
		} catch (InterruptedException e) {
			return false;
		}
		return true;
	}

	@Override
	public void shutdown() {
		executor.shutdown();
		disruptor.shutdown();
		super.shutdown();
	}

	@Override
	public void halt() {
		executor.shutdownNow();
		disruptor.halt();
		super.halt();
	}

	@Override
	protected Task allocateTask() {
		long seqId = ringBuffer.next();
		return ringBuffer.get(seqId).setSequenceId(seqId);
	}

	protected void execute(Task task) {
		ringBuffer.publish(((RingBufferTask) task).getSequenceId());
	}

	private class RingBufferTask extends SingleThreadTask {
		private long sequenceId;

		public long getSequenceId() {
			return sequenceId;
		}

		public RingBufferTask setSequenceId(long sequenceId) {
			this.sequenceId = sequenceId;
			return this;
		}
		public EventRouter getEventRouter(){
			return this.eventRouter;
		}
		public Object getKey(){
			return this.key;
		}
	   
		public Event getEvent(){
			return this.event;
		}
		
		public Registry getRegistry(){
			return this.consumerRegistry;
		}
		public Consumer getConsumer(){
			return this.completionConsumer;
		}
		public Consumer getErrorConsumer(){
			return this.errorConsumer;
		}
		
	}

	@Override
	public void execute(Runnable command) {
		// TODO Auto-generated method stub
		
	}

	private final boolean isBlock(){
		return !disruptor.getRingBuffer().isPublished(disruptor.getCursor());
	}
	@Override
	 public void dispatch(Object key, Event event, Registry consumerRegistry, Consumer errorConsumer, EventRouter eventRouter, Consumer completionConsumer){
        try
        {
        	if(config.isUnblockModel()){
        		if(isBlock()){
        			unblockStrategy.handle(event.getData());
            		return;
            	}
        	}
        	
            Task task = allocateTask();
            task.setKey(key).setEvent(event).setConsumerRegistry(consumerRegistry).setErrorConsumer(errorConsumer).setEventRouter(eventRouter).setCompletionConsumer(completionConsumer);
            execute(task);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
	}
	protected void expandTailRecursionPile(int amount) {
			
	}

}
