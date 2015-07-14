package com.ysoserious.bliinder.activities;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.ysoserious.bliinder.R;
import com.ysoserious.bliinder.entities.Server;
import com.ysoserious.bliinder.entities.User;
import com.ysoserious.bliinder.listener.OnSwipeTouchListener;
import com.ysoserious.bliinder.utils.Data;
import com.ysoserious.bliinder.utils.MyLocation;
import com.ysoserious.bliinder.utils.MyLocation.LocationResult;

public class PartnerSelection extends Activity {
	private MyLocation myLocation;
	private static final int FIRST = 0;
	private TextView nameAndAge;
	private ProfilePictureView userPic;
	private List<User> partners;
	private User lastShown;
	private Button back;
	private Button pref;
	private Button dateList;
	private Button like;
	private Button dislike;
	private View.OnClickListener preferencesButtonClicked = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent moveToPreferences = new Intent(PartnerSelection.this,
					PreferencesScreen.class);
			startActivity(moveToPreferences);
		}
	};

	private View.OnClickListener toDatesListButtonClicked = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			Server.checkForDatesAndUpdateTable();
			Intent moveToMyBliiderDates = new Intent(PartnerSelection.this,
					MyBliiderDates.class);
			startActivity(moveToMyBliiderDates);
		}
	};

	private OnClickListener showLastPerson = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (lastShown == null)
				return;
			partners.add(FIRST, lastShown);
			Server.cancelUserChoice(lastShown);
			printCurrentUserInfo();
		}
	};

	private OnTouchListener pressedEffect = new OnTouchListener() {

		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: {
				v.getBackground().setColorFilter(0xe0f47521,
						PorterDuff.Mode.SRC_ATOP);
				v.invalidate();
				break;
			}
			case MotionEvent.ACTION_UP: {
				v.getBackground().clearColorFilter();
				v.invalidate();
				break;
			}
			}
			return false;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.partner_selection);
		updateLocation();
		fetchViews();
		setViews();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Server.getPotentialPartners(new FindCallback<ParseObject>() {
			public void done(List<ParseObject> partnersFound, ParseException e) {
				if (e == null) {
					partners = User.parseObjectListToUserList(partnersFound);
					printCurrentUserInfo();
					userPic.setOnTouchListener(new SwipeSidesListener(
							PartnerSelection.this));
				} else {
					Log.d("PartnerSelection", "Error: " + e.getMessage());
				}
			}
		});
	}

	private void updateLocation() {
		LocationResult locationResult = new LocationResult() {
			@Override
			public void gotLocation(Location location) {
				Data.location = new ParseGeoPoint(location.getLatitude(),
						location.getLongitude());
				Server.postUserInfo();
			}
		};
		myLocation = new MyLocation();
		myLocation.getLocation(this, locationResult);
	}

	private void fetchViews() {

		nameAndAge = (TextView) findViewById(R.id.userNameAnAgePartnerSelect);
		userPic = (ProfilePictureView) findViewById(R.id.userPicturePartnerSelect);
		pref = (Button) findViewById(R.id.prefButtonPartnerSelection);
		dateList = (Button) findViewById(R.id.toDatesList);
		like = (Button) findViewById(R.id.likeButton);
		dislike = (Button) findViewById(R.id.dislikeButton);
		back = (Button) findViewById(R.id.back);
	}

	private void setViews() {
		setPrefButtonAccordingToGender();
		setPressedEffect();
		pref.setOnClickListener(preferencesButtonClicked);

		dateList.setOnClickListener(toDatesListButtonClicked);

		like.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				v.setClickable(false);
				v.setEnabled(false);
				postChoiceAndMoveToNext(true);
				v.setClickable(true);
				v.setEnabled(true);
			}
		});

		dislike.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				v.setClickable(false);
				v.setEnabled(false);
				postChoiceAndMoveToNext(false);
				v.setClickable(true);
				v.setEnabled(true);
			}
		});

		back.setOnClickListener(showLastPerson);

		Server.getPotentialPartners(new FindCallback<ParseObject>() {
			public void done(List<ParseObject> partnersFound, ParseException e) {
				if (e == null) {
					partners = User.parseObjectListToUserList(partnersFound);
					printCurrentUserInfo();
					userPic.setOnTouchListener(new SwipeSidesListener(
							PartnerSelection.this));
				} else {
					Log.d("PartnerSelection", "Error: " + e.getMessage());
				}
			}
		});

	}

	private void setPressedEffect() {
		pref.setOnTouchListener(pressedEffect);
		dateList.setOnTouchListener(pressedEffect);
		like.setOnTouchListener(pressedEffect);
		dislike.setOnTouchListener(pressedEffect);
		back.setOnTouchListener(pressedEffect);
	}

	private void setPrefButtonAccordingToGender() {
		if (Data.me.isFemale())
			pref.setBackgroundResource(R.drawable.ic_girl);
		else
			pref.setBackgroundResource(R.drawable.ic_boy);

	}

	private void printCurrentUserInfo() {
		if (partners == null || partners.isEmpty()) {
			nameAndAge.setText("No Partners Available :(");
			userPic.setProfileId(null);
			return;
		}
		User first = partners.get(FIRST);
		nameAndAge.setText((first.getName().split(" "))[FIRST] + ", "
				+ first.getAge());
		userPic.setProfileId(first.getFacebookId());
	}

	private final class SwipeSidesListener extends OnSwipeTouchListener {
		public SwipeSidesListener(Context ctx) {
			super(ctx);
		}

		@Override
		public void onSwipeLeft() {
			postChoiceAndMoveToNext(false);
		}

		@Override
		public void onSwipeRight() {
			postChoiceAndMoveToNext(true);
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			return gestureDetector.onTouchEvent(event);
		}
	}

	private void postChoiceAndMoveToNext(boolean isLiked) {
		if (!partners.isEmpty()) {
			lastShown = partners.get(FIRST);
			Server.postUserChoice(partners.get(FIRST), isLiked);
			partners.remove(FIRST);
		}
		// This is not an "else" block in propose, since it might became
		// empty from the closer above.
		if (partners.isEmpty())
			Server.getPotentialPartners(new FindCallback<ParseObject>() {
				public void done(List<ParseObject> partnersFound,
						ParseException e) {
					if (e == null) {
						partners = User
								.parseObjectListToUserList(partnersFound);
						printCurrentUserInfo();
					} else {
						Log.d("PartnerSelection", "Error: " + e.getMessage());
					}
				}
			});
		else
			printCurrentUserInfo();
	}
}
