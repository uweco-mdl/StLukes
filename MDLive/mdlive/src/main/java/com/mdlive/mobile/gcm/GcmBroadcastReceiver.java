package com.mdlive.mobile.gcm;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mdlive.embedkit.uilayer.appointment.AppointmentActivity;
import com.mdlive.embedkit.uilayer.messagecenter.MessageCenterActivity;
import com.mdlive.embedkit.uilayer.messagecenter.MessageCenterInboxDetailsActivity;
import com.mdlive.mobile.R;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.UserBasicInfo;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {

	@Override
	public void onReceive(final Context context, Intent intent) {
		String message = intent.getStringExtra("message");
		sendNotification(message, context);
	}
	/**
	 * Notifies the user when message is received.
	 * */
	private void sendNotification(final String message, final Context context) {
		try {
			if (MdliveUtils.isAppInForground) {
				// This needs to be enalbed once we get the solution for alert pop-up from service.

				showAlert(message, context);
//
//				sendAppNotification(message, context);
//				SharedPreferences settings = context.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
//				settings.edit().putString("has_push_notification", message).commit();
			} else {
				sendAppNotification(message, context);
				SharedPreferences settings = context.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
				settings.edit().putString("has_push_notification", message).commit();
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	private void sendAppNotification(String msg, final Context context) {
		try {
			NotificationManager mNotificationManager = (NotificationManager)
					context.getSystemService(Context.NOTIFICATION_SERVICE);

			Intent myintent = new Intent(context, NotificationHandler.class);
			myintent.putExtra("message", msg);
			PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
					myintent, PendingIntent.FLAG_CANCEL_CURRENT);

			JsonParser parser = new JsonParser();
			JsonObject parsedData = (JsonObject)parser.parse(msg);
			int NOTIFICATION_ID = parsedData.get("acme").getAsJsonArray().get(1).getAsInt();
			NotificationCompat.Builder mBuilder =
					new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.ic_stat_notify_vsee_in_call)
							.setContentTitle(context.getResources().getString(R.string.mdl_app_name))
							.setStyle(new NotificationCompat.BigTextStyle()
									.bigText(parsedData.get("alert").getAsString()))
							.setContentText(parsedData.get("alert").getAsString()).setAutoCancel(true)
							.setVibrate(new long[]{500, 500, 500, 500, 500})
							.setLights(Color.RED, 1000, 1000)
							.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

			mBuilder.setContentIntent(contentIntent);
			mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void showAlert(String message, final Context context){
		try {
			Intent intent = new Intent(context, GCMNotificationDialog.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra("message", message);
			context.startActivity(intent);

		}catch (Exception e){
			e.printStackTrace();
		}
	}

}
