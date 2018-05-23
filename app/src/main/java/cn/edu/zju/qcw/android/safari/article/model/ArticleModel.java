package cn.edu.zju.qcw.android.safari.article.model;

import java.util.List;

import cn.edu.zju.qcw.android.base.BaseObserver;
import cn.edu.zju.qcw.android.safari.article.bean.ArticleBean;
import cn.edu.zju.qcw.android.safari.article.presenter.ArticleInterface;
import cn.edu.zju.qcw.android.util.network.NetworkHelper;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by SQ on 2017/3/26.
 */

public class ArticleModel implements ArticleInterface.IModel {

    //接口实现
    interface ArticleApi {
        @GET("articlelist/{clubId}/{lastArticleId}")
        Observable<List<ArticleBean>> getArticleList(
                @Path("clubId") String clubId,
                @Path("lastArticleId") String lastId
        );
    }

    @Override
    public void loadData(String clubId, String lastId, BaseObserver<List<ArticleBean>> observer) {
        NetworkHelper.requestBuilder(ArticleApi.class)
                .getArticleList(clubId, lastId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
}
