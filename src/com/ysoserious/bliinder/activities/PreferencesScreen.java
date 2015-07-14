package com.ysoserious.bliinder.activities;

import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;
import com.ysoserious.bliinder.R;
import com.ysoserious.bliinder.entities.BliinderPreferences;
import com.ysoserious.bliinder.entities.Server;
import com.ysoserious.bliinder.utils.Data;

public class PreferencesScreen extends Activity {
	private static final String TAG = "Pref Screen Bliinder";
	private static final String AGES_UNSORTED = "You made an unpossible ages choice.\n";

	private TextView nameAndAge;
	private EditText fromAge;
	private EditText toAge;
	private SeekBar distance;
	private TextView distanceText;
	private SeekBar loveRange;
	private TextView loveRangeText;
	private RadioGroup lookingFor;

	private Request.GraphUserCallback saveInfo = new Request.GraphUserCallback() {
		@Override
		public void onCompleted(GraphUser user, Response response) {
			if (user != null) {
				// nameAndAge.setText(user.getName() + ", " + Data.me.getAge());
				((ProfilePictureView) findViewById(R.id.userPicturePref))
						.setProfileId(user.getId());
			} else
				Toast.makeText(getApplicationContext(), "user null?",
						Toast.LENGTH_SHORT).show();
		}
	};

	private View.OnClickListener onOkButtonClicked = new View.OnClickListener() {

		@Override
		public void onClick(View okButton) {
			Data.me.setLookingForMale(getCheckedLookingForMale());
			int minAge = Integer.parseInt(fromAge.getText().toString());
			int maxAge = Integer.parseInt(toAge.getText().toString());
			if (minAge > maxAge) {
				notifyUnableToSavePref(AGES_UNSORTED);
				return;
			}
			Data.minAge = minAge;
			Data.maxAge = maxAge;
			Data.desiredDistance = distance.getProgress();
			Data.me.setRelationshipType(loveRange.getProgress());
			saveBliinderPref();
			Server.blockingPostUserInfo();
			backToPartnerSelect();
		}

		private void saveBliinderPref() {
			BliinderPreferences.setMinAge(Data.minAge);
			BliinderPreferences.setMaxAge(Data.maxAge);
			BliinderPreferences.setDesiredDistance(Data.desiredDistance);
		}

		protected void backToPartnerSelect() {
			finish();
		}

		private void notifyUnableToSavePref(String msg) {
			Toast.makeText(PreferencesScreen.this, msg, Toast.LENGTH_SHORT)
					.show();
		}

		private boolean getCheckedLookingForMale() {
			switch (lookingFor.getCheckedRadioButtonId()) {
			case R.id.malePref:
				return true;
			case R.id.femalePref:
				return false;
			}
			// Should never arrive here...
			return false;
		}

	};

	private OnSeekBarChangeListener distanceSeekBarListener = new OnSeekBarChangeListener() {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			distanceText.setText(progress + " km");

		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// We don't care...

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// We don't care...

		}

	};

	private OnSeekBarChangeListener loveRangeSeekBarListener = new OnSeekBarChangeListener() {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			loveRangeText.setText(progress + "%");

		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// We don't care...

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// We don't care...

		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preferences);
		fetchViews();
		setViews();
	}

	private void setViews() {
		Session session = Session.getActiveSession();
		nameAndAge.setText(Data.me.getName() + ", " + Data.me.getAge()
				+ getMyCity());
		if (session != null && (session.isOpened() || session.isClosed())) {
			onSessionStateChange(session, session.getState(), null);
		}
		setGenderChecked();
		fromAge.setText(Integer.toString(Data.minAge));
		toAge.setText(Integer.toString(Data.maxAge));
		loveRange.setOnSeekBarChangeListener(loveRangeSeekBarListener);
		loveRange.setProgress(Data.me.getRelationshipType());
		distance.setOnSeekBarChangeListener(distanceSeekBarListener);
		distance.setProgress(Data.desiredDistance);
		((Button) findViewById(R.id.buttonOKPref))
				.setOnClickListener(onOkButtonClicked);
	}

	private String getMyCity() {
		Geocoder gcd = new Geocoder(this, Locale.getDefault());
		List<Address> addresses;
		try {
			addresses = gcd.getFromLocation(Data.location.getLatitude(),
					Data.location.getLongitude(), 1);
			if (addresses.size() > 0)
				return addresses.get(0).getLocality() != null ? "\n"
						+ addresses.get(0).getLocality() : "";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	private void setGenderChecked() {
		int userGender = Data.me.lookingForMale() ? R.id.malePref
				: R.id.femalePref;
		lookingFor.check(userGender);

	}

	@SuppressWarnings("deprecation")
	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		if (state.isOpened()) {
			Log.i(TAG, "Logged in...");
			Request.executeMeRequestAsync(session, saveInfo);
		} else if (state.isClosed()) {
			Log.i(TAG, "Logged out...");
		}
	}

	private void fetchViews() {
		nameAndAge = (TextView) findViewById(R.id.userNameAnAgePref);
		fromAge = (EditText) findViewById(R.id.fromPref);
		toAge = (EditText) findViewById(R.id.toPref);
		distance = (SeekBar) findViewById(R.id.seekBarKmPref);
		distanceText = (TextView) findViewById(R.id.kmTextPref);
		loveRange = (SeekBar) findViewById(R.id.loveRange);
		loveRangeText = (TextView) findViewById(R.id.loveRangeTextPref);
		lookingFor = (RadioGroup) findViewById(R.id.radioGroupGender);

	}
}
