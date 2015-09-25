package com.mdlive.mobile;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.login.GCMRegisterService;
import com.mdlive.unifiedmiddleware.services.login.HealthSystemServices;
import com.mdlive.unifiedmiddleware.services.login.LoginService;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import static android.widget.TextView.OnEditorActionListener;

/**
 * Created by dhiman_da on 7/15/2015.
 */

public class LoginFragment extends MDLiveBaseFragment{
    private OnLoginResponse mOnLoginResponse;

    private EditText mUserNameEditText = null;
    private EditText mPasswordEditText = null;
    private WebView mWebView;
    private RelativeLayout healthSystemContainerRl,headerRl;
    private ImageView healthSystemIv;
    private  TextView healthSystemTv;
    private FrameLayout loginContainerFl;
    private static final int SPLASH_TIME_OUT = 4000;

    public static LoginFragment newInstance() {
        final LoginFragment loginFragment = new LoginFragment();
        return loginFragment;
    }

    public LoginFragment() {
        super();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mOnLoginResponse = (OnLoginResponse) activity;
        } catch (ClassCastException cce) {
            logE("Login Fragment", "Exception : " + cce.getMessage());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mUserNameEditText = (EditText)view.findViewById(R.id.userName);
        mPasswordEditText = (EditText)view.findViewById(R.id.password);
        mWebView = (WebView) view.findViewById(R.id.webView);
        healthSystemContainerRl = (RelativeLayout) view.findViewById(R.id.health_system_container_rl);
        headerRl = (RelativeLayout) view.findViewById(R.id.login_header_rl);
        healthSystemIv = (ImageView) view.findViewById(R.id.health_system_niv);
        healthSystemTv = (TextView) view.findViewById(R.id.health_system_tv);
        loginContainerFl = (FrameLayout) view.findViewById(R.id.login_container_fl);
        mPasswordEditText.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    MdliveUtils.hideKeyboard(mPasswordEditText.getContext(), mPasswordEditText);
                    loginService();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mOnLoginResponse = null;
    }

    public void loginService() {
        final String userName = mUserNameEditText.getText().toString();
        final String password = mPasswordEditText.getText().toString();
        if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(password)) {
            try {
                JSONObject parent = new JSONObject();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("email", userName);
                jsonObject.put("password", password);
                parent.put("login", jsonObject);
                loadLoginService(parent.toString());
            } catch (JSONException e) {
                logE("Error", e.getMessage());
            }

        } else {
            if (getActivity() != null) {
                MdliveUtils.showDialog(getActivity(),getActivity().getString(R.string.mdl_app_name), getActivity().getString(R.string.mdl_please_enter_login_fileds));
            }
        }
    }

    private void loadLoginService(String params) {
        showProgressDialog();

        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("Login Response", response.toString());
                handleLoginSuccessResponse(response);
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
                MdliveUtils.handelVolleyErrorResponse(getActivity(), error, getProgressDialog());
            }
        };

        logD("Login", params.toString());

        LoginService service = new LoginService(getActivity(), getProgressDialog());
        service.login(successCallBackListener, errorListener, params);
    }

    private void handleLoginSuccessResponse(JSONObject response) {
        try {
            if(response.getString("msg").equalsIgnoreCase("Success")) {
                logD("Login", "Login Successful : " + response.toString().trim());

                if ("null".equalsIgnoreCase(response.getString("uniqueid")) || response.getString("uniqueid").equalsIgnoreCase(null) || (response.getString("uniqueid") == null)) {
                    MdliveUtils.showDialog(getActivity(),getActivity().getString(R.string.mdl_app_name), response.getString("token"));
                    return;
                }

                // For saving the device token
                MdliveUtils.saveDeviceToken(getActivity(), response.getString("token"));
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
//                editor.putString("Device_Token", response.getString("token"));
//                editor.commit();

                // For saving the REMOTE USER ID
                sharedPref = getActivity().getSharedPreferences(PreferenceConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
                editor = sharedPref.edit();
                editor.putString(PreferenceConstants.USER_UNIQUE_ID, response.getString("uniqueid"));
                editor.commit();

                if (MDLiveGCMPreference.MDLIVE_GCM_INSTANCE_ID != null
                        && MDLiveGCMPreference.MDLIVE_GCM_INSTANCE_ID.length() > 0) {
                    hideProgressDialog();
                    sendGCMInstanceId();
                } else {
                    if (MdliveUtils.getLockType(getActivity()).equalsIgnoreCase("password")) {
                        checkHealthServices();
                    } else {
                        hideProgressDialog();
                        if (mOnLoginResponse != null) {
                            mOnLoginResponse.onLoginSucess();
                        }
                    }
                   /* if (mOnLoginResponse != null) {
                        mOnLoginResponse.onLoginSucess();
                    }*/
                }
            } else {
                MdliveUtils.showDialog(getActivity(),getActivity().getString(R.string.mdl_app_name), response.getString("token"));
            }

        } catch (Exception e) {
            logE("Error", e.getMessage());
            MdliveUtils.showDialog(getActivity(), getActivity().getString(R.string.mdl_app_name), getActivity().getString(R.string.mdl_please_enter_valid_fileds));
        }
    }

    private void sendGCMInstanceId() {
        showProgressDialog();

        try {
            final SharedPreferences preferences = getActivity().getSharedPreferences(PreferenceConstants.DEVICE_OS, Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = preferences.edit();
            editor.putString(PreferenceConstants.DEVICE_OS_KEY, "Android");
            editor.commit();

            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("uuid", MDLiveGCMPreference.MDLIVE_GCM_INSTANCE_ID);
            jsonObject.put("device_type", "Android");

            NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    editor.clear().apply();
                    editor.commit();

                    logD("GCM", response.optString("message"));


                    if (MdliveUtils.getLockType(getActivity()).equalsIgnoreCase("password")) {
                        checkHealthServices();
                    } else {
                        hideProgressDialog();
                        if (mOnLoginResponse != null) {
                            mOnLoginResponse.onLoginSucess();
                        }
                    }
                }
            };

            NetworkErrorListener errorListener = new NetworkErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    editor.clear().apply();
                    editor.commit();

                    logD("GCM", "Not registered");

                    hideProgressDialog();

                    if (mOnLoginResponse != null) {
                        mOnLoginResponse.onLoginSucess();
                    }
                }
            };

            GCMRegisterService service = new GCMRegisterService(getActivity(), getProgressDialog());
            service.register(successCallBackListener, errorListener, jsonObject.toString());
        } catch (JSONException e) {

        }
    }

    public interface OnLoginResponse {
        void onLoginSucess();
    }


    /**
     * This function is used to check the health services associated with the user's location.
     */
    private void checkHealthServices() {
        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("Response", response.toString());
                if (response != null) {
                    mWebView.loadUrl(response.optString("screen_image"));
                    healthSystemTv.setText(response.optString("splash_screen_text"));
                    mWebView.getSettings().setLoadWithOverviewMode(true);
                    mWebView.getSettings().setUseWideViewPort(true);
                    mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
                    mWebView.getSettings().setBuiltInZoomControls(true);
                    mWebView.setWebViewClient(new WebViewClient() {

                        public void onPageFinished(WebView view, String url) {
                            hideProgressDialog();
                            headerRl.setVisibility(View.GONE);
                            loginContainerFl.setVisibility(View.GONE);
                            healthSystemContainerRl.setVisibility(View.VISIBLE);
                            mWebView.setVisibility(View.VISIBLE);
                            healthSystemIv.setVisibility(View.VISIBLE);
                            healthSystemTv.setVisibility(View.VISIBLE);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // This method will be executed once the timer is over
                                    // Start your app main activity
                                    if (mOnLoginResponse != null) {
                                        mOnLoginResponse.onLoginSucess();
                                    }
                                }
                            }, SPLASH_TIME_OUT);
                        }
                    });
                }
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
                if (mOnLoginResponse != null) {
                    mOnLoginResponse.onLoginSucess();
                }
            }
        };

        HealthSystemServices service = new HealthSystemServices(getActivity(), getProgressDialog());
        service.getHealthSystemsData(successCallBackListener, errorListener, getLocalIpAddress());
    }

    /**
     * This function will fetch the Ip Address of the device and send it back as a string value.
     *
     * @return
     */
    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String ip = Formatter.formatIpAddress(inetAddress.hashCode());
                        return ip;
                    }
                }
            }
        } catch (SocketException ex) {
        }
        return null;
    }
}
