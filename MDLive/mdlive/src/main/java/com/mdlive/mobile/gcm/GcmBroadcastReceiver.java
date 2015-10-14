package com.mdlive.mobile.gcm;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.view.View;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mdlive.embedkit.uilayer.appointment.AppointmentActivity;
import com.mdlive.embedkit.uilayer.messagecenter.MessageCenterActivity;
import com.mdlive.embedkit.uilayer.messagecenter.MessageCenterInboxDetailsActivity;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.UserBasicInfo;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {

	@Override
	public void onReceive(final Context context, Intent intent) {
		try {
			if (MdliveUtils.isAppInForground) {
				String message = intent.getStringExtra("message");
				Log.e("Push notification message", message+"");
				try {
					JsonParser parser = new JsonParser();
					JsonObject originalPayload = parser.parse(message).getAsJsonObject();
					final UserBasicInfo userBasicInfo = UserBasicInfo.readFromSharedPreference(context);
					final Intent messageIntent = new Intent(context,
							originalPayload.get("acme").getAsJsonArray().get(0).getAsString().equalsIgnoreCase("message") ?
									MessageCenterInboxDetailsActivity.class : AppointmentActivity.class);
					messageIntent.putExtra("notification_id", originalPayload.get("acme").getAsJsonArray().get(1).getAsInt());
					JsonObject notificationPayload = new JsonObject();
					JsonObject alert = new JsonObject();
					alert.addProperty("alert", originalPayload.get("alert").getAsString());
					notificationPayload.add("aps", alert);
					notificationPayload.add("acme", originalPayload.get("acme").getAsJsonArray());
//{“aps”:{“alert”:“You have a new message”},“acme”:[“message”,”1122233”]}
					DialogInterface.OnClickListener positive = new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (userBasicInfo != null) {
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
					MdliveUtils.showDialog(context, "MDLIVE", originalPayload.get("alert").getAsString(), context.getString(com.embedkit.wrapper.R.string.mdl_Ok), context.getString(com.embedkit.wrapper.R.string.mdl_cancel),
							positive, negtive);
				}catch (Exception e){
					e.printStackTrace();
				}
			} else {
				Log.e("Push notification message else", intent.getStringExtra("message")+"");
				// Explicitly specify that GcmIntentService will handle the intent.
				ComponentName comp = new ComponentName(context.getPackageName(),
						GcmIntentService.class.getName());
				// Start the service, keeping the device awake while it is launching.
				startWakefulService(context, (intent.setComponent(comp)));
				setResultCode(Activity.RESULT_OK);

				SharedPreferences settings = context.getApplicationContext().getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
				settings.edit().putString("has_push_notification", intent.getStringExtra("message")).commit();

			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}


}
