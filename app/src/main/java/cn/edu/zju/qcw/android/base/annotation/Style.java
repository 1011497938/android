package cn.edu.zju.qcw.android.base.annotation;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Author: Shper
 * Version: V0.1 2017/7/17
 */
@StringDef({Style.LIGHT, Style.DARK})
@Retention(RetentionPolicy.SOURCE)
public @interface Style {
    String LIGHT = "light";
    String DARK = "dark";
}