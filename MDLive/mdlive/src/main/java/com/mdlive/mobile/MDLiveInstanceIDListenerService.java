package com.mdlive.mobile;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by dhiman_da on 9/7/2015.
 */
public class MDLiveInstanceIDListenerService extends InstanceIDListenerService {
    private static final String TAG = "MDLiveInstanceIDListenerService";

    @Override
    public void onTokenRefresh() {
        final Intent intent = new Intent(this, MDLiveRegistrationIntentService.class);
        startService(intent);
    }
}
