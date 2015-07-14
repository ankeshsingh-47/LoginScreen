package com.ysoserious.bliinder.activities;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;
import com.ysoserious.bliinder.R;
import com.ysoserious.bliinder.adapters.ItemBliinderDateAdapter;
import com.ysoserious.bliinder.entities.BliinderDate;
import com.ysoserious.bliinder.entities.Server;
import com.ysoserious.bliinder.entities.User;
import com.ysoserious.bliinder.utils.Data;
import com.ysoserious.bliinder.utils.DateState;

public class MyBliiderDates extends Activity {
	private EditText inputSearch;
	static ItemBliinderDateAdapter adapter;
	private ListView listView;
	static List<BliinderDate> myBliiderDates;
	public static BliinderDate currentDate;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_bliider_dates);
		inputSearch = (EditText) findViewById(R.id.inputBliiderDatesSearch);
		inputSearch.setCursorVisible(false);
		inputSearch.addTextChangedListener(new MyTextWatcher());
		listView = (ListView) findViewById(R.id.bliinderDatesList);
		listView.setOnItemClickListener(new BliiderDateItemClickedListener(
				MyBliiderDates.this));
		myBliiderDates = getMyBliiderDates();
		adapter = new ItemBliinderDateAdapter(this, myBliiderDates);
		listView.setAdapter(adapter);
	}

	@Override
	protected void onResume() {
		super.onResume();
		listView = (ListView) findViewById(R.id.bliinderDatesList);
		listView.setOnItemClickListener(new BliiderDateItemClickedListener(
				MyBliiderDates.this));
		myBliiderDates = getMyBliiderDates();
		adapter = new ItemBliinderDateAdapter(this, myBliiderDates);
		listView.setAdapter(adapter);
	}

	public void notifyAboutChange() {
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}
	}

	private List<BliinderDate> getMyBliiderDates() {
		Server.neverEverDateList.clear();
		Server.neverEverObject.clear();
		return Server.getMyDates(this);
	}

	class MyTextWatcher implements TextWatcher {
		@Override
		public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
			if (cs.length() != 0) {
				if (cs.charAt(0) >= 'à' && cs.charAt(0) <= 'ú') {
					// align cursor to the right
					inputSearch.setGravity(Gravity.RIGHT);
				}
				if ((cs.charAt(0) >= 'a' && cs.charAt(0) <= 'z')
						|| (cs.charAt(0) >= 'A' && cs.charAt(0) <= 'Z')) {
					// align cursor to the right
					inputSearch.setGravity(Gravity.LEFT);
				}
			} else {
				inputSearch.setGravity(Gravity.LEFT);
			}
			(MyBliiderDates.this.adapter).getFilter().filter(cs);
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
		}

		@Override
		public void afterTextChanged(Editable arg0) {
		}
	}

	class BliiderDateItemClickedListener implements OnItemClickListener {
		private Context c;

		public BliiderDateItemClickedListener(Context c) {
			this.c = c;
		}

		@Override
		public void onItemClick(AdapterView<?> av, View v, int position, long id) {
			DateState dateState = ((BliinderDate) listView
					.getItemAtPosition(position)).getState();
			goToSuitableDateStateActivity(dateState, position);
		}

		private void goToSuitableDateStateActivity(DateState dateState,
				int position) {
			currentDate = myBliiderDates.get(position);
			Intent intent = null;
			if (dateState == DateState.NOT_FILLED_PARTNER_FILLED
					|| dateState == DateState.NOT_FILLED_PARTNER_NOT_FILLED) {
				intent = new Intent(MyBliiderDates.this, FillDetails.class);
			} else if (dateState == DateState.AWAITING_ANSWER) {
				intent = new Intent(MyBliiderDates.this, WaitingForAnswer.class);
			} else if (dateState == DateState.ITS_A_DATE) {
				intent = new Intent(MyBliiderDates.this, ItsADate.class);
			} else if (dateState == DateState.NEVER_EVER) {
				intent = new Intent(MyBliiderDates.this, NoMatchFound.class);
			}
			intent.putExtra("date",
					new Gson().toJson(myBliiderDates.get(position)));
			startActivity(intent);
		}
	}
}
