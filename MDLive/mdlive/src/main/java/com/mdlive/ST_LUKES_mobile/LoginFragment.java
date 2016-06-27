package com.mdlive.ST_LUKES_mobile;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.BuildConfig;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.VideoView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.mdlive.embedkit.global.MDLiveConfig;
import com.mdlive.unifiedmiddleware.commonclasses.application.ApplicationController;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.DeepLinkUtils;
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

public class LoginFragment extends MDLiveBaseFragment {
    private OnLoginResponse mOnLoginResponse;

    private EditText mUserNameEditText = null;
    private EditText mPasswordEditText = null;
    private ImageView mWebView;
    private RelativeLayout healthSystemContainerRl, headerRl;
    private ImageView healthSystemIv;
    private TextView healthSystemTv;
    private FrameLayout loginContainerFl;
    private String screenImageURL;
    private static final int SPLASH_TIME_OUT = 4000;
    private String footerImageURL;
    private ImageView mHeaderIv;
    //public ScrollView mContainer;
    private VideoView mVideo;
    private CheckBox mRememberMe;
    private Spinner mEnvironment = null;
    private String applicationId;
    private boolean isMDlive;


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
        applicationId = BuildConfig.APPLICATION_ID;
        if (applicationId.equals("com.mdlive.ST_LUKES_mobile")) {
            isMDlive = true;
        }
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getWindow().setFormat(PixelFormat.UNKNOWN);

        mUserNameEditText = (EditText) view.findViewById(R.id.userName);
        mPasswordEditText = (EditText) view.findViewById(R.id.password);
        mWebView = (ImageView) view.findViewById(R.id.webView);
        //mContainer = (ScrollView) view.findViewById(R.id.LoginContainer);
        healthSystemContainerRl = (RelativeLayout) view.findViewById(R.id.health_system_container_rl);
        headerRl = (RelativeLayout) view.findViewById(R.id.login_header_rl);
        healthSystemIv = (ImageView) view.findViewById(R.id.health_system_niv);
        healthSystemTv = (TextView) view.findViewById(R.id.health_system_tv);
        loginContainerFl = (FrameLayout) view.findViewById(R.id.login_container_fl);
        if (isMDlive) {
            mVideo = (VideoView) view.findViewById(R.id.welcomeVideo);
        }

        mRememberMe = (CheckBox) view.findViewById(R.id.remember_me);
        mEnvironment = (Spinner) view.findViewById(R.id.environmentSpinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.environment_spinner, getResources().getStringArray(R.array.environments));

        mEnvironment.setAdapter(adapter);


        mPasswordEditText.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    MdliveUtils.hideKeyboard(mPasswordEditText.getContext(), mPasswordEditText);
                    clearFocus();
                    loginService();

                    return true;
                }
                return false;
            }
        });
        mHeaderIv = (ImageView) view.findViewById(R.id.headerLogoIv);
        mHeaderIv.setVisibility(View.GONE);

        // MDLMO-2812 - This fixes video vertical rendering issues.
        getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        if (DeepLinkUtils.DEEPLINK_DATA != null && DeepLinkUtils.DEEPLINK_DATA.getAffiliationLogoUrl() != null) {
            final ImageLoader imageLoader = ApplicationController.getInstance().getImageLoader(getActivity());
            ImageLoader.ImageListener iListener = new ImageLoader.ImageListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    hideProgressDialog();
                    mHeaderIv.setVisibility(View.VISIBLE);
                }

                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {
                    if (response.getBitmap() != null) {
                        hideProgressDialog();
                        mHeaderIv.setImageBitmap(response.getBitmap());
                        mHeaderIv.setVisibility(View.VISIBLE);

                    }
                }
            };
            imageLoader.get(DeepLinkUtils.DEEPLINK_DATA.getAffiliationLogoUrl(), iListener);
        } else {
            mHeaderIv.setVisibility(View.VISIBLE);
        }

        mEnvironment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String[] environmentList = getResources().getStringArray(R.array.environments);
                String currentEnvironment = environmentList[i];

                switch (currentEnvironment) {
                    case "PRODUCTION":
                        MDLiveConfig.setData(MDLiveConfig.ENVIRON.PROD);
                        break;
                    case "STAGE":
                        MDLiveConfig.setData(MDLiveConfig.ENVIRON.STAGE);
                        break;
                    case "QA_PLUTO":
                    default:
                        MDLiveConfig.setData(MDLiveConfig.ENVIRON.QAPL);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

//        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//        if(imm.isAcceptingText()){
//            healthSystemContainerRl.setBackgroundColor(Color.BLACK);
//            healthSystemContainerRl.setAlpha(0.7F);
//        }else{
//            healthSystemContainerRl.setBackgroundColor(Color.WHITE);
//            healthSystemContainerRl.setAlpha(0.1F);
//        }


//        mPasswordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean b) {
//                if (view.hasFocus()) {
//                    healthSystemContainerRl.setBackgroundColor(Color.BLACK);
//                    healthSystemContainerRl.setAlpha(0.7F);
//// mHeaderIv.setVisibility(View.GONE);
//                } else {
//                    healthSystemContainerRl.setBackgroundColor(Color.WHITE);
//                    healthSystemContainerRl.setAlpha(0.1F);
////mHeaderIv.setVisibility(View.VISIBLE);
//
//                }
//
//            }
//        });
//        mUserNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean b) {
//                if (view.hasFocus()) {
//                    healthSystemContainerRl.setBackgroundColor(Color.BLACK);
//                    healthSystemContainerRl.setAlpha(0.7F);
//// mHeaderIv.setVisibility(View.GONE);
//                } else {
//                    healthSystemContainerRl.setBackgroundColor(Color.WHITE);
//                    healthSystemContainerRl.setAlpha(0.1F);
//// mHeaderIv.setVisibility(View.VISIBLE);
//
//                }
//
//            }
//        });


    }

    public void onResume() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String userName = sharedPref.getString(PreferenceConstants.REMEMBER_ME, null);
        if (userName != null && !TextUtils.isEmpty(userName)) {
            mUserNameEditText.setText(userName);
            mRememberMe.setChecked(true);
        } else {
            mUserNameEditText.setText("");
            mRememberMe.setChecked(false);
        }
        super.onResume();
        if (isMDlive)
            startVideo();
    }

    public void startVideo() {
        //Creating MediaController
        MediaController mediaController = new MediaController(getActivity());
        mediaController.setAnchorView(mVideo);
        mediaController.setVisibility(View.GONE);
        mVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setLooping(true);
            }
        });


        //specify the location of media file
        String uriPath = "android.resource://com.mdlive.ST_LUKES_mobile/" + R.raw.welcome;
        Uri uri = Uri.parse(uriPath);

        //Setting MediaController and URI, then starting the videoView
        mVideo.setMediaController(mediaController);
        mVideo.setVideoURI(uri);
        mVideo.requestFocus();
        mVideo.start();
    }

    public void clearFocus() {
        mPasswordEditText.clearFocus();
        mUserNameEditText.clearFocus();
    }


    @Override
    public void onDetach() {
        super.onDetach();

        mOnLoginResponse = null;
    }

    public void loginService() {


        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPref.edit();
        final String userName = mUserNameEditText.getText().toString();
        final String password = mPasswordEditText.getText().toString();
        if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(password)) {
            try {
                // For saving the user name
                if (mRememberMe.isChecked()) {
                    editor.putString(PreferenceConstants.REMEMBER_ME, userName);
                } else {
                    editor.putString(PreferenceConstants.REMEMBER_ME, "");
                }
                editor.apply();
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
                MdliveUtils.showDialog(getActivity(), getActivity().getString(R.string.mdl_app_name), getActivity().getString(R.string.mdl_please_enter_login_fileds));
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

        /**
         *
         * The error listener will handle the login failure response. THis will also handle the
         * multiple user email login failure case as well.
         *
         */
        NetworkErrorListener errorListener = new NetworkErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mRememberMe.setChecked(false);
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                editor.putString(PreferenceConstants.REMEMBER_ME, "");
                editor.apply();
                try {
                    hideProgressDialog();
                    String responseBody = new String(error.networkResponse.data, "utf-8");
                    JSONObject errorObj = new JSONObject(responseBody);
                    if (errorObj.has("activation_url") && getActivity() instanceof LoginActivity) {
                        ((LoginActivity) getActivity()).onActivateAccount(errorObj.getString("activation_url"), mUserNameEditText.getText().toString(), mPasswordEditText.getText().toString());
                    } else {
                        MdliveUtils.handelVolleyErrorResponse(getActivity(), error, getProgressDialog());
                    }
                } catch (Exception e) {
                    MdliveUtils.handelVolleyErrorResponse(getActivity(), error, getProgressDialog());
                }
            }
        };

        logD("Login", params.toString());

        LoginService service = new LoginService(getActivity(), getProgressDialog());
        service.login(successCallBackListener, errorListener, params);
    }

    private void handleLoginSuccessResponse(JSONObject response) {
        try {
            if (response.getString("msg").equalsIgnoreCase("Success")) {
                logD("Login", "Login Successful : " + response.toString().trim());

                if ("null".equalsIgnoreCase(response.getString("uniqueid")) || response.getString("uniqueid").equalsIgnoreCase(null) || (response.getString("uniqueid") == null)) {
                    MdliveUtils.showDialog(getActivity(), getActivity().getString(R.string.mdl_app_name), response.getString("token"));
                    return;
                }

                // For saving the device token
                SharedPreferences sharedPref = getActivity().getSharedPreferences(PreferenceConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();

                editor.putString(PreferenceConstants.USER_UNIQUE_ID, response.getString("uniqueid"));
                editor.putString(PreferenceConstants.SESSION_ID, response.getString("token"));
                editor.apply();
                //Log.v("LoginFragment", "###$### RemoteUserId = "+ response.getString("uniqueid"));
                //Log.v("LoginFragment", "###$### SessionToken = "+ response.getString("token"));

                final SharedPreferences preferences = getActivity().getSharedPreferences(PreferenceConstants.DEVICE_OS, Context.MODE_PRIVATE);
                final SharedPreferences.Editor editor2 = preferences.edit();
                editor2.putString(PreferenceConstants.DEVICE_OS_KEY, "Android");
                editor2.apply();

                SharedPreferences settings = getActivity().getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, getActivity().MODE_PRIVATE);
                String pushRegID = settings.getString(PreferenceConstants.SAVED_PUSH_NOTIFICATION_ID, null);
                if (pushRegID != null) {
                    hideProgressDialog();
                    sendGCMInstanceId(pushRegID);
                } else {
                    if (MdliveUtils.getLockType(getActivity()).equalsIgnoreCase("password")) {
                        checkHealthServices();
                    } else {
                        hideProgressDialog();
                        if (mOnLoginResponse != null) {
                            mOnLoginResponse.onLoginSucess();
                        }
                    }
                }
            } else {
                MdliveUtils.showDialog(getActivity(), getActivity().getString(R.string.mdl_app_name), response.getString("token"));
            }

        } catch (Exception e) {
            logE("Error", e.getMessage());
            MdliveUtils.showDialog(getActivity(), getActivity().getString(R.string.mdl_app_name), getActivity().getString(R.string.mdl_please_enter_valid_fileds));
        }
    }

    private void sendGCMInstanceId(String pushRegID) {
        showProgressDialog();

        try {
            final SharedPreferences preferences = getActivity().getSharedPreferences(PreferenceConstants.DEVICE_OS, Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = preferences.edit();
            editor.putString(PreferenceConstants.DEVICE_OS_KEY, "Android");
            editor.apply();
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("uuid", pushRegID);
            jsonObject.put("device_type", "Android");

            NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    editor.clear().apply();
                    editor.apply();

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
                    editor.apply();

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

    //
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
                if (response != null && response.optBoolean("additional_screen_applicable", false)) {
                    showProgressDialog();
                    SharedPreferences sharedPref = getActivity().getSharedPreferences(PreferenceConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(PreferenceConstants.HEALTH_SYSTEM_PREFERENCES, response.toString()).commit();
                    screenImageURL = response.optString("screen_image");
                    footerImageURL = response.optString("footer_image");
                    healthSystemTv.setText(response.optString("splash_screen_text"));
                    final ImageLoader imageLoader = ApplicationController.getInstance().getImageLoader(getActivity());
                    ImageLoader.ImageListener iListener = new ImageLoader.ImageListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            imageLoader.get(screenImageURL, new ImageLoader.ImageListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    hideProgressDialog();
                                    if (mOnLoginResponse != null) {
                                        mOnLoginResponse.onLoginSucess();
                                    }
                                }

                                @Override
                                public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {
                                    if (response.getBitmap() != null) {
                                        // load image into imageview
                                        hideProgressDialog();
                                        mWebView.setImageBitmap(response.getBitmap());
                                        headerRl.setVisibility(View.GONE);
                                        loginContainerFl.setVisibility(View.GONE);
                                        healthSystemContainerRl.setVisibility(View.VISIBLE);
                                        healthSystemContainerRl.setBackgroundColor(Color.WHITE);
                                        healthSystemContainerRl.setAlpha(1f);
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
                                }
                            });
                        }

                        @Override
                        public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {
                            if (response.getBitmap() != null) {
                                mWebView.setImageBitmap(response.getBitmap());
                                hideProgressDialog();
                                headerRl.setVisibility(View.GONE);
                                loginContainerFl.setVisibility(View.GONE);
                                healthSystemContainerRl.setVisibility(View.VISIBLE);
                                healthSystemContainerRl.setBackgroundColor(Color.WHITE);
                                healthSystemContainerRl.setAlpha(1f);
                                mWebView.setVisibility(View.VISIBLE);
                                healthSystemIv.setVisibility(View.VISIBLE);
                                healthSystemTv.setVisibility(View.VISIBLE);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (mOnLoginResponse != null) {
                                            mOnLoginResponse.onLoginSucess();
                                        }
                                    }
                                }, SPLASH_TIME_OUT);
                            }
                        }
                    };
                    imageLoader.get(screenImageURL, iListener);

                } else {
                    if (mOnLoginResponse != null) {
                        mOnLoginResponse.onLoginSucess();
                    }
                }
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
                SharedPreferences sharedPref = getActivity().getSharedPreferences(PreferenceConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(PreferenceConstants.HEALTH_SYSTEM_PREFERENCES, "{}").commit();
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
