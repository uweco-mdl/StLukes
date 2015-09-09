package com.mdlive.mobile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.gson.Gson;
import com.mdlive.embedkit.global.MDLiveConfig;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.DeepLinkUtils;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.DeepLink;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.login.DeeplinkService;
import com.mdlive.unifiedmiddleware.services.login.UpgradeAlert;

import org.json.JSONObject;

/**
 * Created by dhiman_da on 7/7/2015.
 */

public class SplashScreenActivity extends Activity {
    private String upgradeOption="";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private ProgressDialog mProgressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_splashscreen);

        mProgressDialog = MdliveUtils.getFullScreenProgressDialog(this);

        /* Pass 1 for Dev env,Pass 2 for QA env, Pass 3 for Stage env, Pass 4 for Prod env, 5 for Pluto*/
        MDLiveConfig.setData(3);

        registerGCMForMDLiveApplication();
        makeUpdateAlertCall();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     *  Making Upgrade alert call
     * */
    private void makeUpdateAlertCall() {
//        mProgressDialog.show();

        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
//                mProgressDialog.dismiss();
                try {
                    if (MdliveUtils.isHigherVersionPresent(response.optString("latest_version"), BuildConfig.VERSION_NAME)) {
                        upgradeOption = response.optString("upgrade");
                    } else {
                        upgradeOption = "";
                    }

                    /**
                     * Calls Deeplink service
                     * */
                    makeDeeplinkCall();
                } catch (Exception e) {
                    /**
                     * Calls Deeplink service
                     * */
                    makeDeeplinkCall();
                }
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                /**
                 * Calls Deeplink service
                 * */
                makeDeeplinkCall();
            }
        };

        UpgradeAlert service = new UpgradeAlert(this, mProgressDialog);
        service.upgradeAlertService(successCallBackListener, errorListener, null);
    }

    /**
     * Show only Install or Install/Later option depending on upgrade options type
     * */
    private void startNextActivity(){
        if(!upgradeOption.equals("")){
            if (upgradeOption.equalsIgnoreCase("force")) {
                showInstall(upgradeOption);
            } else {
                showLaterInstall(upgradeOption);
            }
        } else {
            startNexActivity();
        }
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

    /**
     * Call the deeplink service to get the affiliate data if exists
     *
     * Check for the other service to finish if it finished already then redirect to next step (syncTaskCompleted)
     *
     * No error handling for the deeplink data been captured as this will halt the user being get into application
     *
     * Deeplink data stored in static variable since these data cleared when the application process killed.
     * since the deeplink data should be cleared when the application killed or restarted and behave as normal MDLIVE
     */
    private void makeDeeplinkCall() {
        /**
         * Clears the Deep link data, then loads then tries to load the new deeplink data
         * */
        DeepLinkUtils.DEEPLINK_DATA = null;

        /**
         * Getting the Deeplink id for the running device
         * */
        final String deeplinkId = DeepLinkUtils.getDeviceId(getBaseContext());

        /**
         * Saves the deeplink Id to shared preference, as it is needed as header for deep link url request
         * */
        final SharedPreferences preferences = getSharedPreferences(PreferenceConstants.DEVICE_ID, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PreferenceConstants.DEVICE_ID_KEY, deeplinkId);
        editor.commit();

        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                /**
                 * Clears the deeplink id from shared preference, as this header is not required anymore.
                 * And saves the deep link data
                 * */
                editor.clear().apply();
                editor.commit();
                if(!response.has("error")) {
                    final Gson gson = new Gson();
                    DeepLink deepLink = gson.fromJson(response.toString(), DeepLink.class);
                    DeepLinkUtils.DEEPLINK_DATA = deepLink;
                }

                startNextActivity();
            }
        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                /**
                 * Clears the deeplink id from shared preference, as this header is not required anymore.
                 * And strats the next activity
                 * */
                editor.clear().apply();
                editor.commit();
                startNextActivity();
            }
        };

        DeeplinkService service = new DeeplinkService(this, mProgressDialog);
        service.deeplinkService(successCallBackListener, errorListener, null, deeplinkId);
    }

}


