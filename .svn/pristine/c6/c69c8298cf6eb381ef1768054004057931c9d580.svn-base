/**
 * Copyright (c) www.bugull.com
 */
package com.ldw.downloader.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DownloadDBHelper extends SQLiteOpenHelper {

	private static final int DB_VERSION = 100;
	private static final String DB_NAME = "downloader.db";

	private volatile static SQLiteDatabase mDb = null;

	public static SQLiteDatabase getInstance(Context context) {
		if (mDb == null) {
			synchronized (SQLiteOpenHelper.class) {
				if (mDb == null) {
					mDb = new DownloadDBHelper(context).getWritableDatabase();
				}
			}
		}
		return mDb;
	}

	public DownloadDBHelper(Context context) {
		this(context, DB_NAME, null, DB_VERSION);
	}

	public DownloadDBHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table downloads(_id integer primary key autoincrement, "
				+ "url text, name text, saved_path text, total_size integer, "
				+ "current_size integer, status integer)";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

}
