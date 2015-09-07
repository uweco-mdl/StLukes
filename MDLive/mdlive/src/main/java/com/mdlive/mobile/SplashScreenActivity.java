package com.mdlive.mobile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.mdlive.embedkit.global.MDLiveConfig;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.login.UpgradeAlert;

import org.json.JSONObject;

/**
 * Created by dhiman_da on 7/7/2015.
 */

public class SplashScreenActivity extends Activity {
    private static final String TAG = "SplashScreenActivity";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private ProgressDialog mProgressDialog;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_splashscreen);

        mProgressDialog = MdliveUtils.getFullScreenProgressDialog(this);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                final boolean sentToken = sharedPreferences
                        .getBoolean(MDLiveGCMPreference.MDLIVE_SENT_TOKEN_TO_SERVER, false);

                Log.d(TAG, "Token Sent to MDLive : " + sentToken);
            }
        };

        /* Pass 1 for Dev env,Pass 2 for QA env, Pass 3 for Stage env, Pass 4 for Prod env, 5 for Pluto*/
        MDLiveConfig.setData(3);

        registerGCMForMDLiveApplication();
        makeUpdateAlertCall();
    }

    @Override
    public void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(MDLiveGCMPreference.MDLIVE_REGISTRATION_COMPLETE));
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     *  Making Upgrad alert call
     * */
    private void makeUpdateAlertCall() {
        mProgressDialog.show();

        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mProgressDialog.dismiss();

                try {
                    if (MdliveUtils.isHigherVersionPresent(response.optString("latest_version"), BuildConfig.VERSION_NAME)) {
                        if (response.optString("upgrade").equalsIgnoreCase("force")) {
                            showInstall(response.optString("latest_version"));
                        } else {
                            showLaterInstall(response.optString("latest_version"));
                        }
                    } else {
                        startNexActivity();
                    }
                } catch (Exception e) {
                    startNexActivity();
                }
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.hide();
                MdliveUtils.handelVolleyErrorResponse(SplashScreenActivity.this, error, mProgressDialog);
            }
        };

        UpgradeAlert service = new UpgradeAlert(this, mProgressDialog);
        service.upgradeAlertService(successCallBackListener, errorListener, null);
    }

    /**
     * Start the necessary activity on MDLive Application
     * */
    private void startNexActivity() {
        Intent intent = null;

        if (MdliveUtils.getRemoteUserId(getBaseContext()).length() > 0) {
            if (MdliveUtils.getLockType(getBaseContext()).equalsIgnoreCase("Pin")) {
                intent = UnlockActivity.getUnlockToDashBoardIntent(getBaseContext(), true);
                startActivity(intent);
            } else {
                intent = LoginActivity.getLoginToDashBoardIntent(getBaseContext(), true);
                startActivity(intent);
            }
        } else {
            intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        finish();
    }

    /**
     * Shows Install/ Later Alert dialog
     * */
    private void showLaterInstall(final String version) {
        MdliveUtils.showDialog(this,
                getString(R.string.mdl_app_name),
                getString(R.string.mdl_application_new_version, version),
                getString(R.string.mdl_application_install),
                getString(R.string.mdl_application_later),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(UpgradeAlert.APP_PLAY_STORE_URI)));
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startNexActivity();
                    }
                });
    }

    /**
     * Shows Install Alert dialog
     * */
    private void showInstall(final String version) {
        MdliveUtils.showDialog(this,
                getString(R.string.mdl_app_name),
                getString(R.string.mdl_application_new_version, version),
                getString(R.string.mdl_application_install),
                null,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(UpgradeAlert.APP_PLAY_STORE_URI)));
                    }
                }, null);
    }

    /**
     * Start the GCM registration service
     * */
    private void registerGCMForMDLiveApplication() {
        if (checkPlayServices()) {
            final Intent intent = new Intent(this, MDLiveRegistrationIntentService.class);
            startService(intent);
        }
    }

    /**
     * Checks if proper version of google play service is installed or not.
     * If need to update the google play services, then promts the user with
     * Google Play services update dialog
     *
     * If Google play services is not installed then shuts down the application
     * */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                finish();
            }
            return false;
        }
        return true;
    }
}


