package com.mdlive.mobile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.android.volley.VolleyError;
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
    private ProgressDialog mProgressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_splashscreen);

        mProgressDialog = MdliveUtils.getFullScreenProgressDialog(this);

        /* Pass 1 for Dev env,Pass 2 for QA env, Pass 3 for Stage env, Pass 4 for Prod env, 5 for Pluto*/
        MDLiveConfig.setData(3);

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

        UpgradeAlert service = new UpgradeAlert(SplashScreenActivity.this, mProgressDialog);
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
            intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        finish();
    }

    /**
     * Shows Install/ Later Alert dialog
     * */
    private void showLaterInstall(final String version) {
        MdliveUtils.showDialog(SplashScreenActivity.this,
                getString(R.string.app_name),
                getString(R.string.new_version, version),
                getString(R.string.install),
                getString(R.string.later),
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
        MdliveUtils.showDialog(SplashScreenActivity.this,
                getString(R.string.app_name),
                getString(R.string.new_version, version),
                getString(R.string.install),
                null,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(UpgradeAlert.APP_PLAY_STORE_URI)));
                    }
                }, null);
    }
}


