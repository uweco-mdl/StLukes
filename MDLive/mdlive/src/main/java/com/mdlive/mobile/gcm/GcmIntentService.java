package com.mdlive.mobile.gcm;


import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mdlive.mobile.R;
import com.mdlive.mobile.SplashScreenActivity;

public class GcmIntentService extends IntentService{
	public static int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;
	public static final String TAG = "MDLIVE_NOTIFICATION";

	public GcmIntentService() {
		super("GcmIntentService");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		Bundle extras = intent.getExtras();
		String msg = intent.getStringExtra("message");
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) {

			if (GoogleCloudMessaging.
					MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
				sendNotification("Send error: " + extras.toString());
			} else if (GoogleCloudMessaging.
					MESSAGE_TYPE_DELETED.equals(messageType)) {
				sendNotification("Deleted messages on server: " +
						extras.toString());
				// If it's a regular GCM message, do some work.
			} else if (GoogleCloudMessaging.
					MESSAGE_TYPE_MESSAGE.equals(messageType)) {
				// This loop represents the service doing some work.
				for (int i=0; i<5; i++) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				// Post notification of received message.
				//sendNotification("Received: " + extras.toString());
				sendNotification(msg);
			}
		}
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}
	private void sendNotification(String msg) {
		try {
		mNotificationManager = (NotificationManager)
				this.getSystemService(Context.NOTIFICATION_SERVICE);

		Intent myintent = new Intent(this, NotificationHandler.class);
		myintent.putExtra("message", msg);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				myintent, PendingIntent.FLAG_CANCEL_CURRENT);

		JsonParser parser = new JsonParser();
		JsonObject parsedData = (JsonObject)parser.parse(msg);
			NOTIFICATION_ID = parsedData.get("acme").getAsJsonArray().get(1).getAsInt();
			NotificationCompat.Builder mBuilder =
					new NotificationCompat.Builder(this)
			.setSmallIcon(R.drawable.icon)
			.setContentTitle(getResources().getString(R.string.app_name))
			.setStyle(new NotificationCompat.BigTextStyle()
			.bigText(parsedData.get("alert").getAsString()))
			.setContentText(parsedData.get("alert").getAsString()).setAutoCancel(true)
			.setVibrate(new long[] { 500, 500, 500, 500, 500 })
			.setLights(Color.RED, 1000, 1000)
			.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

			mBuilder.setContentIntent(contentIntent);
			mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}