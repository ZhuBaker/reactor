package com.qf.reactor.eventchain;

import com.qf.reactor.IFunction;

/**
 * 并行的函数装饰类
 *
 *
 * @param <E>
 * @param <R>
 */
public final class DecorateFunction<E, R> implements IFunction<E, R> {

	IFunction<E, R> target;
	
	private CountLock lock;
	
	IFunction<E,E> cloneFunction;
	
	public DecorateFunction(IFunction<E, R> target){
		this.target =target;
	}
	
	@Override
	public R apply(E t) {
		R r =null;
		try{
			if(cloneFunction!=null){
				r=  target.apply(cloneFunction.apply(t));
			}else{
				r= target.apply(t);
			}
		
		}finally{
			String key=this.target.getResultKey();
			this.target = null;
			this.cloneFunction =null;
			lock.tryUnPark(key,r);
			this.lock =null;
		}
		return r;
	}

	public final void setLock(CountLock lock){
		this.lock =lock;
	}
	
	public final DecorateFunction<E, R> setCloneFunction(final IFunction<E,E> cloneFunction){
		this.cloneFunction =cloneFunction;
		return this;
	}

	@Override
	public String getResultKey() {
		// TODO Auto-generated method stub
		return null;
	}
}
