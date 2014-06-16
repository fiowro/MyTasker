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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Calendar;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.widget.Toast;

public class MyService extends Service {
	String scheduleTask;
	String scheduleValue;
	String scheduleWeek;

	int dof = 0;
	int wifi = 0;
	int brightness = 256;
	int audio = 0;
	int network = 1;
	final int DIALOG_EXIT = 1;
	final String LOG_TAG = "myLogs";

	public void onCreate() {
		super.onCreate();		
	}

	public int onStartCommand(Intent intent, int flags, int startId) {		
		getCurrentTime();

		scheduleTask = intent.getStringExtra("task");
		scheduleValue = intent.getStringExtra("value");
		scheduleWeek = intent.getStringExtra("week");
		String mon = scheduleWeek.substring(0, 1);
		String tue = scheduleWeek.substring(1, 2);
		String wed = scheduleWeek.substring(2, 3);
		String thu = scheduleWeek.substring(3, 4);
		String fri = scheduleWeek.substring(4, 5);
		String sat = scheduleWeek.substring(5, 6);
		String sun = scheduleWeek.substring(6, 7);


		if (dof == 1) {
			if (sun.equals("1")) {
				task();
				Wifi(wifi);
				Brightness(brightness);
				Audio(audio);
				Network(network);
			}
		}

		if (dof == 2) {
			if (mon.equals("1")) {
				task();
				Wifi(wifi);
				Brightness(brightness);
				Audio(audio);
				Network(network);
			}
		}

		if (dof == 3) {
			if (tue.equals("1")) {
				task();
				Wifi(wifi);
				Brightness(brightness);
				Audio(audio);
				Network(network);
			}
		}

		if (dof == 4) {
			if (wed.equals("1")) {
				task();
				Wifi(wifi);
				Brightness(brightness);
				Audio(audio);
				Network(network);
			}
		}

		if (dof == 5) {
			if (thu.equals("1")) {
				task();
				Wifi(wifi);
				Brightness(brightness);
				Audio(audio);
				Network(network);
			}
		}

		if (dof == 6) {
			if (fri.equals("1")) {
				task();
				Wifi(wifi);
				Brightness(brightness);
				Audio(audio);
				Network(network);
			}
		}

		if (dof == 7) {
			if (sat.equals("1")) {
				task();
				Wifi(wifi);
				Brightness(brightness);
				Audio(audio);
				Network(network);
			}
		}
				
		stopSelf();
		return super.onStartCommand(intent, flags, startId);
	}

	public void task() {
		if (scheduleTask.equals("Wi-fi")) {
			if (scheduleValue.equals("Turn on")) {
				Toast.makeText(this, "turn off- Wi-fi", Toast.LENGTH_LONG).show();
				Wifi(wifi = 1);
			} else if (scheduleValue.equals("Turn off")) {
				Toast.makeText(this, "turn off - Wi-fi", Toast.LENGTH_LONG).show();
				wifi = 2;
			}

		} else if (scheduleTask.equals("Brightness")) {
			scheduleValue = scheduleValue.substring(0,
					scheduleValue.length() - 1);
			brightness = Integer.valueOf(scheduleValue);
			brightness = 255 * brightness / 100;
			Toast.makeText(this, "вкл - Яркость" + brightness,
					Toast.LENGTH_LONG).show();
		}

		else if (scheduleTask.equals("Sound")) {
			if (scheduleValue.equals("Ring")) {
				Toast.makeText(this, "Ring", Toast.LENGTH_LONG).show();
				audio = 1;
			} else if (scheduleValue.equals("Vibro")) {
				Toast.makeText(this, "Vibro", Toast.LENGTH_LONG).show();
				audio = 2;
			} else if (scheduleValue.equals("Silent")) {
				Toast.makeText(this, "Silent", Toast.LENGTH_LONG).show();
				audio = 3;
			}

		} else if (scheduleTask.equals("Data transfer")) {
			if (scheduleValue.equals("Turn on")) {
				Toast.makeText(this, "turn on - data", Toast.LENGTH_LONG).show();
				network = 1;
			} else if (scheduleValue.equals("Turn off")) {
				Toast.makeText(this, "turn off - data", Toast.LENGTH_LONG).show();
				network = 2;
			}

		}
	}

	// wifi
	public void Wifi(int status) {
		if (status == 1) {
			WifiManager Wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
			Wifi.setWifiEnabled(true);
		} else if (status == 2) {
			WifiManager Wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
			Wifi.setWifiEnabled(false);
		}
	}

	// brightness
	public void Brightness(int status) {
		if (status != 256) {
			android.provider.Settings.System.putInt(getContentResolver(),
					android.provider.Settings.System.SCREEN_BRIGHTNESS, status); // ВАХ
		}
	}

	// sound profiles
	public void Audio(int status) {
		if (status == 1) {
			AudioManager manager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			manager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
		} else if (status == 2) {
			AudioManager manager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			manager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
		} else if (status == 3) {
			AudioManager manager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			manager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
		}
	}

	// data transfer
	public void Network(int status) {
		if (status == 1) {
			try {
				// create instance of connectivity manager and get system
				// connectivity service
				final ConnectivityManager conman = (ConnectivityManager) this
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				// create instance of class and get name of connectivity manager
				// system service class
				final Class conmanClass = Class.forName(conman.getClass()
						.getName());
				// create instance of field and get mService Declared field
				final Field iConnectivityManagerField = conmanClass
						.getDeclaredField("mService");
				// Attempt to set the value of the accessible flag to true
				iConnectivityManagerField.setAccessible(true);
				// create instance of object and get the value of field conman
				final Object iConnectivityManager = iConnectivityManagerField
						.get(conman);
				// create instance of class and get the name of
				// iConnectivityManager field
				final Class iConnectivityManagerClass = Class
						.forName(iConnectivityManager.getClass().getName());
				// create instance of method and get declared method and type
				final Method setMobileDataEnabledMethod = iConnectivityManagerClass
						.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
				// Attempt to set the value of the accessible flag to true
				setMobileDataEnabledMethod.setAccessible(true);
				// dynamically invoke the iConnectivityManager object according
				// to your need (true/false)
				setMobileDataEnabledMethod.invoke(iConnectivityManager, true);
			} catch (Exception e) {
			}
		} else if (status == 2) {
			try {
				// create instance of connectivity manager and get system
				// connectivity service
				final ConnectivityManager conman = (ConnectivityManager) this
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				// create instance of class and get name of connectivity manager
				// system service class
				final Class conmanClass = Class.forName(conman.getClass()
						.getName());
				// create instance of field and get mService Declared field
				final Field iConnectivityManagerField = conmanClass
						.getDeclaredField("mService");
				// Attempt to set the value of the accessible flag to true
				iConnectivityManagerField.setAccessible(true);
				// create instance of object and get the value of field conman
				final Object iConnectivityManager = iConnectivityManagerField
						.get(conman);
				// create instance of class and get the name of
				// iConnectivityManager field
				final Class iConnectivityManagerClass = Class
						.forName(iConnectivityManager.getClass().getName());
				// create instance of method and get declared method and type
				final Method setMobileDataEnabledMethod = iConnectivityManagerClass
						.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
				// Attempt to set the value of the accessible flag to true
				setMobileDataEnabledMethod.setAccessible(true);
				// dynamically invoke the iConnectivityManager object according
				// to your need (true/false)
				setMobileDataEnabledMethod.invoke(iConnectivityManager, false);
			} catch (Exception e) {
			}
		}
	}	

	public void onDestroy() {
		super.onDestroy();		
	}

	@Override
	public IBinder onBind(Intent arg0) {		
		// TODO Auto-generated method stub
		return null;
	}

	private int getCurrentTime() {
		Calendar calendar = Calendar.getInstance();

		dof = calendar.get(Calendar.DAY_OF_WEEK);
		return dof;
	}

}
