package cn.edu.zju.qcw.android.base.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Author:
 * Version: V0.1 2017/3/30
 */
public class IconmoonTextView extends TextView {

    public IconmoonTextView(Context context) {
        super(context);
        setTypeface(context);
    }

    public IconmoonTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface(context);
    }

    public IconmoonTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypeface(context);
    }

    private void setTypeface(Context context) {
        if (!isInEditMode()) {
            setTypeface(Typeface.createFromAsset(context.getAssets(), "icomoon.ttf"));
        }
    }

}
