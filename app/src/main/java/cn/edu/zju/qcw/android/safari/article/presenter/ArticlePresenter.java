package cn.edu.zju.qcw.android.safari.article.presenter;

import java.util.List;

import cn.edu.zju.qcw.android.base.BaseObserver;
import cn.edu.zju.qcw.android.common.club.ClubHelper;
import cn.edu.zju.qcw.android.safari.article.bean.ArticleBean;
import cn.edu.zju.qcw.android.safari.article.model.ArticleModel;
import cn.edu.zju.qcw.android.safari.article.view.ArticleFragment;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by SQ on 2017/3/27.
 */

public class ArticlePresenter implements ArticleInterface.IPresenter {

    private ArticleFragment mView;

    private ArticleInterface.IModel mModel;

    private final String ALL_ARTICLE = "0";

    public ArticlePresenter(ArticleFragment view) {
        mView = view;
        mModel = new ArticleModel();
    }

    @Override
    public void getNewData(String clubName) {
        mModel.loadData(ClubHelper.getInstance().getClubIdWithName(clubName), ALL_ARTICLE, new BaseObserver<List<ArticleBean>>() {
            @Override
            public void onNext(List<ArticleBean> value) {
                mView.reloadData(value);
            }

            @Override
            public void onError(Throwable e) {
                mView.onError();
            }
        });
    }

    @Override
    public void getMoreData(String clubName, String lastId) {
        mModel.loadData(ClubHelper.getInstance().getClubIdWithName(clubName), lastId, new BaseObserver<List<ArticleBean>>() {
            @Override
            public void onNext(List<ArticleBean> value) {
                mView.insertData(value);
            }

            @Override
            public void onError(Throwable e) {
                mView.onError();
            }
        });
    }
}
