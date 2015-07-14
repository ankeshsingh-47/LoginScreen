package com.ysoserious.bliinder.utils;

import java.util.List;

import android.app.Activity;
import android.location.LocationManager;

import com.facebook.Session;
import com.parse.ParseGeoPoint;
import com.ysoserious.bliinder.entities.BliinderDate;
import com.ysoserious.bliinder.entities.User;

public class Data {
	public static User me;
	public static LocationManager locationManager;
	public static int desiredDistance;
	public static int minAge;
	public static int maxAge;
	public static List<BliinderDate> myDates;
	public static ParseGeoPoint location;
	public static Session facebookSession;
	// This is here because it is an actual data used during the run.
	public static final int desiredSeriousityRange = 15;
	public static List<Activity> lastsFoursquareActivities;
}