package com.mdlive.mobile;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

/**
 * Created by dhiman_da on 9/7/2015.
 */
public class MDLiveRegistrationIntentService extends IntentService {
    private static final String TAG = "RegIntentService";

    public MDLiveRegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            final InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.mdl_application_gcm_project_id),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            Log.d(TAG, "GCM Registration Token: " + token);
            sendRegistrationToMDLiveServer(token);

            sharedPreferences.edit().putBoolean(MDLiveGCMPreference.MDLIVE_SENT_TOKEN_TO_SERVER, true).apply();
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            sharedPreferences.edit().putBoolean(MDLiveGCMPreference.MDLIVE_SENT_TOKEN_TO_SERVER, false).apply();
        }

        Intent registrationComplete = new Intent(MDLiveGCMPreference.MDLIVE_REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendRegistrationToMDLiveServer(final String token) {

    }
}
