package com.ysoserious.bliinder.activities.login;

import java.util.Arrays;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.parse.ParseGeoPoint;
import com.ysoserious.bliinder.R;
import com.ysoserious.bliinder.activities.PartnerSelection;
import com.ysoserious.bliinder.activities.PreferencesScreen;
import com.ysoserious.bliinder.entities.BliinderPreferences;
import com.ysoserious.bliinder.entities.Server;
import com.ysoserious.bliinder.entities.User;
import com.ysoserious.bliinder.utils.Constant;
import com.ysoserious.bliinder.utils.Data;
import com.ysoserious.bliinder.utils.MyLocation;
import com.ysoserious.bliinder.utils.MyLocation.LocationResult;

public class LoginFragment extends Fragment {

	private MyLocation myLocation;
	private static final int DEFAULT_LOVE_RANGE = 50;
	private static final String TAG = "LoginFragment";
	protected static final int DEFAULT_DISTANCE = 45;
	protected static final long MIN_TIME_BW_UPDATES = 0;
	protected static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 0;
	private UiLifecycleHelper uiHelper;
	private boolean firstServerAccess = true;
	private Activity hostActivity;

	private Request.GraphUserCallback saveInfo = new Request.GraphUserCallback() {
		@Override
		public void onCompleted(GraphUser user, Response response) {
			if (user != null) {
				if (Server.userAlreadySignedup(user.getId())
						&& BliinderPreferences.dataSavedInThisDevice()) {
					fetchKnownUserInfo(user);
				} else
					fetchUserInfo(user);
			} else
				Toast.makeText(getActivity().getApplicationContext(),
						"user null?", Toast.LENGTH_SHORT).show();

		}

		private void fetchKnownUserInfo(GraphUser user) {
			Toast.makeText(getActivity().getApplicationContext(),
					" Hello " + user.getName(), Toast.LENGTH_LONG).show();
			// Retrieving data from fb.
			Data.me.setName(user.getName());
			Data.me.setFacebookId(user.getId());
			Data.me.setFemale(user.asMap().get("gender").toString());
			// The date received as dd/mm/yyyy, so we need the 6th char.
			Data.me.setYearOfBirth(user.getBirthday().substring(6));
			User fromServer = Server.getUser(user.getId());
			Data.me.setRelationshipType(fromServer.getRelationshipType());
			Data.me.setLookingForMale(fromServer.lookingForMale());
			setBioIfHas(user);
			fetchInfoAndMoveToPartnerSelection();
		}

		private void fetchUserInfo(GraphUser user) {

			Toast.makeText(getActivity().getApplicationContext(),
					" Hello " + user.getName(), Toast.LENGTH_LONG).show();
			// Retrieving data from fb.
			Data.me.setName(user.getName());
			Data.me.setFacebookId(user.getId());
			Data.me.setFemale(user.asMap().get("gender").toString());
			// The date received as dd/mm/yyyy, so we need the 6th char.
			Data.me.setYearOfBirth(user.getBirthday().substring(6));
			Data.me.setRelationshipType(DEFAULT_LOVE_RANGE);
			Data.me.setLookingForMale(Data.me.isFemale());
			Data.desiredDistance = DEFAULT_DISTANCE;
			// Data.me.setProfession(user.getProperty("work").toString());
			setBioIfHas(user);
			calculateAndSaveDatingPreference();

		}

		private void setBioIfHas(GraphUser user) {
			// getProperty is same as asMap().get(string).
			Object bioObject = user.getProperty("bio");
			Data.me.setAbout(bioObject == null ? "Don't Have a Bio "
					: bioObject.toString());
		}

		private void fetchInfoAndMoveToPartnerSelection() {
			Data.minAge = BliinderPreferences.getMinAge();
			Data.maxAge = BliinderPreferences.getMaxAge();
			Data.desiredDistance = BliinderPreferences.getDesiredDistance();
			getLocationAndPost();
		}

		private void calculateAndSaveDatingPreference() {
			Data.minAge = Data.me.getAge()
					- (Data.me.isFemale() ? Constant.WOMAN_MIN_AGE_DEFAULT_DIFF
							: Constant.MAN_MIN_AGE_DEFAULT_DIFF);
			BliinderPreferences.setMinAge(Data.minAge);
			Data.maxAge = Data.me.getAge()
					+ (Data.me.isFemale() ? Constant.WOMAN_MAX_AGE_DEFAULT_DIFF
							: Constant.MAN_MAX_AGE_DEFAULT_DIFF);
			BliinderPreferences.setMaxAge(Data.maxAge);
			Data.desiredDistance = Constant.DEFAULT_DESIRED_DISTANCE;
			BliinderPreferences.setDesiredDistance(Data.desiredDistance);
			getLocationAndPost();

		}

		private void getLocationAndPost() {
			LocationResult locationResult = new LocationResult() {
				@Override
				public void gotLocation(Location location) {
					Log.d("aj", "this is getLocationAndPost()");
				/*	Data.location = new ParseGeoPoint(location.getLatitude(),
							location.getLongitude());*/
					Data.location = new ParseGeoPoint(31.771959,
							35.217018);
					if (firstServerAccess) {
						Server.postUserInfo();
						firstServerAccess = false;
					}
					goToPartnerSelect();
				}
			};
			myLocation = new MyLocation();
			myLocation.getLocation(getActivity(), locationResult);
		}

	};
	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		hostActivity = getActivity();
		uiHelper = new UiLifecycleHelper(getActivity(), callback);
		uiHelper.onCreate(savedInstanceState);
	}

	protected void goToPartnerSelect() {
		myLocation.cancelTimer();
		Intent moveToPartnerSelection = new Intent(hostActivity,
				PartnerSelection.class);
		startActivity(moveToPartnerSelection);
		getActivity().finish();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.login_screen, container, false);
		LoginButton authButton = (LoginButton) view
				.findViewById(R.id.authButton);
		authButton.setFragment(this);
		authButton.setReadPermissions(Arrays.asList("user_status",
				"public_profile", "user_birthday", "user_about_me"));

		return view;
	}

	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		if (state.isOpened()) {
			Log.i(TAG, "Logged in...");
			Data.facebookSession = session;
			Request.executeMeRequestAsync(session, saveInfo);
			// XXX: newMeRequest(...).executeAsync
		} else if (state.isClosed()) {
			Log.i(TAG, "Logged out...");
		}
	}

	@Override
	public void onResume() {

		// For scenarios where the main activity is launched and user
		// session is not null, the session state change notification
		// may not be triggered. Trigger it if it's open/closed.
		Session session = Session.getActiveSession();
		if (session != null && (session.isOpened() || session.isClosed())) {
			onSessionStateChange(session, session.getState(), null);
		}

		super.onResume();
		uiHelper.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}
}
