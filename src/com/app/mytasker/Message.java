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
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Message extends Activity {

	long m_id = 0;

	EditText edit1;

	LocationManager mngr;
	Location location;

	public static String title;
	public static String trigger = "Off";
	public long editFlag;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message);

		edit1 = (EditText) findViewById(R.id.editText1);

		// turn edit mode flag on
		if (MainActivity.remove == 1) {
			editFlag = 1;
			MainActivity.remove = 0;
			setDataMessage(title, trigger);
		}

	}

	void setDataMessage(String title, String trigger) {
		edit1.setText(title);
	}

	// processing "save" button event
	public void onButton1Click_Save(View view) {
		if (edit1.getText().toString().length() != 0 && editFlag == 1) {
			editFlag = 0;
			MainActivity.m_self.editReccordToDB(edit1.getText().toString(),
					trigger);
			finish();
		} else if (edit1.getText().toString().length() != 0) {
			MainActivity.m_self.addReccordToDB(edit1.getText().toString(),
					trigger);
			finish();
		} else
			Toast.makeText(getApplicationContext(),
					"Enter the profile name", Toast.LENGTH_LONG).show();
	}

	// processing "cancel" button event
	public void onButton2Click_Cancel(View view) {
		finish();
	}

}
