package com.medianova.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		 Intent myIntent = new Intent(context, NotificationService.class);
         context.startService(myIntent);
	}

}
