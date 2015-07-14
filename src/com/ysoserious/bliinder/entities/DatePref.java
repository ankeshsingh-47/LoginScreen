package com.ysoserious.bliinder.entities;

import java.util.ArrayList;
import java.util.List;

import com.ysoserious.bliinder.utils.AvailableTime;
import com.ysoserious.bliinder.utils.PlaceType;

public class DatePref {
	private List<ArrayList<AvailableTime>> daysAvailable;
	private List<PlaceType> places;

	public DatePref(List<ArrayList<AvailableTime>> daysAvailable,
			List<PlaceType> places) {
		this.daysAvailable = daysAvailable;
		this.places = places;
	}

	public List<ArrayList<AvailableTime>> getDaysAvailable() {
		return daysAvailable;
	}

	public void setDaysAvailable(List<ArrayList<AvailableTime>> daysAvailable) {
		this.daysAvailable = daysAvailable;
	}

	public List<PlaceType> getPlaces() {
		return places;
	}

	public void setPlaces(List<PlaceType> places) {
		this.places = places;
	}
}
