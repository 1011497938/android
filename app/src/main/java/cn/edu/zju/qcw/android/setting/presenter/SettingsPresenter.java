package cn.edu.zju.qcw.android.setting.presenter;

import cn.edu.zju.qcw.android.base.mvp_activity.BaseMvpPresenter;
import cn.edu.zju.qcw.android.setting.model.SettingsModel;
import cn.edu.zju.qcw.android.setting.view.SettingsActivity;

/**
 * Created by SQ on 2017/5/21.
 */

public class SettingsPresenter extends BaseMvpPresenter<SettingsActivity, SettingsModel> {

    public SettingsPresenter(SettingsActivity view) {
        super(view);
    }

    @Override
    public SettingsModel initModel() {
        return new SettingsModel();
    }
}
