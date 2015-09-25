package com.mdlive.mobile;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import com.mdlive.embedkit.uilayer.login.MDLiveDashboardActivity;
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
    private static final String GO_To_DASHBOARD = "go_to_dash_board";

    public static Intent getLockLoginIntnet(final Context context) {
        final Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(UNLOCK_FLAG, 1);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    public static Intent getLoginToDashBoardIntent(final Context context, final boolean goToDashboard) {
        final Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(GO_To_DASHBOARD, goToDashboard);

        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LocalizationSingleton.localiseLayout(this,(ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0));
/*        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            setTitle("");
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }*/

        getSupportFragmentManager().addOnBackStackChangedListener(this);
        if (savedInstanceState == null) {
            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.container, LoginFragment.newInstance(), TAG).
                    commit();
        }
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
            final Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(AppSpecificConfig.URL_FORGOT_USERNAME));
            startActivity(intent);
        } catch (Exception e) {
            MdliveUtils.showDialog(this, getString(R.string.mdl_app_name), getString(R.string.mdl_no_compitable_app));
        }
    }

    public void onForgotPasswordClicked(View view) {
        try {
            final Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(AppSpecificConfig.URL_FORGOT_PASSWORD));
            startActivity(intent);
        } catch (Exception e) {
            MdliveUtils.showDialog(this, getString(R.string.mdl_app_name), getString(R.string.mdl_no_compitable_app));
        }
    }

    public void onSignInClicked(View view) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG);
        if(fragment != null && fragment instanceof LoginFragment) {
            ((LoginFragment)fragment).loginService();
        }
    }

    public void onCreateFreeAccountClicked(View view) {
        if(getSupportActionBar()!=null) {
            getSupportActionBar().hide();
        }

        getSupportFragmentManager().
                beginTransaction().
                addToBackStack(TAG).
                add(R.id.container, CreateAccountFragment.newInstance(), TAG).
                commit();
    }
    /* End Of Click listeners for Login Fragment*/

    @Override
    public void onLoginSucess() {
        if (getIntent() != null && getIntent().hasExtra(UNLOCK_FLAG) && getIntent().getExtras().getInt(UNLOCK_FLAG) == 1) {
            finish();
            return;
        }

        if (getIntent() != null && getIntent().hasExtra(GO_To_DASHBOARD) && getIntent().getExtras().getBoolean(GO_To_DASHBOARD)) {
            final Intent intent = new Intent(getBaseContext(), MDLiveDashboardActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        if (MdliveUtils.getLockType(this).equalsIgnoreCase("password")) {
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

    public void onActivateAccount(String activationUrl) {
        if(getSupportActionBar()!=null) {
            getSupportActionBar().hide();
        }
        getSupportFragmentManager().
                beginTransaction().
                addToBackStack(TAG).
                add(R.id.container, CreateAccountFragment.newInstance(activationUrl), TAG).
                commit();
    }
}
