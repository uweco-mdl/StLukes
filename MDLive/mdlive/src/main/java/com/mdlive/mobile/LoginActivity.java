package com.mdlive.mobile;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.mdlive.embedkit.uilayer.login.MDLiveDashboardActivity;
import com.mdlive.embedkit.uilayer.sav.LocationCooridnates;
import com.mdlive.mobile.CreateAccountFragment.OnSignupSuccess;
import com.mdlive.mobile.LoginFragment.OnLoginResponse;
import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.commonclasses.application.LocalizationSingleton;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;

/**
 * Created by dhiman_da on 7/22/2015.
 */

public class LoginActivity extends AppCompatActivity implements OnLoginResponse,
        OnSignupSuccess, OnBackStackChangedListener {
    public static final String TAG = "LOGIN";
    private static final String UNLOCK_FLAG = "UNLOCK_FLAG";

    public static Intent getLockLoginIntnet(final Context context) {
        final Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(UNLOCK_FLAG, 1);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_login);
            this.setTitle(TAG);

            LocalizationSingleton.localiseLayout(this, (ViewGroup) ((ViewGroup) this
                    .findViewById(android.R.id.content)).getChildAt(0));

            getSupportFragmentManager().addOnBackStackChangedListener(this);
            if (savedInstanceState == null) {
                getSupportFragmentManager().
                        beginTransaction().
                        add(R.id.container, LoginFragment.newInstance(), TAG).
                        commit();
            }
            LocationCooridnates locationService = new LocationCooridnates(LoginActivity.this);
            if (!locationService.checkLocationServiceSettingsEnabled(getApplicationContext())) {
                showSettingsAlert();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginActivity.this);
        // Setting Dialog Title
        alertDialog.setTitle("");
        // Setting Dialog Message
        alertDialog.setMessage(getString(R.string.mdl_gps_settings));
        // On pressing Settings button
        alertDialog.setPositiveButton(getString(R.string.mdl_settings), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton(getString(R.string.mdl_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.setCancelable(true);
        // Showing Alert Message
        alertDialog.show();
    }
    @Override
    public void onBackPressed() {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG);
        if (fragment != null && fragment instanceof  CreateAccountFragment) {
            if (((CreateAccountFragment) fragment).canGoBack()) {
                ((CreateAccountFragment) fragment).goBack();
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    public void joinNowAction(View view) {
        getSupportFragmentManager().beginTransaction().addToBackStack(TAG).replace(R.id.container, LoginFragment.newInstance(), TAG).commit();
    }

    /* Start Of Click listeners for Login Fragment*/
    public void onForgotUserNameClicked(View view) {
        try {
            MdliveUtils.hideSoftKeyboard(this);
            final Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(AppSpecificConfig.URL_FORGOT_USERNAME));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                MdliveUtils.showDialog(this, getString(R.string.mdl_app_name), getString(R.string.mdl_no_compitable_app));
            }
        } catch (Exception e) {
            MdliveUtils.showDialog(this, getString(R.string.mdl_app_name), getString(R.string.mdl_no_compitable_app));
        }
    }

    public void onForgotPasswordClicked(View view) {
        try {
            MdliveUtils.hideSoftKeyboard(this);
            final Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(AppSpecificConfig.URL_FORGOT_PASSWORD));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                MdliveUtils.showDialog(this, getString(R.string.mdl_app_name), getString(R.string.mdl_no_compitable_app));
            }
        } catch (Exception e) {
            MdliveUtils.showDialog(this, getString(R.string.mdl_app_name), getString(R.string.mdl_no_compitable_app));
        }
    }

    public void onSignInClicked(View view) {
        MdliveUtils.hideSoftKeyboard(this);
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG);
        if(fragment != null && fragment instanceof LoginFragment) {
            ((LoginFragment)fragment).loginService();
            ((LoginFragment)fragment).clearFocus();
        }
    }

    public void onCreateFreeAccountClicked(View view) {
        MdliveUtils.hideSoftKeyboard(this);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().hide();
        }

        getSupportFragmentManager().beginTransaction().addToBackStack(TAG).add(R.id.container, CreateAccountFragment.newInstance(), TAG).
                commit();
    }
    /* End Of Click listeners for Login Fragment*/

    @Override
    public void onLoginSucess() {

        Log.e("pin or password" , MdliveUtils.getLockType(this).equalsIgnoreCase(getString(com.mdlive.embedkit.R.string.mdl_password))+"");
        if (MdliveUtils.getLockType(this).equalsIgnoreCase(getString(com.mdlive.embedkit.R.string.mdl_password))) {
            final Intent intent = new Intent(getBaseContext(), MDLiveDashboardActivity.class);
            startActivity(intent);
        } else {
            final Intent intent = new Intent(getBaseContext(), PinActivity.class);
            startActivity(intent);
        }

        finish();
    }

    @Override
    public void onSignUpSucess() {
        final Intent intent = new Intent(getBaseContext(), PinActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackStackChanged() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            if(getSupportActionBar()!=null) {
                getSupportActionBar().show();
            }
        }
    }

    public void onActivateAccount(String activationUrl, String username, String password) {
        if(getSupportActionBar()!=null) {
            getSupportActionBar().hide();
        }
        getSupportFragmentManager().
                beginTransaction().
                addToBackStack(TAG).
                add(R.id.container, CreateAccountFragment.newInstance(activationUrl,username, password), TAG).
                commit();
    }
}
