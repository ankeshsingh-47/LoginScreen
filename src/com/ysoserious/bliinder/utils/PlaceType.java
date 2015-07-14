package com.ysoserious.bliinder.utils;

public enum PlaceType {
	BAR, RESTAURANT, CINEMA, NONE;

	public static PlaceType fromString(String s) {
		if ("BAR".equals(s)) {
			return BAR;
		} else if ("RESTAURANT".equals(s)) {
			return RESTAURANT;
		} else if ("CINEMA".equals(s)) {
			return CINEMA;
		} else if ("NONE".equals(s)) {
			return NONE;
		}
		return null;
	}

	@Override
	public String toString() {
		if (BAR.equals(this)) {
			return "BAR";
		} else if (RESTAURANT.equals(this)) {
			return "RESTAURANT";
		} else if (CINEMA.equals(this)) {
			return "CINEMA";
		} else if (NONE.equals(this)) {
			return "NONE";
		}
		return null;
	}
}
