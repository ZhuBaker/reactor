package com.qf.reactor.core;

import reactor.core.Environment;
import reactor.core.Observable;
import reactor.core.Reactor;
import reactor.core.composable.Composable;
import reactor.core.composable.Deferred;
import reactor.core.composable.Promise;
import reactor.core.composable.spec.ComposableSpec;
import reactor.event.dispatch.Dispatcher;
import reactor.event.selector.Selector;
import reactor.tuple.Tuple2;

public final class DeferredPromiseSpec<T> extends
		ComposableSpec<DeferredPromiseSpec<T>, Deferred<T, Promise<T>>> {
	private Composable<?> parent;
	private Observable observable;

	/**
	 * @return {@code this}
	 */
	public DeferredPromiseSpec<T> link(Composable<?> parent) {
		this.parent = parent;
		return this;
	}

	/*@Override
	protected Deferred<T, Promise<T>> configure(final Dispatcher dispatcher,
			Environment env) {
		if (observable == null)
			observable = new Reactor(dispatcher);
		return createComposable(env, observable, null);
	}*/

	@Override
	protected Deferred<T, Promise<T>> createComposable(Environment env,
			Observable observable, Tuple2<Selector, Object> accept) {
		return new Deferred<T, Promise<T>>(new Promise<T>(observable, env,
				parent));
	}

	public DeferredPromiseSpec<T> observable(Observable observable) {
		this.observable = observable;
		return this;
	}

}
