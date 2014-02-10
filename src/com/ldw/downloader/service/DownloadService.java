/**
 * Copyright (c) www.bugull.com
 */
package com.ldw.downloader.service;

import com.ldw.downloader.aidl.IDownloadService;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;

/**
 * 不需要直接手动启动这个Service 所以不需要对外公开这个Service
 * @author longdw(longdawei1988@gmail.com)
 *
 * 2014-1-13
 */
public class DownloadService extends Service {
	
	private DownloadControl mControl;

	@Override
	public IBinder onBind(Intent intent) {
		return new ServiceStub();
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		mControl = new DownloadControl(this);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}
	
	private class ServiceStub extends IDownloadService.Stub {

		@Override
		public void addTask(String url) throws RemoteException {
			if(!TextUtils.isEmpty(url)) {
				mControl.addTask(url);
			}
		}

		@Override
		public void pauseTask(String url) throws RemoteException {
			if(!TextUtils.isEmpty(url)) {
				mControl.pauseTask(url);
			}
		}

		@Override
		public void deleteTask(String url) throws RemoteException {
			if(!TextUtils.isEmpty(url)) {
				mControl.deleteTask(url);
			}
		}

		@Override
		public void continueTask(String url) throws RemoteException {
			if(!TextUtils.isEmpty(url)) {
				mControl.continueTask(url);
			}
		}
	}
}
