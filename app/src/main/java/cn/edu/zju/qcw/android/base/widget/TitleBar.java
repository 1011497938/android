package cn.edu.zju.qcw.android.base.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import cn.edu.zju.qcw.android.R;
import cn.edu.zju.qcw.android.base.annotation.Style;
import cn.edu.zju.qcw.android.util.SizeUtils;
import cn.edu.zju.qcw.android.util.image.ImageHelper;

/**
 * Author: Shper
 * Version: V0.1 2017/3/30
 */
public class TitleBar extends RelativeLayout {

    private static final int NO_SET = -99999999;

    public static final int RIGHT_LEFT_DEFAULT_SIZE_SP = 19;

    @Style
    private String style = Style.LIGHT;

    private int titleBarBg = NO_SET;
    private int leftIconColor = NO_SET;
    private int titleColor = NO_SET;
    private int rightTextEnableColor;
    private int rightTextDisableColor;

    private RelativeLayout rootView;
    public View leftView;
    private View titleView;
    private View rightView;
    private View bottomLine;

    private View mask;
    public RelativeLayout leftImageContainer;

    public TitleBar(Context context) {
        super(context);
        initView(null);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }

    public void refreshMask(float alpha, @ColorInt int color) {
        mask.setBackgroundColor(color);
        mask.setAlpha(alpha);
    }

    public void showLeftImage(String url) {
        leftImageContainer = (RelativeLayout) rootView.findViewById(R.id.common_titlebar_left_image);
        ImageView leftImage = (ImageView) rootView.findViewById(R.id.common_titlebar_left_iv);
        leftImageContainer.setVisibility(VISIBLE);
        leftView.setVisibility(GONE);
        ImageHelper.loadCircleImage(getContext(), url, leftImage);
    }

    private void initView(@Nullable AttributeSet attrs) {
        rootView = (RelativeLayout) View.inflate(getContext(), R.layout.common_layout_titlebar, this);
        leftView = rootView.findViewById(R.id.common_titlebar_left_icon);
        titleView = rootView.findViewById(R.id.common_titlebar_title_tv);
        rightView = rootView.findViewById(R.id.common_titlebar_right_icon);
        bottomLine = rootView.findViewById(R.id.common_titlebar_bottom_line);
        mask = rootView.findViewById(R.id.common_titlbar_mask);

        // Init View by Xml Style
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.common_titleBar);

        initTitleBar(typedArray);
        initLeftView(typedArray);
        initTitle(typedArray);
        initRightView(typedArray);
        initBottomLine(typedArray);


        typedArray.recycle();
    }

    // 设置 整体风格
    private void initTitleBar(TypedArray typedArray) {
        this.style = typedArray.getInt(R.styleable.common_titleBar_style, 0) == 1 ? Style.DARK : Style.LIGHT;
        if (typedArray.hasValue(R.styleable.common_titleBar_android_background) ) {
            if (typedArray.getDrawable(R.styleable.common_titleBar_android_background) != null) {
                return;
            }
            titleBarBg = typedArray.getColor(R.styleable.common_titleBar_android_background, ContextCompat.getColor(getContext(),
                    isLightStyle() ? R.color.common_titleBar_light_bg : R.color.common_titleBar_dark_bg));
        }

        setTitleBarBg();
    }

    private void initLeftView(TypedArray typedArray) {
        String leftIcon = typedArray.getString(R.styleable.common_titleBar_leftIcon);
        setLeftIcon(null != leftIcon ? leftIcon : getContext().getString(R.string.icon_back));

        if (typedArray.hasValue(R.styleable.common_titleBar_leftIconColor)) {
            leftIconColor = typedArray.getColor(R.styleable.common_titleBar_leftIconColor, ContextCompat.getColor(getContext(),
                    isLightStyle() ? R.color.common_titleBar_light_content : R.color.common_titleBar_dark_content));
        }
        setLeftIconColor();

        int leftIconSize = typedArray.getDimensionPixelSize(R.styleable.common_titleBar_leftIconSize, SizeUtils.sp2px(RIGHT_LEFT_DEFAULT_SIZE_SP));
        setLeftIconSize(SizeUtils.px2sp(leftIconSize));

        setLeftOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == getContext() || !(getContext() instanceof Activity)) {
                    return;
                }

                ((Activity) getContext()).finish();
            }
        });
    }

    private void initTitle(TypedArray typedArray) {
        int titleStyle = typedArray.getInt(R.styleable.common_titleBar_titleStyle, Typeface.BOLD);
        setTitleTypeface(titleStyle);

        if (typedArray.hasValue(R.styleable.common_titleBar_titleTextColor)) {
            titleColor = typedArray.getColor(R.styleable.common_titleBar_titleTextColor, ContextCompat.getColor(getContext(),
                    isLightStyle() ? R.color.common_titleBar_light_content : R.color.common_titleBar_dark_content));
        }
        setTitleColor();

        int titleSize = typedArray.getDimensionPixelSize(R.styleable.common_titleBar_titleTextSize, SizeUtils.sp2px(15));
        setTitleSize(SizeUtils.px2sp(titleSize));

        String titleText = typedArray.getString(R.styleable.common_titleBar_titleText);
        setTitle(null != titleText ? titleText : "");
    }

    private void initRightView(TypedArray typedArray) {
        String rightText = typedArray.getString(R.styleable.common_titleBar_rightText);
        if (!TextUtils.isEmpty(rightText)) {
            setRightText(rightText);
        }

        rightTextEnableColor = typedArray.getColor(R.styleable.common_titleBar_rightTextColor,
                ContextCompat.getColor(getContext(), isLightStyle() ?
                        R.color.common_titleBar_light_right_enable : R.color.common_titleBar_dark_right_enable));
        setRightTextColorInt(rightTextEnableColor);

        rightTextDisableColor = typedArray.getColor(R.styleable.common_titleBar_rightTextDisableColor,
                ContextCompat.getColor(getContext(), isLightStyle() ?
                        R.color.common_titleBar_light_right_disable : R.color.common_titleBar_dark_right_disable));

        int rightSize = typedArray.getDimensionPixelSize(R.styleable.common_titleBar_rightTextSize, SizeUtils.sp2px(RIGHT_LEFT_DEFAULT_SIZE_SP));
        setRightTextSize(SizeUtils.px2sp(rightSize));
        if (!TextUtils.isEmpty(rightText) && rightText.length() > 1) {
            setRightTextSize(15);
        }

        int rightTextStyle = typedArray.getInt(R.styleable.common_titleBar_rightTextStyle, Typeface.NORMAL);
        setRightTextTypeface(rightTextStyle);
    }

    private void initBottomLine(TypedArray typedArray) {
        boolean isHideLine = typedArray.getBoolean(R.styleable.common_titleBar_hideLine, false);
        bottomLine.setVisibility(isHideLine ? GONE : VISIBLE);

        bottomLine.setBackgroundColor(ContextCompat.getColor(getContext(),
                isLightStyle() ? R.color.common_titleBar_light_line : R.color.common_titleBar_dark_line));
    }

    public void setStyle(@Style String style) {
        if (this.style.equals(style)) {
            return;
        }

        synchronized (this) {
            this.style = style;
        }

        setTitleBarBg();
        setLeftIconColor();
        setTitleColor();
        setRightViewColor();
        setBottomLineBg();
    }

    @Style
    public String getStyle() {
        synchronized (this) {
            return this.style;
        }
    }

    public void hideStatusOffset() {
        rootView.findViewById(R.id.common_titlebar_status_bg).setVisibility(GONE);
    }

    private void setTitleBarBg() {
        setBackgroundColor(titleBarBg != NO_SET ? titleBarBg : ContextCompat.getColor(getContext(),
                isLightStyle() ? R.color.common_titleBar_light_bg : R.color.common_titleBar_dark_bg));
    }

    private void setLeftIconColor() {
        setLeftIconColorInt(leftIconColor != NO_SET ? leftIconColor : ContextCompat.getColor(getContext(),
                isLightStyle() ? R.color.common_titleBar_light_content : R.color.common_titleBar_dark_content));
    }

    private void setTitleColor() {
        setTitleColorInt(titleColor != NO_SET ? titleColor : ContextCompat.getColor(getContext(),
                isLightStyle() ? R.color.common_titleBar_light_content : R.color.common_titleBar_dark_content));
    }

    private void setRightViewColor() {
        if (rightView.isEnabled()) {
            setRightTextColorInt(ContextCompat.getColor(getContext(),
                    isLightStyle() ? R.color.common_titleBar_light_right_enable : R.color.common_titleBar_dark_right_enable));
        } else {
            setRightTextColorInt(ContextCompat.getColor(getContext(),
                    isLightStyle() ? R.color.common_titleBar_light_right_disable : R.color.common_titleBar_dark_right_disable));
        }
    }

    private void setBottomLineBg() {
        bottomLine.setBackgroundColor(ContextCompat.getColor(getContext(),
                isLightStyle() ? R.color.common_titleBar_light_line : R.color.common_titleBar_dark_line));
    }

    public void setLeftIcon(@StringRes int resId) {
        if (null == leftView) {
            return;
        }

        if (leftView instanceof IconTextView) {
            ((IconTextView) leftView).setText(resId);
        }
    }

    public void setLeftIcon(CharSequence text) {
        if (null == leftView) {
            return;
        }

        if (leftView instanceof IconTextView) {
            ((IconTextView) leftView).setText(text);
        }
    }

    public void setLeftIconColor(@ColorRes int resId) {
        if (null == leftView) {
            return;
        }

        if (leftView instanceof IconTextView) {
            ((IconTextView) leftView).setTextColor(ContextCompat.getColor(getContext(), resId));
        }
    }

    public void setLeftIconColor(@NonNull String colorStr) {
        if (!colorStr.startsWith("#") || colorStr.length() != 7 || colorStr.length() != 9) {
            return;
        }

        setLeftIconColorInt(Color.parseColor(colorStr));
    }

    public void setLeftIconColorInt(@ColorInt int colorInt) {
        if (null == leftView) {
            return;
        }

        if (leftView instanceof IconTextView) {
            ((IconTextView) leftView).setTextColor(colorInt);
        }
    }

    public void setLeftIconSize(@IntRange(from = 1) int size) {
        if (null == leftView) {
            return;
        }

        if (leftView instanceof IconTextView) {
            ((IconTextView) leftView).setTextSize(size);
        }
    }

    public void setLeftView(@NonNull View view) {
        setLeftView(view, null);
    }

    public void setLeftView(@NonNull View view, LayoutParams layoutParams) {
        if (null != leftView) {
            ((ViewGroup) leftView.getParent()).removeView(leftView);
        }

        leftView = view;
        rootView.addView(view, getLayoutParams(view, layoutParams, RelativeLayout.ALIGN_PARENT_LEFT));
    }

    public void setLeftOnClickListener(@Nullable OnClickListener listener) {
        if (null == leftView) {
            return;
        }
        rootView.findViewById(R.id.common_titlebar_left_image).setOnClickListener(listener);
        leftView.setOnClickListener(listener);
    }

    public void setTitle(CharSequence text) {
        if (null == titleView) {
            return;
        }

        if (titleView instanceof IconTextView) {
            ((IconTextView) titleView).setText(text);
        }
    }

    public void setTitle(@StringRes int resId) {
        setTitle(getContext().getString(resId));
    }

    public CharSequence getTitle() {
        if (null == titleView) {
            return "";
        }

        if (titleView instanceof IconTextView) {
            return ((IconTextView) titleView).getText();
        }

        return "";
    }

    public void setTitleSize(float size) {
        if (null == titleView) {
            return;
        }

        if (titleView instanceof IconTextView) {
            ((IconTextView) titleView).setTextSize(size);
        }
    }

    public void setTitleColor(@ColorRes int resId) {
        if (null == titleView) {
            return;
        }

        if (titleView instanceof IconTextView) {
            ((IconTextView) titleView).setTextColor(ContextCompat.getColor(getContext(), resId));
        }
    }

    public void setTitleColorInt(@ColorInt int colorInt) {
        if (null == titleView) {
            return;
        }

        if (titleView instanceof IconTextView) {
            ((IconTextView) titleView).setTextColor(colorInt);
        }
    }

    public void setTitleTypeface(@IntRange(from = 0) int style) {
        if (null == titleView) {
            return;
        }

        if (titleView instanceof IconTextView) {
            ((IconTextView) titleView).setTypeface(style);
        }
    }

    public void setTitleView(@NonNull View view) {
        setTitleView(view, null);
    }

    public void setTitleView(@NonNull View view, LayoutParams layoutParams) {
        if (null != titleView) {
            ((ViewGroup) titleView.getParent()).removeView(titleView);
        }

        titleView = view;
        rootView.addView(view, getLayoutParams(view, layoutParams, RelativeLayout.CENTER_IN_PARENT));
    }

    public void setTitleOnClickListener(@Nullable OnClickListener listener) {
        if (null == titleView) {
            return;
        }

        titleView.setOnClickListener(listener);
    }

    public void setRightText(@StringRes int resId) {
        if (null == rightView) {
            return;
        }

        if (rightView instanceof IconTextView) {
            ((IconTextView) rightView).setText(resId);
        }
    }

    public void setRightText(CharSequence text) {
        if (null == rightView) {
            return;
        }

        if (rightView instanceof IconTextView) {
            ((IconTextView) rightView).setText(text);
        }
    }

    public void setRightTextColor(@ColorRes int resId) {
        if (null == rightView) {
            return;
        }

        if (rightView instanceof IconTextView) {
            ((IconTextView) rightView).setTextColor(ContextCompat.getColor(getContext(), resId));
        }
    }

    public void setRightTextColorInt(@ColorInt int colorInt) {

        if (null == rightView) {
            return;
        }

        if (rightView instanceof IconTextView) {
            ((IconTextView) rightView).setTextColor(colorInt);
        }
    }

    public void setRightTextSize(@IntRange(from = 1) int size) {
        if (null == rightView) {
            return;
        }

        if (rightView instanceof IconTextView) {
            ((IconTextView) rightView).setTextSize(size);

        }
    }

    public void setRightTextTypeface(@IntRange(from = 0) int style) {
        if (null == rightView) {
            return;
        }

        if (rightView instanceof IconTextView) {
            ((IconTextView) rightView).setTypeface(style);
        }
    }

    public void setRightView(@NonNull View view) {
        setRightView(view, null);
    }

    public void setRightView(@NonNull View view, LayoutParams layoutParams) {
        if (null != rightView) {
            ((ViewGroup) rightView.getParent()).removeView(rightView);
        }
        rightView = view;
        rootView.addView(view, getLayoutParams(view, layoutParams, RelativeLayout.ALIGN_PARENT_RIGHT));
    }

    public void setRightOnClickListener(@Nullable OnClickListener listener) {
        if (null == rightView) {
            return;
        }

        rightView.setOnClickListener(listener);
    }

    public void setRightEnable(boolean enable) {
        if (null == rightView) {
            return;
        }

        rightView.setEnabled(enable);
        setRightTextColorInt(enable ? rightTextEnableColor : rightTextDisableColor);
    }

    public void setLineVisibility(boolean isVisibility) {
        bottomLine.setVisibility(isVisibility ? VISIBLE : GONE);
    }

    public void setRightVisibility(boolean isVisibility) {
        rightView.setVisibility(isVisibility ? VISIBLE : GONE);
    }

    private LayoutParams getLayoutParams(@NonNull View view, LayoutParams layoutParams, int verb) {
        if (null == layoutParams) {
            layoutParams = (LayoutParams) view.getLayoutParams();
        }
        if (null == layoutParams) {
            layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        } else {
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        }
        layoutParams.addRule(verb);

        return layoutParams;
    }

    private boolean isLightStyle() {
        return Style.LIGHT.equals(getStyle());
    }

}
