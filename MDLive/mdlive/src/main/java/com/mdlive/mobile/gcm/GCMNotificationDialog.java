package com.mdlive.mobile.gcm;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.mdlive.mobile.R;

public class GCMNotificationDialog extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gcmnotification_dialog);
        if(getActionBar()!=null) {
            getActionBar().hide();
        }
        setTitle(getString(R.string.mdl_app_name));
        String message = getIntent().getStringExtra("Message");
        ((TextView)findViewById(R.id.MessageTv)).setText(message);
    }
}
