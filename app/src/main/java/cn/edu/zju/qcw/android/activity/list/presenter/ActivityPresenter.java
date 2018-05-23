package cn.edu.zju.qcw.android.activity.list.presenter;

import cn.edu.zju.qcw.android.activity.list.bean.ActivityApiBean;
import cn.edu.zju.qcw.android.activity.list.bean.ActivityBean;
import cn.edu.zju.qcw.android.activity.list.bean.ActivityListItem;
import cn.edu.zju.qcw.android.activity.list.model.ActivityModel;
import cn.edu.zju.qcw.android.activity.list.view.ActivityListFragment;
import cn.edu.zju.qcw.android.base.BaseObserver;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SQ on 2017/3/27.
 */

public class ActivityPresenter implements ActivityInterface.IPresenter {

    private ActivityListFragment mView;

    private ActivityInterface.IModel mModel;

    public ActivityPresenter(ActivityListFragment view) {
        mView = view;
        mModel = new ActivityModel();
    }

    @Override
    public void getNewData() {
        mModel.loadData(new BaseObserver<List<ActivityListItem>>(){
            @Override
            public void onNext(List<ActivityListItem> value) {
                if(mView == null) return;
                mView.reloadData(value);
            }

            @Override
            public void onError(Throwable e) {
                if(mView == null) return;
                mView.onError();
            }
        });
    }

    @Override
    public void getMoreData(String lastItemId) {

    }
}
