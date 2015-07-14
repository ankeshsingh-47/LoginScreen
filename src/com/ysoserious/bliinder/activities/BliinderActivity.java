package com.ysoserious.bliinder.activities;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;

import com.ysoserious.bliinder.R;
import com.ysoserious.bliinder.adapters.ItemBliinderDateAdapter;
import com.ysoserious.bliinder.entities.BliinderDate;

public class BliinderActivity extends Activity {
	static ItemBliinderDateAdapter adapter;
	static List<BliinderDate> myBliiderDates;
	static BliinderDate currentDate;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_bliider_dates);
	}

}
