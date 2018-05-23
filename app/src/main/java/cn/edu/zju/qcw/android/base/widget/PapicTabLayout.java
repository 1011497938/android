package cn.edu.zju.qcw.android.base.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

/**
 * Created by SQ on 12/08/2017.
 */

public class PapicTabLayout extends SmartTabLayout {
    public PapicTabLayout(Context context) {
        super(context);
    }

    public PapicTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PapicTabLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected TextView createDefaultTabView(CharSequence title) {
        TextView textView = super.createDefaultTabView(title);
        textView.setTypeface(Typeface.DEFAULT);
        return textView;
    }
}
