package com.medianova.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import com.medianova.doctorfinder.Home;
import com.medianova.doctorfinder.R;

import java.util.Random;



public class NotificationService extends Service {
	private NotificationManager mManager;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		this.getApplicationContext();
		Context context = getApplicationContext();
		// Getting Notification Service
		/*mManager = (NotificationManager) this.getApplicationContext()
				.getSystemService(Context.NOTIFICATION_SERVICE);
		
		Intent intent1 = new Intent(this.getApplicationContext(), Home.class);

		Notification notification = new Notification();
		Context context = getApplicationContext();
		String notificationTitle = "Restaurant";
		String notificationText = "Your table is Booked";
		intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

		PendingIntent pendingNotificationIntent = PendingIntent.getActivity(this.getApplicationContext(), 0, intent1,
				PendingIntent.FLAG_UPDATE_CURRENT);

		Notification.Builder builder = new Notification.Builder(context).setContentIntent(pendingNotificationIntent)
				.setSmallIcon(R.drawable.ic_launcher).setContentTitle(notificationTitle)
				.setContentText(notificationText);
		notification = builder.build();
		
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		builder.setAutoCancel(true);

		Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		notification.sound = uri;
		mManager.notify(0, notification);*/
		
		Random r = new Random();
		int i1 = r.nextInt(100 - 1) + 1;

		int icon = R.mipmap.ic_launcher;
		long when = System.currentTimeMillis();
		
		Log.d("when", ""+when);
		
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		@SuppressWarnings("deprecation")
		Notification notification = new Notification();

		String title = context.getString(R.string.app_name);

		Intent notificationIntent = new Intent(context, Home.class);
		// set intent so it does not start a new activity
		
		// notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
		// Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent intent1 = PendingIntent.getActivity(context, i1, notificationIntent, 0);

		Notification.Builder builder = new Notification.Builder(context).setContentIntent(intent1).setSmallIcon(icon)
				.setContentTitle(title).setContentText(getString(R.string.app_name));
		builder.setAutoCancel(true);

		notification = builder.build();
		// notification.setLatestEventInfo(context, notificationText,
		// notificationTitle, pendingNotificationIntent);
		// notification.flags = Notification.FLAG_AUTO_CANCEL;

		Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		notification.sound = uri;
		// notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notificationManager.notify(0, notification);
		
		
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		// Logger.error("Alam Services Destroyed");
		super.onDestroy();
	}

}
