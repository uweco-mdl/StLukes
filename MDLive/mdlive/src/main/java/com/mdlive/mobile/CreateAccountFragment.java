package com.mdlive.mobile;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.mdlive.unifiedmiddleware.commonclasses.application.AppSpecificConfig;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.DeepLinkUtils;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.URI;
import java.net.URISyntaxException;
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
        } else if(loadUrl !=null && !loadUrl.isEmpty()){
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

                showProgressDialog();

                List<NameValuePair> params = null;
                try {
                    params = URLEncodedUtils.parse(new URI(url), "UTF-8");
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }

                for (NameValuePair param : params) {
                    if (param.getName().equals("remoteUserId")) {
                        if (getActivity() != null) {
                            SharedPreferences sharedPref = getActivity().getSharedPreferences(PreferenceConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString(PreferenceConstants.USER_UNIQUE_ID, param.getValue());
                            editor.commit();

                            if (mOnSignupSuccess != null) {
                                mOnSignupSuccess.onSignUpSucess();
                            }
                        }
                    }
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                hideProgressDialog();

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    mWebView.evaluateJavascript("javascript:getUserCredential('"+ username + "', '"+password+"');",null);
                } else {
                    mWebView.loadUrl("javascript:getUserCredential('"+ username + "', '"+password+"');",null);
                }
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mOnSignupSuccess = null;
    }

    public boolean canGoBack() {
        if (mWebView != null) {
            return mWebView.canGoBack();
        }

        return false;
    }

    public void goBack() {
        if (mWebView != null && mWebView.canGoBack()) {
            mWebView.goBack();
        }
    }

    public static interface OnSignupSuccess {
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
