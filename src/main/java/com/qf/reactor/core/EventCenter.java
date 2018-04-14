package com.qf.reactor.core;

import com.qf.reactor.*;
import com.qf.reactor.annotation.EventHandler;
import com.qf.reactor.dispatcher.RingBufferDispatcher2;
import com.qf.reactor.dispatcher.RingBufferPoolDispatcher;
import com.qf.reactor.eventchain.EventChain;
import com.qf.reactor.model.EventKey;
import com.qf.reactor.model.EventType;
import com.qf.reactor.model.IEvent;
import com.qf.reactor.model.ReplyKey;
import com.lmax.disruptor.Sequence;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import reactor.core.Environment;
import reactor.core.Observable;
import reactor.core.Reactor;
import reactor.core.Reactor.ReplyToEvent;
import reactor.core.spec.ReactorSpec;
import reactor.core.spec.Reactors;
import reactor.event.Event;
import reactor.event.dispatch.Dispatcher;
import reactor.event.dispatch.RingBufferDispatcher;
import reactor.event.registry.Registry;
import reactor.event.selector.ObjectSelector;
import reactor.event.selector.Selector;
import reactor.function.Consumer;
import reactor.function.Function;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 事件中心
 *
 *
 */

@SuppressWarnings({"unused","rawtypes","unchecked"})
public final class EventCenter implements IEventCenter {
	static Log log =LogFactory.getLog(EventCenter.class);
	
	int parallelNum;
	
	final int indexMask;
	
	final Sequence sequence=new Sequence(0);
	
	
	final private Reactor[] reactors;
	/*
	 * 事件中心的环境
	 */
	final private  Environment   env;
	
	final private String name;
	
	private String[] dispatchersName;
	
	public static IEvent emptyModel=new IEvent(){};
	
	public EventCenter(IConfig config,Environment env) throws Exception{
		this.env =env;
		this.name = config.getName();
		initOtherDispatcher(config,env);
		this.indexMask  = this.parallelNum;
		reactors=createReactors(env,name);
	}
	
	
	public Reactor nextReactor(){
		if(indexMask<2)return reactors[0];
		int seq=(int)(sequence.incrementAndGet()%indexMask);
		return reactors[seq];
	}
	
	
	private void initOtherDispatcher(IConfig config,Environment env) throws Exception{
		parallelNum = config.getParallel();
		dispatchersName  =new String[parallelNum];
		if(EventType.RINGBUFFERPOOL.equals(config.getDispatcherType())){
			for(int i=0;i<parallelNum;i++){
				dispatchersName[i] = config.getName()+String.valueOf(i);
				env.addDispatcher(dispatchersName[i], createRingBufferPool(config,env,dispatchersName[i]));
			}
		}else if(EventType.RINGBUFFER.equals(config.getDispatcherType())){
			env.addDispatcher(config.getName(), this.createRingBuffer(config, env, config.getName()));
		}
		/*else  if(DispatcherType.RINGBUFFER.getType().equals(config.dispatcherType())){
			for(int i=0;i<parallelNum;i++){
				dispatchersName[i] = config.name()+i;
				env.addDispatcher(dispatchersName[i], createRingBufferDispatcher(config,env,dispatchersName[i]));
			}
		}*/
	}   
	private Dispatcher createRingBufferPool(IConfig config,Environment env,String name) throws Exception{
		return new RingBufferPoolDispatcher(name,config);
	}

	private Dispatcher createRingBuffer(IConfig config,Environment env,String name)throws Exception{
		return new RingBufferDispatcher2(name,config);
	}
   
	private RingBufferDispatcher createRingBufferDispatcher(IConfig config,Environment env,String dispatcher) throws Exception
    {
        return new RingBufferDispatcher(dispatcher, config.getBufferSize(), null, config.getProducerType(),config.getWaitStrategy().newInstance());
    }


	private Selector build(Class<?> name){
		return  new ObjectSelector<String>(name.getName());
	}
	@Override
	public <T> IEventChain<T>    eventChain(T model){
		return (EventChain<T>) new EventChain<T>(this,model);
	}

	/*
	 * 创建反应堆
	 */
	private Reactor[] createReactors(Environment env,String dispatcher) {
		ReactorSpec reactorSpec = Reactors.reactor().env(env);
		Reactor[] reactors=new Reactor[this.parallelNum];
		for(int i=0;i<this.parallelNum;i++){
			reactorSpec.dispatcher(dispatchersName[i]);
			reactorSpec.consumerRegistry(createRegistry());
			reactors[i] = reactorSpec.get();
		}
		return reactors;
	}
	private Registry createRegistry() {
		return new EventChainRegistry<Consumer<? extends Event<?>>>();
	}
	
	public Environment getEnv(){
		return env;
	}
	/*
	 * 	 * 注册跳转的事件
	 *     处理完后通知另外的事件
	 * 注册事件
	 */
	@Override
	public <E,R> void register(IEventHandler<E,R> handler) {
		Selector k = this.build(handler);
		for(Reactor reactor:reactors){
			reactor.on(k, new ReplyToConsumer( new DefaultHandler<E,R>( handler),reactor));
		}
	}
	private Selector build(IEventHandler handler){
		EventHandler e=handler.getClass().getAnnotation(EventHandler.class);
		if(e.key()!=Object.class)
			return  build(e.key());
		/*return build(findFirstGeneric(handler.getClass()).getSimpleName());*/
		return build(handler.getClass());
	}

	private Class findFirstGeneric(Class clazz) {
		Type genType = clazz.getGenericSuperclass();
		if (clazz.getSuperclass() == Object.class) {
			genType = clazz.getGenericInterfaces()[0];
		}
		if(genType instanceof ParameterizedType){
			Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
			return (Class) params[0];
		}
		return null;
	}
	/*
	 * 通知事件
	 *  参数model与对应注册事件的，处理器的参数类型一致。
	 */
	private <T> void notice(EventKey key, T model) {
		Reactor reactor=this.nextReactor();
		reactor.notify(key.getKey(), Event.wrap(model));
	}

	private <T> void notice(Class<?> key, T model) {
		Reactor reactor=this.nextReactor();
		reactor.notify(key.getName(), Event.wrap(model));
	}
	private <T> void notice(ReplyKey key, T model) {
		Event event= Event.wrap(model);
		event.setReplyTo(key.getReplyKey());
		Reactor reactor=this.nextReactor();
		reactor.notify(key.getKey(), event);
	}

	public <T> void notice(T model,Class<?>...keys){
		if(keys.length==1){
			notice(keys[0],model);
		}else if(keys.length>1){
			notice(this.build(keys),model);
		}else{
			notice(model.getClass(),model);
		}
	}
	
	public final String getDispatcher() {
		return name;
	}
	private  ReplyKey build(Class<?>...keys){
		int length = keys.length;
		int y = length -1;
		ReplyKey head=null;
		
		String tail =null;
		
		for(int i=y;i>=0;i--){
			if(tail==null){
				tail =keys[i].getName();
			}else if ( head ==null){
				head =new ReplyKey(keys[i].getName());
				head.setReplyKey(tail);
			}else if(head !=null){
				ReplyKey	head1 =new ReplyKey(keys[i].getName());
				head1.setReplyKey(head);
				head =head1;
			}
		}
		return head;
	}
	
	private  ReplyKey build(EventKey...keys){
		int length = keys.length;
		int y = length -1;
		ReplyKey head=null;
		
		String tail =null;
		
		for(int i=y;i>=0;i--){
			if(tail==null){
				tail =keys[i].getKey();
			}else if ( head ==null){
				head =new ReplyKey(keys[i].getKey());
				head.setReplyKey(tail);
			}else if(head !=null){
				ReplyKey	head1 =new ReplyKey(keys[i].getKey());
				head1.setReplyKey(head);
				head =head1;
			}
		}
		return head;
	}
	public  final class ReplyToConsumer<E extends Event<?>, V> implements Consumer<E> {
		private final Function<E, V> fn;
		final Reactor reactor;

		private ReplyToConsumer(Function<E, V> fn,Reactor reactor) {
			this.fn = fn;
			this.reactor = reactor;
		}

		@Override
		public void accept(E ev) {
			Observable replyToObservable =reactor;

			if (ReplyToEvent.class.isAssignableFrom(ev.getClass())) {
				Observable o = ((ReplyToEvent<?>) ev).getReplyToObservable();
				if (null != o) {
					replyToObservable = o;
				}
			}
			try {
				V reply = fn.apply(ev);

				Event<?> replyEv;
				if(null == reply) {
					replyEv = new Event<Void>(Void.class);
				} else {
					replyEv = wrap(reply,ev.getReplyTo());
				}
				
				Object replyTo=getReplyTo(ev.getReplyTo());
				if(replyTo!=null ){
					replyToObservable.notify(replyTo, replyEv);
				}
			} catch (Throwable x) {
				replyToObservable.notify(x.getClass(), Event.wrap(x));
			}
		}

		/*public Function<E, V> getDelegate() {
			return fn;
		}*/
		private Event wrap(Object reply,Object replyTo){
			if(Event.class.isAssignableFrom(reply.getClass())){
				return (Event)reply;
			}else if(replyTo!=null && replyTo instanceof  String){
				return Event.wrap(reply);
			}else if(replyTo!=null && replyTo instanceof  ReplyKey){
				return Event.wrap(reply).setReplyTo(((ReplyKey)replyTo).getReplyKey());
			}else if(replyTo!=null && replyTo instanceof  EventKey){
				return Event.wrap(reply);
			}
			return null;
		}
		private Object getReplyTo(Object replyTo){
			if(replyTo!=null && String.class.isAssignableFrom(replyTo.getClass())){
				return replyTo;
			}else if(replyTo!=null && replyTo instanceof  EventKey){
				return ((EventKey)replyTo).getKey();
			}
			return null;
		}
	}
	
	public final class DefaultHandler<T extends Object, R>  implements IFunction<Event<T>,R>,IEventHandler<T ,R>  {

		IEventHandler<T,R> h;
		
		public DefaultHandler(IEventHandler<T,R> h){
			this.h  = h;
		}

		@Override
		public final R apply(Event<T> t) {
			return this.handler(t.getData());
		}


		@Override
		public R handler(T t) {
			return h.handler(t);
		}

	
		public EventKey getKey() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getResultKey() {
			// TODO Auto-generated method stub
			return null;
		}
	}
}
