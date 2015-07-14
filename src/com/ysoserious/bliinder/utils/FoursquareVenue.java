package com.ysoserious.bliinder.utils;

public class FoursquareVenue {
	private String name;
	private String city;

	private String category;

	private String longitude;

	private String latitude;

	public FoursquareVenue() {
		this.name = "";
		this.city = "";
		this.setCategory("");
	}

	public String getCity() {
		if (city.length() > 0) {
			return city;
		}
		return city;
	}

	public void setCity(String city) {
		if (city != null) {
			this.city = city.replaceAll("\\(", "").replaceAll("\\)", "");
			;
		}
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

}
