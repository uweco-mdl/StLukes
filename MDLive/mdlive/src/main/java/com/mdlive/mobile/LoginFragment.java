package com.mdlive.mobile;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.android.volley.VolleyError;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.login.GCMRegisterService;
import com.mdlive.unifiedmiddleware.services.login.LoginService;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dhiman_da on 7/15/2015.
 */

public class LoginFragment extends MDLiveBaseFragment{
    private OnLoginResponse mOnLoginResponse;

    private EditText mUserNameEditText = null;
    private EditText mPasswordEditText = null;

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
                MdliveUtils.showDialog(getActivity(),getActivity().getString(R.string.mdl_app_name), getActivity().getString(R.string.mdl_application_please_enter_mandetory_fileds));
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
            hideProgressDialog();

            if(response.getString("msg").equalsIgnoreCase("Success")) {
                logD("Login", "Login Successful : " + response.toString().trim());

                // For saving the device token
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("Device_Token", response.getString("token"));
                editor.commit();

                // For saving the REMOTE USER ID
                sharedPref = getActivity().getSharedPreferences(PreferenceConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
                editor = sharedPref.edit();
                editor.putString(PreferenceConstants.USER_UNIQUE_ID, response.getString("uniqueid"));
                editor.commit();

                if (MDLiveGCMPreference.MDLIVE_GCM_INSTANCE_ID != null
                        && MDLiveGCMPreference.MDLIVE_GCM_INSTANCE_ID.length() > 0) {
                    sendGCMInstanceId();
                } else {
                    if (mOnLoginResponse != null) {
                        mOnLoginResponse.onLoginSucess();
                    }
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

                    hideProgressDialog();
                    logD("GCM", response.optString("message"));


                    if (mOnLoginResponse != null) {
                        mOnLoginResponse.onLoginSucess();
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
}
