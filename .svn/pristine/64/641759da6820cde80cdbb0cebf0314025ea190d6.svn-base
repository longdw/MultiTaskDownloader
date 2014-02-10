/**
 * Copyright (c) www.bugull.com
 */
package com.ldw.downloader.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ldw.downloader.model.Downloader;

public class DownloadDao {

	private final String TABLE = "downloads";
	private SQLiteDatabase mDb;

	public DownloadDao(Context context) {
		mDb = DownloadDBHelper.getInstance(context);
	}

	public void save(Downloader downloader) {
		if (!exist(downloader.getUrl())) {
			ContentValues cv = new ContentValues();
			cv.put("url", downloader.getUrl());
			cv.put("name", downloader.getName());
			cv.put("saved_path", downloader.getSavedPath());
			cv.put("total_size", downloader.getTotalSize());
			cv.put("current_size", downloader.getCurrentSize());
			cv.put("status", downloader.getStatus());
			mDb.insert(TABLE, null, cv);
		}
	}
	
	public void updateTotalSizeByUrl(String url, long totalSize) {
		ContentValues cv = new ContentValues();
		cv.put("total_size", totalSize);
		mDb.update(TABLE, cv, "url=?", new String[]{ url });
	}
	
	public void updateCurrentSizeByUrl(String url, long currentSize) {
		ContentValues cv = new ContentValues();
		cv.put("current_size", currentSize);
		mDb.update(TABLE, cv, "url=?", new String[]{ url });
	}

	public void updateStatusByUrl(String url, int status) {
		ContentValues cv = new ContentValues();
		cv.put("status", status);
		mDb.update(TABLE, cv, "url=?", new String[] { url });
	}

	public int getStatusByUrl(String url) {
		String sql = "select status from " + TABLE + " where url=?";
		Cursor cursor = mDb.rawQuery(sql, new String[] { url });
		int status = 0;
		if (cursor.moveToFirst()) {
			status = cursor.getInt(0);
		}
		cursor.close();
		return status;
	}

	public boolean exist(String url) {
		String sql = "select * from " + TABLE + " where url=?";
		Cursor cursor = mDb.rawQuery(sql, new String[] { url });
		boolean exist = false;
		if (cursor.moveToFirst()) {
			exist = true;
		}
		cursor.close();
		return exist;
	}
	
	public void deleteByUrl(String url) {
		mDb.delete(TABLE, "url=?", new String[]{ url });
	}

}
