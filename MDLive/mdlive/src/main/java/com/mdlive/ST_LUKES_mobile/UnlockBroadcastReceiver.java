package com.mdlive.ST_LUKES_mobile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mdlive.unifiedmiddleware.commonclasses.constants.BroadcastConstant;

public class UnlockBroadcastReceiver extends BroadcastReceiver {
    public UnlockBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("UnlockBroadCast", "Action : " + intent.getAction());

        if (BroadcastConstant.UNLOCK_ACTION.equals(intent.getAction())) {
            if (intent.hasExtra(BroadcastConstant.UNLOCK_FLAG)) {
                final int flag = intent.getIntExtra(BroadcastConstant.UNLOCK_FLAG, BroadcastConstant.SHOW_LOCK);
                Log.d("UnlockBroadCast", "Flag : " + flag);

                if (flag == BroadcastConstant.SHOW_LOCK) {
                    Log.d("UnlockBroadCast", "Flag is Lock Screen");
                    final Intent unLockIntent = new Intent(context, UnlockActivity.class);
                    unLockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(unLockIntent);
                } else if (flag == BroadcastConstant.SHOW_LOGIN) {
                    Log.d("UnlockBroadCast", "Flag is Login Screen");
                    context.startActivity(LoginActivity.getLockLoginIntnet(context));
                }
            }
        }
    }
}
