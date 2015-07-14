package com.ysoserious.bliinder.activities;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.ParseGeoPoint;
import com.ysoserious.bliinder.R;
import com.ysoserious.bliinder.entities.BliinderPreferences;
import com.ysoserious.bliinder.entities.Server;
import com.ysoserious.bliinder.entities.User;
import com.ysoserious.bliinder.listener.OnSwipeTouchListener;
import com.ysoserious.bliinder.utils.Data;
import com.ysoserious.bliinder.utils.Utils;

public class PartnerDetails extends Activity {
	private TextView partnerAge;
	private ImageView partnerPhoto;
	private User partner;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.partner_details);
		partner = MyBliiderDates.currentDate.getPartner();

		LinearLayout layout = (LinearLayout) findViewById(R.id.partnerDetailsLayout);
		layout.setOnTouchListener(new SwipeUpListener(PartnerDetails.this));
		User user = MyBliiderDates.currentDate.getPartner();

		partnerAge = (TextView) findViewById(R.id.meetingUOn);
		partnerPhoto = (ImageView) findViewById(R.id.partnerPhoto);

		partnerAge.setText("Name: " + user.getFirstName() + "\nAge: "
				+ user.getAge() + getPartnerCity() + "\nSerious: "
				+ user.getRelationshipType() + "%");

		// TODO: insure that every user has at least on profile picture - even a
		// "not available" picture.
		// Drawable myDrawable = getApplicationContext().getResources()
		// .getDrawable(R.drawable.waiting);
		// Bitmap bitmap = Utils.blurBitmap(
		// ((BitmapDrawable) myDrawable).getBitmap(), PartnerDetails.this);
		//
		// partnerPhoto.setImageBitmap(bitmap);
		setPartnerProfilePic();
	}

	private String getPartnerCity() {
		ParseGeoPoint partnerLoc = Server.getPartnerLocation(partner);
		Geocoder gcd = new Geocoder(this, Locale.getDefault());
		List<Address> addresses;
		try {
			addresses = gcd.getFromLocation(partnerLoc.getLatitude(),
					partnerLoc.getLongitude(), 1);
			if (addresses.size() > 0)
				return addresses.get(0).getLocality() != null ? "\nCity: "
						+ addresses.get(0).getLocality() : "";
		} catch (Exception e) {
			// TODO Auto-generated catch block
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
				profPict = Utils.blurBitmap(profPict, PartnerDetails.this);
			partnerPhoto.setImageBitmap(profPict);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private final class SwipeUpListener extends OnSwipeTouchListener {
		public SwipeUpListener(Context ctx) {
			super(ctx);
		}

		@Override
		public void onSwipeTop() {
			finish();
			overridePendingTransition(R.anim.slide_down, R.anim.slide_up);
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			return gestureDetector.onTouchEvent(event);
		}
	}
}