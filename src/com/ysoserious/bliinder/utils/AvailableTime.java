package com.ysoserious.bliinder.utils;

public enum AvailableTime {
	NOT_AVAILABLE, MORNING, NOON, EVENING;

	public static AvailableTime fromString(String s) {
		if ("NOT_AVAILABLE".equals(s)) {
			return NOT_AVAILABLE;
		} else if ("MORNING".equals(s)) {
			return MORNING;
		} else if ("NOON".equals(s)) {
			return NOON;
		} else if ("EVENING".equals(s)) {
			return EVENING;
		}
		return null;
	}

	@Override
	public String toString() {
		if (NOT_AVAILABLE.equals(this)) {
			return "NOT_AVAILABLE";
		} else if (MORNING.equals(this)) {
			return "MORNING";
		} else if (NOON.equals(this)) {
			return "NOON";
		} else if (EVENING.equals(this)) {
			return "EVENING";
		}
		return null;
	}
}
