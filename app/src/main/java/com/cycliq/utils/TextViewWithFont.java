package com.cycliq.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.widget.TextView;

import com.cycliq.Application.CycliqApplication;
import com.cycliq.R;

/**
 * Created by Shreya Kotak on 12/05/16.
 */
@SuppressLint("AppCompatCustomView")
public class TextViewWithFont extends TextView {
    private int defaultDimension = 0;
    private int TYPE_BOLD = 1;
    private int TYPE_ITALIC = 2;
    private int TYPE_MEDIUM = 3;
    private int TYPE_LIGHT = 4;
    private int FONT_UBUNTU = 1;
    private int FONT_OPEN_SANS = 2;
    private int fontType;
    private int fontName;

    public TextViewWithFont(Context context) {
        super(context);
        init(null, 0);
    }
    public TextViewWithFont(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }
    public TextViewWithFont(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }
    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.font, defStyle, 0);
        fontName = a.getInt(R.styleable.font_name, defaultDimension);
        fontType = a.getInt(R.styleable.font_type, defaultDimension);
        a.recycle();
        CycliqApplication application = (CycliqApplication ) getContext().getApplicationContext();
        if (fontName == FONT_UBUNTU) {
            setFontType(application .getUbunthuFont());
        } else if (fontName == FONT_OPEN_SANS) {
            setFontType(application .getOpenSans());
        }
    }
    private void setFontType(Typeface font) {
        if (fontType == TYPE_BOLD) {
            setTypeface(font, Typeface.BOLD);
        } else if (fontType == TYPE_ITALIC) {
            setTypeface(font, Typeface.NORMAL);
        } else if (fontType == TYPE_MEDIUM) {
            setTypeface(font, Typeface.NORMAL);
        } else {
            setTypeface(font);
        }
    }
}