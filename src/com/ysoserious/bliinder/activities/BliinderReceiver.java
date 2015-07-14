package com.ysoserious.bliinder.activities;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BliinderReceiver extends com.parse.ParsePushBroadcastReceiver {
	public static final String LOGTAG = "BliinderReciever";

	@Override
	protected void onPushReceive(Context context, Intent intent) {
		super.onPushReceive(context, intent);
		Toast.makeText(context, "Receive Push!!", Toast.LENGTH_LONG).show();
	}

}
