package com.ysoserious.bliinder.entities;

import java.util.Date;

import com.parse.ParseGeoPoint;
import com.ysoserious.bliinder.utils.DateState;
import com.ysoserious.bliinder.utils.PlaceType;

public class BliinderDate implements Comparable<BliinderDate> {
	private User partner;
	private DateState state;
	private Date date;
	private PlaceType placeType;
	private String dateName;
	private DatePref myPref;
	private DatePref partnerPref;
	private String dateLocation;
	private ParseGeoPoint location;
	private boolean isInCalendar;

	public BliinderDate(DateState state, Date date, PlaceType placeType,
			String dateName, User partner, DatePref myPref,
			DatePref partnerPref, String dateLocation) {
		this.state = state;
		this.date = date;
		this.placeType = placeType;
		this.dateName = dateName;
		this.partner = partner;
		this.myPref = myPref;
		this.partnerPref = partnerPref;
		this.dateLocation = dateLocation;
		setInCalendar(false);
	}

	public DatePref getMyPref() {
		return myPref;
	}

	public DatePref getPartnerPref() {
		return partnerPref;
	}

	public void setMyPref(DatePref myPref) {
		this.myPref = myPref;
	}

	public void setPartnerPref(DatePref partnerPref) {
		this.partnerPref = partnerPref;
	}

	public User getPartner() {
		return partner;
	}

	public void setPartner(User partner) {
		this.partner = partner;
	}

	public DateState getState() {
		return state;
	}

	public void setState(DateState state) {
		this.state = state;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public PlaceType getPlaceType() {
		return placeType;
	}

	public void setPlaceType(PlaceType placeType) {
		this.placeType = placeType;
	}

	public String getDateName() {
		return dateName;
	}

	public void setDateName(String dateName) {
		this.dateName = dateName;
	}

	// Two dates are compared by their name.
	@Override
	public int compareTo(BliinderDate another) {
		return dateName.compareTo(another.dateName);
	}

	public String getDateLocation() {
		return dateLocation;
	}

	public void setDateLocation(String dateLocation) {
		this.dateLocation = dateLocation;
	}

	public ParseGeoPoint getLocation() {
		return location;
	}

	public void setLocation(double lat, double lng) {
		location = new ParseGeoPoint(lat, lng);
	}

	public void setLocation(ParseGeoPoint parseGeoPoint) {
		location = parseGeoPoint;
	}

	public boolean isInCalendar() {
		return isInCalendar;
	}

	public void setInCalendar(boolean isInCalendar) {
		this.isInCalendar = isInCalendar;
	}
}
