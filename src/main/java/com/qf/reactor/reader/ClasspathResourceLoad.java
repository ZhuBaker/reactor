package com.qf.reactor.reader;

import java.io.InputStream;

public class ClasspathResourceLoad implements IResourceLoad {

	@Override
	public InputStream load(String name) {
		InputStream stream=Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
		if(stream==null){
			stream=Thread.currentThread().getClass().getResourceAsStream(name);
		}
		if(stream==null)
			stream=this.getClass().getResourceAsStream(name);
		
		return stream;
	}

}
