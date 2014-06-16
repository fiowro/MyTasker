/*
 * Project: MyTasker
 * Copyright (C) 2014 monsternyaa gmail com,
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You may obtain a copy of the License at

 *  <http://www.gnu.org/licenses/>.
 */

package com.app.mytasker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DB {
	
	// database name 
	private static final String DB_NAME = "mytask.db";
	// database version
	private static final int DB_VERSION = 1;

	// database table name
	private static final String DB_TABLE = "Profiles";

	// database columns names
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_TRIGGER = "trigger";

	// SQL-request to create tables of profiles
	private static final String DB_CREATE1 = "create table " + DB_TABLE + "("
			+ COLUMN_ID + " integer primary key autoincrement, " + COLUMN_TITLE
			+ " text, " + COLUMN_TRIGGER + " text " + ");";

	private static final String DB_TABLE2 = "Task";

	public static final String COLUMN_ID_2 = "_id";
	public static final String COLUMN_NUM_2 = "num";
	public static final String COLUMN_TASK_2 = "task";
	public static final String COLUMN_VALUE_2 = "value";
	public static final String COLUMN_TIME_2 = "time";
	public static final String COLUMN_DAYS_2 = "days";
	public static final String COLUMN_WEEK_2 = "week";

	private static final String DB_CREATE2 = "create table " + DB_TABLE2 + "("
			+ COLUMN_ID_2 + " integer primary key autoincrement, "
			+ COLUMN_NUM_2 + " text, " + COLUMN_TASK_2 + " text, "
			+ COLUMN_VALUE_2 + " text, " + COLUMN_TIME_2 + " text, "
			+ COLUMN_DAYS_2 + " text, " + COLUMN_WEEK_2 + " text " + ");";

	private final Context mCtx;

	private DBHelper mDBHelper;
	public SQLiteDatabase mDB;
	public String m_id;
	String idOn;
	long idOnLong;
	public static long click_id;
	public static String test;
	final String LOG_TAG = "myLogs";

	public DB(Context ctx) {
		mCtx = ctx;
	}

	// open connection
	public void open() {
		mDBHelper = new DBHelper(mCtx, DB_NAME, null, DB_VERSION);
		mDB = mDBHelper.getWritableDatabase();
	}

	// close connection
	public void close() {
		if (mDBHelper != null)
			mDBHelper.close();
	}

	// Profiles TABLE
	// /////////////////////////////////////////////////////////////////////////
	// get all data from the DB_TABLE table
	public Cursor getAllData() {
		return mDB.query(DB_TABLE, null, null, null, null, null, null);
	}

	// acquire data from the DB_TABLE table
	public String getTitle(long rowIndex) {
		String value = "";

		Cursor c = mDB.query(DB_TABLE,
				new String[] { COLUMN_ID, COLUMN_TITLE }, COLUMN_ID + "="
						+ rowIndex, null, null, null, null);

		int columnIndex = c.getColumnIndex(COLUMN_TITLE);
		if (c.moveToNext()) {
			value = c.getString(columnIndex);
		}

		return value;
	}

	public String getTrigger(long rowIndex) {
		String value = "";

		Cursor c = mDB.query(DB_TABLE,
				new String[] { COLUMN_ID, COLUMN_TRIGGER }, COLUMN_ID + "="
						+ rowIndex, null, null, null, null);

		int columnIndex = c.getColumnIndex(COLUMN_TRIGGER);
		if (c.moveToNext()) {
			value = c.getString(columnIndex);
		}

		return value;
	}

	// add record to DB_TABLE
	public void addRec(String title, String trigger) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_TITLE, title);
		cv.put(COLUMN_TRIGGER, trigger);
		mDB.insert(DB_TABLE, null, cv);
	}

	// remove record DB_TABLE
	public void delRec(long id) {
		mDB.delete(DB_TABLE, COLUMN_ID + " = " + id, null);
	}

	// edit record
	public void editRec(long id, String title, String trigger) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_TITLE, title);
		cv.put(COLUMN_TRIGGER, trigger);
		mDB.update(DB_TABLE, cv, COLUMN_ID + "=?",
				new String[] { String.valueOf(id) });
	}
	
	// getting records 
	public void readRec(SQLiteDatabase mDB, String table1, String table2) {
		Cursor c = mDB.query(table1, new String[] { "_id", "title" },
				"trigger = 'On'", null, null, null, null);
		if (c != null) {
			if (c.moveToFirst()) {
				do {
					// id of the record which is in the "on" state
					idOn = c.getString(c.getColumnIndex("_id"));
				} while (c.moveToNext());
			}
			c.close();
		}

		Cursor c2 = mDB.query(table2, new String[] { "_id", "task", "value",
				"time", "week" }, "num = " + idOn, null, null, null, null);
		if (c2 != null) {
			MainActivity.schedule.clear();
			MainActivity.count = c2.getCount(); // get amount of tasks
			if (c2.moveToFirst()) {
				do {
					MainActivity.schedule.add(c2.getString(c2
							.getColumnIndex("_id")));
					MainActivity.schedule.add(c2.getString(c2
							.getColumnIndex("task")));
					MainActivity.schedule.add(c2.getString(c2
							.getColumnIndex("value")));
					MainActivity.schedule.add(c2.getString(c2
							.getColumnIndex("time")));
					MainActivity.schedule.add(c2.getString(c2
							.getColumnIndex("week")));

				} while (c2.moveToNext());
			}
			c2.close();
		}
	}

	// turn toggleButton off if profile is in the "On" state
	public void readRecOn(SQLiteDatabase mDB, String table) {
		Cursor c = mDB.query(table, new String[] { "_id", "title" },
				"trigger = 'On'", null, null, null, null);
		if (c != null) {
			if (c.moveToFirst()) {
				do {
					// id of the record which is in the "on" state
					idOnLong = Long.valueOf(c.getString(c.getColumnIndex("_id")));
					
				} while (c.moveToNext());
			}
			c.close();
		}
	}

	public void profilesOnOff(long id) {
		ContentValues cv = new ContentValues();


		String trigger = "Off";

		cv.put(COLUMN_TRIGGER, trigger);
		mDB.update(DB_TABLE, cv, COLUMN_TRIGGER + "=?",
				new String[] { String.valueOf("On") });


		trigger = "On";
		cv.put(COLUMN_TRIGGER, trigger);
		mDB.update(DB_TABLE, cv, COLUMN_ID + "=?",
				new String[] { String.valueOf(id) });
	}

	// Profiles TABLE END
	// /////////////////////////////////////////////////////////////////////////

	// Task TABLE 2
	// /////////////////////////////////////////////////////////////////////////
	// get all data from the DB_TABLE2 table
	public Cursor getAllData2() {
		return mDB.query(DB_TABLE2, null, null, null, null, null, null);
	}

	// get data from DB_TABLE2 with concrete id
	public Cursor getTaskData(long id) {
		return mDB.rawQuery("SELECT * FROM Task WHERE num = " + id, null);
	}


	// get data from the DB_TABLE2 table
	public String getNum2(long rowIndex) {
		String value = "";

		Cursor c = mDB.query(DB_TABLE2, new String[] { COLUMN_ID_2,
				COLUMN_NUM_2 }, COLUMN_ID_2 + "=" + rowIndex, null, null, null,
				null);

		int columnIndex = c.getColumnIndex(COLUMN_NUM_2);
		if (c.moveToNext()) {
			value = c.getString(columnIndex);
		}

		return value;
	}

	public String getTask2(long rowIndex) {
		String value = "";

		Cursor c = mDB.query(DB_TABLE2, new String[] { COLUMN_ID_2,
				COLUMN_TASK_2 }, COLUMN_ID_2 + "=" + rowIndex, null, null,
				null, null);

		int columnIndex = c.getColumnIndex(COLUMN_TASK_2);
		if (c.moveToNext()) {
			value = c.getString(columnIndex);
		}

		return value;
	}

	public String getValue2(long rowIndex) {
		String value = "";

		Cursor c = mDB.query(DB_TABLE2, new String[] { COLUMN_ID_2,
				COLUMN_VALUE_2 }, COLUMN_ID_2 + "=" + rowIndex, null, null,
				null, null);

		int columnIndex = c.getColumnIndex(COLUMN_VALUE_2);
		if (c.moveToNext()) {
			value = c.getString(columnIndex);
		}

		return value;
	}

	public String getTime2(long rowIndex) {
		String value = "";

		Cursor c = mDB.query(DB_TABLE2, new String[] { COLUMN_ID_2,
				COLUMN_TIME_2 }, COLUMN_ID_2 + "=" + rowIndex, null, null,
				null, null);

		int columnIndex = c.getColumnIndex(COLUMN_TIME_2);
		if (c.moveToNext()) {
			value = c.getString(columnIndex);
		}

		return value;
	}

	public String getDays2(long rowIndex) {
		String value = "";

		Cursor c = mDB.query(DB_TABLE2, new String[] { COLUMN_ID_2,
				COLUMN_DAYS_2 }, COLUMN_ID_2 + "=" + rowIndex, null, null,
				null, null);

		int columnIndex = c.getColumnIndex(COLUMN_DAYS_2);
		if (c.moveToNext()) {
			value = c.getString(columnIndex);
		}

		return value;
	}

	public String getWeek2(long rowIndex) {
		String value = "";

		Cursor c = mDB.query(DB_TABLE2, new String[] { COLUMN_ID_2,
				COLUMN_WEEK_2 }, COLUMN_ID_2 + "=" + rowIndex, null, null,
				null, null);

		int columnIndex = c.getColumnIndex(COLUMN_WEEK_2);
		if (c.moveToNext()) {
			value = c.getString(columnIndex);
		}

		return value;
	}

	// add record to DB_TABLE2
	public void addRec2(String num, String task, String value, String time,
			String days, String week) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_NUM_2, num);
		cv.put(COLUMN_TASK_2, task);
		cv.put(COLUMN_VALUE_2, value);
		cv.put(COLUMN_TIME_2, time);
		cv.put(COLUMN_DAYS_2, days);
		cv.put(COLUMN_WEEK_2, week);
		mDB.insert(DB_TABLE2, null, cv);
	}

	// remove record from DB_TABLE2
	public void delRec2(long id) {
		mDB.delete(DB_TABLE2, COLUMN_ID_2 + " = " + id, null);
	}

	// remove records from  DB_TABLE2 which are related to records removed from DB_TABLE1
	public void delRecFromTab2(long id) {
		mDB.delete(DB_TABLE2, COLUMN_NUM_2 + " = " + id, null);
	}

	// edit record
	public void editRec2(long id, String num, String task, String value,
			String time, String days, String week) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_NUM_2, num);
		cv.put(COLUMN_TASK_2, task);
		cv.put(COLUMN_VALUE_2, value);
		cv.put(COLUMN_TIME_2, time);
		cv.put(COLUMN_DAYS_2, days);
		cv.put(COLUMN_WEEK_2, week);
		mDB.update(DB_TABLE2, cv, COLUMN_ID_2 + "=?",
				new String[] { String.valueOf(id) });
	}

	// read records
	public void readRec2(int numInt, long m_id2, SQLiteDatabase mDB, String table1,
			String table2) {
		int idOnInt = 0;
		Cursor c = mDB.query(table1, new String[] { "_id", "title" },
				"trigger = 'On'", null, null, null, null);
		if (c != null) {
			if (c.moveToFirst()) {
				do {
					// id of record which is in its "on" state
					idOn = c.getString(c.getColumnIndex("_id"));
					idOnInt = Integer.valueOf(idOn);
				} while (c.moveToNext());
			}
			c.close();
		}

		if (idOnInt == numInt) {
			Cursor c2 = mDB.query(table2, new String[] { "_id", "task",
					"value", "time", "week" }, "_id = " + m_id2, null, null,
					null, null);
			if (c2 != null) {
				Message2.schedule.clear();				
				if (c2.moveToFirst()) {
					do {

						Message2.schedule.add(c2.getString(c2
								.getColumnIndex("_id")));
						Message2.schedule.add(c2.getString(c2
								.getColumnIndex("task")));
						Message2.schedule.add(c2.getString(c2
								.getColumnIndex("value")));
						Message2.schedule.add(c2.getString(c2
								.getColumnIndex("time")));
						Message2.schedule.add(c2.getString(c2
								.getColumnIndex("week")));

						
					} while (c2.moveToNext());
				}
				c2.close();
			}
		}
	}

	// Task TABLE 2 END
	// /////////////////////////////////////////////////////////////////////////

	// class for DB creation an control
	private class DBHelper extends SQLiteOpenHelper {

		public DBHelper(Context context, String title, CursorFactory factory,
				int version) {
			super(context, title, factory, version);
		}

		// create and fill the DB
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DB_CREATE1);
			db.execSQL(DB_CREATE2);


		}

		@Override
		// update version of existing DB
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE2);
			onCreate(db);
		}
	}
}
