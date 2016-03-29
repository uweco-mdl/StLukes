package com.mdlive.mobile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.MailTo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.DeepLinkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Created by dhiman_da on 8/12/2015.
 */
public class CreateAccountFragment extends MDLiveBaseFragment {
    private OnSignupSuccess mOnSignupSuccess;

    private WebView mWebView;
    private String loadUrl;
    private String username;
    private String password;

    public static CreateAccountFragment newInstance() {
        final CreateAccountFragment fragment = new CreateAccountFragment();
        return fragment;
    }

    public static CreateAccountFragment newInstance(String url, String username, String password) {
        final CreateAccountFragment fragment = new CreateAccountFragment();
        fragment.setLoadUrl(url);
        fragment.setUsername(username);
        fragment.setPassword(password);
        return fragment;
    }

    public CreateAccountFragment() {
        super();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mOnSignupSuccess = (OnSignupSuccess) activity;
        } catch (ClassCastException cce) {
            logE("CreateAccountFragment", cce.getMessage());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_createaccount, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mWebView = (WebView) view.findViewById(R.id.webView);

        /**
         * Loads Deeplink url is deeplink is available otherwise
         * loads the standard  sign up url
         * */
        if (DeepLinkUtils.DEEPLINK_DATA != null && !DeepLinkUtils.DEEPLINK_DATA.getRegistrationUrl().isEmpty()) {
            mWebView.loadUrl(DeepLinkUtils.DEEPLINK_DATA.getRegistrationUrl());
        } else if (loadUrl != null && !loadUrl.isEmpty()) {
            mWebView.loadUrl(loadUrl);
            mWebView.getSettings().setLoadWithOverviewMode(true);
            mWebView.getSettings().setUseWideViewPort(true);
            mWebView.getSettings().setBuiltInZoomControls(true);
            mWebView.getSettings().setJavaScriptEnabled(true);
        } else {

            mWebView.loadUrl(AppSpecificConfig.URL_SIGN_UP);
        }
        mWebView.getSettings().setJavaScriptEnabled(true);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                //List<NameValuePair> params = null;
                //Replaced Deprecated classes with new class.
                try {
                    URL activationUrl = new URL(url);
                    String query = activationUrl.getQuery();
                    if (null != query) {
                        String[] pairs = query.split("&");
                        for (String pair : pairs) {
                            if (pair.startsWith("remoteUserId=")) {
                                String desiredString = pair.replace("remoteUserId=", "");
                                if (getActivity() != null) {
                                    SharedPreferences sharedPref = getActivity().getSharedPreferences(PreferenceConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPref.edit();
                                    editor.putString(PreferenceConstants.USER_UNIQUE_ID, desiredString);
                                    editor.commit();

                                    if (mOnSignupSuccess != null) {
                                        // This change fixes issues reported in MM-2549.
                                        UnlockActivity.onSignoutClicked(getActivity());
                                        //mOnSignupSuccess.onSignUpSucess();
                                    }

                                }
                                break;
                            }
                            //params = URLEncodedUtils.parse(new URI(url), "UTF-8");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

//                for (NameValuePair param : params) {
//                    if (param.getName().equals("remoteUserId")) {
//                        if (getActivity() != null) {
//                            SharedPreferences sharedPref = getActivity().getSharedPreferences(PreferenceConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
//                            SharedPreferences.Editor editor = sharedPref.edit();
//                            editor.putString(PreferenceConstants.USER_UNIQUE_ID, param.getValue());
//                            editor.commit();
//
//                            if (mOnSignupSuccess != null) {
//                                mOnSignupSuccess.onSignUpSucess();
//                            }
//                        }
//                    }
//                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    mWebView.evaluateJavascript("javascript:getUserCredential('" + username + "', '" + password + "');", null);
                } else {
                    mWebView.loadUrl("javascript:getUserCredential('" + username + "', '" + password + "');", null);
                }
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith(WebView.SCHEME_TEL)) {
                    // Otherwise allow the OS to handle things like tel, mailto, etc.
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return true;
                } else if (url.startsWith("mailto:")) {
                    MailTo mt = MailTo.parse(url);
                    Intent i = newEmailIntent(getActivity(), mt.getTo(), mt.getSubject(), mt.getBody(), mt.getCc());
                    getActivity().startActivity(i);
                    view.reload();
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
    }

    private Intent newEmailIntent(Context context, String address, String subject, String body, String cc) {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{address});
        intent.putExtra(Intent.EXTRA_TEXT, body);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_CC, cc);
        intent.setType("message/rfc822");

        final PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, 0);

        if (list.size() > 0) {
            return intent;
        } else {
            return Intent.createChooser(intent, getActivity().getString(R.string.choose_mail_client));
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mOnSignupSuccess = null;
    }

    public boolean canGoBack() {
        if (mWebView != null) {
            Log.d("URL", mWebView.getUrl());
            return !(mWebView.getUrl().contains("get_eligibilty_details") || mWebView.getUrl().contains("get_non270_details")) && mWebView.canGoBack();
        }

        return false;
    }

    public void goBack() {
        if (mWebView != null && mWebView.canGoBack()) {
            mWebView.goBack();
        }
    }

    public interface OnSignupSuccess {
        void onSignUpSucess();
    }

    public String getLoadUrl() {
        return loadUrl;
    }

    public void setLoadUrl(String loadUrl) {
        this.loadUrl = loadUrl;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
