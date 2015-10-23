package com.mdlive.mobile.gcm;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.appointment.AppointmentActivity;
import com.mdlive.embedkit.uilayer.messagecenter.MessageCenterInboxDetailsActivity;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.UserBasicInfo;

public class GCMNotificationDialog extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            JsonParser parser = new JsonParser();
            final JsonObject originalPayload = parser.parse(getIntent().getStringExtra("message")).getAsJsonObject();
            final UserBasicInfo userBasicInfo = UserBasicInfo.readFromSharedPreference(this);
            final Intent messageIntent = new Intent(this,
                    originalPayload.get("acme").getAsJsonArray().get(0).getAsString().equalsIgnoreCase("message") ?
                            MessageCenterInboxDetailsActivity.class : AppointmentActivity.class);
            messageIntent.putExtra("notification_id", originalPayload.get("acme").getAsJsonArray().get(1).getAsInt());

            // Can start the dialog activity but that clear all the existing activity and shows only the dialog.
            // Hence this approach dropped

            final DialogInterface.OnClickListener positive = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (userBasicInfo != null) {
                        messageIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        messageIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        messageIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(messageIntent);
                    }
                    dialog.dismiss();
                }
            };
            final DialogInterface.OnClickListener negtive = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            };
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    this);
            alertDialogBuilder
                    .setTitle("MDLIVE")
                    .setMessage(originalPayload.get("alert").getAsString())
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.mdl_Ok), positive);

            alertDialogBuilder.setNegativeButton(getString(R.string.mdl_cancel), negtive);
            final AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
