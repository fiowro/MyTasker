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

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity {

	ListView lvData;
	DB db;
	SimpleCursorAdapter scAdapter;
	Cursor cursor;
	Intent intent;
	TextView textTrigger;
	TextView textTitle;
	ToggleButton toggleButton;
	SharedPreferences sPref;

	AlarmManager alarmManager;
	PendingIntent pintent1, pintent2;
	View olos;
	int currentHour;
	int currentMinute;
	int currentSecond;
	int currentDof;
	int currentMillis;
	public RelativeLayout relativeLayout;
	public static ArrayList<String> schedule = new ArrayList<String>();
	public static MainActivity m_self;
	public static int remove = 0;
	public static long m_id = 0;
	String status = "0";

	public static int count;

	final String SAVE_STATUS = "saved_status";

	private final int IDM_ACTION = 0;
	private final int IDM_EXIT = 1;
	private final int IDM_REMOVE = 2;
	private final int IDM_CRASH = 3;

	final CharSequence[] mActions = { "Rename", "Tasks", "Delete", "Cancel" };

	public MainActivity() {
		m_self = this;
	}

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// connect to DB
		db = new DB(this);
		db.open();

		// get cursor
		refreshCursor();

		// compose columns of tasks DB
		String[] from = new String[] { DB.COLUMN_TITLE, DB.COLUMN_TRIGGER };
		int[] to = new int[] { R.id.itemTitle, R.id.itemTrigger };

		// create adapter
		scAdapter = new SimpleCursorAdapter(this, R.layout.item, cursor, from,
				to);

		// find list
		lvData = (ListView) findViewById(R.id.lvData);

		toggleButton = (ToggleButton) findViewById(R.id.onOffBut);

		// set choice mode for the list items
		lvData.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		// assign adapter to the list
		lvData.setAdapter(scAdapter);

		textTrigger = (TextView) findViewById(R.id.itemTrigger);
		textTitle = (TextView) findViewById(R.id.itemTitle);

		loadStatus();

		lvData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				m_id = id;
				DB.click_id = id;

				showDialog(IDM_ACTION);
			}
		});

		lvData.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				m_id = id;
				DB.click_id = id;

				boolean on = toggleButton.isChecked();
				if (on) {
					toggleButton.setChecked(false);
					onToggleClicked(null);
				}

				db.profilesOnOff(id);
				// refresh cursor
				refreshCursor();
				// pass new cursor to adapter
				scAdapter.changeCursor(cursor);

				m_id = 0;

				return true;
			}
		});

	}

	// on-off switch of a service
	public void onToggleClicked(View view) {
		int indexId = 0;
		int indexTask = 1;
		int indexValue = 2;
		int indexTime = 3;
		int indexWeek = 4;
		int scheduleId;
		String scheduleTask;
		String scheduleValue;
		int scheduleHour;
		int scheduleMinute;
		String scheduleWeek;

		Intent intent = new Intent(this, MyService.class);

		alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

		db.readRec(db.mDB, "Profiles", "Task");

		boolean on = toggleButton.isChecked();
		if (on) {
			status = "1";
			saveStatus();			
			
			for (int i = 0; i < count; i++) {

				int alarmTime = 0;
				int runNow = 1;
				int runHour = 1;
				int runHM = 1;
				
				scheduleId = Integer.valueOf(schedule.get(indexId));
				scheduleTask = schedule.get(indexTask);
				scheduleValue = schedule.get(indexValue);
				scheduleHour = Integer.valueOf(schedule.get(indexTime)
						.substring(0, 2));
				scheduleMinute = Integer.valueOf(schedule.get(indexTime)
						.substring(3, 5));

				scheduleWeek = schedule.get(indexWeek);

				getCurrentTime();
				currentHour = Integer.valueOf(currentHour);
				currentMinute = Integer.valueOf(currentMinute);
				currentSecond = Integer.valueOf(currentSecond);
				currentMillis = Integer.valueOf(currentMillis);
				currentDof = Integer.valueOf(currentDof);

				// calculate the amount of hours till alarmManager
				if ((currentHour < scheduleHour)
						&& (currentMinute > scheduleMinute))
					scheduleHour = scheduleHour - currentHour - 1;
				else if ((currentHour < scheduleHour)
						&& (currentMinute == scheduleMinute)) {
					scheduleHour = scheduleHour - currentHour;
					runHour = 0;
				} else if ((currentHour < scheduleHour)
						&& (currentMinute < scheduleMinute)) {
					scheduleHour = scheduleHour - currentHour;
					runHM = 0;
				} else if ((currentHour > scheduleHour)
						&& (currentMinute > scheduleMinute)) {
					currentHour = 24 - currentHour - 1;
					scheduleHour = currentHour + scheduleHour;
				} else if ((currentHour > scheduleHour)
						&& (currentMinute < scheduleMinute)) {
					currentHour = 24 - currentHour;
					scheduleHour = currentHour + scheduleHour;
				} else if ((currentHour > scheduleHour)
						&& (currentMinute == scheduleMinute)) {
					currentHour = 24 - currentHour - 1;
					scheduleHour = currentHour + scheduleHour;
				} else if ((currentHour == scheduleHour)
						&& (currentMinute < scheduleMinute))
					scheduleHour = 0;
				else if ((currentHour == scheduleHour)
						&& (currentMinute > scheduleMinute))
					scheduleHour = 23;
				else if ((currentHour == scheduleHour)
						&& (currentMinute == scheduleMinute))
					runNow = 0;

				if ((runNow != 0) && (runHour != 0) && (runHM != 0)) {
					// calculate amount of minutes till alarmManager
					if (currentMinute < scheduleMinute)
						scheduleMinute = scheduleMinute - currentMinute - 1;
					else if (currentMinute > scheduleMinute) {
						currentMinute = 60 - currentMinute - 1;
						scheduleMinute = currentMinute + scheduleMinute;
					} else if (currentMinute == scheduleMinute) {
						currentMinute = 60 - currentMinute - 1;
						scheduleMinute = currentMinute + scheduleMinute;
					}

					// calculate amount of seconds till alarmManager
					if (currentSecond > 0)
						currentSecond = 60 - currentSecond - 1;
					else if (currentSecond == 0)
						currentSecond = 0;

					// calculate amount of milliseconds till alarmManager
					if (currentMillis > 0)
						currentMillis = 1000 - currentMillis;
					else if (currentMillis == 0)
						currentMillis = 0;
				}

				if (runNow != 0 && runHour != 0 && runHM != 0)
					alarmTime = (scheduleHour * 60 * 60 * 1000)
							+ (scheduleMinute * 60 * 1000)
							+ (currentSecond * 1000) + currentMillis;

				else if (runHour == 0)
					alarmTime = (scheduleHour * 60 * 60 * 1000)
							- (currentMinute * 60 * 1000)
							- (currentSecond * 1000) - currentMillis;

				else if (runHM == 0)
					alarmTime = (scheduleHour * 60 * 60 * 1000)
							+ ((scheduleMinute + 1) * 60 * 1000)
							- (currentSecond * 1000) - currentMillis;

				else
					alarmTime = 0;

				intent.putExtra("task", scheduleTask);
				intent.putExtra("value", scheduleValue);
				intent.putExtra("week", scheduleWeek);

				alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
						System.currentTimeMillis() + alarmTime,
						AlarmManager.INTERVAL_DAY,
						PendingIntent.getService(this, scheduleId, intent, PendingIntent.FLAG_CANCEL_CURRENT));
				indexId += 5;
				indexTask += 5;
				indexValue += 5;
				indexTime += 5;
				indexWeek += 5;
			}

			Toast.makeText(getApplicationContext(), "MyTasker: on",
					Toast.LENGTH_LONG).show();
		} else {
			status = "0";
			saveStatus();
			stopService(new Intent(this, MyService.class));
			for (int i = 0; i < count; i++) {
				scheduleId = Integer.valueOf(schedule.get(indexId));
				alarmManager.cancel(PendingIntent.getService(this, scheduleId,
						intent, 0));
				indexId += 5;
			}
			stopService(new Intent(this, MyService.class));
			Toast.makeText(getApplicationContext(), "MyTasker: off",
					Toast.LENGTH_LONG).show();
		}
	}

	private String getCurrentTime() {
		Calendar calendar = Calendar.getInstance();

		currentHour = calendar.get(Calendar.HOUR_OF_DAY);
		currentMinute = calendar.get(Calendar.MINUTE);
		currentSecond = calendar.get(Calendar.SECOND);
		currentMillis = calendar.get(Calendar.MILLISECOND);
		currentDof = calendar.get(Calendar.DAY_OF_WEEK);
		// 1 - Sunday, 2 - Monday
		String HHMMSSD = String.format("%02d:%02d:%02d:%d", currentHour,
				currentMinute, currentSecond, currentDof); // HH:MM:SS:D
		return HHMMSSD;
	}

	void editDate() {
		Intent iMessage = new Intent(getApplicationContext(), Message.class);
		startActivity(iMessage);
		remove = 1;
		Message.title = db.getTitle(m_id);
		Message.trigger = db.getTrigger(m_id);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, IDM_CRASH, Menu.NONE, "Exit").setIcon(
				R.drawable.menu_exit);
		return (super.onCreateOptionsMenu(menu));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case IDM_CRASH: {
			onBackPressed();
		}
			break;
		}
		return (super.onOptionsItemSelected(item));
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {

		case IDM_ACTION:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Select the action");

			builder.setItems(mActions, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {

					// edit
					if (item == 0) {
						editDate();
					}

					// tasks
					if (item == 1) {
						Intent Task = new Intent(getApplicationContext(),
								Task.class);
						startActivity(Task);
					}

					// delete
					if (item == 2) {
						showDialog(IDM_REMOVE);
					}

					// cancel
					if (item == 3) {
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
							MainActivity.this.finish();
							onDestroy();
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
			builder2.setMessage("Delete selected profile and all its tasks?");
			builder2.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							if (m_id != 0) {
								db.readRecOn(db.mDB, "Profiles");
								if (db.idOnLong == m_id) {
									boolean on = toggleButton.isChecked();
									if (on) {
										toggleButton.setChecked(false);
										onToggleClicked(null);
									}
								}
								m_delRec();
								db.delRecFromTab2(DB.click_id);
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
	void refreshCursor() {
		stopManagingCursor(cursor);
		cursor = db.getAllData();
		startManagingCursor(cursor);
	}

	// keypress processing
	public void onButtonClick_addRec(View view) {
		Intent iMessage = new Intent(this, Message.class);
		startActivity(iMessage);
	}

	public void editReccordToDB(String title, String trigger) {
		// edit record
		db.editRec(m_id, title, trigger);
		// refresh cursor
		refreshCursor();
		// pass new cursor to adapter
		scAdapter.changeCursor(cursor);
		m_id = 0;
	}

	public void addReccordToDB(String title, String trigger) {

		// add record
		db.addRec(title, trigger);
		// refresh cursor
		refreshCursor();
		// pass new cursor to adapter
		scAdapter.changeCursor(cursor);
		m_id = 0;
	}

	void m_delRec() {
		db.delRec(m_id);
		// refresh cursor
		refreshCursor();
		// pass new cursor to adapter
		scAdapter.changeCursor(cursor);
		m_id = 0;
	}

	void saveStatus() {
		// only our application can access data
		sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
		Editor ed = sPref.edit();
		ed.putString(SAVE_STATUS, status);
		ed.commit();
	}

	void loadStatus() {
		sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
		status = sPref.getString(SAVE_STATUS, "");
		if (status.equals("1"))
			toggleButton.setChecked(true);
		else
			toggleButton.setChecked(false);
	}

	protected void onDestroy() {
		super.onDestroy();
		// close connection to DB
		db.close();
	}

	@Override
	public void onBackPressed() {
		showDialog(IDM_EXIT);
	}
}
