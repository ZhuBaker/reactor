package com.qf.reactor.core;

import reactor.event.lifecycle.Pausable;
import reactor.event.registry.Registration;
import reactor.event.registry.Registry;
import reactor.event.selector.ObjectSelector;
import reactor.event.selector.Selector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class EventChainRegistry<T> implements Registry<T> {
	 private int index = -1;
	 private  Registration[] registrations=new Registration[8];
	 
	 
	 private static final Selector NO_MATCH = new ObjectSelector(null) {
	        public boolean matches(Object key){ return false; }};
	        
	private class Simple<V> implements Registration {

		public Selector getSelector() {
			return cancelled ? EventChainRegistry.NO_MATCH : selector;
		}

		public V getObject() {
			return cancelled || paused ? null : object;
		}

		public Registration cancelAfterUse() {
			cancelAfterUse = true;
			return this;
		}

		public boolean isCancelAfterUse() {
			return cancelAfterUse;
		}

		public Registration cancel() {
			if (!cancelled) {
				if (lifecycle)
					((Pausable) object).cancel();
				cancelled = true;
			}
			return this;
		}

		public boolean isCancelled() {
			return cancelled;
		}

		public Registration pause() {
			paused = true;
			if (lifecycle)
				((Pausable) object).pause();
			return this;
		}

		public boolean isPaused() {
			return paused;
		}

		public Registration resume() {
			paused = false;
			if (lifecycle)
				((Pausable) object).resume();
			return this;
		}

		private final Selector selector;
		private final V object;
		private final boolean lifecycle=false;
		private  boolean cancelled;
		private  boolean cancelAfterUse;
		private  boolean paused;

		private Simple(Selector selector, V object) {
			cancelled = false;
			cancelAfterUse = false;
			paused = false;
			this.selector = selector;
			this.object = object;
		}

	}

	@Override
	public Iterator<Registration<? extends T>> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

	@Override
	public <V extends T> Registration<V> register(Selector s, V v) {
		int l=registrations.length;
		int i=this.nextIndex();
		if(l <= i){
			int t=l;
			Registration[] nr=new Registration[t+l];
			System.arraycopy(registrations, 0, nr, 0, l);
			registrations = nr;
		}
		registrations[i]= new Simple(s,v);
		return registrations[i];
	}

	@Override
	public List<Registration<? extends T>> select(Object key) {
		if (null == key) {
			return Collections.emptyList();
		}
	    List rs=new ArrayList(3);
		for(Registration r: registrations){
			if(r!=null&& !r.isCancelled()&&r.getSelector().matches(key)){
				rs.add(r);//TODO
				break;
			}
		}
		return rs;
	}

	private int nextIndex(){
		return ++index;
	}
	@Override
	public boolean unregister(Object obj) {
		// TODO Auto-generated method stub
		return false;
	}

}
