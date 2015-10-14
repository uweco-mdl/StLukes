package com.mdlive.mobile.gcm;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mdlive.embedkit.uilayer.appointment.AppointmentActivity;
import com.mdlive.embedkit.uilayer.messagecenter.MessageCenterInboxDetailsActivity;
import com.mdlive.mobile.R;
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
    private void handleNewPushMessage(String message){
        try{
            JsonParser parser = new JsonParser();
            JsonObject originalPayload = parser.parse(message).getAsJsonObject();
            final UserBasicInfo userBasicInfo = UserBasicInfo.readFromSharedPreference(this);
            final Intent messageIntent = new Intent(this,
                    originalPayload.get("acme").getAsJsonArray().get(0).getAsString().equalsIgnoreCase("message") ?
                            MessageCenterInboxDetailsActivity.class : AppointmentActivity.class);
            messageIntent.putExtra("notification_id", originalPayload.get("acme").getAsJsonArray().get(1).getAsInt());

            if (userBasicInfo != null) {
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
