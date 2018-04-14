package com.qf.reactor.model;




public  enum Scope {
	
	/**
	 * The prototype.
	 */
	PROTOTYPE("原型"),
	
	/**
	 * The request.
	 */
	REQUEST("请求"),
	
	/**
	 * The singleton.
	 */
	SINGLETON("单态");
	;
	String scope;
	private Scope(String scope){
		this.scope =scope;
	}
}
