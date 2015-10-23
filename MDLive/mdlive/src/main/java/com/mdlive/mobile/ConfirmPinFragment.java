package com.mdlive.mobile;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.mdlive.unifiedmiddleware.commonclasses.application.ApplicationController;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.login.HealthSystemServices;
import com.mdlive.unifiedmiddleware.services.login.PinCreation;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by dhiman_da on 7/23/2015.
 */
public class ConfirmPinFragment extends MDLiveBaseFragment implements TextWatcher, View.OnClickListener {
    private static final String PIN_TAG = "PIN";

    private OnCreatePinSucessful mOnCreatePinSucessful;

    private ToggleButton mPassCode1 = null;
    private ToggleButton mPassCode2 = null;
    private ToggleButton mPassCode3 = null;
    private ToggleButton mPassCode4 = null;
    private ToggleButton mPassCode5 = null;
    private ToggleButton mPassCode6 = null;

    private EditText mPassCode7 = null;

    private Button mButton1;
    private Button mButton2;
    private Button mButton3;
    private Button mButton4;
    private Button mButton5;
    private Button mButton6;
    private Button mButton7;
    private Button mButton8;
    private Button mButton9;
    private Button mButton0;
    private View mButtonCross;

    private TextView mTitleTextView = null, healthSystemTv;
    private StringBuffer mStringBuffer;
    private ImageView mWebView;
    private RelativeLayout healthSystemContainerRl;
    private ImageView healthSystemIv;
    private static final int SPLASH_TIME_OUT = 4000;
    private String screenImageURL, footerImageURL;

    public static ConfirmPinFragment newInstance(String createPin) {
        final Bundle bundle = new Bundle();
        bundle.putString(PIN_TAG, createPin);

        final ConfirmPinFragment confirmPinFragment = new ConfirmPinFragment();
        confirmPinFragment.setArguments(bundle);
        return confirmPinFragment;
    }

    public ConfirmPinFragment() {
        super();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        getActivity().setTitle(getString(R.string.mdl_confirm_your_pin));
        try {
            mOnCreatePinSucessful = (OnCreatePinSucessful) activity;
        } catch (ClassCastException cce) {
            logE("Error", cce.getMessage());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragments_change_pin, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(getString(R.string.mdl_confirm_your_pin));
        init(view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mOnCreatePinSucessful = null;
    }

    public void init(View changePin) {
        mStringBuffer = new StringBuffer();

        mPassCode1 = (ToggleButton) changePin.findViewById(R.id.passCode1);
        mPassCode2 = (ToggleButton) changePin.findViewById(R.id.passCode2);
        mPassCode3 = (ToggleButton) changePin.findViewById(R.id.passCode3);
        mPassCode4 = (ToggleButton) changePin.findViewById(R.id.passCode4);
        mPassCode5 = (ToggleButton) changePin.findViewById(R.id.passCode5);
        mPassCode6 = (ToggleButton) changePin.findViewById(R.id.passCode6);
        mWebView = (ImageView) changePin.findViewById(R.id.webView);
        healthSystemContainerRl = (RelativeLayout) changePin.findViewById(R.id.health_system_container_rl);
        healthSystemIv = (ImageView) changePin.findViewById(R.id.health_system_niv);
        healthSystemTv = (TextView) changePin.findViewById(R.id.health_system_tv);
        mPassCode7 = (EditText) changePin.findViewById(R.id.etPasscode);

        mPassCode7.addTextChangedListener(this);

        mTitleTextView = (TextView) changePin.findViewById(R.id.fragment_change_pin_text_view);
        mTitleTextView.setText(R.string.mdl_application_please_confirm_your_pin);

        changePin.findViewById(R.id.linear_layout).setVisibility(View.INVISIBLE);
        mTitleTextView.setText(R.string.mdl_application_please_confirm_your_pin);


        mButton0 = (Button) changePin.findViewById(R.id.num_pad_0);
        if (mButton0 != null) {
            mButton0.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mStringBuffer.append(mButton0.getText().toString().trim());
                    mPassCode7.setText(mStringBuffer.toString());
                }
            });
        }

        mButton1 = (Button) changePin.findViewById(R.id.num_pad_1);
        if (mButton1 != null) {
            mButton1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mStringBuffer.append(mButton1.getText().toString().trim());
                    mPassCode7.setText(mStringBuffer.toString());
                }
            });
        }

        mButton2 = (Button) changePin.findViewById(R.id.num_pad_2);
        if (mButton2 != null) {
            mButton2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mStringBuffer.append(mButton2.getText().toString().trim());
                    mPassCode7.setText(mStringBuffer.toString());
                }
            });
        }

        mButton3 = (Button) changePin.findViewById(R.id.num_pad_3);
        if (mButton3 != null) {
            mButton3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mStringBuffer.append(mButton3.getText().toString().trim());
                    mPassCode7.setText(mStringBuffer.toString());
                }
            });
        }

        mButton4 = (Button) changePin.findViewById(R.id.num_pad_4);
        if (mButton4 != null) {
            mButton4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mStringBuffer.append(mButton4.getText().toString().trim());
                    mPassCode7.setText(mStringBuffer.toString());
                }
            });
        }

        mButton5 = (Button) changePin.findViewById(R.id.num_pad_5);
        if (mButton5 != null) {
            mButton5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mStringBuffer.append(mButton5.getText().toString().trim());
                    mPassCode7.setText(mStringBuffer.toString());
                }
            });
        }

        mButton6 = (Button) changePin.findViewById(R.id.num_pad_6);
        if (mButton6 != null) {
            mButton6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mStringBuffer.append(mButton6.getText().toString().trim());
                    mPassCode7.setText(mStringBuffer.toString());
                }
            });
        }

        mButton7 = (Button) changePin.findViewById(R.id.num_pad_7);
        if (mButton7 != null) {
            mButton7.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mStringBuffer.append(mButton7.getText().toString().trim());
                    mPassCode7.setText(mStringBuffer.toString());
                }
            });
        }

        mButton8 = (Button) changePin.findViewById(R.id.num_pad_8);
        if (mButton8 != null) {
            mButton8.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mStringBuffer.append(mButton8.getText().toString().trim());
                    mPassCode7.setText(mStringBuffer.toString());
                }
            });
        }

        mButton9 = (Button) changePin.findViewById(R.id.num_pad_9);
        if (mButton9 != null) {
            mButton9.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mStringBuffer.append(mButton9.getText().toString().trim());
                    mPassCode7.setText(mStringBuffer.toString());
                }
            });
        }

        mButtonCross = changePin.findViewById(R.id.num_pad_cross);
        if (mButtonCross != null) {
            mButtonCross.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    logD("Text", "length :" + mPassCode7.getText().toString().length());
                    if (mStringBuffer.length() > 0) {
                        mStringBuffer.deleteCharAt(mStringBuffer.length() - 1);
                        logD("Text", "After -1 :" + mStringBuffer.toString());
                    }
                    mPassCode7.setText(mStringBuffer.toString());
                    mPassCode7.invalidate();
                }
            });
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        int iLength = mPassCode7.getText().length();
        switch (iLength) {
            case 0:
                mPassCode1.setChecked(false);
                mPassCode2.setChecked(false);
                mPassCode3.setChecked(false);
                mPassCode4.setChecked(false);
                mPassCode5.setChecked(false);
                mPassCode6.setChecked(false);
                break;
            case 1:
                mPassCode1.setChecked(true);
                mPassCode2.setChecked(false);
                mPassCode3.setChecked(false);
                mPassCode4.setChecked(false);
                mPassCode5.setChecked(false);
                mPassCode6.setChecked(false);
                break;
            case 2:
                mPassCode1.setChecked(true);
                mPassCode2.setChecked(true);
                mPassCode3.setChecked(false);
                mPassCode4.setChecked(false);
                mPassCode5.setChecked(false);
                mPassCode6.setChecked(false);
                break;
            case 3:
                mPassCode1.setChecked(true);
                mPassCode2.setChecked(true);
                mPassCode3.setChecked(true);
                mPassCode4.setChecked(false);
                mPassCode5.setChecked(false);
                mPassCode6.setChecked(false);
                break;
            case 4:
                mPassCode1.setChecked(true);
                mPassCode2.setChecked(true);
                mPassCode3.setChecked(true);
                mPassCode4.setChecked(true);
                mPassCode5.setChecked(false);
                mPassCode6.setChecked(false);
                break;
            case 5:
                mPassCode1.setChecked(true);
                mPassCode2.setChecked(true);
                mPassCode3.setChecked(true);
                mPassCode4.setChecked(true);
                mPassCode5.setChecked(true);
                mPassCode6.setChecked(false);
                break;
            case 6:
                mPassCode1.setChecked(true);
                mPassCode2.setChecked(true);
                mPassCode3.setChecked(true);
                mPassCode4.setChecked(true);
                mPassCode5.setChecked(true);
                mPassCode6.setChecked(true);
                break;
        }
        if (iLength == 6) {
            MdliveUtils.hideKeyboard(getActivity(), (View) mPassCode7);
            final String pin = mPassCode7.getText().toString();

            if (pin.equals(getArguments().getString(PIN_TAG))) {
                loadConfirmPin(pin);
            } else {
                MdliveUtils.showDialog(getActivity(), getString(R.string.mdl_application_name), getString(R.string.mdl_application_pin_mismatch), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (getActivity() != null && getActivity() instanceof PinActivity) {
                            dialog.dismiss();
                            ((PinActivity) getActivity()).onTwoPasswordMismatch();
                        }
                    }
                });
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onClick(View v) {
        v.requestFocus();
    }

    public void loadConfirmPin(final String confirmPin) {
        String createPin = getArguments().getString(PIN_TAG);

        if (createPin.equals(confirmPin)) {
            try {
                //final SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);

                final JSONObject jsonObject = new JSONObject();
                jsonObject.put("device_token", MdliveUtils.getDeviceToken(getActivity()));
                jsonObject.put("passcode", confirmPin);
                fetachPinWebserviceCall(jsonObject.toString());
            } catch (JSONException e) {
                logE("Error", e.getMessage());
            }
        } else {
            MdliveUtils.showDialog(getActivity(), getString(R.string.mdl_application_name), getString(R.string.mdl_application_pin_mismatch));
        }
    }

    private void fetachPinWebserviceCall(String params) {
        MdliveUtils.hideKeyboard(getActivity(), (View) mPassCode7);
        showProgressDialog();

        NetworkSuccessListener<JSONObject> successCallBackListener = new NetworkSuccessListener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                handleCreatePinSuccessResponse(response);
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
                try {
                    MdliveUtils.handelVolleyErrorResponse(getActivity(), error, null);
                } catch (Exception e) {
                    MdliveUtils.connectionTimeoutError(getProgressDialog(), getActivity());
                }
            }
        };

        PinCreation service = new PinCreation(getActivity(), null);
        service.createPin(successCallBackListener, errorListener, params);
    }

    private void handleCreatePinSuccessResponse(JSONObject response) {

        try {
//            hideProgressDialog();

            if (response.getString("message").equalsIgnoreCase("Success")) {

//                if (mOnCreatePinSucessful != null) {
//                    mOnCreatePinSucessful.startDashboard();
//                }
                checkHealthServices();
            } else {
                MdliveUtils.showDialog(getActivity(), getString(R.string.mdl_application_name), getString(R.string.mdl_application_pin_creation_failed));
            }

        } catch (Exception e) {
            logE("Error", e.getMessage());
        }
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
                    if (getActivity() != null && getActivity() instanceof AppCompatActivity) {
                        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
                    }
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
                                    if (mOnCreatePinSucessful != null) {
                                        mOnCreatePinSucessful.startDashboard();
                                    }
                                }

                                @Override
                                public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {
                                    if (response.getBitmap() != null) {
                                        // load image into imageview
                                        hideProgressDialog();
                                        mWebView.setImageBitmap(response.getBitmap());
                                        healthSystemContainerRl.setVisibility(View.VISIBLE);
                                        mWebView.setVisibility(View.VISIBLE);
                                        healthSystemIv.setVisibility(View.VISIBLE);
                                        healthSystemTv.setVisibility(View.VISIBLE);
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (mOnCreatePinSucessful != null) {
                                                    mOnCreatePinSucessful.startDashboard();
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
                                healthSystemContainerRl.setVisibility(View.VISIBLE);
                                mWebView.setVisibility(View.VISIBLE);
                                healthSystemIv.setVisibility(View.VISIBLE);
                                healthSystemTv.setVisibility(View.VISIBLE);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (mOnCreatePinSucessful != null) {
                                            mOnCreatePinSucessful.startDashboard();
                                        }
                                    }
                                }, SPLASH_TIME_OUT);
                            } else {
                                Log.d("ARG1 - ", arg1 + "");
                            }
                        }
                    };
                    imageLoader.get(screenImageURL, iListener);

                } else {
                    if (mOnCreatePinSucessful != null) {
                        mOnCreatePinSucessful.startDashboard();
                    }
                }
            }
        };

        NetworkErrorListener errorListener = new NetworkErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
                if(getActivity()!=null) {
                    SharedPreferences sharedPref = getActivity().getSharedPreferences(PreferenceConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(PreferenceConstants.HEALTH_SYSTEM_PREFERENCES, "{}").commit();
                }
                if (mOnCreatePinSucessful != null) {
                    mOnCreatePinSucessful.startDashboard();
                }
            }
        };

        HealthSystemServices service = new HealthSystemServices(getActivity(), getProgressDialog());
        service.getHealthSystemsData(successCallBackListener, errorListener, getLocalIpAddress());
    }

    public String getEnteredPin() {
        return mPassCode7 == null ? null : mPassCode7.toString().trim();
    }

    public interface OnCreatePinSucessful {
        void startDashboard();
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
