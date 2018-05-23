package cn.edu.zju.qcw.android.base.mvp_activity;

/**
 * Created by SQ on 2017/5/14.
 */

public abstract class BaseMvpPresenter<A extends BaseMvpActivty, M extends BaseMvpModel> {
    private A mView;

    private M mModel;

    public A getView() {
        return mView;
    }


    public M getModel() {
        return mModel;
    }

    public BaseMvpPresenter(A view) {
        this.mView = view;
        this.mModel = initModel();
    }

    public abstract M initModel();

    public void onDestroy(){
        mModel = null;
    }

    public boolean isActivityBind() {
        return mView != null;
    }
}
