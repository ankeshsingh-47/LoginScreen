package com.ysoserious.bliinder.entities;

import android.content.Context;
import android.content.SharedPreferences;

public class BliinderPreferences {

	private static SharedPreferences myPrefs;
	private static final String MIN_AGE = "minAge";
	private static final String MAX_AGE = "maxAge"; 
	private static final String DESIRED_DISTANCE = "desiredDistance"; 
	
	@SuppressWarnings({ "static-access", "deprecation" })
	public static void initBliinderPreferences(Context c){
		myPrefs = c.getSharedPreferences("myPrefs",
				c.MODE_WORLD_READABLE);
	}
	
	public static void setMinAge(int minAge) {
		SharedPreferences.Editor mEdit1 = myPrefs.edit();
		mEdit1.putInt(MIN_AGE, minAge);
		mEdit1.commit();
	}

	public static void setMaxAge(int maxAge) {
		SharedPreferences.Editor mEdit1 = myPrefs.edit();
		mEdit1.putInt(MAX_AGE, maxAge);
		mEdit1.commit();
	}

	public static void setDesiredDistance(int desiredDistance) {
		SharedPreferences.Editor mEdit1 = myPrefs.edit();
		mEdit1.putInt(DESIRED_DISTANCE, desiredDistance);
		mEdit1.commit();
	}

	public static int getDesiredDistance() {
		return myPrefs.getInt(DESIRED_DISTANCE, 0);
	}

	public static int getMinAge() {
		return myPrefs.getInt(MIN_AGE, 0);
	}

	public static int getMaxAge() {
		return myPrefs.getInt(MAX_AGE, 0);
	}

	public static boolean dataSavedInThisDevice() {
		return myPrefs.getInt(MIN_AGE, -1) != -1;
	}

}
