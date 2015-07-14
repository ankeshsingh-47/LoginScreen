package com.ysoserious.bliinder.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ysoserious.bliinder.R;

public class WaitingForAnswer extends Activity {
	private String dateName;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.waiting_for_answer);
		Bundle b = getIntent().getExtras();
		dateName = b.getString("dateName");
	}

	public void goToParterDetails(View v) {
		Intent intent = new Intent(WaitingForAnswer.this, PartnerDetails.class);
		Bundle b = new Bundle();
		b.putString("dateName", dateName);
		intent.putExtras(b);
		startActivity(intent);
		overridePendingTransition(R.anim.slide_down, 0);
	}
}