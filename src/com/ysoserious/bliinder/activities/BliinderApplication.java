package com.ysoserious.bliinder.activities;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseCrashReporting;
import com.parse.ParseUser;
import com.parse.PushService;
import com.ysoserious.bliinder.entities.BliinderPreferences;
import com.ysoserious.bliinder.entities.User;
import com.ysoserious.bliinder.utils.Data;

public class BliinderApplication extends Application {
	private final String APP_ID = "5GPxWxmY6hjvj3uk0blOHhugP5tYN4V6iqNhpad8";
	private final String CLIENT_KEY = "XZgj7hRyoN1Dl33IESSncw4oljlUuBZytNpv3s7z";

	@Override
	public void onCreate() {
		super.onCreate();

		// Initialize Crash Reporting.
		ParseCrashReporting.enable(this);

		// Add your initialization code here
		Parse.initialize(this, APP_ID, CLIENT_KEY);

		ParseUser.enableAutomaticUser();

		ParseUser.getCurrentUser().saveInBackground();

		BliinderPreferences.initBliinderPreferences(getApplicationContext());
		
		ParseACL defaultACL = new ParseACL();

		// If you would like all objects to be private by default, remove this
		// line.
		defaultACL.setPublicReadAccess(true);
		defaultACL.setPublicWriteAccess(true);

		ParseACL.setDefaultACL(defaultACL, true);
		PushService.setDefaultPushCallback(BliinderApplication.this,
				MyBliiderDates.class);
	}
}
