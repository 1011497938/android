package cn.edu.zju.qcw.android.activity.history.presenter;

import cn.edu.zju.qcw.android.activity.history.model.HistoryModel;
import cn.edu.zju.qcw.android.activity.history.view.HistoryActivity;
import cn.edu.zju.qcw.android.activity.list.bean.ActivityBean;
import cn.edu.zju.qcw.android.base.BaseObserver;
import cn.edu.zju.qcw.android.base.mvp_activity.BaseMvpPresenter;
import cn.edu.zju.qcw.android.util.date.DateUtil;
import cn.edu.zju.qcw.android.util.toast.ToastHelper;

import java.util.List;

/**
 * Created by SQ on 2017/5/17.
 */

public class HistoryPresenter extends BaseMvpPresenter<HistoryActivity, HistoryModel> {

    public HistoryPresenter(HistoryActivity view) {
        super(view);
    }

    @Override
    public HistoryModel initModel() {
        return new HistoryModel();
    }

    public void getData() {
        getModel().getActivityHistory(new BaseObserver<List<ActivityBean>>() {
            @Override
            public void onNext(List<ActivityBean> value) {
                if (!isActivityBind()) return;
                getView().changeEmptyView();
                getView().getAdapter().setNewData(value);
            }

            @Override
            public void onError(Throwable e) {
                if (!isActivityBind()) return;
                ToastHelper.showShortToast("哎呀出错了，检查下网络吧~");
                getView().onBackPressed();
            }
        });
    }

    public void getMoreData() {
        Long timeStamp = DateUtil.string2Date("yyyy-MM-dd HH:mm:ss", getView().getAdapter().getData().get(getView().getAdapter().getData().size() - 1).getTime()).getTime() / 1000;
        getModel().getMoreData(timeStamp, new BaseObserver<List<ActivityBean>>() {
            @Override
            public void onNext(List<ActivityBean> value) {
                if (!isActivityBind()) return;
                getView().loadMoreFinish(value);
            }

            @Override
            public void onError(Throwable e) {
                if (!isActivityBind()) return;
                getView().loadMoreFailed();
            }
        });
    }
}
