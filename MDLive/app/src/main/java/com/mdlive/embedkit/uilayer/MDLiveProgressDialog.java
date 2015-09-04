package com.mdlive.embedkit.uilayer;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.mdlive.embedkit.R;

/**
 * Created by dhiman_da on 9/4/2015.
 */
public class MDLiveProgressDialog extends ProgressDialog {
    public MDLiveProgressDialog(Context context) {
        super(context, R.style.MDLive_Progress_Dialog_Theme);
//        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        WindowManager.LayoutParams wlmp = getWindow().getAttributes();
        wlmp.gravity = Gravity.CENTER_HORIZONTAL;
        getWindow().setAttributes(wlmp);
        setTitle(null);
        setCancelable(false);
        setOnCancelListener(null);
        final View layout = getLayoutInflater().inflate(R.layout.mdlive_progress_dialog, null, false);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        addContentView(layout, params);
    }
}