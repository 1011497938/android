package cn.edu.zju.qcw.android.base.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.support.annotation.StringRes;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import cn.edu.zju.qcw.android.R;

/**
 * Author: Shper
 * Version: V0.1 2017/3/30
 */
public class IconTextView extends AppCompatTextView {

    public IconTextView(Context context) {
        super(context);
        initViews(null);
    }

    public IconTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(attrs);
    }

    public IconTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(attrs);
    }

    private void initViews(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.common_iconTextView);

        if (typedArray.hasValue(R.styleable.common_iconTextView_iconColor)) {
            int iconColor = typedArray.getColor(R.styleable.common_iconTextView_iconColor,
                    Color.WHITE);
            setTextColor(iconColor);
        }

        if (!typedArray.hasValue(R.styleable.common_iconTextView_android_textStyle)) {
            setTypeface();
            return;
        }

        int textStyle = typedArray.getInt(R.styleable.common_iconTextView_android_textStyle, Typeface.NORMAL);
        setTypeface(textStyle);

        typedArray.recycle();
    }

    private void setTypeface() {
        if (!isInEditMode()) {
            setTypeface(Typeface.createFromAsset(getContext().getAssets(), "iconfont.ttf"));
        }
    }

    public void setTypeface(int style) {
        if (!isInEditMode()) {
            super.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "iconfont.ttf"), style);
        }
    }

    public void setIcon(CharSequence text) {
        setText(text);
    }

    public void setIcon(@StringRes int resId) {
        setText(resId);
    }

    public void setIconColor(@ColorInt int color) {
        setTextColor(color);
    }

    public void setIconSize(float size) {
        setTextSize(size);
    }

    public void setIconSize(int unit, float size) {
        setTextSize(unit, size);
    }

}
