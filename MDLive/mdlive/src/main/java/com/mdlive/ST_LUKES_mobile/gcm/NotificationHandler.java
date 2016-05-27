package com.mdlive.ST_LUKES_mobile.gcm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mdlive.embedkit.uilayer.appointment.AppointmentActivity;
import com.mdlive.embedkit.uilayer.messagecenter.MessageCenterInboxDetailsActivity;
import com.mdlive.ST_LUKES_mobile.LoginActivity;
import com.mdlive.ST_LUKES_mobile.R;
import com.mdlive.ST_LUKES_mobile.UnlockActivity;
import com.mdlive.unifiedmiddleware.commonclasses.constants.IntegerConstants;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.UserBasicInfo;

public class NotificationHandler extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_handler);
//        MdliveUtils.getFullScreenProgressDialog(this).show();
        handleNewPushMessage(getIntent().getStringExtra("message"));
    }
    /**
     * Should show Pin screen or Not
     * */
    private boolean ShowPinScreen() {
        final SharedPreferences preferences = getSharedPreferences(PreferenceConstants.TIME_PREFERENCE, MODE_PRIVATE);
        final long lastTime = preferences.getLong(PreferenceConstants.TIME_KEY, System.currentTimeMillis());

        final long difference = System.currentTimeMillis() - lastTime;
        return difference > IntegerConstants.SESSION_TIMEOUT;
    }
    private void handleNewPushMessage(String message){
        try{
            if (MdliveUtils.getRemoteUserId(getBaseContext()).length() > 0) {
                if (MdliveUtils.getPreferredLockType(getBaseContext()).equalsIgnoreCase("Pin") && ShowPinScreen()) {
                    Intent intent = UnlockActivity.getUnlockToDashBoardIntent(getBaseContext(), true);
                    startActivity(intent);
                } else {
                    JsonParser parser = new JsonParser();
                    JsonObject originalPayload = parser.parse(message).getAsJsonObject();
                    final UserBasicInfo userBasicInfo = UserBasicInfo.readFromSharedPreference(this);
                    Intent messageIntent = new Intent(this,
                            originalPayload.get("acme").getAsJsonArray().get(0).getAsString().equalsIgnoreCase("message") ?
                                    MessageCenterInboxDetailsActivity.class : AppointmentActivity.class);
                    if (userBasicInfo != null) {
                        messageIntent.putExtra("notification_id", originalPayload.get("acme").getAsJsonArray().get(1).getAsInt());
                        messageIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        messageIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(messageIntent);
                    }
                }
            }else{
                Intent messageIntent = new Intent(this, LoginActivity.class);
                messageIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                messageIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(messageIntent);
            }


            finish();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
