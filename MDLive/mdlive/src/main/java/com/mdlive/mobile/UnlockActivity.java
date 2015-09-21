package com.mdlive.mobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.mdlive.embedkit.uilayer.login.MDLiveDashboardActivity;
import com.mdlive.mobile.CreateAccountFragment.OnSignupSuccess;
import com.mdlive.mobile.UnlockFragment.OnUnlockSucessful;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;

/**
 * Created by dhiman_da on 8/22/2015.
 */
public class UnlockActivity extends AppCompatActivity implements OnSignupSuccess, OnBackStackChangedListener, OnUnlockSucessful {
    private static final String GO_To_DASHBOARD = "go_to_dash_board";

    public static Intent getUnlockToDashBoardIntent(final Context context, final boolean goToDashboard) {
        final Intent intent = new Intent(context, UnlockActivity.class);
        intent.putExtra(GO_To_DASHBOARD, goToDashboard);

        return intent;
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
                    add(R.id.main_container, UnlockFragment.newInstance()).
                    commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            return ;
        } else {
            super.onBackPressed();
        }
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

        getSupportFragmentManager().
                beginTransaction().
                addToBackStack(TAG).
                add(R.id.main_container, ForgotPinFragment.newInstance(), TAG).
                commit();
    }

    public void onSignInClicked(View view) {
        final Intent intent = new Intent(getBaseContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
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
        final Intent intent = new Intent(getBaseContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showInitialToolbar() {
        findViewById(R.id.toolbar).setBackgroundColor(getResources().getColor(R.color.window_background_color));
        findViewById(R.id.toolbar_cross).setVisibility(View.GONE);
        findViewById(R.id.sign_up).setVisibility(View.VISIBLE);
        findViewById(R.id.forgot_pin).setVisibility(View.VISIBLE);
        findViewById(R.id.headerTxt).setVisibility(View.GONE);

    }

    private void showForgotPinToolbar() {
        findViewById(R.id.toolbar).setBackgroundColor(getResources().getColor(R.color.red_theme_primary));
        findViewById(R.id.toolbar_cross).setVisibility(View.VISIBLE);
        findViewById(R.id.sign_up).setVisibility(View.GONE);
        findViewById(R.id.forgot_pin).setVisibility(View.GONE);
        findViewById(R.id.headerTxt).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.headerTxt)).setText(getString(R.string.mdl_application_forgot_pin).toUpperCase());
    }

    private void clearMinimizedTime() {
        final SharedPreferences preferences = getSharedPreferences(PreferenceConstants.TIME_PREFERENCE, MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

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
        MdliveUtils.showDialog(this, getString(R.string.mdl_app_name), getString(R.string.mdl_pin_mismatch));
    }
}
