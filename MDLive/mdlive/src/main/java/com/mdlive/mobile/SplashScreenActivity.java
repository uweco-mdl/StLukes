package com.mdlive.mobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.mdlive.embedkit.global.MDLiveConfig;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;

/**
 * Created by dhiman_da on 7/7/2015.
 */

public class SplashScreenActivity extends Activity {
    private Handler mHandler;
    private Runnable mRunnable;
    private static final int SPLASH_TIME_OUT = 5000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_splashscreen);

        /* Pass 1 for Dev env,Pass 2 for QA env, Pass 3 for Stage env, Pass 4 for Prod env, 5 for Pluto*/
        MDLiveConfig.setData(3);

        mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                startNexActivity();
            }
        };
        mHandler.postDelayed(mRunnable, SPLASH_TIME_OUT);
    }

    @Override
    public void onResume() {
        super.onResume();

        mHandler.postDelayed(mRunnable, SPLASH_TIME_OUT);
    }

    @Override
    public void onPause() {
        super.onPause();

        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mHandler = null;
        mRunnable = null;
    }

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
}


