/**
 * Copyright (c) www.bugull.com
 */
package com.ldw.downloader.utils;

public class StatusCode {
	
	public static final String ERROR_FILE_EXIST = "100";
	/** URL不正确 */
	public static final String ERROR_URL = "101";
	public static final String ERROR_NOMEMORY = "102";
	/** 下载过程中网络断开或者超时 该异常发生时下载终端需要用户点击下载以继续下载 */
	public static final String ERROR_DOWNLOAD_INTERRUPT = "103"; 

}
