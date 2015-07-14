package com.ysoserious.bliinder.activities;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseGeoPoint;
import com.ysoserious.bliinder.R;
import com.ysoserious.bliinder.entities.Server;
import com.ysoserious.bliinder.entities.User;
import com.ysoserious.bliinder.utils.Data;
import com.ysoserious.bliinder.utils.Utils;

public class ItsADate extends Activity {
	private TextView partnerInfo;
	private TextView partnerMeetingU;
	private TextView secretQuestion;
	private TextView partnerConfirm;
	private Button navigateToButton;
	private Button great;
	private Button noWay;
	private ImageView partnerPicture;
	private User partner;

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

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.its_a_date);
		// We do so because in current the currentDate there is only the fbid of
		// the partner.
		fetchViews();
		setViews();
		manageCalendar();

	}

	private void manageCalendar() {
		if (!MyBliiderDates.currentDate.isInCalendar())
			addDateToCalendar();

	}

	private void setPressedEffect() {
		navigateToButton.setOnTouchListener(pressedEffect);
		noWay.setOnTouchListener(pressedEffect);
		great.setOnTouchListener(pressedEffect);
	}

	private void addDateToCalendar() {
		MyBliiderDates.currentDate.setInCalendar(true);
		Server.updateUpcomingDate(MyBliiderDates.currentDate);
		Calendar beginTime = Calendar.getInstance();
		beginTime.setTime(MyBliiderDates.currentDate.getDate());
		beginTime.set(Calendar.HOUR_OF_DAY, MyBliiderDates.currentDate
				.getDate().getHours());
		beginTime.set(Calendar.MINUTE, MyBliiderDates.currentDate.getDate()
				.getMinutes());
		Intent intent = new Intent(Intent.ACTION_INSERT)
				.setData(Events.CONTENT_URI)
				.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
						beginTime.getTimeInMillis())
				.putExtra(Events.TITLE,
						"Bliinder with " + partner.getFirstName())
				.putExtra(Events.EVENT_LOCATION,
						MyBliiderDates.currentDate.getDateLocation());
		startActivity(intent);

	}

	private void setViews() {
		setPartnerProfilePic();
		setPressedEffect();
		partnerInfo.setText(partner.getFirstName() + ", " + partner.getAge()
				+ getPartnerCity() + " is meeting you!");

		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"EEEEEEEEE MMM d, HH:mm");
		partnerMeetingU.setText("ON: "
				+ dateFormat.format(MyBliiderDates.currentDate.getDate())
				+ "\n\nAT: " + MyBliiderDates.currentDate.getDateLocation());
		navigateToButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(ItsADate.this, "Navigating to location!",
						Toast.LENGTH_SHORT).show();
				navigateByGivenCoordinates(MyBliiderDates.currentDate
						.getLocation().getLongitude(),
						MyBliiderDates.currentDate.getLocation().getLatitude());
			}

			public void navigateByGivenCoordinates(double longitude,
					double latitude) {

				if (Data.location != null) {
					double myLong = Data.location.getLongitude();
					double myLat = Data.location.getLatitude();
					Intent navigation = new Intent(Intent.ACTION_VIEW, Uri
							.parse("http://maps.google.com/maps?saddr=" + myLat
									+ "," + myLong + "&daddr=" + latitude + ","
									+ longitude));
					ItsADate.this.startActivity(navigation);
				}
			}
		});
		setGreatAndNowayButtons();

	}

	private void setGreatAndNowayButtons() {
		noWay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showCancelDateAlertToUser();
			}
		});
		great.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				noWay.setVisibility(View.GONE);
				great.setVisibility(View.GONE);
			secretQuestion.setText(addSecretQuestion());
			//comment
			/*	Server.confirmDate(MyBliiderDates.currentDate);*/
				Toast.makeText(ItsADate.this,
						"Your confirmation has been sent!", Toast.LENGTH_LONG)
						.show();
			}
		});
		//comment
	/*	if (MyBliiderDates.currentDate.isPartnerConfirmed())
			partnerConfirm.setText(partner.getFirstName()
					+ " has confirmed the date!");*/
		//comment
		// if (MyBliiderDates.currentDate.isUserConfirmed()) {
		// noWay.setVisibility(View.GONE);
		// great.setVisibility(View.GONE);
		// secretQuestion.setText(addSecretQuestion());
		// }
	}

	private String addSecretQuestion() {
		return "\n\nSecret Question: What time is it?\n\nAnswer: Bliinder Time!";
	}

	private void fetchViews() {
		partner = MyBliiderDates.currentDate.getPartner();
		partnerInfo = (TextView) findViewById(R.id.datePartnerInfo);
		partnerMeetingU = (TextView) findViewById(R.id.meetingUOn);
		partnerPicture = (ImageView) findViewById(R.id.partnerProfPic);
		secretQuestion = (TextView) findViewById(R.id.secretQuestion);
		navigateToButton = (Button) findViewById(R.id.navigate);
		great = (Button) findViewById(R.id.showSecret);
		noWay = (Button) findViewById(R.id.cancelDateButton);
		partnerConfirm = (TextView) findViewById(R.id.partnerConfirm);
	}

	private String getPartnerCity() {
		ParseGeoPoint partnerLoc = Server.getPartnerLocation(partner);
		Geocoder gcd = new Geocoder(this, Locale.getDefault());
		List<Address> addresses;
		try {
			addresses = gcd.getFromLocation(partnerLoc.getLatitude(),
					partnerLoc.getLongitude(), 1);
			if (addresses.size() > 0)
				return addresses.get(0).getLocality() != null ? ", "
						+ addresses.get(0).getLocality() : "";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	private void setPartnerProfilePic() {
		URL image_value;
		try {
			image_value = new URL("https://graph.facebook.com/"
					+ partner.getFacebookId() + "/picture?type=large");
			Bitmap profPict = BitmapFactory.decodeStream(image_value
					.openConnection().getInputStream());
			if (!Data.me.isFemale())
				profPict = Utils.blurBitmap(profPict, ItsADate.this);
			partnerPicture.setImageBitmap(profPict);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void showCancelDateAlertToUser() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder
				.setMessage("Are you sure you want to cancel the date?")
				.setCancelable(true)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								Server.cancelDate(MyBliiderDates.currentDate
										.getPartner().getFacebookId());
								gotoPartnerSelection();
							}
						});
		alertDialogBuilder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alert = alertDialogBuilder.create();
		alert.show();
	}

	private void gotoPartnerSelection() {
		Intent moveToPreferences = new Intent(ItsADate.this,
				PartnerSelection.class);
		startActivity(moveToPreferences);
		finish();

	}
}