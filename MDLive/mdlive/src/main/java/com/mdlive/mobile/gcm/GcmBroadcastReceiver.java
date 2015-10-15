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
			Log.e("Push notification message", message+"");
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
							.setSmallIcon(R.drawable.icon)
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
			JsonParser parser = new JsonParser();
			JsonObject originalPayload = parser.parse(message).getAsJsonObject();
			final UserBasicInfo userBasicInfo = UserBasicInfo.readFromSharedPreference(context);
			final Intent messageIntent = new Intent(context,
					originalPayload.get("acme").getAsJsonArray().get(0).getAsString().equalsIgnoreCase("message") ?
							MessageCenterInboxDetailsActivity.class : AppointmentActivity.class);
			messageIntent.putExtra("notification_id", originalPayload.get("acme").getAsJsonArray().get(1).getAsInt());

			// Can start the dialog activity but that clear all the existing activity and shows only the dialog.
			// Hence this approach dropped

            DialogInterface.OnClickListener positive = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (userBasicInfo != null) {
						messageIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        messageIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        messageIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(messageIntent);
                    }
                    dialog.dismiss();
                }
            };
            DialogInterface.OnClickListener negtive = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            };
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    context);
            alertDialogBuilder
                    .setTitle("MDLIVE")
                    .setMessage(originalPayload.get("alert").getAsString())
                    .setCancelable(false)
                    .setPositiveButton(context.getString(R.string.mdl_Ok), positive);

            alertDialogBuilder.setNegativeButton(context.getString(R.string.mdl_cancel), negtive);
            final AlertDialog alertDialog = alertDialogBuilder.create();

			alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

			alertDialog.getWindow().getAttributes().windowAnimations = R.style.MDLive_Dialog_Theme;
            alertDialog.show();
		}catch (Exception e){
			e.printStackTrace();
		}
	}

}
