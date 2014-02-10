/**
 * Copyright (c) www.bugull.com
 */
package com.ldw.downloader.model;

import com.ldw.downloader.utils.DownloadConstants;

public class Downloader {
	
	private String url;
	//文件真实的名字
	private String name;
	//文件保存的路径
	private String savedPath;
	private int totalSize;
	private int currentSize;
	private int status = DownloadConstants.STATUS_DEFAULT;
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getTotalSize() {
		return totalSize;
	}
	public void setTotalSize(int totalSize) {
		this.totalSize = totalSize;
	}
	public int getCurrentSize() {
		return currentSize;
	}
	public void setCurrentSize(int currentSize) {
		this.currentSize = currentSize;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSavedPath() {
		return savedPath;
	}
	public void setSavedPath(String savedPath) {
		this.savedPath = savedPath;
	}
}
