package com.ysoserious.bliinder.adapters;

import com.ysoserious.bliinder.utils.DateState;

public class ItemDetails {

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getItemDescription() {
		return itemDescription;
	}

	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String price) {
		this.description = price;
	}

	public DateState getDateState() {
		return dateState;
	}

	public void setDateState(DateState imageState) {
		this.dateState = imageState;
	}

	private String name;
	private String itemDescription;
	private String description;
	private DateState dateState;

}
