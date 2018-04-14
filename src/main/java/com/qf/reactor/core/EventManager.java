package com.qf.reactor.core;

import com.qf.reactor.IConfig;
import com.qf.reactor.IEventCenter;
import com.qf.reactor.IEventChain;
import com.qf.reactor.IEventHandler;
import com.qf.reactor.annotation.Event;
import com.qf.reactor.annotation.EventHandler;
import com.qf.reactor.config.AdapterConfig;
import com.qf.reactor.config.Config;
import com.qf.reactor.model.EventModel;
import com.qf.reactor.model.EventType;
import com.qf.reactor.model.LogUnblockStrategy;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * 异步事件 管理中心
 * 
 *
 *
 */
@SuppressWarnings("rawtypes")
public  final class EventManager {

	static final Log log=LogFactory.getLog(EventManager.class);
	
	public final static  String defaultGroup="log";
	
	final static  EventManager manager =new EventManager();
	
	final static IConfig defaultConfig=new Config("ringBufferPool",EventType.RINGBUFFERPOOL,ProducerType.MULTI,1024,4,1024,2,BlockingWaitStrategy.class,EventModel.BLOCK,LogUnblockStrategy.class);
	
	private EventManager(){
		
	}
	
	public static EventManager getManager(){
		return manager;
	}
	
	private  IEventCenter get(IConfig config) throws Exception{
		return EventCenterFactory.get(config);
	}
	

	public <T,R> IEventCenter get(IEventHandler<T,R> handler){
		EventHandler eh=	handler.getClass().getAnnotation(EventHandler.class);
		if(eh==null){
			System.out.println("error.error.handler :"+handler.getClass().getSimpleName() +",没有标注 EventHandler.");
			log.error("handler :"+handler.getClass().getSimpleName() +",没有标注 EventHandler.");
			return null;
		}
		try{
			return this.get(eh);
		}catch(Exception g){
			g.printStackTrace();
			log.error("", g.getCause());
		}
		return null;
	}
	
	private IEventCenter get(EventHandler ev) throws Exception{
		return this.get(new AdapterConfig(ev.reactor()));
	}
	
	public <T> IEventCenter get(T model){
		return this.find(model);
	}
	
	private <T> IEventCenter find(T  model) {
		Event e=model.getClass().getAnnotation(Event.class);
		if(e==null){
			System.out.println("error.error.model :"+model.getClass().getSimpleName() +",没有标注 EventModel.");
			log.error("model :"+model.getClass().getSimpleName() +",没有标注 EventModel.");
			return null;
		}
		return find(e.reactor());
	}
	private IEventCenter find(String  name) {
		return EventCenterFactory.find(name);
	}
	public  <T,R> IEventCenter registerEventHandler(IEventHandler<T,R> handler){
		IEventCenter center=this.get(handler);
		center.register(handler);
		return center;
	} 

/*	public  <T extends IEvent> IEventCenter noticeEvent(T model,EventKey...keys){
		IEventCenter center=find(model);
		center.notice(model, keys);
		return center;
	}
	public  <T extends IEvent> IEventCenter noticeEvent(T model,String...keys){
		IEventCenter center=find(model);
		center.notice(model, keys);
		return center;
	}*/
	
	public  <T> IEventCenter noticeEvent(T model,Class<?>...keys){
		IEventCenter center=find(model);
		center.notice(model, keys);
		return center;
	}
    public <T> IEventChain<T> eventChain(T model)throws Exception{
    	return this.get(defaultConfig).eventChain(model);
    }
  
    public <T> IEventChain<T> eventChain(IConfig config,T model)throws Exception{
    	return this.get(config).eventChain(model);
    }
  
}
