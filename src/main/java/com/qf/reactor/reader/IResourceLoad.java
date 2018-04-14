package com.qf.reactor.reader;

import java.io.InputStream;


/**
 * 资源加载接口
 *
 *
 */
public interface IResourceLoad {

	/*
	 * @para   name, 需要加载资源的名称
	 */
	InputStream load(String name);
}
