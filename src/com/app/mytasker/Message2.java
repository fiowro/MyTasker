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
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class Message2 extends Activity {

	TextView textValue;
	TextView textSeek;
	CheckBox cbMon;
	CheckBox cbTue;
	CheckBox cbWed;
	CheckBox cbThu;
	CheckBox cbFri;
	CheckBox cbSat;
	CheckBox cbSun;

	DB db;
	TimePicker timepicker;
	Spinner spinner;
	Button buttonSave;
	SeekBar seekBar;
	LinearLayout view;
	SharedPreferences sPref;

	long id;
	String timePicker;
	public static String num;
	public static String task;
	public static String value;
	public static String time;
	public static String days;
	public static String week;
	public long editFlag; // flag of the edit mode
	public long editSeek; // value of the seekBar

	int currentHour;
	int currentMinute;
	int currentSecond;
	int currentDof;
	int currentMillis;

	String cbf1 = "0";
	String cbf2 = "0";
	String cbf3 = "0";
	String cbf4 = "0";
	String cbf5 = "0";
	String cbf6 = "0";
	String cbf7 = "0";
	String cbAll;

	String cb1 = "";
	String cb2 = "";
	String cb3 = "";
	String cb4 = "";
	String cb5 = "";
	String cb6 = "";
	String cb7 = "";
	String cbDays;

	String nums = String.valueOf(DB.click_id);
	public static ArrayList<String> schedule = new ArrayList<String>();

	String[] data = { "no", "Wi-fi", "Brightness", "Sound profiles",
			"Data transfer" };

	final String SAVE_STATUS = "saved_status";

	private final int IDM_WIFI = 0;
	private final int IDM_BRIGHTNESS = 1;
	private final int IDM_AUDIO = 2;
	private final int IDM_NETWORK = 3;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message2);

		textValue = (TextView) findViewById(R.id.textValue);

		cbMon = (CheckBox) findViewById(R.id.checkMon);
		cbTue = (CheckBox) findViewById(R.id.checkTue);
		cbWed = (CheckBox) findViewById(R.id.checkWed);
		cbThu = (CheckBox) findViewById(R.id.checkThu);
		cbFri = (CheckBox) findViewById(R.id.checkFri);
		cbSat = (CheckBox) findViewById(R.id.checkSat);
		cbSun = (CheckBox) findViewById(R.id.checkSun);

		timepicker = (TimePicker) findViewById(R.id.timePicker);
		timepicker.setIs24HourView(true);

		spinner = (Spinner) findViewById(R.id.spinnerTask);

		// edit mode flag
		if (Task.remove == 1) {
			editFlag = 1;
			Task.remove = 0;
			setDataMessage2(num, task, value, time, days, week);
		}

		// listview adapter
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, data);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinner.setAdapter(adapter);

		// header
		spinner.setPrompt("List of tasks");

		if (editFlag == 1) {
			if (task.equals("Wi-fi"))
				spinner.setSelection(1);
			if (task.equals("Brightness")) {
				spinner.setSelection(2);
				editSeek = 1;
			}
			if (task.equals("Sound"))
				spinner.setSelection(3);
			if (task.equals("Data transfer"))
				spinner.setSelection(4);
		} else
			spinner.setSelection(0);

		// set keypress handler
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {

				// wi-fi
				if (position == 1) {
					task = "Wi-fi";
					showDialog(IDM_WIFI);
				}

				// brightness
				if (position == 2) {
					task = "Brightness";
					showDialog(IDM_BRIGHTNESS);
				}

				// sound profile
				if (position == 3) {
					task = "Sound";
					showDialog(IDM_AUDIO);
				}

				// data transfer
				if (position == 4) {
					task = "Data transfer";
					showDialog(IDM_NETWORK);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		cbMon.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (buttonView.isChecked()) {
					cbf1 = "1";
					cb1 = "Mon.";
				} else {
					cbf1 = "0";
					cb1 = "";
				}
			}
		});

		cbTue.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (buttonView.isChecked()) {
					cbf2 = "1";
					cb2 = "Tue.";
				} else {
					cbf2 = "0";
					cb2 = "";
				}
			}
		});

		cbWed.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (buttonView.isChecked()) {
					cbf3 = "1";
					cb3 = "Wed.";
				} else {
					cbf3 = "0";
					cb3 = "";
				}
			}
		});

		cbThu.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (buttonView.isChecked()) {
					cbf4 = "1";
					cb4 = "Thu.";
				} else {
					cbf4 = "0";
					cb4 = "";
				}
			}
		});

		cbFri.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (buttonView.isChecked()) {
					cbf5 = "1";
					cb5 = "Fri.";
				} else {
					cbf5 = "0";
					cb5 = "";
				}
			}
		});

		cbSat.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (buttonView.isChecked()) {
					cbf6 = "1";
					cb6 = "Sat.";
				} else {
					cbf6 = "0";
					cb6 = "";
				}
			}
		});

		cbSun.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (buttonView.isChecked()) {
					cbf7 = "1";
					cb7 = "Sun.";
				} else {
					cbf7 = "0";
					cb7 = "";
				}
			}

		});

	}

	// get data for editing
	void setDataMessage2(String num, String task, String value, String time,
			String days, String week) {

		textValue.setText(value);
		String myHourStr = time.substring(0, 2);
		int myHour = Integer.valueOf(myHourStr);
		String myMinuteStr = time.substring(3, 5);
		int myMinute = Integer.valueOf(myMinuteStr);
		timepicker.setCurrentHour(myHour);
		timepicker.setCurrentMinute(myMinute);

		// get day of week from the "code" to use it in a checkbox
		char c = week.charAt(0);
		if (c == 0x31) {
			cbMon.setChecked(true);
			cbf1 = "1";
			cb1 = "Mon.";
		}
		c = week.charAt(1);
		if (c == 0x31) {
			cbTue.setChecked(true);
			cbf2 = "1";
			cb2 = "Tue.";
		}
		c = week.charAt(2);
		if (c == 0x31) {
			cbWed.setChecked(true);
			cbf3 = "1";
			cb3 = "Wed.";
		}
		c = week.charAt(3);
		if (c == 0x31) {
			cbThu.setChecked(true);
			cbf4 = "1";
			cb4 = "Thu.";
		}
		c = week.charAt(4);
		if (c == 0x31) {
			cbFri.setChecked(true);
			cbf5 = "1";
			cb5 = "Fri.";
		}
		c = week.charAt(5);
		if (c == 0x31) {
			cbSat.setChecked(true);
			cbf6 = "1";
			cb6 = "Sat.";
		}
		c = week.charAt(6);
		if (c == 0x31) {
			cbSun.setChecked(true);
			cbf7 = "1";
			cb7 = "Sun.";
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {

		case IDM_WIFI:

			AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
			builder1.setTitle("Configure wi-fi");

			final CharSequence[] mActions1 = { "Turn on", "Turn off" };

			builder1.setItems(mActions1, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {

					if (item == 0) {
						textValue.setText("Turn on");
					}

					if (item == 1) {
						textValue.setText("Turn off");
					}
				}
			});

			builder1.setCancelable(false);
			return builder1.create();

		case IDM_BRIGHTNESS:

			AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
			// create view from dialog.xml
			view = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog,
					null);

			seekBar = (SeekBar) view.findViewById(R.id.seekBar);
			textSeek = (TextView) view.findViewById(R.id.textSeek);

			seekBar.setOnSeekBarChangeListener(SeekBarListener);

			if (editSeek == 1) {
				editSeek = 0;
				value = value.replace("%", "");
				int val = Integer.valueOf(value);
				seekBar.setProgress(val);
			}

			// set it as content of the dialog body
			builder2.setView(view);

			builder2.setPositiveButton("Save",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							textValue.setText(String.valueOf(seekBar
									.getProgress()) + "%");
							dialog.cancel();
						}
					});

			builder2.setTitle("Brightness settings");

			builder2.setCancelable(false);
			return builder2.create();

		case IDM_AUDIO:

			AlertDialog.Builder builder3 = new AlertDialog.Builder(this);
			builder3.setTitle("Sound settings");

			final CharSequence[] mActions3 = { "Ring", "Vibro", "Silent" };

			builder3.setItems(mActions3, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {

					if (item == 0) {
						textValue.setText("Ring");
					}

					if (item == 1) {
						textValue.setText("Vibro");
					}

					if (item == 2) {
						textValue.setText("Silent");
					}
				}
			});

			builder3.setCancelable(false);
			return builder3.create();

		case IDM_NETWORK:

			AlertDialog.Builder builder4 = new AlertDialog.Builder(this);
			builder4.setTitle("Data transfer");

			final CharSequence[] mActions4 = { "Turn on", "Turn off" };

			builder4.setItems(mActions4, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {

					if (item == 0) {
						textValue.setText("Turn on");
					}

					if (item == 1) {
						textValue.setText("Turn off");
					}
				}
			});

			builder4.setCancelable(false);
			return builder4.create();
		}
		return super.onCreateDialog(id);
	}

	private SeekBar.OnSeekBarChangeListener SeekBarListener = new OnSeekBarChangeListener() {

		// notification about changed seekbar value
		public void onProgressChanged(SeekBar seekBark, int progress,
				boolean fromUser) {
			textSeek.setText(String.valueOf(seekBar.getProgress()) + "%");
		}

		// notification if user started to change seekbar position
		public void onStartTrackingTouch(SeekBar seekBar) {
		}

		// notification if user finished to change seekbar position
		public void onStopTrackingTouch(SeekBar seekBar) {
			textSeek.setText(String.valueOf(seekBar.getProgress()) + "%");
		}

	};

	// processing "save" button event
	public void onButton1Click_Save(View view) {

		if (timepicker.getCurrentHour() < 10
				&& timepicker.getCurrentMinute() > 9) {
			timePicker = ("0") + timepicker.getCurrentHour() + (":")
					+ timepicker.getCurrentMinute();
		}

		if (timepicker.getCurrentHour() < 10
				&& timepicker.getCurrentMinute() < 10) {
			timePicker = ("0") + timepicker.getCurrentHour() + (":0")
					+ timepicker.getCurrentMinute();
		}

		if (timepicker.getCurrentHour() > 9
				&& timepicker.getCurrentMinute() < 10) {
			timePicker = timepicker.getCurrentHour() + (":0")
					+ timepicker.getCurrentMinute();
		}

		if (timepicker.getCurrentHour() > 9
				&& timepicker.getCurrentMinute() > 9) {
			timePicker = timepicker.getCurrentHour() + (":")
					+ timepicker.getCurrentMinute();
		}

		cbDays = cb1 + cb2 + cb3 + cb4 + cb5 + cb6 + cb7;

		cbAll = cbf1 + cbf2 + cbf3 + cbf4 + cbf5 + cbf6 + cbf7;

		if (spinner.getSelectedItemPosition() != 0 && editFlag == 1
				&& cbDays.length() != 0) {
			editFlag = 0;
			Task.m_self.editReccordToDB2(nums, task, textValue.getText()
					.toString(), timePicker, cbDays, cbAll);
			int serviceFlag = 1;
			loadStatus(serviceFlag);
			finish();
		} else if (spinner.getSelectedItemPosition() != 0
				&& cbDays.length() != 0) {
			Task.m_self.addReccordToDB2(nums, task, textValue.getText()
					.toString(), timePicker, cbDays, cbAll);
			int serviceFlag = 2;
			loadStatus(serviceFlag);
			finish();
		} else if (spinner.getSelectedItemPosition() == 0
				|| cbDays.length() == 0)
			Toast.makeText(getApplicationContext(),
					"Select the task and the day of week", Toast.LENGTH_LONG)
					.show();
	}

	// process "cancel" button event
	public void onButton2Click_Cancel(View view) {
		finish();
	}

	void m_service(int serviceFlag) {
		if (serviceFlag == 1) {
			long m_id2 = getIntent().getLongExtra("m_id2", 0);
			int numInt = Integer.valueOf(nums);
			db = new DB(this);
			db.open();
			db.readRec2(numInt, m_id2, db.mDB, "Profiles", "Task");
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
			int alarmTime = 0;
			int runNow = 1;
			int runHour = 1;
			int runHM = 1;

			Intent intent = new Intent(this, MyService.class);
			AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

			scheduleId = Integer.valueOf(schedule.get(indexId));
			scheduleTask = schedule.get(indexTask);
			scheduleValue = schedule.get(indexValue);
			scheduleHour = Integer.valueOf(schedule.get(indexTime).substring(0,
					2));
			scheduleMinute = Integer.valueOf(schedule.get(indexTime).substring(
					3, 5));

			scheduleWeek = schedule.get(indexWeek);

			getCurrentTime();
			currentHour = Integer.valueOf(currentHour);
			currentMinute = Integer.valueOf(currentMinute);
			currentSecond = Integer.valueOf(currentSecond);
			currentMillis = Integer.valueOf(currentMillis);
			currentDof = Integer.valueOf(currentDof);

			// calculate amount of hours till alarmManager
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
						+ (scheduleMinute * 60 * 1000) + (currentSecond * 1000)
						+ currentMillis;

			else if (runHour == 0)
				alarmTime = (scheduleHour * 60 * 60 * 1000)
						- (currentMinute * 60 * 1000) - (currentSecond * 1000)
						- currentMillis;

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
					AlarmManager.INTERVAL_DAY, PendingIntent.getService(this,
							scheduleId, intent,
							PendingIntent.FLAG_CANCEL_CURRENT));

		}

	}

	private String getCurrentTime() {
		Calendar calendar = Calendar.getInstance();

		currentHour = calendar.get(Calendar.HOUR_OF_DAY);
		currentMinute = calendar.get(Calendar.MINUTE);
		currentSecond = calendar.get(Calendar.SECOND);
		currentMillis = calendar.get(Calendar.MILLISECOND);
		currentDof = calendar.get(Calendar.DAY_OF_WEEK);
		// 1 - sunday, 2 - monday
		String HHMMSSD = String.format("%02d:%02d:%02d:%d", currentHour,
				currentMinute, currentSecond, currentDof); // HH:MM:SS:D
		return HHMMSSD;
	}

	void loadStatus(int serviceFlag) {
		sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
		String status = sPref.getString(SAVE_STATUS, "");
		if (status.equals("1"))
			m_service(serviceFlag);
	}

}
