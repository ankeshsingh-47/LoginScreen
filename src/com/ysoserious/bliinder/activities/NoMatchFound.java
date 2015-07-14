package com.ysoserious.bliinder.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;

import com.ysoserious.bliinder.R;
import com.ysoserious.bliinder.entities.BliinderDate;
import com.ysoserious.bliinder.entities.Server;
import com.ysoserious.bliinder.utils.DateState;

public class NoMatchFound extends Activity {
	private String dateName;
	private Button rschdl;
	private Button letItGo;

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
		setContentView(R.layout.no_match_found);
		fetchView();
		setView();
		Bundle b = getIntent().getExtras();
		dateName = b.getString("dateName");
	}

	private void setView() {
		rschdl.setOnTouchListener(pressedEffect);
		letItGo.setOnTouchListener(pressedEffect);
		rschdl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				rschdl.setClickable(false);
				/*Server.restartDatePref(MyBliiderDates.currentDate.getPartner()
						.getFacebookId());*/
				resetCurrentDate();

				gotoMyBliinderDates();
			}

		});
		letItGo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showCancelDateAlertToUser();
			}
		});
	}

	private void resetCurrentDate() {
		BliinderDate curr = MyBliiderDates.currentDate;
		curr.setState(DateState.NOT_FILLED_PARTNER_NOT_FILLED);
		curr.setMyPref(null);
		curr.setPartnerPref(null);
	}

	private void fetchView() {
		rschdl = (Button) findViewById(R.id.reschdle);
		letItGo = (Button) findViewById(R.id.letItGo_btn);
	}

	public void goToParterDetails(View v) {
		Intent intent = new Intent(NoMatchFound.this, PartnerDetails.class);
		Bundle b = new Bundle();
		b.putString("dateName", dateName);
		intent.putExtras(b);
		startActivity(intent);
		overridePendingTransition(R.anim.slide_down, 0);
	}

	private void showCancelDateAlertToUser() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder
				.setMessage("Are you sure you want to cancel the date?")
				.setCancelable(true)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
							/*	Server.cancelDateFromNeverEver(MyBliiderDates.currentDate
										.getPartner().getFacebookId());*/
								gotoMyBliinderDates();
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

	private void gotoMyBliinderDates() {
		Intent moveToDates = new Intent(NoMatchFound.this, MyBliiderDates.class);
		startActivity(moveToDates);
		finish();

	}
}