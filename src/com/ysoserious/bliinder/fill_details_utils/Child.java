package com.ysoserious.bliinder.fill_details_utils;

import com.ysoserious.bliinder.utils.AvailableTime;
import com.ysoserious.bliinder.utils.PlaceType;

public class Child {
	private String name;
	private String text1;
	private String text2;
	private boolean checked;
	private boolean isPlace;
	private AvailableTime availableTime;
	private PlaceType placeType;

	public boolean isPlace() {
		return isPlace;
	}

	public void setIsPlace(boolean isPlace) {
		this.isPlace = isPlace;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getText1() {
		return text1;
	}

	public void setText1(String text1) {
		this.text1 = text1;
	}

	public String getText2() {
		return text2;
	}

	public void setText2(String text2) {
		this.text2 = text2;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public void getPlaceType(PlaceType placeType) {
		this.placeType = placeType;
	}

	public PlaceType getPlaceType() {
		return placeType;
	}

	public AvailableTime getAvailableTime() {
		return availableTime;
	}

	public void setAvailableTime(AvailableTime availableTime) {
		this.availableTime = availableTime;
	}

	public void setPlaceType(PlaceType placeType) {
		this.placeType = placeType;
	}

}