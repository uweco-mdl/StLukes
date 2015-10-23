package com.mdlive.mobile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.gson.Gson;
import com.mdlive.embedkit.global.MDLiveConfig;
import com.mdlive.embedkit.global.MDLiveConfig.ENVIRON;
import com.mdlive.embedkit.global.MDLiveConfig.SIGNALS;
import com.mdlive.embedkit.uilayer.login.MDLiveDashboardActivity;
import com.mdlive.mobile.gcm.MDLiveRegistrationIntentService;
import com.mdlive.unifiedmiddleware.commonclasses.constants.IntegerConstants;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.DeepLinkUtils;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.DeepLink;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.login.DeeplinkService;
import com.mdlive.unifiedmiddleware.services.login.SSOBaylorService;
import com.mdlive.unifiedmiddleware.services.login.UpgradeAlert;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by dhiman_da on 7/7/2015.
 */

public class SplashScreenActivity extends Activity {
    private String upgradeOption="", latestVersion = "";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private ProgressDialog mProgressDialog;
    ENVIRON env;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_splashscreen);
        this.setTitle(getString(R.string.mdl_application_splash_virtual_care));

        // listen for EmbedKit exit signal and respond accordingly
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, new IntentFilter(SIGNALS.EXIT_SIGNAL.name()));
        getTracker();
        mProgressDialog = MdliveUtils.getFullScreenProgressDialog(this);

        /* Select the environment type here : */
        env = ENVIRON.QA;
        // ******************************************
        MDLiveConfig.setData(env);
        ClearBaylorCache();
        registerGCMForMDLiveApplication();
        makeUpdateAlertCall();
    }
    private void ClearBaylorCache(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor baylorEditor = sharedPref.edit();
        baylorEditor.remove(PreferenceConstants.BAYLOR_GUID).commit();
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
     * Listener for the EmbedKit exit signal (LocalBroadcast).
     * Simply resume/reload this activity upon EmbedKit termination
     */
    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            Toast t = Toast.makeText(SplashScreenActivity.this,"< Received 'Finish' signal from EmbedKit >",Toast.LENGTH_SHORT);
            TextView v = (TextView) t.getView().findViewById(android.R.id.message);
            v.setTextColor(Color.CYAN);
            t.show();

            // relaunch current activity
            Intent intentRestart = new Intent(context, SplashScreenActivity.class);
            intentRestart.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intentRestart);
        }
    };

    /**
     *  Making Upgrade alert call
     * */
    private void makeUpdateAlertCall() {

        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (MdliveUtils.isHigherVersionPresent(response.optString("latest_version"), BuildConfig.VERSION_NAME)) {
                        upgradeOption = response.optString("upgrade");
                        latestVersion = response.optString("latest_version");
                    } else {
                        upgradeOption = "";
                        latestVersion = "";
                    }

                    if(!upgradeOption.equals("")){
                        if (upgradeOption.equalsIgnoreCase("force")) {
                            showInstall(latestVersion);
                            return;
                        } else {
                            showLaterInstall(latestVersion);
                            return;
                        }
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
                showInstall(latestVersion);
            } else {
                showLaterInstall(latestVersion);
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
            if(DeepLinkUtils.DEEPLINK_DATA != null && DeepLinkUtils.DEEPLINK_DATA.getAffiliate().equalsIgnoreCase(DeepLinkUtils.DeeplinkAffiliate.BAYLOR.name()))
            {
                intent = new Intent(getBaseContext(), MDLiveDashboardActivity.class);
                startActivity(intent);
            } else if (MdliveUtils.getPreferredLockType(getBaseContext()).equalsIgnoreCase("Pin")) {
                if (ShowPinScreen()) {
                    intent = UnlockActivity.getUnlockToDashBoardIntent(getBaseContext(), true);
                    startActivity(intent);
                } else {
                    intent = new Intent(getBaseContext(), MDLiveDashboardActivity.class);
                    startActivity(intent);
                }
            } else {
                intent = new Intent(this, LoginActivity.class);
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
        SharedPreferences settings = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, MODE_PRIVATE);
        if (checkPlayServices() && (settings.getString(PreferenceConstants.SAVED_PUSH_NOTIFICATION_ID, null) == null)) {
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

                if(DeepLinkUtils.DEEPLINK_DATA != null && DeepLinkUtils.DEEPLINK_DATA.getAffiliate().equalsIgnoreCase(DeepLinkUtils.DeeplinkAffiliate.BAYLOR.name()))
                {
                    // set flag indicating Baylor-affiliated user
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor baylorEditor = sharedPref.edit();
                    String baylor_guid = getIntent().getStringExtra(PreferenceConstants.BAYLOR_GUID);
                    if(baylor_guid!=null){
                        baylorEditor.putString(PreferenceConstants.BAYLOR_GUID, baylor_guid).commit();
                    }
                    // Call Ruby service to fetch the useruniqueID (i.e. the new RemoteUserID).
                    MakeBaylorSSOLogin();
                    return;     // break out at this point and allow async task to continue

                }else{
                    startNextActivity();
                }
                editor.commit();
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

    /**
     * Should show Pin screen or Not
     * */
    private boolean ShowPinScreen() {
        final SharedPreferences preferences = getSharedPreferences(PreferenceConstants.TIME_PREFERENCE, MODE_PRIVATE);
        final long lastTime = preferences.getLong(PreferenceConstants.TIME_KEY, System.currentTimeMillis());

        final long difference = System.currentTimeMillis() - lastTime;
        if (difference > IntegerConstants.SESSION_TIMEOUT) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Call the SSO service to auto login user if they come through baylor application
     * For baylor user the login and pin creation screens are not applicable
     * The user will be directed ot destination screen without any interruption
     */
    private void MakeBaylorSSOLogin() {

        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // success data handing
                if(!response.has("error")) {
                    try {
                        // For saving the REMOTE USER ID
                        SharedPreferences sharedPref = getSharedPreferences(PreferenceConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(PreferenceConstants.USER_UNIQUE_ID, response.getString("uniqueid"));
                        editor.commit();
                        IntegerConstants.SESSION_TIMEOUT = 30 * 60 * 1000; // 30 minutes timeout session for Baylor user
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    startNextActivity();
                }else{
                    BaylorSSOError();
                }
            }
        };
        NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                try {
                    BaylorSSOError();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };

        SSOBaylorService service = new SSOBaylorService(this, mProgressDialog);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String guid = sharedPref.getString(PreferenceConstants.BAYLOR_GUID, null);
        HashMap<String, String> postParam = new HashMap<>();
        postParam.put("user_guid", guid);
        postParam.put("affiliation_id", DeepLinkUtils.DEEPLINK_DATA.getAffiliationId()+"");

        service.BaylorSSO(successCallBackListener, errorListener, (new JSONObject(postParam)).toString());
    }

    /**
     * When the baylor sso integration fails to login then display this error and ask your to login again from baylor app
     * This current activity will be closed once the user hits ok button
     */
    private void BaylorSSOError(){
        DialogInterface.OnClickListener backToBaylor = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DeepLinkUtils.openBaylorApp(SplashScreenActivity.this);
                finish();
            }
        };
        MdliveUtils.showDialog(this,getString(com.mdlive.embedkit.R.string.mdl_app_name),getString(com.mdlive.embedkit.R.string.mdl_failed_baylor_login),getString(com.mdlive.embedkit.R.string.mdl_Ok),null,backToBaylor,null);
    }

    public static Tracker tracker = null;

    /**
     *
     * This function retrieves tracker with the corresponding google analytics config file.
     *
     * @return GoogleAnalytics Tracker
     */
    synchronized Tracker getTracker() {
        if (tracker == null) {
            GoogleAnalytics googleAnalytics = GoogleAnalytics.getInstance(this);
            googleAnalytics.enableAutoActivityReports(getApplication());
            tracker = googleAnalytics.newTracker(R.xml.analytics);
            tracker.enableExceptionReporting(false);
            final Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
            Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread thread, Throwable throwable) {
                    String stackTrace = Log.getStackTraceString(throwable);
                    tracker.send(new HitBuilders.ExceptionBuilder().setDescription("{ " + throwable.getMessage() + " } " + stackTrace).setFatal(true).build());
                    defaultUncaughtExceptionHandler.uncaughtException(thread, throwable);
                }
            });
        }
        return tracker;
    }
}


