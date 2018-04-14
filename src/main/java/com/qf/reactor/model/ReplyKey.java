package com.qf.reactor.model;

public final class ReplyKey<T> extends EventKey {
	
	T replyKey;

	public ReplyKey() {
		super();
	}

	public ReplyKey(String key) {
		super(key);
	}

	public ReplyKey(String key, T replyKey) {
		super(key);
		this.replyKey = replyKey;
	}

	public T getReplyKey() {
		return replyKey;
	}

	public void setReplyKey(T replyKey) {
		this.replyKey = replyKey;
	}
	
	
}
