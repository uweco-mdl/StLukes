package com.mdlive.ST_LUKES_mobile.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.mdlive.ST_LUKES_mobile.R;
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
            Log.e(TAG, "GCM Registration ID : " + regid);


            // The following methods provides token details
            InstanceID instanceID = InstanceID.getInstance(this);


            String token = instanceID.getToken(getString(R.string.mdl_application_gcm_project_id),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            Log.d(TAG, "GCM Registration Token: " + token);

            //MDLiveGCMPreference.MDLIVE_GCM_INSTANCE_ID = token;

            SharedPreferences settings = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, MODE_PRIVATE);
            settings.edit().putString(PreferenceConstants.SAVED_PUSH_NOTIFICATION_ID, token).commit();
        } catch (Exception e) {
            Log.e(TAG, "Failed to complete token refresh", e);
        }
    }
}
