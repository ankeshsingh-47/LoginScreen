package com.ysoserious.bliinder.activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.ExpandableListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.javacodegeeks.foursquareapiexample.AndroidFoursquare;
import com.ysoserious.bliinder.R;
import com.ysoserious.bliinder.entities.BliinderDate;
import com.ysoserious.bliinder.entities.BliinderPreferences;
import com.ysoserious.bliinder.entities.DatePref;
import com.ysoserious.bliinder.entities.Server;
import com.ysoserious.bliinder.entities.User;
import com.ysoserious.bliinder.fill_details_utils.Child;
import com.ysoserious.bliinder.fill_details_utils.Parent;
import com.ysoserious.bliinder.utils.AvailableTime;
import com.ysoserious.bliinder.utils.Data;
import com.ysoserious.bliinder.utils.DateState;
import com.ysoserious.bliinder.utils.PlaceType;

public class FillDetails extends ExpandableListActivity {
	private static final int DAYS_IN_A_WEEK = 7;
	private static PlaceType placeType;

	private ArrayList<Parent> parents;
	private List<PlaceType> places;
	private List<ArrayList<AvailableTime>> daysAvailable;
	private BliinderDate currentDate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle b = getIntent().getExtras();
		// date = new Gson().fromJson(b.getString("date"), BliinderDate.class);
		currentDate = MyBliiderDates.currentDate;
		places = new ArrayList<PlaceType>();
		daysAvailable = new ArrayList<ArrayList<AvailableTime>>();
		for (int i = 0; i < DAYS_IN_A_WEEK; i++) {
			daysAvailable.add(new ArrayList<AvailableTime>());
		}
		getExpandableListView().setGroupIndicator(null);
		getExpandableListView().setDividerHeight(1);
		registerForContextMenu(getExpandableListView());

		// Creating list data in arraylist
		final ArrayList<Parent> dataList = BuildDays();

		// Adding ArrayList data to ExpandableListView values
		loadHosts(dataList);
	}

	private ArrayList<Parent> BuildDays() {
		// Creating ArrayList of type parent class to store parent class objects
		final ArrayList<Parent> listDays = new ArrayList<Parent>();
		for (int i = 0; i <= 10; i++) {
			// Create parent class object
			final Parent parent = new Parent();

			// Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday.
			// Set values in parent class object
			if (i == 0) {
				parent.setName("Partner Details");
				parent.setText1("");
			} else if (i == 1) {
				parent.setName("Time Available");
				parent.setText1("Time Available: ");
			} else if (i == 2) {
				parent.setName("Sunday");
				parent.setText1("Sunday");
				parent.setDayIndex(0);
				parent.setIsPlace(false);
			} else if (i == 3) {
				parent.setName("Monday");
				parent.setText1("Monday");
				parent.setDayIndex(1);
				parent.setIsPlace(false);
			} else if (i == 4) {
				parent.setName("Tuesday");
				parent.setText1("Tuesday");
				parent.setDayIndex(2);
				parent.setIsPlace(false);
			} else if (i == 5) {
				parent.setName("Wednesday");
				parent.setText1("Wednesday");
				parent.setDayIndex(3);
				parent.setIsPlace(false);
			} else if (i == 6) {
				parent.setName("Thursday");
				parent.setText1("Thursday");
				parent.setDayIndex(4);
			} else if (i == 7) {
				parent.setName("Friday");
				parent.setText1("Friday");
				parent.setDayIndex(5);
				parent.setIsPlace(false);
			} else if (i == 8) {
				parent.setName("Saturday");
				parent.setText1("Saturday");
				parent.setDayIndex(6);
				parent.setIsPlace(false);
			} else if (i == 9) {
				parent.setName("Prefered Date Type");
				parent.setText1("Prefered Date Type: ");
				parent.setIsPlace(true);
				addDateTypeChildren(parent);
			} else if (i == 10) {
				parent.setName("OkButton");
				parent.setText1("");
			}
			if (i != 0 && i != 1 && i != 9 && i != 10)
				addDayTimeChildren(parent);
			listDays.add(parent);
		}
		return listDays;
	}

	private void addDayTimeChildren(final Parent parent) {
		parent.setChildren(new ArrayList<Child>());
		final Child morningChild = new Child();
		morningChild.setName("morning");
		morningChild.setText1("Morning");
		morningChild.setAvailableTime(AvailableTime.MORNING);
		morningChild.setIsPlace(false);
		parent.getChildren().add(morningChild);

		final Child afternoonChild = new Child();
		afternoonChild.setName("afternoon");
		afternoonChild.setText1("Afternoon");
		afternoonChild.setAvailableTime(AvailableTime.NOON);
		afternoonChild.setIsPlace(false);
		parent.getChildren().add(afternoonChild);

		final Child eveningChild = new Child();
		eveningChild.setName("evening");
		eveningChild.setText1("Evening");
		eveningChild.setAvailableTime(AvailableTime.EVENING);
		eveningChild.setIsPlace(false);
		parent.getChildren().add(eveningChild);
	}

	private void addDateTypeChildren(final Parent parent) {
		parent.setChildren(new ArrayList<Child>());
		final Child RestaurantChild = new Child();
		RestaurantChild.setName("restaurant");
		RestaurantChild.setText1("Restaurant");
		RestaurantChild.setPlaceType(PlaceType.RESTAURANT);
		RestaurantChild.setIsPlace(true);
		parent.getChildren().add(RestaurantChild);

		final Child barChild = new Child();
		barChild.setName("bar");
		barChild.setText1("Bar");
		barChild.setPlaceType(PlaceType.BAR);
		barChild.setIsPlace(true);
		parent.getChildren().add(barChild);

		final Child cinemaChild = new Child();
		cinemaChild.setName("cinema");
		cinemaChild.setText1("Cinema");
		cinemaChild.setPlaceType(PlaceType.CINEMA);
		cinemaChild.setIsPlace(true);
		parent.getChildren().add(cinemaChild);
	}

	private void loadHosts(final ArrayList<Parent> newParents) {
		if (newParents == null)
			return;

		parents = newParents;

		// Check for ExpandableListAdapter object
		if (this.getExpandableListAdapter() == null) {
			// Create ExpandableListAdapter Object
			final MyExpandableListAdapter mAdapter = new MyExpandableListAdapter();

			// Set Adapter to ExpandableList Adapter
			this.setListAdapter(mAdapter);
		} else {
			// Refresh ExpandableListView data
			((MyExpandableListAdapter) getExpandableListAdapter())
					.notifyDataSetChanged();
		}
	}

	/**
	 * A Custom adapter to create Parent view (Used dayrow.xml) and Child
	 * View((Used childrow.xml).
	 */
	private class MyExpandableListAdapter extends BaseExpandableListAdapter {

		private LayoutInflater inflater;

		public MyExpandableListAdapter() {
			inflater = LayoutInflater.from(FillDetails.this);
		}

		// This Function used to inflate parent rows view

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parentView) {

			if (groupPosition == 0) { // if equals to first node - partner
										// details button row
				convertView = inflater.inflate(
						R.layout.parent_partnerbutton_row, parentView, false);
				Button button = ((Button) convertView
						.findViewById(R.id.partner_details_button));
				button.setOnClickListener(new PartnerbuttonClicked());
				return convertView;
			} else if (groupPosition == 10) { // if equals to last node - ok
												// button row
				convertView = inflater.inflate(R.layout.parent_okbutton_row,
						parentView, false);
				ImageButton image = ((ImageButton) convertView
						.findViewById(R.id.okButton));
				image.setOnClickListener(new OKbuttonClicked());
				return convertView;
			}
			final Parent parent = parents.get(groupPosition);

			// Inflate dayrow.xml file for parent rows
			convertView = inflater.inflate(R.layout.dayrow, parentView, false);

			// Get dayrow.xml file elements and set values
			TextView text1 = ((TextView) convertView.findViewById(R.id.text1));
			text1.setText(parent.getText1());

			// ImageView image = (ImageView)
			// convertView.findViewById(R.id.image);

			// image.setImageResource(getResources().getIdentifier(
			// "com.ysoserious.bliinder:drawable/setting"
			// + parent.getName(), null, null));

			ImageView rightcheck = (ImageView) convertView
					.findViewById(R.id.rightcheck);

			// Log.i("onCheckedChanged", "isChecked: "+parent.isChecked());

			// Change right check image on parent at runtime
			if (parent.isChecked() == true) {
				rightcheck.setImageResource(getResources().getIdentifier(
						"com.ysoserious.bliinder:drawable/rightcheck", null,
						null));
			} else {
				rightcheck.setImageResource(getResources().getIdentifier(
						"com.ysoserious.bliinder:drawable/button_check", null,
						null));
			}

			// Get dayrow.xml file checkbox elements
			CheckBox checkbox = (CheckBox) convertView
					.findViewById(R.id.checkbox);
			checkbox.setChecked(parent.isChecked());

			// Set CheckUpdateListener for CheckBox (see below
			// CheckUpdateListener class)
			checkbox.setOnCheckedChangeListener(new CheckUpdateListenerParent(
					parent));

			if (groupPosition == 1 || groupPosition == 9) {
				checkbox.setVisibility(View.INVISIBLE);
				rightcheck.setVisibility(View.INVISIBLE);
				convertView.setBackgroundResource(R.drawable.blue_bar_bg);
			}

			return convertView;
		}

		// This Function used to inflate child rows view
		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parentView) {
			// childPosition - 0:Morning, 1:Noon, 2:Evening
			final Parent parent = parents.get(groupPosition);
			final Child child = parent.getChildren().get(childPosition);

			// Inflate childrow.xml file for child rows
			convertView = inflater
					.inflate(R.layout.childrow, parentView, false);

			// Get childrow.xml file elements and set values
			((TextView) convertView.findViewById(R.id.text1)).setText(child
					.getText1());
			ImageView image = (ImageView) convertView.findViewById(R.id.image);
			image.setImageResource(getResources().getIdentifier(
					"com.ysoserious.bliinder:drawable/" + child.getName(),
					null, null));

			// Change right check image on child at runtime
			ImageView rightcheck = (ImageView) convertView
					.findViewById(R.id.rightcheckChild);

			if (child.isChecked() == true) {
				rightcheck.setImageResource(getResources().getIdentifier(
						"com.ysoserious.bliinder:drawable/rightcheck", null,
						null));
			} else {
				rightcheck.setImageResource(getResources().getIdentifier(
						"com.ysoserious.bliinder:drawable/button_check", null,
						null));
			}

			// Get dayrow.xml file checkbox elements
			CheckBox checkbox = (CheckBox) convertView
					.findViewById(R.id.checkboxChild);
			checkbox.setChecked(child.isChecked());

			// Set CheckUpdateListener for CheckBox (see below
			// CheckUpdateListener class)
			checkbox.setOnCheckedChangeListener(new CheckUpdateListenerChild(
					parent, child));

			return convertView;
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			// Log.i("Childs", groupPosition+"=  getChild =="+childPosition);
			return parents.get(groupPosition).getChildren().get(childPosition);
		}

		// Call when child row clicked
		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			int size = 0;
			if (parents.get(groupPosition).getChildren() != null)
				size = parents.get(groupPosition).getChildren().size();
			return size;
		}

		@Override
		public Object getGroup(int groupPosition) {
			return parents.get(groupPosition);
		}

		@Override
		public int getGroupCount() {
			return parents.size();
		}

		// Call when parent row clicked
		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public void notifyDataSetChanged() {
			// Refresh List rows
			super.notifyDataSetChanged();
		}

		@Override
		public boolean isEmpty() {
			return ((parents == null) || parents.isEmpty());
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public boolean areAllItemsEnabled() {
			return true;
		}

		/******************* Checkbox Checked Change Listener ********************/

		private final class CheckUpdateListenerParent implements
				OnCheckedChangeListener {
			private final Parent parent;

			private CheckUpdateListenerParent(Parent parent) {
				this.parent = parent;
			}

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				parent.setChecked(isChecked);
				ArrayList<Child> childs = parent.getChildren();
				for (Child child : childs) {
					child.setChecked(isChecked);
					if (child.isPlace()) {
						if (isChecked) {
							places.add(child.getPlaceType());
						} else {
							places.remove(child.getPlaceType());
						}
					} else { // days
						int dayIndex = parent.getDayIndex();
						if (isChecked && dayIndex != -1) {
							daysAvailable.get(dayIndex).add(
									child.getAvailableTime());
						} else {
							daysAvailable.get(dayIndex).remove(
									child.getAvailableTime());
						}
					}

				}
				((MyExpandableListAdapter) getExpandableListAdapter())
						.notifyDataSetChanged();
			}
		}

		private final class CheckUpdateListenerChild implements
				OnCheckedChangeListener {
			private final Child child;
			private final Parent parent;

			private CheckUpdateListenerChild(Parent parent, Child child) {
				this.child = child;
				this.parent = parent;
			}

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				child.setChecked(isChecked);
				if (child.isPlace()) { // places
					if (isChecked) {
						places.add(child.getPlaceType());
					} else {
						places.remove(child.getPlaceType());
					}
				} else { // days
					int dayIndex = parent.getDayIndex();
					if (isChecked && dayIndex != -1) {
						daysAvailable.get(dayIndex).add(
								child.getAvailableTime());
					}
				}
				if (isChecked || (!isChecked && allSonsNotChecked(parent))) {
					parent.setChecked(isChecked);
				}
				child.setChecked(isChecked);
				((MyExpandableListAdapter) getExpandableListAdapter())
						.notifyDataSetChanged();
			}

			private boolean allSonsNotChecked(Parent parent) {
				for (Child c : parent.getChildren()) {
					if (c.isChecked()) {
						return false;
					}
				}
				return true;
			}
		}

		/***********************************************************************/

		/******************* Buttons OnClick Listeners ********************/

		private final class PartnerbuttonClicked implements OnClickListener {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(FillDetails.this,
						PartnerDetails.class);
				Bundle b = new Bundle();
				b.putString("dateName", currentDate.getDateName());
				intent.putExtras(b);
				startActivity(intent);
				overridePendingTransition(R.anim.slide_down, 0);
			}
		}

		private final class OKbuttonClicked implements OnClickListener {

			@Override
			public void onClick(View v) {
				if (places.isEmpty()) {
					Toast.makeText(getApplicationContext(),
							"Please choose place type.", Toast.LENGTH_LONG)
							.show();
					return;
				}
				Toast.makeText(getApplicationContext(),
						"Your choices were sent to your dater.",
						Toast.LENGTH_LONG).show();

				// remove the old date
				for (BliinderDate bDate : MyBliiderDates.myBliiderDates) {
					if (bDate.getDateName().equals(currentDate.getDateName())) {
						MyBliiderDates.myBliiderDates.remove(bDate);
						break;
					}
				}
				DatePref myPref = new DatePref(daysAvailable, places);
				currentDate.setMyPref(myPref);
				if (currentDate.getState() == DateState.NOT_FILLED_PARTNER_FILLED) {
					DatePref partnerPref = currentDate.getPartnerPref();
					Date matchDateTime = getMatch(myPref, partnerPref,
							currentDate.getPartner());
					currentDate
							.setState((matchDateTime != null) ? DateState.ITS_A_DATE
									: DateState.NEVER_EVER);
					if (matchDateTime != null) {
						currentDate.setDate(matchDateTime);
						currentDate.setPlaceType(placeType);
					}
				} else { // partner not filled, so user needs to wait for his
							// answer.
					currentDate.setState(DateState.AWAITING_ANSWER);
				}
				MyBliiderDates.myBliiderDates.add(currentDate);
				java.util.Collections.sort(MyBliiderDates.myBliiderDates);
				MyBliiderDates.adapter.notifyDataSetChanged();
				MyBliiderDates.currentDate = currentDate;

				if (currentDate.getState() == DateState.ITS_A_DATE) {
					// This activity finds the place of the date and save all
					// date details in the server (at the upcoming bliinder
					// dates table).
					startActivity(new Intent(FillDetails.this,
							AndroidFoursquare.class));
				} else {
					Server.savePotentialDate(currentDate);
				}

				Server.notifyPartnerUserFilledDateDetails();
				finish();
			}

		}

		/*******************************************************************/

	}

	public static PlaceType getDatePlaceType(DatePref myPref,
			DatePref partnerPref, User partner) {
		PlaceType placeType = null;
		List<PlaceType> toIterate;
		List<PlaceType> otherList;

		if (Data.me == null) {
			toIterate = myPref.getPlaces();
			otherList = partnerPref.getPlaces();
		} else {
			// Determines on which list to iterate. it should be
			// symmetric
			// so the result
			// will be identical on both sides.
			if (Data.me.getFacebookId().compareTo(partner.getFacebookId()) == -1) {
				toIterate = myPref.getPlaces();
				otherList = partnerPref.getPlaces();
			} else {
				toIterate = partnerPref.getPlaces();
				otherList = myPref.getPlaces();
			}
		}
		for (PlaceType p : toIterate) {
			if (otherList.contains(p)) {
				placeType = p;
				break;
			}
		}
		return placeType;
	}

	public static Date getMatch(DatePref myPref, DatePref partnerPref,
			User partner) {
		PlaceType placeType = getDatePlaceType(myPref, partnerPref, partner);
		if (placeType == null) {
			// There is no mutual wanted place, so there is no match.
			return null;
		}
		FillDetails.placeType = placeType;
		List<ArrayList<AvailableTime>> myDays = myPref.getDaysAvailable();
		List<ArrayList<AvailableTime>> partnerDays = partnerPref
				.getDaysAvailable();

		int dateDay = -1;
		AvailableTime dateTime = AvailableTime.NOT_AVAILABLE;
		boolean toBreak = false;
		for (int i = 0; i < 7; i++) {
			for (AvailableTime a : myDays.get(i)) {
				if (partnerDays.get(i).contains(a)) {
					dateDay = i;
					dateTime = a;
					toBreak = true;
					break;
				}
			}
			if (toBreak) {
				break;
			}
		}
		if (dateDay == -1 || dateTime == AvailableTime.NOT_AVAILABLE) {
			return null;
		}
		Calendar date = Calendar.getInstance();
		date.add(Calendar.DATE, 1);
		for (int i = 0; i < 6; i++) {
			if (date.getTime().getDay() == dateDay) {
				break;
			}
			date.add(Calendar.DATE, 1);
		}
		switch (dateTime) {
		case MORNING:
			date.set(Calendar.HOUR_OF_DAY, 8);
			break;
		case NOON:
			date.set(Calendar.HOUR_OF_DAY, 12);
			break;
		case EVENING:
			date.set(Calendar.HOUR_OF_DAY, 20);
			break;
		default:
			break;
		}
		date.set(Calendar.MINUTE, 0);
		date.set(Calendar.SECOND, 0);
		return date.getTime();
	}

}