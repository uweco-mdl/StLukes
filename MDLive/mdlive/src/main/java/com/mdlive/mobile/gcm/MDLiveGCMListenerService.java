package com.mdlive.mobile.gcm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;

import com.google.android.gms.gcm.GcmListenerService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mdlive.embedkit.uilayer.appointment.AppointmentActivity;
import com.mdlive.embedkit.uilayer.messagecenter.MessageCenterInboxDetailsActivity;
import com.mdlive.mobile.R;
import com.mdlive.mobile.SplashScreenActivity;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.UserBasicInfo;

/**
 * Created by dhiman_da on 9/7/2015.
 */
public class MDLiveGCMListenerService extends GcmListenerService {
    private static final String TAG = "MDLiveGCMListenerService";

    /**
     * Called when Down strem message is received from Google Cloud server
     * */
    @SuppressLint("LongLogTag")
    @Override
    public void onMessageReceived(String from, Bundle data) {
        final String message = data.getString("message");

        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);

//        sendNotification(message);
    }

    @Override
    public void onMessageSent(String msgId) {
        super.onMessageSent(msgId);
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    private void sendAppNotification(String msg) {
        try {
            NotificationManager mNotificationManager = (NotificationManager)
                    this.getSystemService(Context.NOTIFICATION_SERVICE);

            Intent myintent = new Intent(this, NotificationHandler.class);
            myintent.putExtra("message", msg);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                    myintent, PendingIntent.FLAG_CANCEL_CURRENT);

            JsonParser parser = new JsonParser();
            JsonObject parsedData = (JsonObject)parser.parse(msg);
            int NOTIFICATION_ID = parsedData.get("acme").getAsJsonArray().get(1).getAsInt();
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_stat_notify_vsee_in_call)
                            .setContentTitle(getResources().getString(R.string.mdl_app_name))
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(parsedData.get("aps").getAsJsonObject().get("alert").getAsString()))
                            .setContentText(parsedData.get("aps").getAsJsonObject().get("alert").getAsString()).setAutoCancel(true)
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
