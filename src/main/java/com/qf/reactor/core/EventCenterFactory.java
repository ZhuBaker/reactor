package com.qf.reactor.core;

import com.qf.reactor.IConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import reactor.core.Environment;
import reactor.core.configuration.ConfigurationReader;
import reactor.core.configuration.ReactorConfiguration;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;


/*
 * 
 */
public final class EventCenterFactory {
	
	static final Log log=LogFactory.getLog(EventCenterFactory.class);
	
	public final static  String defaultGroup="log";
	
	private static Map<String,EventCenter> events=new ConcurrentHashMap<String, EventCenter>(4);
	
	private EventCenterFactory(){}

	
	public final static EventCenter get(IConfig config) throws Exception{
		EventCenter  event=events.get(config.getName());
		if(event==null){
			synchronized(events){
				 event=events.get(config.getName());
				if(event !=null )return event;
				log.info("create EventCenter. name is "+config.getName());
				event=new EventCenter(config,new Environment(new DefaultReader()));
				events.put(config.getName(), event);
				log.info(" create EventCenter success. name is "+config.getName());
			}
		}
		return event;
	}
	public final static EventCenter find(String name) {
		return events.get(name);
	}
	static class DefaultReader implements ConfigurationReader{
		ReactorConfiguration config=new ReactorConfiguration(Collections.EMPTY_LIST,"",new Properties());
		@Override
		public ReactorConfiguration read() {
			return config;
		}
		
	}
}
