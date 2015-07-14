package com.ysoserious.bliinder.fill_details_utils;

import java.util.ArrayList;

public class Parent {
	private String name;
	private String text1;
	private String text2;
	private String checkedtype;
	private boolean checked;
	private boolean isPlace;

	public boolean isPlace() {
		return isPlace;
	}

	public void setIsPlace(boolean isPlace) {
		this.isPlace = isPlace;
	}

	// ArrayList to store child objects
	private ArrayList<Child> children;
	private int dayIndex = -1;

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

	public String getCheckedType() {
		return checkedtype;
	}

	public void setCheckedType(String checkedtype) {
		this.checkedtype = checkedtype;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	// ArrayList to store child objects
	public ArrayList<Child> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<Child> children) {
		this.children = children;
	}

	public int getDayIndex() {
		return dayIndex;
	}

	public void setDayIndex(int dayIndex) {
		this.dayIndex = dayIndex;
	}

}
