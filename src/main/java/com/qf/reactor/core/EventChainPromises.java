package com.qf.reactor.core;

import com.qf.reactor.IEventCenter;
import reactor.core.Environment;
import reactor.core.Reactor;
import reactor.core.composable.Deferred;
import reactor.core.composable.Promise;
import reactor.core.composable.spec.Promises;
import reactor.event.Event;
import reactor.event.dispatch.Dispatcher;
import reactor.function.Consumer;

public abstract class EventChainPromises extends Promises {
	public static <T> Deferred<T, Promise<T>> defer(final Environment env,final  Dispatcher dispatcher) {
		return new DeferredPromiseSpec<T>().env(env)
		.observable(new Reactor(new EventChainRegistry<Consumer<? extends Event<?>>>(),dispatcher,null,null,null))
		.dispatcher(dispatcher).get();
		//return new DeferredPromiseSpec<T>().env(env).dispatcher(dispatcher).get();
	}
	
	public static <T> Deferred<T, Promise<T>> defer(final IEventCenter eventCenter) {
		final Reactor reactor = eventCenter.nextReactor();
		return new DeferredPromiseSpec<T>().env(eventCenter.getEnv())
		.observable(reactor)
		.dispatcher(reactor.getDispatcher()).get();
		//return new DeferredPromiseSpec<T>().env(env).dispatcher(dispatcher).get();
	}
}
