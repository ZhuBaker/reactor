package com.qf.reactor.eventchain;

import com.qf.reactor.ICopy;
import com.qf.reactor.IFunction;
import com.qf.reactor.model.IEvent;

/**
 * 消息属性 浅拷贝功能函数
 *
 *
 */
public final class CloneFunction<T  extends IEvent> implements IFunction<ICopy<T>, Object> {

	
	
	@Override
	public Object apply(ICopy<T> t) {
		// TODO Auto-generated method stub
		return null;
	}

	/*@Override
	public Object apply(T t) {
		if(t==null){
			return null;
		}
		try {
			Object o= t.;
			return o;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return t;
	}*/

	@Override
	public String getResultKey() {
		// TODO Auto-generated method stub
		return null;
	}

}
