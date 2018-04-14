package com.qf.reactor.reader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;


/**
 * 从绝对路径中加载资源。
 * 		加载顺序： 首先，从引用当前jar的项目classpath中查找，没找着，从当前jar里查找。
 * 
 *
 *
 */

public class PathResourceLoad implements IResourceLoad {

	/*
	 * @param  name 为绝对文件路径
	 * (non-Javadoc)
	 * @see com.bj58.ecat.redis.config.IResourceLoad#load(java.lang.String)
	 */
	@Override
	public InputStream load(String name) {
		try{
			return new BufferedInputStream(new FileInputStream(new File(name)));
		}catch(Exception g){
			throw new RuntimeException(g.getMessage());
		}
	}

}
