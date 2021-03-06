package com.cycliq.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.cycliq.Application.CycliqApplication;
import com.cycliq.R;

public class OtpView extends LinearLayout {
    private EditText mOtpOneField, mOtpTwoField, mOtpThreeField, mOtpFourField,
            mCurrentlyFocusedEditText;

    MyListener listener;

    public OtpView(Context context) {
        super(context);
        init(null);
    }

    public void passInterface(MyListener listener) {

        this.listener = listener;


    }

    public OtpView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public interface MyListener {
        // you can define any parameter as per your requirement
        void callback(String result);
    }


    public OtpView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray styles = getContext().obtainStyledAttributes(attrs, R.styleable.OtpView);
        LayoutInflater mInflater =
                (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInflater.inflate(R.layout.otpview_layout, this);
        mOtpOneField = (EditText) findViewById(R.id.otp_one_edit_text);
        mOtpTwoField = (EditText) findViewById(R.id.otp_two_edit_text);
        mOtpThreeField = (EditText) findViewById(R.id.otp_three_edit_text);
        mOtpFourField = (EditText) findViewById(R.id.otp_four_edit_text);
        styleEditTexts(styles);
        styles.recycle();

        CycliqApplication application = (CycliqApplication ) getContext().getApplicationContext();

        setFontType(mOtpOneField, application .getOpenSans());
        setFontType(mOtpTwoField, application .getOpenSans());
        setFontType(mOtpThreeField, application .getOpenSans());
        setFontType(mOtpFourField, application .getOpenSans());

    }

    private int defaultDimension = 0;
    private int TYPE_BOLD = 1;
    private int TYPE_ITALIC = 2;
    private int TYPE_MEDIUM = 3;
    private int TYPE_LIGHT = 4;
    private int FONT_UBUNTU = 1;
    private int FONT_OPEN_SANS = 2;
    private int fontType;
    private int fontName;

    private void setFontType(EditText ed, Typeface font) {
        if (fontType == TYPE_BOLD) {
            ed.setTypeface(font, Typeface.BOLD);
        } else if (fontType == TYPE_ITALIC) {
            ed.setTypeface(font, Typeface.NORMAL);
        } else if (fontType == TYPE_MEDIUM) {
            ed.setTypeface(font, Typeface.NORMAL);
        } else {
            ed.setTypeface(font);
        }
    }

    /**
     * Get an instance of the present otp
     */
    private String makeOTP() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(mOtpOneField.getText().toString());
        stringBuilder.append(mOtpTwoField.getText().toString());
        stringBuilder.append(mOtpThreeField.getText().toString());
        stringBuilder.append(mOtpFourField.getText().toString());
        return stringBuilder.toString();
    }

    /**
     * Checks if all four fields have been filled
     *
     * @return length of OTP
     */
    public boolean hasValidOTP() {
        return makeOTP().length() == 4;
    }

    /**
     * Returns the present otp entered by the user
     *
     * @return OTP
     */
    public String getOTP() {
        return makeOTP();
    }

    /**
     * Used to set the OTP. More of cosmetic value than functional value
     *
     * @param otp Send the four digit otp
     */
    public void setOTP(String otp) {
        if (otp.length() != 4) {
            Log.e("OTPView", "Invalid otp param");
            return;
        }
        if (mOtpOneField.getInputType() == InputType.TYPE_CLASS_NUMBER
                && !otp.matches("[0-9]+")) {
            Log.e("OTPView", "OTP doesn't match INPUT TYPE");
            return;
        }
        mOtpOneField.setText(String.valueOf(otp.charAt(0)));
        mOtpTwoField.setText(String.valueOf(otp.charAt(1)));
        mOtpThreeField.setText(String.valueOf(otp.charAt(2)));
        mOtpFourField.setText(String.valueOf(otp.charAt(3)));
    }

    private void styleEditTexts(TypedArray styles) {
        int textColor = styles.getColor(R.styleable.OtpView_text_background_color, Color.BLACK);
        int backgroundColor =
                styles.getColor(R.styleable.OtpView_text_background_color, Color.TRANSPARENT);
        if (styles.getColor(R.styleable.OtpView_text_background_color, Color.TRANSPARENT)
                != Color.TRANSPARENT) {
            mOtpOneField.setBackgroundColor(backgroundColor);
            mOtpTwoField.setBackgroundColor(backgroundColor);
            mOtpThreeField.setBackgroundColor(backgroundColor);
            mOtpFourField.setBackgroundColor(backgroundColor);
        } else {
//      mOtpOneField.getBackground().mutate().setColorFilter(textColor, PorterDuff.Mode.SRC_ATOP);
//      mOtpTwoField.getBackground().mutate().setColorFilter(textColor, PorterDuff.Mode.SRC_ATOP);
//      mOtpThreeField.getBackground().mutate().setColorFilter(textColor, PorterDuff.Mode.SRC_ATOP);
//      mOtpFourField.getBackground().mutate().setColorFilter(textColor, PorterDuff.Mode.SRC_ATOP);
        }
        mOtpOneField.setTextColor(textColor);
        mOtpTwoField.setTextColor(textColor);
        mOtpThreeField.setTextColor(textColor);
        mOtpFourField.setTextColor(textColor);
        setEditTextInputStyle(styles);
    }

    private void setEditTextInputStyle(TypedArray styles) {
        int inputType =
                styles.getInt(R.styleable.OtpView_android_inputType, EditorInfo.TYPE_TEXT_VARIATION_NORMAL);
        mOtpOneField.setInputType(inputType);
        mOtpTwoField.setInputType(inputType);
        mOtpThreeField.setInputType(inputType);
        mOtpFourField.setInputType(inputType);
        String text = styles.getString(R.styleable.OtpView_otp);
        if (!TextUtils.isEmpty(text) && text.length() == 4) {
            mOtpOneField.setText(String.valueOf(text.charAt(0)));
            mOtpTwoField.setText(String.valueOf(text.charAt(1)));
            mOtpThreeField.setText(String.valueOf(text.charAt(2)));
            mOtpFourField.setText(String.valueOf(text.charAt(3)));
        }
        setFocusListener();
        setOnTextChangeListener();
    }

    private void setFocusListener() {
        OnFocusChangeListener onFocusChangeListener = new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                mCurrentlyFocusedEditText = (EditText) v;
                mCurrentlyFocusedEditText.setSelection(mCurrentlyFocusedEditText.getText().length());
            }
        };
        mOtpOneField.setOnFocusChangeListener(onFocusChangeListener);
        mOtpTwoField.setOnFocusChangeListener(onFocusChangeListener);
        mOtpThreeField.setOnFocusChangeListener(onFocusChangeListener);
        mOtpFourField.setOnFocusChangeListener(onFocusChangeListener);
    }

    public void disableKeypad() {
        OnTouchListener touchListener = new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.onTouchEvent(event);
                InputMethodManager imm =
                        (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                return true;
            }
        };
        mOtpOneField.setOnTouchListener(touchListener);
        mOtpTwoField.setOnTouchListener(touchListener);
        mOtpThreeField.setOnTouchListener(touchListener);
        mOtpFourField.setOnTouchListener(touchListener);
    }

    public void enableKeypad() {
        OnTouchListener touchListener = new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        };
        mOtpOneField.setOnTouchListener(touchListener);
        mOtpTwoField.setOnTouchListener(touchListener);
        mOtpThreeField.setOnTouchListener(touchListener);
        mOtpFourField.setOnTouchListener(touchListener);
    }

    public EditText getCurrentFoucusedEditText() {
        return mCurrentlyFocusedEditText;
    }

    private void setOnTextChangeListener() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mCurrentlyFocusedEditText.getText().length() >= 1
                        && mCurrentlyFocusedEditText != mOtpFourField) {
                    mCurrentlyFocusedEditText.focusSearch(View.FOCUS_RIGHT).requestFocus();
                } else if (mCurrentlyFocusedEditText.getText().length() >= 1
                        && mCurrentlyFocusedEditText == mOtpFourField) {
                    InputMethodManager imm =
                            (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(getWindowToken(), 0);
                    }
                } else {
                    String currentValue = mCurrentlyFocusedEditText.getText().toString();
                    if (currentValue.length() <= 0 && mCurrentlyFocusedEditText.getSelectionStart() <= 0) {
                        mCurrentlyFocusedEditText.focusSearch(View.FOCUS_LEFT).requestFocus();
                    }
                }

                if(hasValidOTP()) {

                    listener.callback(getOTP());

                }
            }
        };

        mOtpOneField.addTextChangedListener(textWatcher);
        mOtpTwoField.addTextChangedListener(textWatcher);
        mOtpThreeField.addTextChangedListener(textWatcher);
        mOtpFourField.addTextChangedListener(textWatcher);

    }

    public void simulateDeletePress() {
        mCurrentlyFocusedEditText.setText("");
    }
}
