package com.mdlive.ST_LUKES_mobile;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.mdlive.embedkit.uilayer.login.MDLiveDashboardActivity;
import com.mdlive.ST_LUKES_mobile.CreateAccountFragment.OnSignupSuccess;
import com.mdlive.ST_LUKES_mobile.UnlockFragment.OnUnlockSucessful;
import com.mdlive.unifiedmiddleware.commonclasses.constants.BroadcastConstant;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;

/**
 * Created by dhiman_da on 8/22/2015.
 */
public class UnlockActivity extends AppCompatActivity implements OnSignupSuccess, OnBackStackChangedListener, OnUnlockSucessful {

    private static final String GO_To_DASHBOARD = "go_to_dash_board";
    private boolean isForgetPinCalled = false;

    public static Intent getUnlockToDashBoardIntent(final Context context, final boolean goToDashboard) {
        final Intent intent = new Intent(context, UnlockActivity.class);
        intent.putExtra(GO_To_DASHBOARD, goToDashboard);
        return intent;
    }


    private Handler mHandler;
    @Override
    public void onPause() {
        super.onPause();

        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }



    public static final String TAG = "UNLOCK";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock);
        clearMinimizedTime();
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        showInitialToolbar();

        getSupportFragmentManager().addOnBackStackChangedListener(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.main_container, UnlockFragment.newInstance(), TAG).
                    commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
        } else {
            if(isForgetPinCalled){
                super.onBackPressed();
            }else{
                onSignoutClicked(this);
            }
        }
    }

    public static void onSignoutClicked(Activity activityInstance) {
        MdliveUtils.clearNecessarySharedPrefernces(activityInstance);
        final Intent intent = new Intent();
        intent.setAction(BroadcastConstant.LOGIN_ACTION);
        intent.putExtra(BroadcastConstant.UNLOCK_FLAG, BroadcastConstant.SHOW_LOGIN_AFTER_LOGOUT);
        activityInstance.sendBroadcast(intent);
        activityInstance.finish();
    }


    public void onBackClicked(View view) {
        onBackPressed();
    }

    public void onSignUpClicked(View view) {
        getSupportActionBar().hide();
        getSupportFragmentManager().
                beginTransaction().
                addToBackStack(TAG).
                add(R.id.main_container, CreateAccountFragment.newInstance(), TAG).
                commit();
    }

    public void onForgotPinClicked(View view) {
        showForgotPinToolbar();
        isForgetPinCalled = true;
        getSupportFragmentManager().
                beginTransaction().
                addToBackStack(TAG).
                add(R.id.main_container, ForgotPinFragment.newInstance(), TAG).
                commit();
    }

    public void onSignInClicked(View view) {
        onSignoutClicked(this);
        /*final Intent intent = new Intent(getBaseContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);*/
    }

    /**
     * Called whenever the contents of the back stack change.
     */
    @Override
    public void onBackStackChanged() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            getSupportActionBar().show();
            showInitialToolbar();
        }
    }

    @Override
    public void onSignUpSucess() {
        final Intent intent = new Intent(getBaseContext(), PinActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void onResetPinClicked(View view) {
        onSignoutClicked(this);
    }

    private void showInitialToolbar() {
        //findViewById(R.id.toolbar).setBackgroundColor(getResources().getColor(R.color.window_background_color));
        findViewById(R.id.toolbar_cross).setVisibility(View.GONE);
        findViewById(R.id.sign_up).setVisibility(View.VISIBLE);
        findViewById(R.id.forgot_pin).setVisibility(View.VISIBLE);
        findViewById(R.id.headerTxt).setVisibility(View.GONE);

    }

    private void showForgotPinToolbar() {
        findViewById(R.id.toolbar).setBackgroundColor(getResources().getColor(R.color.darker_gray));
        findViewById(R.id.toolbar_cross).setVisibility(View.VISIBLE);
        findViewById(R.id.sign_up).setVisibility(View.GONE);
        findViewById(R.id.forgot_pin).setVisibility(View.GONE);
        findViewById(R.id.headerTxt).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.headerTxt)).setText(getString(R.string.mdl_application_forgot_pin).toUpperCase());
    }

    private void clearMinimizedTime() {
        if (mHandler == null) {
            mHandler = new Handler();
        }

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                final SharedPreferences preferences = getSharedPreferences(PreferenceConstants.TIME_PREFERENCE, MODE_PRIVATE);
                final SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.commit();
                Log.d("Timer", "clear called");
            }
        }, 1000);
    }

  /*  private void clearMinimizedTime() {
        final SharedPreferences preferences = getSharedPreferences(PreferenceConstants.TIME_PREFERENCE, MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }*/

    @Override
    public void onUnlockSuccesful() {
        if (getIntent().hasExtra(GO_To_DASHBOARD) && getIntent().getBooleanExtra(GO_To_DASHBOARD, false)) {
            startActivity(new Intent(getBaseContext(), MDLiveDashboardActivity.class));
            finish();
        } else {
            finish();
        }
    }

    @Override
    public void onUnlockUnSuccesful() {
        DialogInterface.OnClickListener tryAgain = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG);
                if (fragment != null && fragment instanceof  UnlockFragment) {
                    ((UnlockFragment) fragment).clearPincode();
                }
            }
        };
        DialogInterface.OnClickListener reSet = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MdliveUtils.clearNecessarySharedPrefernces(getApplicationContext());
                final Intent intent = new Intent();
                intent.setAction(BroadcastConstant.LOGIN_ACTION);
                intent.putExtra(BroadcastConstant.UNLOCK_FLAG, BroadcastConstant.SHOW_LOGIN_AFTER_LOGOUT);
                sendBroadcast(intent);
                finish();
            }
        };
        MdliveUtils.showDialog(this, getString(R.string.mdl_app_name), getString(R.string.mdl_incorrect_pin_message), getString(R.string.mdl_try_again),
                getString(R.string.mdl_reset), tryAgain, reSet);
    }
}
