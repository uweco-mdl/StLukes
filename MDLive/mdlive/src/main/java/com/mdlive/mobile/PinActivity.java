package com.mdlive.mobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.mdlive.embedkit.uilayer.login.MDLiveDashboardActivity;
import com.mdlive.mobile.ConfirmPinFragment.OnCreatePinSucessful;
import com.mdlive.mobile.CreatePinFragment.OnCreatePinCompleted;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;

/**
 * Created by dhiman_da on 8/21/2015.
 */
public class PinActivity extends AppCompatActivity implements OnCreatePinCompleted,
        OnCreatePinSucessful, FragmentManager.OnBackStackChangedListener {
    private static final String TAG = "TAG";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);
        setTitle("");

        getSupportFragmentManager().addOnBackStackChangedListener(this);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            showPinToolbar();
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.container, CreatePinFragment.newInstance(), TAG).
                    commit();
        }
    }

    @Override
    public void onBackPressed() {
        MdliveUtils.clearRemoteUserId(getApplicationContext());
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreatePinCompleted(String pin) {
        showConfirmPinToolbar();

        getSupportFragmentManager().
                beginTransaction().
                addToBackStack(TAG).
                add(R.id.container, ConfirmPinFragment.newInstance(pin), TAG).
                commit();
    }

    @Override
    public void onClickNoPin() {
        MdliveUtils.hideSoftKeyboard(this);
        MdliveUtils.setLockType(getBaseContext(), getString(R.string.mdl_password));
        startDashboard();
    }

    @Override
    public void startDashboard() {
        MdliveUtils.setLockType(getBaseContext(), getString(R.string.mdl_password));
        final Intent dashboard = new Intent(getBaseContext(), MDLiveDashboardActivity.class);
        startActivity(dashboard);
        finish();
    }

    /**
     * Called whenever the contents of the back stack change.
     */
    @Override
    public void onBackStackChanged() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            showPinToolbar();
        } else {
            showConfirmPinToolbar();
        }
    }

    public void onBackClicked(View view) {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            final Intent intent = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

    public void onTickClicked(View view) {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG);
        if (fragment != null) {
            String pin = null;

            if (fragment instanceof CreatePinFragment) {
                pin = ((CreatePinFragment) fragment).getEnteredPin();
                if (pin != null && pin.length() == 6) {
                    onCreatePinCompleted(pin);
                }
            } else if (fragment instanceof  ConfirmPinFragment) {
                pin = ((ConfirmPinFragment) fragment).getEnteredPin();
                if (pin != null && pin.length() == 6) {
                    ((ConfirmPinFragment) fragment).loadConfirmPin(pin);
                }
            }
        }
    }

    public void onTwoPasswordMismatch() {
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        getSupportFragmentManager().
                beginTransaction().
                add(R.id.container, CreatePinFragment.newInstance(), TAG).
                commit();
    }

    private void showPinToolbar() {
        ((TextView) findViewById(R.id.headerTxt)).setText(getString(R.string.mdl_create_a_pin).toUpperCase());
    }

    private void showConfirmPinToolbar() {
        ((TextView) findViewById(R.id.headerTxt)).setText(getString(R.string.mdl_confirm_your_pin).toUpperCase());
    }
}
