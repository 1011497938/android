package cn.edu.zju.qcw.android.user.parttime_apply;

import cn.edu.zju.qcw.android.base.BaseObserver;
import cn.edu.zju.qcw.android.base.mvp_activity.BaseMvpPresenter;
import cn.edu.zju.qcw.android.parttime.list.bean.ParttimeBean;
import cn.edu.zju.qcw.android.util.String.StringUtil;
import cn.edu.zju.qcw.android.util.toast.ToastHelper;

import java.util.List;

/**
 * Created by SQ on 2017/5/23.
 */

public class ApplyPresenter extends BaseMvpPresenter<ApplyActivity, ApplyModel> {
    public ApplyPresenter(ApplyActivity view) {
        super(view);
    }

    @Override
    public ApplyModel initModel() {
        return new ApplyModel();
    }

    public void getData() {
        getModel().getUserParttime(new BaseObserver<List<ParttimeBean>>() {
            @Override
            public void onNext(List<ParttimeBean> value) {
                if (!isActivityBind()) return;
                getView().changeEmptyView();
                getView().getAdapter().setNewData(value);
            }

            @Override
            public void onError(Throwable e) {
                if (!isActivityBind()) return;
                ToastHelper.showShortToast(StringUtil.genUnhappyFace() + "哎呀出错了，检查下网络吧~");
                getView().onBackPressed();
            }
        });
    }
}
