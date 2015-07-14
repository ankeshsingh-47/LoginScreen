package com.ysoserious.bliinder.activities.login;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.*;
import com.ysoserious.bliinder.R;
import com.ysoserious.bliinder.entities.User;
import com.ysoserious.bliinder.listener.OnSwipeTouchListener;
import com.ysoserious.bliinder.utils.Data;

public class LoginScreen extends FragmentActivity {

	private LoginFragment loginFragment;
	private LinearLayout background;
	private int currentBackground;
	private static final String BACKGROUND_NAME = "login_";

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_screen);
		 try {
		    	PackageInfo info = getPackageManager().getPackageInfo( this.getPackageName(), PackageManager.GET_SIGNATURES);
		    for (Signature signature : info.signatures) { 
		    	MessageDigest md = MessageDigest.getInstance("SHA");
		    	md.update(signature.toByteArray()); 
		    	Log.d("hash:", Base64.encodeToString(md.digest(), Base64.DEFAULT)); }
		    }
		    catch (NameNotFoundException e) {

		    } catch (NoSuchAlgorithmException e) { }
		checkForGPSConnection();
		setBackgroungManager();
		Data.me = new User();

		if (savedInstanceState == null) {
			// Add the fragment on initial activity setup
			loginFragment = new LoginFragment();
			getSupportFragmentManager().beginTransaction()
					.add(android.R.id.content, loginFragment).commit();
		} else {
			// Or set the fragment from restored state info
			loginFragment = (LoginFragment) getSupportFragmentManager()
					.findFragmentById(android.R.id.content);
		}
	}

	private void setBackgroungManager() {
		background = (LinearLayout) findViewById(R.id.loginBackground);
		findViewById(R.id.swipeLayer).setOnTouchListener(
				new SwipeSidesListener(this));
		currentBackground = 1;
		background.setBackgroundResource(R.drawable.login_1);
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Logs 'install' and 'app activate' App Events.
		AppEventsLogger.activateApp(this);

	}

	@Override
	protected void onPause() {
		super.onPause();

		// Logs 'app deactivate' App Event.
		AppEventsLogger.deactivateApp(this);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode,
				resultCode, data);

	}

	private void checkForGPSConnection() {
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			Toast.makeText(this, "GPS is Enabled in your devide",
					Toast.LENGTH_SHORT).show();
		} else {
			showGPSDisabledAlertToUser();
		}
	}

	private void showGPSDisabledAlertToUser() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder
				.setMessage(
						"Bliinder can't work without a GPS connection. Would you like to turn it on?")
				.setCancelable(false)
				.setPositiveButton("Go To Settings Page To Enable GPS",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								Intent callGPSSettingIntent = new Intent(
										android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
								startActivity(callGPSSettingIntent);
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

	private final class SwipeSidesListener extends OnSwipeTouchListener {
		public SwipeSidesListener(Context ctx) {
			super(ctx);
		}

		@Override
		public void onSwipeLeft() {
			currentBackground = currentBackground == 3 ? 1
					: currentBackground + 1;

			setBackgroundAccordingToCurrent();

		}

		@Override
		public void onSwipeRight() {
			currentBackground = currentBackground == 1 ? 3
					: currentBackground - 1;
			setBackgroundAccordingToCurrent();
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			return gestureDetector.onTouchEvent(event);
		}

		private void setBackgroundAccordingToCurrent() {
			switch (currentBackground) {
			case 1: {
				background.setBackgroundResource(R.drawable.login_1);
				break;
			}
			case 2: {
				background.setBackgroundResource(R.drawable.login_2);
				break;
			}
			case 3: {
				background.setBackgroundResource(R.drawable.login_3);
				break;
			}
			}

		}
	}
}
