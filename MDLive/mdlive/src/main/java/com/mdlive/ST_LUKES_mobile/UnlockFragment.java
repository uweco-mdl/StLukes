package com.mdlive.ST_LUKES_mobile;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.android.volley.VolleyError;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.UserBasicInfo;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.UnlockService;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dhiman_da on 8/22/2015.
 */
public class UnlockFragment extends MDLiveBaseFragment implements TextWatcher, View.OnClickListener {

    private OnUnlockSucessful mOnUnlockSucessful;

    private ToggleButton mPassCode1 = null;
    private ToggleButton mPassCode2 = null;
    private ToggleButton mPassCode3 = null;
    private ToggleButton mPassCode4 = null;
    private ToggleButton mPassCode5 = null;
    private ToggleButton mPassCode6 = null;

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

    private EditText mPassCode7 = null;
    private StringBuffer mStringBuffer;
    private Activity mActivity = null;

    public static UnlockFragment newInstance() {
        final UnlockFragment fragment = new UnlockFragment();
        return fragment;
    }

    public UnlockFragment() {
        super();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;

        try {
            mOnUnlockSucessful = (OnUnlockSucessful) activity;
        } catch (ClassCastException e) {

        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_unlock, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(getString(R.string.mdl_forgot_your_pin));

        init(view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mOnUnlockSucessful = null;
    }

    public void init(View view) {
        mStringBuffer = new StringBuffer();
        ImageView mHeaderIv = (ImageView) view.findViewById(R.id.headerLogoIv);
        mHeaderIv.setVisibility(View.GONE);
        final UserBasicInfo userBasicInfo = UserBasicInfo.readFromSharedPreference(getActivity());
        mPassCode1 = (ToggleButton) view.findViewById(R.id.passCode1);
        mPassCode2 = (ToggleButton) view.findViewById(R.id.passCode2);
        mPassCode3 = (ToggleButton) view.findViewById(R.id.passCode3);
        mPassCode4 = (ToggleButton) view.findViewById(R.id.passCode4);
        mPassCode5 = (ToggleButton) view.findViewById(R.id.passCode5);
        mPassCode6 = (ToggleButton) view.findViewById(R.id.passCode6);
        mPassCode7 = (EditText) view.findViewById(R.id.etPasscode);

        mPassCode7.addTextChangedListener(this);


        if(userBasicInfo!=null && userBasicInfo.getAffiliationLogo()!=null) {
            mHeaderIv.setVisibility(View.VISIBLE);
//            showProgressDialog();
//            final ImageLoader imageLoader = ApplicationController.getInstance().getImageLoader(getActivity());
//            ImageLoader.ImageListener iListener = new ImageLoader.ImageListener() {
//
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    mHeaderIv.setVisibility(View.VISIBLE);
//                    hideProgressDialog();
//                }
//
//                @Override
//                public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {
//                    if (response.getBitmap() != null) {
//                        hideProgressDialog();
//                        mHeaderIv.setImageBitmap(response.getBitmap());
//                        mHeaderIv.setVisibility(View.VISIBLE);
//                    }
//                }
//            };
//            imageLoader.get(userBasicInfo.getAffiliationLogo(), iListener);
        } else {
            mHeaderIv.setVisibility(View.VISIBLE);
        }



        mButton0 = (Button) view.findViewById(R.id.num_pad_0);
        if (mButton0 != null) {
            mButton0.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mPassCode7.getText().length()<6) {
                        mStringBuffer.append(mButton0.getText().toString().trim());
                        mPassCode7.setText(mStringBuffer.toString());
                    }
                }
            });
        }

        mButton1 = (Button) view.findViewById(R.id.num_pad_1);
        if (mButton1 != null) {
            mButton1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mPassCode7.getText().length()<6) {
                        mStringBuffer.append(mButton1.getText().toString().trim());
                        mPassCode7.setText(mStringBuffer.toString());
                    }
                }
            });
        }

        mButton2 = (Button) view.findViewById(R.id.num_pad_2);
        if (mButton2 != null) {
            mButton2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mPassCode7.getText().length()<6) {
                        mStringBuffer.append(mButton2.getText().toString().trim());
                        mPassCode7.setText(mStringBuffer.toString());
                    }
                }
            });
        }

        mButton3 = (Button) view.findViewById(R.id.num_pad_3);
        if (mButton3 != null) {
            mButton3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mStringBuffer.append(mButton3.getText().toString().trim());
                    mPassCode7.setText(mStringBuffer.toString());
                }
            });
        }

        mButton4 = (Button) view.findViewById(R.id.num_pad_4);
        if (mButton4 != null) {
            mButton4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mPassCode7.getText().length()<6) {
                        mStringBuffer.append(mButton4.getText().toString().trim());
                        mPassCode7.setText(mStringBuffer.toString());
                    }
                }
            });
        }

        mButton5 = (Button) view.findViewById(R.id.num_pad_5);
        if (mButton5 != null) {
            mButton5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mPassCode7.getText().length()<6) {
                        mStringBuffer.append(mButton5.getText().toString().trim());
                        mPassCode7.setText(mStringBuffer.toString());
                    }
                }
            });
        }

        mButton6 = (Button) view.findViewById(R.id.num_pad_6);
        if (mButton6 != null) {
            mButton6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mPassCode7.getText().length()<6) {
                        mStringBuffer.append(mButton6.getText().toString().trim());
                        mPassCode7.setText(mStringBuffer.toString());
                    }
                }
            });
        }

        mButton7 = (Button) view.findViewById(R.id.num_pad_7);
        if (mButton7 != null) {
            mButton7.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mPassCode7.getText().length()<6) {
                        mStringBuffer.append(mButton7.getText().toString().trim());
                        mPassCode7.setText(mStringBuffer.toString());
                    }
                }
            });
        }

        mButton8 = (Button) view.findViewById(R.id.num_pad_8);
        if (mButton8 != null) {
            mButton8.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mPassCode7.getText().length()<6) {
                        mStringBuffer.append(mButton8.getText().toString().trim());
                        mPassCode7.setText(mStringBuffer.toString());
                    }
                }
            });
        }

        mButton9 = (Button) view.findViewById(R.id.num_pad_9);
        if (mButton9 != null) {
            mButton9.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mPassCode7.getText().length()<6) {
                        mStringBuffer.append(mButton9.getText().toString().trim());
                        mPassCode7.setText(mStringBuffer.toString());
                    }
                }
            });
        }

        View mButtonCross = view.findViewById(R.id.num_pad_cross);
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
    public void onTextChanged(CharSequence s, int start, int before, int count){
            logD("Text", mPassCode7.getText().toString());
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
            final String pin = mPassCode7.getText().toString();
            loadConfirmPin(pin);
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
        try {
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("device_token", MdliveUtils.getDeviceToken(getActivity()));
            jsonObject.put("passcode", confirmPin);
            fetchPinWebserviceCall(jsonObject.toString());
        } catch (JSONException e) {
            logE("Error", e.getMessage());
        }
    }


    private void fetchPinWebserviceCall(String params) {
        MdliveUtils.hideKeyboard(getActivity(), (View) mPassCode7);
        if (MdliveUtils.isNetworkAvailable(getActivity())) {
            showProgressDialog();

            logD("Unlock screen Request", "Unlock response : " + params);

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
                    clearPincode();
                    try {
//                        MdliveUtils.handelVolleyErrorResponse(getActivity(), error, getProgressDialog());
                        if (mOnUnlockSucessful != null) {
                            mOnUnlockSucessful.onUnlockUnSuccesful();
                        }
                        clearPincode();
                    } catch (Exception e) {
                        MdliveUtils.connectionTimeoutError(getProgressDialog(), getActivity());
                    }
                }
            };

            UnlockService service = new UnlockService(getActivity(), null);
            service.unlock(successCallBackListener, errorListener, params);
        }else{
            MdliveUtils.connectionTimeoutError(getProgressDialog(), getActivity());
        }
    }

    private void handleCreatePinSuccessResponse(JSONObject response) {

        try {
            hideProgressDialog();

            logD("Unlock screen response", "Unlock response : " + response.toString());
            if (response.getString("msg").equalsIgnoreCase("Success")) {

                // For saving the device token ("Session Id").
                SharedPreferences sharedPref = getActivity().getSharedPreferences(PreferenceConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(PreferenceConstants.USER_UNIQUE_ID, response.getString("uniqueid"));
                editor.putString(PreferenceConstants.SESSION_ID, response.getString("token"));
                //Log.v("UnlockFragment", "###$### RemoteUserId = "+ response.getString("uniqueid"));
                //Log.v("UnlockFragment", "###$### SessionToken = "+ response.getString("token"));
                editor.apply();

                if (mOnUnlockSucessful != null) {
                    mOnUnlockSucessful.onUnlockSuccesful();
                }
            } else {
                if (mOnUnlockSucessful != null) {
                    mOnUnlockSucessful.onUnlockUnSuccesful();
                }
            }

        } catch (Exception e) {
            logE("Error", e.getMessage());
        }
    }

    public void clearPincode() {
        mPassCode7.setText("");
        mStringBuffer.replace(0, mStringBuffer.length() - 1, "");
    }

    public interface OnUnlockSucessful {
        void onUnlockSuccesful();
        void onUnlockUnSuccesful();
    }
}
