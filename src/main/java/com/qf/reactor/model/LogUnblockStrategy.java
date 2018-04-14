package com.qf.reactor.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 *
 *
 */
@SuppressWarnings("rawtypes")
public final class LogUnblockStrategy implements IUnblockStrategy {

	static Log log =LogFactory.getLog(LogUnblockStrategy.class);
	@Override
	public void handle(Object d) {
		log.error(d.toString());
	}

}
