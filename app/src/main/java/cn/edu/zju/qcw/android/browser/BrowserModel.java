package cn.edu.zju.qcw.android.browser;

import android.text.TextUtils;
import cn.edu.zju.qcw.android.base.BaseObserver;
import cn.edu.zju.qcw.android.base.mvp_activity.BaseMvpModel;
import cn.edu.zju.qcw.android.safari.article.bean.ArticleBean;
import cn.edu.zju.qcw.android.util.date.DateUtil;
import cn.edu.zju.qcw.android.util.network.NetworkHelper;
import com.avos.avoscloud.*;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by SQ on 2017/5/17.
 */

class BrowserModel extends BaseMvpModel {
    void getLikeCount(String articleId, CountCallback callback) {
        AVQuery query = AVQuery.getQuery("ArticleLike");
        query.whereEqualTo("articleId", articleId);
        query.countInBackground(callback);
    }

    void getCommentCount(String articleId, CountCallback callback) {
        AVQuery query = AVQuery.getQuery("Comment");
        query.whereEqualTo("articleId", articleId);
        query.countInBackground(callback);
    }

    void likeOrNot(String articleId, GetCallback<AVObject> callback) {
        if (AVUser.getCurrentUser() == null) {
            callback.done(null, null);
            return;
        }
        AVQuery<AVObject> query = AVQuery.getQuery("ArticleLike");
        query.whereEqualTo("articleId", articleId);
        query.whereEqualTo("user", AVUser.getCurrentUser());
        query.getFirstInBackground(callback);
    }

    AVObject likeAction(String articleId) {
        AVObject likeObj = AVObject.create("ArticleLike");
        likeObj.put("articleId", articleId);
        likeObj.put("user", AVUser.getCurrentUser());
        likeObj.saveInBackground();
        return likeObj;
    }

    void getBtnData(String url, BaseObserver<ArticleBean> observer) {
        if (!url.contains("/")) return;
        String id = url.split("/")[Arrays.asList(url.split("/")).indexOf("id") + 1];
        NetworkHelper.requestBuilder(ArticleDataApi.class)
                .getArticleData(id)
                .map(new Function<ArticleBean, ArticleBean>() {
                    @Override
                    public ArticleBean apply(ArticleBean articleBeen) throws Exception {
                        if (!TextUtils.isEmpty(articleBeen.getTicketId())){
                            AVQuery<AVObject> query = AVQuery.getQuery("Ticket");
                            query.whereEqualTo("objectId", articleBeen.getTicketId());
                            AVObject ticket = query.getFirst();
                            Date beginDate = ticket.getDate("ticketBeginTime");
                            Date endDate = ticket.getDate("ticketEndTime");
                            if (beginDate == null || endDate == null) {
                                articleBeen.setBeginTicketDate("");
                                return articleBeen;
                            }
                            if (beginDate.getTime() > new Date().getTime()) {
                                articleBeen.setBeginTicketDate(DateUtil.date2String("MM月dd日 HH:mm  开始领票", beginDate));
                            }
                            if (endDate.getTime() < new Date().getTime()) {
                                articleBeen.setBeginTicketDate("领票已结束");
                            }
                            if (beginDate.getTime() < new Date().getTime() && endDate.getTime() > new Date().getTime()){
                                articleBeen.setBeginTicketDate("线上领票");
                            }
                        }
                        return articleBeen;
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);

    }

    interface ArticleDataApi {
        @GET("articleInfo/{articleId}")
        Observable<ArticleBean> getArticleData(@Path("articleId") String articleId);
    }
}
