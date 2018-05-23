package cn.edu.zju.qcw.android.parttime.list.presenter;

import java.util.List;

import cn.edu.zju.qcw.android.base.BaseObserver;
import cn.edu.zju.qcw.android.parttime.list.bean.ParttimeBean;
import cn.edu.zju.qcw.android.parttime.list.model.ParttimeListModel;
import cn.edu.zju.qcw.android.parttime.list.view.ParttimeListFragment;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by SQ on 2017/3/27.
 */

public class ParttimeListPresenter implements ParttimeListInterface.IPresenter {

    private ParttimeListFragment mView;

    private ParttimeListInterface.IModel mModel;

    public ParttimeListPresenter(ParttimeListFragment view) {
        mView = view;
        mModel = new ParttimeListModel();
    }

    @Override
    public void refreshWithKind(String kind) {
        if (kind == null) kind = "家教";
        mModel.loadData(kind, new BaseObserver<List<ParttimeBean>>() {
            @Override
            public void onNext(List<ParttimeBean> value) {
                mView.reloadData(value);
            }

            @Override
            public void onError(Throwable e) {
                mView.onError();
            }
        });
    }
}
