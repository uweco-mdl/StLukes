package com.mdlive.mobile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mdlive.unifiedmiddleware.commonclasses.constants.BroadcastConstant;

public class LoginBroadcastReceiver extends BroadcastReceiver {
    public LoginBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("LoginBroadCast", "Login Broadcast Called");

        if (BroadcastConstant.LOGIN_ACTION.equals(intent.getAction())) {
            Log.d("LoginBroadCast", "Action : " + intent.getAction());

            if (intent.hasExtra(BroadcastConstant.UNLOCK_FLAG)) {
                final int flag = intent.getIntExtra(BroadcastConstant.UNLOCK_FLAG, BroadcastConstant.SHOW_LOGIN);
                Log.d("LoginBroadCast", "Flag : " + flag);

                if (flag == BroadcastConstant.SHOW_LOCK) {
                    Log.d("LoginBroadCast", "Flag is Lock Screen");
                    final Intent unLockIntent = new Intent(context, UnlockActivity.class);
                    unLockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(unLockIntent);
                } else if (flag == BroadcastConstant.SHOW_LOGIN) {
                    Log.d("LoginBroadCast", "Flag is Login Screen");
                    context.startActivity(LoginActivity.getLockLoginIntnet(context));
                } else if (flag == BroadcastConstant.SHOW_LOGIN_AFTER_LOGOUT) {
                    Log.d("LoginBroadCast", "Flag is Login Screen after logout");
                    final Intent unLockIntent = new Intent(context, LoginActivity.class);
                    unLockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    unLockIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    unLockIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(unLockIntent);
                }
            }
        }
    }
}
