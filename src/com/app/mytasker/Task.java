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

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class Task extends Activity {

	ListView lvData2;
	DB db;
	SimpleCursorAdapter scAdapter2;
	Cursor cursor2;
	AlarmManager alarmManager;
	SharedPreferences sPref;

	public static Task m_self;
	public static int remove = 0;
	long m_id2 = 0;

	private final int IDM_ACTION = 0;
	private final int IDM_EXIT = 1;
	private final int IDM_REMOVE = 2;

	final CharSequence[] mActions = { "Edit", "Delete", "Cancel" };

	final String SAVE_STATUS = "saved_status";

	public Task() {
		m_self = this;
	}

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_2);

		// open connection to DB
		db = new DB(this);
		db.open();

		// get cursor
		refreshCursor2();

		// compose columns 
		String[] from2 = new String[] { DB.COLUMN_TASK_2, DB.COLUMN_VALUE_2,
				DB.COLUMN_TIME_2, DB.COLUMN_DAYS_2 };
		int[] to2 = new int[] { R.id.itemTask, R.id.itemValue, R.id.itemTime,
				R.id.itemDays };

		// create adapter and customize listview
		scAdapter2 = new SimpleCursorAdapter(this, R.layout.item2, cursor2,
				from2, to2);
		lvData2 = (ListView) findViewById(R.id.lvData2);
		lvData2.setAdapter(scAdapter2);

		lvData2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				m_id2 = id;

				showDialog(IDM_ACTION);
			}
		});

	}

	void editDate2() {
		Intent iMessage2 = new Intent(getApplicationContext(), Message2.class)
				.putExtra("m_id2", m_id2);
		startActivity(iMessage2);
		remove = 1;
		Message2.num = db.getNum2(m_id2);
		Message2.task = db.getTask2(m_id2);
		Message2.value = db.getValue2(m_id2);
		Message2.time = db.getTime2(m_id2);
		Message2.days = db.getDays2(m_id2);
		Message2.week = db.getWeek2(m_id2);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {

		case IDM_ACTION:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Chose an action");

			builder.setItems(mActions, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {

					// edit
					if (item == 0) {
						editDate2();
					}
					// delete
					if (item == 1) {
						showDialog(IDM_REMOVE);
					}

					// cancel
					if (item == 2) {
						dialog.cancel();
					}
				}
			});

			builder.setCancelable(false);
			return builder.create();
		default:

		case IDM_EXIT:
			AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
			builder1.setMessage("Exit, really?");
			builder1.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							Task.this.finish();
						}
					});
			builder1.setNegativeButton("No",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			builder1.setCancelable(false);
			return builder1.create();

		case IDM_REMOVE:
			AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
			builder2.setMessage("Delete chosen task?");
			builder2.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							if (m_id2 != 0) {
								loadStatus();
								m_delRec2();
							}
						}
					});
			builder2.setNegativeButton("No",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			builder2.setCancelable(false);
			return builder2.create();

		}
	}

	// refresh cursor
	void refreshCursor2() {
		stopManagingCursor(cursor2);
		cursor2 = db.getTaskData(DB.click_id);
		startManagingCursor(cursor2);
	}

	// processing the "add task" button
	public void onButtonClick_addRec2(View view) {
		Intent iMessage2 = new Intent(this, Message2.class);
		startActivity(iMessage2);
	}

	public void editReccordToDB2(String num, String task, String value,
			String time, String days, String week) {
		// edit record
		db.editRec2(m_id2, num, task, value, time, days, week);
		// refresh cursor
		refreshCursor2();
		// pass new cursor to adapter
		scAdapter2.changeCursor(cursor2);
		m_id2 = 0;
	}

	public void addReccordToDB2(String num, String task, String value,
			String time, String days, String week) {

		// add record 
		db.addRec2(num, task, value, time, days, week);
		// refresh cursor
		refreshCursor2();
		// pass new cursor to adapter
		scAdapter2.changeCursor(cursor2);
		m_id2 = 0;
	}

	void m_delRec2() {
		db.delRec2(m_id2);
		// refresh cursor
		refreshCursor2();
		// pass new cursor to adapter
		scAdapter2.changeCursor(cursor2);
		m_id2 = 0;
	}

	void loadStatus() {
		sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
		String status = sPref.getString(SAVE_STATUS, "");
		if (status.equals("1")) {
			int scheduleId = (int) m_id2;
			Intent intent = new Intent(getApplicationContext(), MyService.class);
			alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
			alarmManager.cancel(PendingIntent.getService(
					getApplicationContext(), scheduleId, intent, 0));
		}
	}

	protected void onDestroy() {
		super.onDestroy();
		// close connection at exit
		db.close();
	}


}
