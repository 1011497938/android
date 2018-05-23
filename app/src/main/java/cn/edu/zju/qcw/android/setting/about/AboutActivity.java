package cn.edu.zju.qcw.android.setting.about;

import android.widget.TextView;
import cn.edu.zju.qcw.android.BuildConfig;
import cn.edu.zju.qcw.android.R;
import cn.edu.zju.qcw.android.base.mvp_activity.BaseMvpActivty;
import cn.edu.zju.qcw.android.base.mvp_activity.BaseMvpPresenter;

public class AboutActivity extends BaseMvpActivty {

    @Override
    protected boolean isThemeStyle() {
        return true;
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_about;
    }

    @Override
    protected BaseMvpPresenter initPresenter() {
        return null;
    }

    @Override
    protected void init() {
        TextView versionText = (TextView) findViewById(R.id.version);
        versionText.setText("版本" + BuildConfig.VERSION_NAME);
    }

    @Override
    protected void initListeners() {

    }

}
