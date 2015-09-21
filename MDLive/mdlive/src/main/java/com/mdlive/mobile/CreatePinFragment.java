package com.mdlive.mobile;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;

/**
 * Created by dhiman_da on 7/23/2015.
 */

public class CreatePinFragment extends MDLiveBaseFragment implements TextWatcher {
    private OnCreatePinCompleted mOnCreatePinCompleted;

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

    private TextView mTitleTextView;
    private StringBuffer mStringBuffer;

    public CreatePinFragment() {
        super();
    }

    public static CreatePinFragment newInstance() {
        final CreatePinFragment createPinFragment = new CreatePinFragment();
        return createPinFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mOnCreatePinCompleted = (OnCreatePinCompleted) activity;
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

        init(view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mOnCreatePinCompleted = null;
    }

    public void init(View changePin) {
        mStringBuffer = new StringBuffer();

        mPassCode1 = (ToggleButton) changePin.findViewById(R.id.passCode1);
        mPassCode2 = (ToggleButton) changePin.findViewById(R.id.passCode2);
        mPassCode3 = (ToggleButton) changePin.findViewById(R.id.passCode3);
        mPassCode4 = (ToggleButton) changePin.findViewById(R.id.passCode4);
        mPassCode5 = (ToggleButton) changePin.findViewById(R.id.passCode5);
        mPassCode6 = (ToggleButton) changePin.findViewById(R.id.passCode6);

        mPassCode7 = (EditText) changePin.findViewById(R.id.etPasscode);

        mPassCode7.addTextChangedListener(this);
        mPassCode7.requestFocus();

        mTitleTextView = (TextView) changePin.findViewById(R.id.fragment_change_pin_text_view);
        mTitleTextView.setText(R.string.mdl_application_please_create_a_6_digit_pin);

        changePin.findViewById(R.id.dont_use_pin_text_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnCreatePinCompleted != null) {
                    mOnCreatePinCompleted.onClickNoPin();
                }
            }
        });

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

        mButton6 = (Button) changePin.findViewById(R.id.num_pad_7);
        if (mButton6 != null) {
            mButton6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mStringBuffer.append(mButton6.getText().toString().trim());
                    mPassCode7.setText(mStringBuffer.toString());
                }
            });
        }

        mButton7 = (Button) changePin.findViewById(R.id.num_pad_8);
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
            if (mOnCreatePinCompleted != null) {
                mOnCreatePinCompleted.onCreatePinCompleted(mPassCode7.getText().toString());
            }
        }

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    public String getEnteredPin() {
        return mPassCode7 == null ? null : mPassCode7.toString().trim();
    }

    public interface OnCreatePinCompleted {
        void onCreatePinCompleted(final String pin);

        void onClickNoPin();
    }
}