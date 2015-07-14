package com.ysoserious.bliinder.entities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.javacodegeeks.foursquareapiexample.AndroidFoursquare;
import com.javacodegeeks.foursquareapiexample.AndroidFoursquareFromMyBliinderDates;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.ysoserious.bliinder.activities.FillDetails;
import com.ysoserious.bliinder.utils.AvailableTime;
import com.ysoserious.bliinder.utils.Constant;
import com.ysoserious.bliinder.utils.Data;
import com.ysoserious.bliinder.utils.DateState;
import com.ysoserious.bliinder.utils.PlaceType;

public class Server {

	private static final int MIN_LOVE_RANGE = 0;
	private static final int MAX_LOVE_RANGE = 100;
	private static final String TAG = "Server";
	private static final String LOCATION = "location";
	private static final String SEPARATOR = "_";
	/* ~~~~~~~~~~~~~~ Tables' Names ~~~~~~~~~~~~~~ */
	private static final String USERS_TABLE_NAME = "Users";
	private static final String MATCHES_TABLE_NAME = "Matches";
	private static final String USER_CURRENT_COUNT_TABLE = "UserCurrentDateNum";
	private static final String POTENTIAL_BLIINDER_DATES = "PotentialBliinderDates";
	private static final String UPCOMING_BLIINDER_DATES = "UpcomingBliinderDates";
	private static final String NEVER_EVER_BLIINDER_DATES = "NeverEverBliinderDates";
	private static final String WHO_WANTS_WHO = "WhoWantsWho";

	// in 'DATE_NUM' table, for every potential or upcoming date, there are 2
	// rows, one with ID1_ID2, and the other: ID2_ID1.
	private static final String DATE_NUM_TABLE = "DateNum";

	/* ~~~~~~~~~~~~~~ Potential User Column Names ~~~~~~~~~~~~~~ */
	private static final String GENDER = "gender";
	private static final String ID = "ID";
	private static final String ID1 = "ID1";
	private static final String ID2 = "ID2";
	private static final String FEMALE = "FEMALE";
	private static final String MALE = "MALE";
	private static final String RELATION_TYPE = "relationType";
	private static final String YEAR_OF_BIRTH = "yearOfBirth";

	/* ~~~~~~~~~~~~~~ Potential Bliinder Dates Column Names ~~~~~~~~~~~~~~ */
	private static final String DAY_PREF_ID1 = "dayPrefID1";
	private static final String DAY_PREF_ID2 = "dayPrefID2";
	private static final String PLACE_TYPE_ID1 = "placeTypeID1";
	private static final String PLACE_TYPE_ID2 = "placeTypeID2";

	/* ~~~~~~~~~~~~~~ Upcoming Bliinder Dates Column Names ~~~~~~~~~~~~~~ */
	private static final String DATE = "date";
	private static final String DATE_LOCATION = "dateLocation";
	private static final String PLACE_TYPE = "placeType";

	/* ~~~~~~~~~~~~~~ Date Num Column Names ~~~~~~~~~~~~~~ */
	private static final String NUM = "num";

	/* ~~~~~~~~~~~~~~ WhoWantsWho Table ~~~~~~~~~~~~~~~~~~ */
	private static final String WANT = "want";
	private static final String ME = "me";
	private static final String PARTNER = "partner";

	/* ~~~~~~~~~~~~~~ GENERAL CONSTANTS ~~~~~~~~~~~~~~~~~~ */
	protected static final int FIRST = 0;
	private static final int INITIAL_DATE_NUM_VALUE = 1;
	private static final String DATE_NUM = "dateNum";
	private static final String GEO_LOCATION = "geoLocation";
	private static final String USER1_IN_CALENDAR = "inCalendar1";
	private static final String USER2_IN_CALENDAR = "inCalendar2";

	public static List<ParseObject> neverEverObject = new ArrayList<ParseObject>();
	public static List<BliinderDate> neverEverDateList = new ArrayList<BliinderDate>();
	public static List<BliinderDate> itsADateList = new ArrayList<BliinderDate>();

	public static void postUserInfo() {
		ParseQuery<ParseObject> query = ParseQuery.getQuery(USERS_TABLE_NAME);
		query.whereEqualTo(ID, Data.me.getFacebookId());
		query.findInBackground(new FindCallback<ParseObject>() {
			public void done(List<ParseObject> userDataOnCloud, ParseException e) {
				if (e == null) {
					ParseObject user = userDataOnCloud.isEmpty() ? new ParseObject(
							USERS_TABLE_NAME) : userDataOnCloud.get(FIRST);
					user.put(ID, Data.me.getFacebookId());
					user.put(GENDER, Data.me.isFemale() ? FEMALE : MALE);
					user.put(RELATION_TYPE, Data.me.getRelationshipType());
					user.put(WANT, Data.me.lookingForMale() ? MALE : FEMALE);
					user.put(YEAR_OF_BIRTH, Data.me.getYearOfBirth());
					user.put(LOCATION, Data.location);
					user.saveInBackground();
				} else {
					Log.d(TAG, "Error: " + e.getMessage());
				}
			}
		});

	}

	public static void blockingPostUserInfo() {
		ParseQuery<ParseObject> query = ParseQuery.getQuery(USERS_TABLE_NAME);
		query.whereEqualTo(ID, Data.me.getFacebookId());

		try {
			List<ParseObject> userDataOnCloud = query.find();
			ParseObject user = userDataOnCloud.isEmpty() ? new ParseObject(
					USERS_TABLE_NAME) : userDataOnCloud.get(FIRST);
			user.put(ID, Data.me.getFacebookId());
			user.put(GENDER, Data.me.isFemale() ? FEMALE : MALE);
			user.put(RELATION_TYPE, Data.me.getRelationshipType());
			user.put(WANT, Data.me.lookingForMale() ? MALE : FEMALE);
			user.put(YEAR_OF_BIRTH, Data.me.getYearOfBirth());
			user.put(LOCATION, Data.location);
			user.saveInBackground();
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}

	public static void getPotentialPartners(FindCallback<ParseObject> callback) {

		ParseQuery<ParseObject> query = ParseQuery.getQuery(USERS_TABLE_NAME);
		query.whereEqualTo(GENDER, Data.me.lookingForMale() ? MALE : FEMALE);
		query.whereLessThanOrEqualTo(YEAR_OF_BIRTH,
				Calendar.getInstance().get(Calendar.YEAR) - Data.minAge);
		query.whereGreaterThanOrEqualTo(YEAR_OF_BIRTH, Calendar.getInstance()
				.get(Calendar.YEAR) - Data.maxAge);
		query.whereNotEqualTo(ID, Data.me.getFacebookId());
		query.whereLessThanOrEqualTo(
				RELATION_TYPE,
				Math.min(Data.me.getRelationshipType()
						+ Data.desiredSeriousityRange, MAX_LOVE_RANGE));
		query.whereGreaterThanOrEqualTo(
				RELATION_TYPE,
				Math.max(Data.me.getRelationshipType()
						- Data.desiredSeriousityRange, MIN_LOVE_RANGE));
		query.whereWithinKilometers(LOCATION, Data.location,
				Data.desiredDistance);

		try {
			List<ParseObject> potentianlPartners = query.find();
			Set<String> usersIAlreadyDecidedAbout = getUsersAlreadyMet();
			callback.done(
					filterUsersAlreadyMatched(potentianlPartners,
							usersIAlreadyDecidedAbout), null);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private static Set<String> getUsersAlreadyMet() {
		ParseQuery<ParseObject> query = ParseQuery.getQuery(WHO_WANTS_WHO);
		query.whereEqualTo(ME, Data.me.getFacebookId());
		try {
			return toIdSet(query.find(), PARTNER);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static List<ParseObject> filterUsersAlreadyMatched(
			List<ParseObject> potentianlPartners, Set<String> inMatchAlready) {
		List<ParseObject> result = new LinkedList<ParseObject>();
		for (ParseObject po : potentianlPartners)
			if (!inMatchAlready.contains(po.getString(ID)))
				result.add(po);

		return result;
	}

	public static ParseGeoPoint getPartnerLocation(User partner) {
		ParseQuery<ParseObject> query = ParseQuery.getQuery(USERS_TABLE_NAME);
		query.whereEqualTo(ID, partner.getFacebookId());
		try {
			ParseObject partnerParseObject = query.find().get(FIRST);
			return partnerParseObject.getParseGeoPoint(LOCATION);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void postUserChoice(User partner, boolean wantPartner) {
		ParseObject pair = new ParseObject(WHO_WANTS_WHO);
		pair.put(ME, Data.me.getFacebookId());
		pair.put(PARTNER, partner.getFacebookId());
		pair.put(WANT, wantPartner);
		pair.saveInBackground();
	}

	public static boolean isAMatch(User user1, User user2) {
		String lesserID = user1.getFacebookId()
				.compareTo(user2.getFacebookId()) < 0 ? user1.getFacebookId()
				: user2.getFacebookId();
		String biggerID = user1.getFacebookId().equals(lesserID) ? user2
				.getFacebookId() : user1.getFacebookId();
		ParseQuery<ParseObject> query = ParseQuery.getQuery(MATCHES_TABLE_NAME);
		query.whereEqualTo(ID1, lesserID);
		query.whereEqualTo(ID2, biggerID);
		try {
			if (query.find().isEmpty())
				return false;
			else
				return true;
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * @return the potential user dates based on the match in the selection
	 *         part.
	 */
	public static List<BliinderDate> getMyDates(Context c) {
		List<String> partnerIds = new ArrayList<String>();

		// Gets all partners which are in the column ID1 in the Matches table.
		updatePartners(partnerIds, ID1, ID2);

		// Gets all partners which are in the column ID2 in the Matches table.
		updatePartners(partnerIds, ID2, ID1);

		// Get all Bliinder dates of the current user.
		List<BliinderDate> $ = getBliinderDateList(partnerIds, c);
		if (neverEverObject.size() > 0) {
			removeFromUpcomingAndFromNeverEverAndAddToNeverEver();
		}
		if (itsADateList.size() > 0) {
			c.startActivity(new Intent(c,
					AndroidFoursquareFromMyBliinderDates.class));
		}
		Collections.sort($);
		return $;

	}

	// Extract the corresponding bliinderDates for the corresponding
	// partners.
	private static List<BliinderDate> getBliinderDateList(
			List<String> partnerIds, Context c) {
		List<BliinderDate> $ = new ArrayList<BliinderDate>();
		for (String partnerId : partnerIds) {
			try {
				List<ParseObject> objs = getPartnersRows(partnerId);
				for (ParseObject partnerRow : objs) {
					User partner = extractPartner(partnerRow);
					boolean isPartnerID2 = (Data.me.getFacebookId().compareTo(
							partnerId) < 0);
					objs = getPotentialBliinderDatesRows(partnerId);
					for (ParseObject potentialBliinderDateRow : objs) {
						addPotentialBliinderDates($, partnerId, partner,
								isPartnerID2, potentialBliinderDateRow, c,
								potentialBliinderDateRow);
					}
					objs = getUpcomingBliinderDatesRows(partnerId);
					for (ParseObject upcomingBliinderDateRow : objs) {
						addUpcomingBliinderDates($, partnerId, partner,
								upcomingBliinderDateRow);
					}
					objs = getNeverEverBliinderDatesRows(partnerId);
					for (ParseObject neverEverBliinderDateRow : objs) {
						addNeverEverBliinderDates($, partnerId, partner,
								isPartnerID2, neverEverBliinderDateRow, c);
					}
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return $;
	}

	private static void addPotentialBliinderDates(List<BliinderDate> $,
			String partnerId, User partner, boolean isPartnerID2,
			ParseObject potentialBliinderDateRow, Context c, ParseObject obj) {
		JSONArray myJSONPlaceType = potentialBliinderDateRow
				.getJSONArray((isPartnerID2) ? PLACE_TYPE_ID1 : PLACE_TYPE_ID2);
		JSONArray partnerJSONPlaceType = potentialBliinderDateRow
				.getJSONArray((isPartnerID2) ? PLACE_TYPE_ID2 : PLACE_TYPE_ID1);
		JSONArray myJSONDayPref = potentialBliinderDateRow
				.getJSONArray((isPartnerID2) ? DAY_PREF_ID1 : DAY_PREF_ID2);
		JSONArray partnerJSONDayPref = potentialBliinderDateRow
				.getJSONArray((isPartnerID2) ? DAY_PREF_ID2 : DAY_PREF_ID1);
		DateState dateState = getDateState(myJSONDayPref, partnerJSONDayPref,
				partner, partnerJSONPlaceType, myJSONPlaceType);

		DatePref myPref = getDatePref(myJSONDayPref, myJSONPlaceType);
		String dateName = "Date " + getNumberDate(partnerId);
		DatePref partnerPref = getDatePref(partnerJSONDayPref,
				partnerJSONPlaceType);

		Date date = FillDetails.getMatch(myPref, partnerPref, partner);
		// If this is only a potential date, the placeType, date and
		// dateLocation are not determined yet.
		BliinderDate b = new BliinderDate(dateState, null, PlaceType.NONE,
				dateName, partner, myPref, partnerPref, null);

		/** START CHANGE **/
		if (dateState == DateState.NEVER_EVER) {
			neverEverObject.add(obj);
			neverEverDateList.add(b);
		}
		if (dateState == DateState.ITS_A_DATE
				&& Data.me.getFacebookId().compareTo(partnerId) < 0) {
			b.setDate(date);
			itsADateList.add(b);
			// MyBliiderDates.currentDate = b;
			// c.startActivity(new Intent(c, AndroidFoursquare.class));
		}
		if (dateState == DateState.ITS_A_DATE
				&& Data.me.getFacebookId().compareTo(partnerId) >= 0) {
			b.setState(DateState.AWAITING_ANSWER);
		}
		$.add(b);
	}

	private static void addUpcomingBliinderDates(List<BliinderDate> $,
			String partnerId, User partner, ParseObject bliinderDateRow) {
		String dateName = "Date " + getNumberDate(partnerId);

		// In an upcomingDate, the date state must be "ITS_A_DATE".
		// Furthermore, UserPref and PartnerPref is null, cause we need only the
		// final date and place. all other prior preferences are not important
		// anymore.
		BliinderDate b = new BliinderDate(DateState.ITS_A_DATE,
				bliinderDateRow.getDate(DATE),
				PlaceType.fromString(bliinderDateRow.getString(PLACE_TYPE)),
				dateName, partner, null, null,
				bliinderDateRow.getString(DATE_LOCATION));
		b.setLocation(bliinderDateRow.getParseGeoPoint(GEO_LOCATION));
		b.setInCalendar(bliinderDateRow.getBoolean(myCalendarColumn(partnerId)));
		$.add(b);
	}

	private static String myCalendarColumn(String partnerId) {
		return ((Data.me.getFacebookId().compareTo(partnerId) < 0) ? USER1_IN_CALENDAR
				: USER2_IN_CALENDAR);
	}

	private static void addNeverEverBliinderDates(List<BliinderDate> $,
			String partnerId, User partner, boolean isPartnerID2,
			ParseObject neverEverBliinderDateRow, Context c) {
		JSONArray myJSONPlaceType = neverEverBliinderDateRow
				.getJSONArray((isPartnerID2) ? PLACE_TYPE_ID1 : PLACE_TYPE_ID2);
		JSONArray partnerJSONPlaceType = neverEverBliinderDateRow
				.getJSONArray((isPartnerID2) ? PLACE_TYPE_ID2 : PLACE_TYPE_ID1);
		JSONArray myJSONDayPref = neverEverBliinderDateRow
				.getJSONArray((isPartnerID2) ? DAY_PREF_ID1 : DAY_PREF_ID2);
		JSONArray partnerJSONDayPref = neverEverBliinderDateRow
				.getJSONArray((isPartnerID2) ? DAY_PREF_ID2 : DAY_PREF_ID1);
		String dateName = "Date " + getNumberDate(partnerId);
		DatePref userPref = getDatePref(myJSONDayPref, myJSONPlaceType);
		DatePref partnerPref = getDatePref(partnerJSONDayPref,
				partnerJSONPlaceType);
		// If this is only a never ever date, the placeType, date and
		// dateLocation are not determined yet.
		BliinderDate b = new BliinderDate(DateState.NEVER_EVER, null,
				PlaceType.NONE, dateName, partner, userPref, partnerPref, null);
		$.add(b);
	}

	// Extract the partner himself from his facebookID.
	private static User extractPartner(ParseObject partnerRow) {
		return new User(partnerRow);
		// return new User(null, FEMALE.equals(partnerRow.getString(GENDER)),
		// partnerRow.getInt(RELATION_TYPE), partnerRow.getString(ID), "",
		// partnerRow.getInt(YEAR_OF_BIRTH), FEMALE.equals(partnerRow
		// .getString(WANT)));
	}

	private static int getNumberDate(String partnerId) {
		// String myID = Data.me.getFacebookId();
		// String dateSequenceID = myID + SEPARATOR + partnerId;
		// try {
		// ParseQuery<ParseObject> query = ParseQuery.getQuery(DATE_NUM_TABLE);
		// query.whereEqualTo(ID, dateSequenceID);
		// List<ParseObject> objs = query.find();
		// for (ParseObject o : objs) {
		// // not suppose to be more than a one suitable row.
		// return o.getInt(NUM);
		// }
		// } catch (ParseException e) {
		// e.printStackTrace();
		// }
		// return 0;
		return 1;
	}

	private static DateState getDateState(JSONArray myJSONDayPref,
			JSONArray partnerJSONDayPref, User partner,
			JSONArray partnerJSONPlaceType, JSONArray myJSONPlaceType) {
		DateState dateState = null;
		if (myJSONDayPref.length() == 0 && partnerJSONDayPref.length() == 0) {
			dateState = DateState.NOT_FILLED_PARTNER_NOT_FILLED;
		} else if (myJSONDayPref.length() == 0
				&& partnerJSONDayPref.length() != 0) {
			dateState = DateState.NOT_FILLED_PARTNER_FILLED;
		} else if (myJSONDayPref.length() != 0
				&& partnerJSONDayPref.length() == 0) {
			dateState = DateState.AWAITING_ANSWER;
		} else {
			// If both partners filled their details,
			// and there is a row in the potentialBliinderDates
			// table and not in the bliinderDate, it means there
			// is no match or its a date.

			DatePref myPref = getDatePref(myJSONDayPref, myJSONPlaceType);
			DatePref partnerPref = getDatePref(partnerJSONDayPref,
					partnerJSONPlaceType);
			Date matchDateTime = FillDetails.getMatch(myPref, partnerPref,
					partner);
			if (matchDateTime == null) {
				return DateState.NEVER_EVER;
			}
			return DateState.ITS_A_DATE;
		}
		return dateState;
	}

	private static DatePref getDatePref(JSONArray myJSONDayPref,
			JSONArray myJSONPlaceType) {
		List<ArrayList<AvailableTime>> daysAvailable = extractDays(myJSONDayPref);
		List<PlaceType> places = extractPlaces(myJSONPlaceType);
		return new DatePref(daysAvailable, places);
	}

	private static List<PlaceType> extractPlaces(JSONArray myJSONPlaceType) {
		List<PlaceType> places = new ArrayList<PlaceType>();
		for (int i = 0; i < myJSONPlaceType.length(); i++) {
			String s;
			try {
				s = (String) myJSONPlaceType.get(i);
				places.add(PlaceType.fromString(s));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return places;
	}

	private static List<ArrayList<AvailableTime>> extractDays(
			JSONArray myJSONDayPref) {
		List<ArrayList<AvailableTime>> daysAvailable = new ArrayList<ArrayList<AvailableTime>>();
		for (int i = 0; i < Constant.DAYS_IN_WEEK; i++) {
			daysAvailable.add(new ArrayList<AvailableTime>());
		}
		for (int i = 0; i < myJSONDayPref.length(); i++) {
			JSONArray a;
			try {
				a = (JSONArray) myJSONDayPref.get(i);
				for (int j = 0; j < a.length(); j++) {
					String s = (String) a.get(j);
					daysAvailable.get(i).add(AvailableTime.fromString(s));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return daysAvailable;
	}

	private static List<ParseObject> getPartnersRows(String partnerId)
			throws ParseException {
		ParseQuery<ParseObject> query = ParseQuery.getQuery(USERS_TABLE_NAME);
		query.whereEqualTo(ID, partnerId);
		List<ParseObject> objs = query.find();
		return objs;
	}

	/**
	 * 
	 * @param partnerId
	 * @param myColumn
	 *            - the column of the user in the potentialBliinderDate table.
	 *            The possibilities are: ID1 or ID2.
	 * @return
	 * @throws ParseException
	 */
	private static List<ParseObject> getPotentialBliinderDatesRows(
			String partnerId) {
		try {
			String dateID = getDateID(partnerId);
			ParseQuery<ParseObject> query = ParseQuery
					.getQuery(POTENTIAL_BLIINDER_DATES);
			query.whereEqualTo(ID, dateID);
			List<ParseObject> objs = query.find();
			return objs;
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static List<ParseObject> getUpcomingBliinderDatesRows(
			String partnerId) throws ParseException {
		String dateID = getDateID(partnerId);
		ParseQuery<ParseObject> query = ParseQuery
				.getQuery(UPCOMING_BLIINDER_DATES);
		query.whereEqualTo(ID, dateID);
		List<ParseObject> objs = query.find();
		return objs;
	}

	/**
	 * 
	 * @param partnerId
	 * @param myColumn
	 *            - the column of the user in the neverEverBliinderDate table.
	 *            The possibilities are: ID1 or ID2.
	 * @return
	 * @throws ParseException
	 */
	private static List<ParseObject> getNeverEverBliinderDatesRows(
			String partnerId) {
		try {
			String dateID = getDateID(partnerId);
			ParseQuery<ParseObject> query = ParseQuery
					.getQuery(NEVER_EVER_BLIINDER_DATES);
			query.whereEqualTo(ID, dateID);
			List<ParseObject> objs = query.find();
			return objs;
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	// Extracts all partners which are in the column 'partnerColumn' in the
	// Matches table.
	private static void updatePartners(List<String> partnerIds,
			String myColumn, String partnerColumn) {
		try {
			ParseQuery<ParseObject> query = ParseQuery
					.getQuery(MATCHES_TABLE_NAME);
			query.whereEqualTo(myColumn, Data.me.getFacebookId());
			List<ParseObject> objs = query.find();
			for (ParseObject o : objs) {
				partnerIds.add(o.getString(partnerColumn));
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}

	private static void removeFromUpcomingAndFromNeverEverAndAddToNeverEver() {
		for (ParseObject o : neverEverObject) {
			try {
				o.delete();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		for (BliinderDate d : neverEverDateList) {
			removeNeverEverDateRow(d.getPartner().getFacebookId());
			insertNeverEverDate(d);
		}
	}

	public static void notifyPartnerUserFilledDateDetails() {
	}

	
	// First we remove the row that indicates the given date (if exist), and
	// than insert the new row with updated details.
	public static void savePotentialDate(BliinderDate date) {
		String partnerId = date.getPartner().getFacebookId();
		// removePotentialDateRow(partnerId);
		// insertPotentialDate(date);
		updatePotentialDate(date, partnerId);
	}

	private static void updatePotentialDate(BliinderDate date, String partnerId) {
		List<ParseObject> objs = getPotentialBliinderDatesRows(partnerId);
		String myDaysTitle = getMyDaysTitle(partnerId);
		String myPlaceTitle = getMyPlaceTitle(partnerId);
		List<String> placeTypeID1 = getPlaceType(date, "1");
		List<String> placeTypeID2 = getPlaceType(date, "2");
		List<List<String>> dayPrefID1 = getDayPref(date, "1");
		List<List<String>> dayPrefID2 = getDayPref(date, "2");

		for (ParseObject o : objs) {
			// o.delete();
			o.remove(myDaysTitle);
			o.put(myDaysTitle, myDaysTitle.equals(DAY_PREF_ID1) ? dayPrefID1
					: dayPrefID2);
			o.remove(myPlaceTitle);
			o.put(myPlaceTitle,
					myPlaceTitle.equals(PLACE_TYPE_ID1) ? placeTypeID1
							: placeTypeID2);
			o.saveInBackground();
		}
	}

	private static String getMyPlaceTitle(String partnerId) {
		return (partnerId.compareTo(Data.me.getFacebookId()) < 0) ? PLACE_TYPE_ID2
				: PLACE_TYPE_ID1;
	}

	private static String getMyDaysTitle(String partnerId) {
		return (partnerId.compareTo(Data.me.getFacebookId()) < 0) ? DAY_PREF_ID2
				: DAY_PREF_ID1;
	}

	// First we remove the row that indicates the given date (if exist), and
	// than insert the new row with updated details.
	public static void saveUpcomingDate(BliinderDate date) {
		String partnerId = date.getPartner().getFacebookId();
		removePotentialDateRow(partnerId);
		insertUpcomingDate(date);
	}

	private static void insertUpcomingDate(BliinderDate date) {
		String dateID = getDateID(date.getPartner().getFacebookId());
		ParseObject o = new ParseObject(UPCOMING_BLIINDER_DATES);
		o.put(ID, dateID);
		o.put(PLACE_TYPE, date.getPlaceType().toString());
		o.put(DATE_LOCATION, date.getDateLocation());
		o.put(DATE, date.getDate());
		o.put(GEO_LOCATION, date.getLocation());
		o.put(USER1_IN_CALENDAR, false);
		o.put(USER2_IN_CALENDAR, false);
		try {
			o.save();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private static void insertPotentialDate(BliinderDate date) {
		String dateID = getDateID(date.getPartner().getFacebookId());
		List<String> placeTypeID1 = getPlaceType(date, "1");
		List<String> placeTypeID2 = getPlaceType(date, "2");
		List<List<String>> dayPrefID1 = getDayPref(date, "1");
		List<List<String>> dayPrefID2 = getDayPref(date, "2");
		ParseObject o = new ParseObject(POTENTIAL_BLIINDER_DATES);
		o.put(ID, dateID);
		o.put(PLACE_TYPE_ID1, placeTypeID1);
		o.put(PLACE_TYPE_ID2, placeTypeID2);
		o.put(DAY_PREF_ID1, dayPrefID1);
		o.put(DAY_PREF_ID2, dayPrefID2);
		o.saveInBackground();
	}

	private static void insertNeverEverDate(BliinderDate date) {
		String dateID = getDateID(date.getPartner().getFacebookId());
		List<String> placeTypeID1 = getPlaceType(date, "1");
		List<String> placeTypeID2 = getPlaceType(date, "2");
		List<List<String>> dayPrefID1 = getDayPref(date, "1");
		List<List<String>> dayPrefID2 = getDayPref(date, "2");
		ParseObject o = new ParseObject(NEVER_EVER_BLIINDER_DATES);
		o.put(ID, dateID);
		o.put(PLACE_TYPE_ID1, placeTypeID1);
		o.put(PLACE_TYPE_ID2, placeTypeID2);
		o.put(DAY_PREF_ID1, dayPrefID1);
		o.put(DAY_PREF_ID2, dayPrefID2);
		// TODO: check what's better - save or save in background...
		o.saveInBackground();
	}

	private static List<List<String>> getDayPref(BliinderDate date,
			String dayPrefId) {
		DatePref pref = getRightPref(date, dayPrefId);
		List<List<String>> $ = new ArrayList<List<String>>();
		for (List<AvailableTime> l : pref.getDaysAvailable()) {
			List<String> timeList = new ArrayList<String>();
			for (AvailableTime t : l) {
				timeList.add(t.toString());
			}
			$.add(timeList);
		}
		return $;
	}

	private static List<String> getPlaceType(BliinderDate date,
			String placeTypeId) {
		DatePref pref = getRightPref(date, placeTypeId);
		List<String> $ = new ArrayList<String>();
		for (PlaceType p : pref.getPlaces()) {
			$.add(p.toString());
		}
		return $;
	}

	// Returns the adequate preferences. If we ask for "1" and user
	// ID smaller than partner ID, we returns the user ID, if it's bigger, we
	// returns the partner ID. The same if "2" is requested.
	private static DatePref getRightPref(BliinderDate date, String placeTypeId) {
		String partnerId = date.getPartner().getFacebookId();
		boolean isPartnerID2 = (Data.me.getFacebookId().compareTo(partnerId) < 0);
		DatePref pref;
		if (placeTypeId.equals("1")) {
			pref = (isPartnerID2) ? date.getMyPref() : date.getPartnerPref();
		} else {
			pref = (isPartnerID2) ? date.getPartnerPref() : date.getMyPref();
		}
		return pref;
	}

	// The assumption is that cannot be a situation in which there is more than
	// one date with the same partner. If there is, all the dates will be
	// erased.
	private static void removePotentialDateRow(String partnerId) {
		List<ParseObject> objs = getPotentialBliinderDatesRows(partnerId);
		for (ParseObject o : objs) {
			try {
				o.delete();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}

	private static void removeNeverEverDateRow(String partnerId) {
		List<ParseObject> objs = getNeverEverBliinderDatesRows(partnerId);
		for (ParseObject o : objs) {
			try {
				o.delete();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}

	private static String getDateID(String partnerId) {
		String myID = Data.me.getFacebookId();
		return (myID.compareTo(partnerId) < 0) ? myID + SEPARATOR + partnerId
				: partnerId + SEPARATOR + myID;
	}

	public static void checkForDatesAndUpdateTable() {
		try {
			Set<String> inMatchAlready = getUsersInMatchAlready();
			Set<String> whoIwant = getUsersILiked();
			Set<String> whoWantsMe = getUsersWhoWantsMe();

			// Intersect between who I want and who wants me.
			Set<String> newMatches = new TreeSet<String>(whoIwant);
			newMatches.retainAll(whoWantsMe);
			// Remove all the people we are already matched with.
			newMatches.removeAll(inMatchAlready);

			for (String partnerID : newMatches) {
				if (Data.me.getFacebookId().compareTo(partnerID) < 0)
					updateFoundMatchWith(partnerID);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void updateFoundMatchWith(String partnerID)
			throws ParseException {
		String bliindateID = Data.me.getFacebookId() + SEPARATOR + partnerID;
		updateMatchesTable(partnerID);
		updateDateNumTable(partnerID);
		updatePotentialDateTable(bliindateID);
		updateUserCurrentCountTable(partnerID);
	}

	private static void updateUserCurrentCountTable(String partnerID)
			throws ParseException {
		updateUserCountFor(Data.me.getFacebookId());
		updateUserCountFor(partnerID);
	}

	private static void updateUserCountFor(String id) throws ParseException {
		// ParseQuery<ParseObject> getMyCount = ParseQuery
		// .getQuery(USER_CURRENT_COUNT_TABLE);
		// getMyCount.whereEqualTo(ID, id);
		// List<ParseObject> result = getMyCount.find();
		// if (result.isEmpty()) {
		// ParseObject myDateCount = new ParseObject(USER_CURRENT_COUNT_TABLE);
		// myDateCount.put(ID, id);
		// myDateCount.put(DATE_NUM, INITIAL_DATE_NUM_VALUE);
		// myDateCount.saveInBackground();
		// } else {
		// result.get(FIRST).increment(DATE_NUM);
		// result.get(FIRST).saveInBackground();
		// }
	}

	private static void updatePotentialDateTable(String bliindateID) {
		ParseObject potentialBliinderDates = new ParseObject(
				POTENTIAL_BLIINDER_DATES);
		potentialBliinderDates.put(ID, bliindateID);
		potentialBliinderDates.put(PLACE_TYPE_ID1, new LinkedList<String>());
		potentialBliinderDates.put(PLACE_TYPE_ID2, new LinkedList<String>());
		potentialBliinderDates.put(DAY_PREF_ID1,
				new LinkedList<LinkedList<String>>());
		potentialBliinderDates.put(DAY_PREF_ID2,
				new LinkedList<LinkedList<String>>());
		potentialBliinderDates.saveInBackground();
	}

	private static void updateDateNumTable(String partnerID) {
		String myID = Data.me.getFacebookId();
		updateDateNumTableFor(myID, partnerID);
		updateDateNumTableFor(partnerID, myID);
	}

	private static void updateDateNumTableFor(String id1, String id2) {
		// ParseQuery<ParseObject> getMyDateCount = ParseQuery
		// .getQuery(DATE_NUM_TABLE);
		// getMyDateCount.whereEqualTo(ID, id2 + SEPARATOR + id1);
		// try {
		// int dateCount = getMyDateCount.count();
		// ParseObject dateNum = new ParseObject(DATE_NUM_TABLE);
		// dateNum.put(ID, id2 + SEPARATOR + id1);
		// dateNum.put(NUM, dateCount + 1);
		// dateNum.saveInBackground();
		// } catch (ParseException e) {
		// e.printStackTrace();
		// }
	}

	private static void updateMatchesTable(String partnerID) {
		ParseObject match = new ParseObject(MATCHES_TABLE_NAME);
		match.put(ID1, Data.me.getFacebookId());
		match.put(ID2, partnerID);
		match.saveInBackground();
	}

	private static Set<String> getUsersWhoWantsMe() throws ParseException {
		// Find all the people who wants me.
		ParseQuery<ParseObject> query = ParseQuery.getQuery(WHO_WANTS_WHO);
		query.whereEqualTo(PARTNER, Data.me.getFacebookId());
		query.whereEqualTo(WANT, true);
		Set<String> whoWantsMe = toIdSet(query.find(), ME);
		return whoWantsMe;
	}

	private static Set<String> getUsersILiked() throws ParseException {
		// Find all the people I want.
		ParseQuery<ParseObject> query = ParseQuery.getQuery(WHO_WANTS_WHO);
		query.whereEqualTo(ME, Data.me.getFacebookId());
		query.whereEqualTo(WANT, true);
		Set<String> whoIwant = toIdSet(query.find(), PARTNER);
		return whoIwant;
	}

	private static Set<String> getUsersInMatchAlready() throws ParseException {
		// Find all the people we are already matched with.
		ParseQuery<ParseObject> query1 = ParseQuery
				.getQuery(MATCHES_TABLE_NAME);
		query1.whereEqualTo(ID1, Data.me.getFacebookId());
		Set<String> inMatchAlready = toIdSet(query1.find(), ID2);
		ParseQuery<ParseObject> query2 = ParseQuery
				.getQuery(MATCHES_TABLE_NAME);
		query2.whereEqualTo(ID2, Data.me.getFacebookId());
		inMatchAlready.addAll(toIdSet(query2.find(), ID1));
		return inMatchAlready;
	}

	private static Set<String> toIdSet(List<ParseObject> parseObjectList,
			String IDColumnName) {
		Set<String> IDs = new TreeSet<String>();
		for (ParseObject po : parseObjectList)
			IDs.add(po.getString(IDColumnName));
		return IDs;
	}

	public static boolean userAlreadySignedup(String id) {
		ParseQuery<ParseObject> query = ParseQuery.getQuery(USERS_TABLE_NAME);
		query.whereEqualTo(ID, id);
		try {
			return query.count() > 0;
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static User getUser(String id) {
		ParseQuery<ParseObject> query = ParseQuery.getQuery(USERS_TABLE_NAME);
		query.whereEqualTo(ID, id);
		try {
			return new User(query.find().get(FIRST));
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void cancelDate(String partnerId) {
		removeFromUpcoming(partnerId);
		removeFromMatches(partnerId);
		removeFromWhoWantsWho(partnerId);
	}

	private static void removeFromWhoWantsWho(String partnerId) {
		ParseQuery<ParseObject> query1 = ParseQuery.getQuery(WHO_WANTS_WHO);
		query1.whereEqualTo(ME, Data.me.getFacebookId());
		query1.whereEqualTo(PARTNER, partnerId);
		try {
			List<ParseObject> objs = query1.find();
			if (!objs.isEmpty())
				objs.get(FIRST).delete();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		ParseQuery<ParseObject> query2 = ParseQuery.getQuery(WHO_WANTS_WHO);
		query2.whereEqualTo(PARTNER, Data.me.getFacebookId());
		query2.whereEqualTo(ME, partnerId);
		try {
			List<ParseObject> objs = query2.find();
			if (!objs.isEmpty())
				objs.get(FIRST).delete();
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}

	private static void removeFromMatches(String partnerId) {
		ParseQuery<ParseObject> queryMatches = ParseQuery
				.getQuery(MATCHES_TABLE_NAME);
		queryMatches.whereEqualTo(ID1, lowerId(partnerId));
		queryMatches.whereEqualTo(ID2, greaterId(partnerId));
		try {
			List<ParseObject> objs = queryMatches.find();
			if (!objs.isEmpty())
				objs.get(FIRST).delete();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private static void removeFromUpcoming(String partnerId) {
		String dateID = getDateID(partnerId);
		ParseQuery<ParseObject> query = ParseQuery
				.getQuery(UPCOMING_BLIINDER_DATES);
		query.whereEqualTo(ID, dateID);
		try {
			List<ParseObject> objs = query.find();
			if (!objs.isEmpty())
				objs.get(FIRST).delete();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private static String greaterId(String partnerId) {
		return ((Data.me.getFacebookId().compareTo(partnerId) > 0) ? Data.me
				.getFacebookId() : partnerId);
	}

	private static String lowerId(String partnerId) {
		return ((Data.me.getFacebookId().compareTo(partnerId) < 0) ? Data.me
				.getFacebookId() : partnerId);
	}

	public static void cancelUserChoice(User partner) {
		ParseQuery<ParseObject> query = ParseQuery.getQuery(WHO_WANTS_WHO);
		query.whereEqualTo(ME, Data.me.getFacebookId());
		query.whereEqualTo(PARTNER, partner.getFacebookId());
		try {
			List<ParseObject> objs = query.find();
			objs.get(FIRST).delete();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public static void updateUpcomingDate(BliinderDate date) {
		ParseQuery<ParseObject> query = ParseQuery
				.getQuery(UPCOMING_BLIINDER_DATES);
		query.whereEqualTo(ID, getDateID(date.getPartner().getFacebookId()));
		try {
			List<ParseObject> objs = query.find();
			ParseObject myDate = objs.get(FIRST);
			myDate.put(myCalendarColumn(date.getPartner().getFacebookId()),
					true);
			myDate.save();
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}
}
