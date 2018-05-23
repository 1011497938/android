package cn.edu.zju.qcw.android.base.mvp_activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import butterknife.ButterKnife;
import cn.edu.zju.qcw.android.base.annotation.Style;
import cn.edu.zju.qcw.android.util.dialog.DialogUtil;
import com.avos.avoscloud.AVAnalytics;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by SQ on 2017/5/14.
 */

public abstract class BaseMvpActivty<P extends BaseMvpPresenter> extends SwipeBackActivity {

    private P mPresenter;

    @Style
    public String style = Style.LIGHT;
    protected ViewGroup mRootView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPresenter = initPresenter();

        setContentView(initLayout());

        // 获得 ContentView mRootView
        ViewGroup mContentView = (ViewGroup) this.findViewById(Window.ID_ANDROID_CONTENT);
        mRootView = (ViewGroup) mContentView.getChildAt(0);

        // 设置 沉浸式系统状态栏
        setSystemStatusBar(mRootView);

        ButterKnife.bind(this);

        init();

        initListeners();
    }

    @Override
    protected void onPause() {
        super.onPause();
        AVAnalytics.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AVAnalytics.onResume(this);
    }

    protected abstract int initLayout();

    protected abstract P initPresenter();

    protected abstract void init();

    protected abstract void initListeners();

    /**
     * 设置 Activity 主题
     */
    protected boolean isThemeStyle() {
        synchronized (this) {
            return Style.DARK.equals(this.style);
        }
    }

    protected boolean isImmersion() {
        return true;
    }

    public P getPresenter() {
        return mPresenter;
    }

    @Override
    protected void onDestroy() {
        if (null != mPresenter) {
            mPresenter.onDestroy();
            mPresenter = null;
        }
        if (DialogUtil.getInstance().isShowing()) {
            DialogUtil.getInstance().closeLoading();
        }

        super.onDestroy();
    }

    /**
     * 系统状态栏字体颜色设置
     */
    protected void setSystemStatusBar(View rootView) {
        // setFitsSystemWindows 设置
        ViewCompat.setFitsSystemWindows(rootView, !isImmersion());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0及以上
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4到5.0
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//android6.0以后可以对状态栏文字颜色和图标进行修改
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        setStatusBarStyle(!this.isThemeStyle());
    }

    protected void setStatusBarStyle(boolean isDarkStyle) {
        // 6.0+ 状态栏 浅色 显示
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (isDarkStyle) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_VISIBLE);
            }
        }

        // 兼容 小米
        Class<? extends Window> clazz = getWindow().getClass();
        try {
            int darkModeFlag;
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(getWindow(), !isDarkStyle ? 0 : darkModeFlag, darkModeFlag);
        } catch (Exception e) {
//            e.printStackTrace();
        }

        // 兼容 魅族
        try {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
            Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
            darkFlag.setAccessible(true);
            meizuFlags.setAccessible(true);
            int bit = darkFlag.getInt(null);
            int value = meizuFlags.getInt(lp);
            if (isDarkStyle) {
                value |= bit;
            } else {
                value &= ~bit;
            }

            meizuFlags.setInt(lp, value);
            getWindow().setAttributes(lp);
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }
}
