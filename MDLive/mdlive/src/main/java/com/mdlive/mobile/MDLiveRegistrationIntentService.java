package com.mdlive.mobile;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;

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

        try {
            final GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
            String regid = gcm.register(getString(R.string.mdl_application_gcm_project_id));
            Log.d(TAG, "GCM Registration Token 1 : " + regid);

            /*
            The following methods provides token details
            InstanceID instanceID = InstanceID.getInstance(this);


            String token = instanceID.getToken(getString(R.string.mdl_application_gcm_project_id),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            Log.d(TAG, "GCM Registration Token: " + token);

            MDLiveGCMPreference.MDLIVE_GCM_INSTANCE_ID = token;
            */
            SharedPreferences settings = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, MODE_PRIVATE);
            settings.edit().putString(PreferenceConstants.SAVED_PUSH_NOTIFICATION_ID, regid).commit();
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
        }
    }
}
