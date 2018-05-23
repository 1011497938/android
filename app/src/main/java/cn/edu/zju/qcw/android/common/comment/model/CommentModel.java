package cn.edu.zju.qcw.android.common.comment.model;

import android.text.TextUtils;
import cn.edu.zju.qcw.android.base.BaseObserver;
import cn.edu.zju.qcw.android.base.mvp_activity.BaseMvpModel;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by SQ on 2017/5/17.
 */

public class CommentModel extends BaseMvpModel {
    public AVObject latestObj;

    public void getComment(String url, BaseObserver<List<AVObject>> observer) {

        url = url.split("://")[1];//考虑网址以http或https的情况

        final String finalUrl = url;
        Observable.create(new ObservableOnSubscribe<List<AVObject>>() {
            @Override
            public void subscribe(ObservableEmitter<List<AVObject>> e) throws Exception {
                AVQuery<AVObject> query = AVQuery.getQuery("Comment");
                query.whereContains("urlStr", finalUrl);
                query.include("owner.username");
                query.include("owner.head");
                query.include("owner.head.url");
                query.orderByDescending("createdAt");
                e.onNext(query.find());
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void saveComment(String url, String comment, SaveCallback callback) {

        String articleId = null;

        List<String> list = Arrays.asList(url.split("/"));
        int index = list.indexOf("id")+1;
        if (list.size() > index) {
            articleId = list.get(index);
        }
        if (TextUtils.isEmpty(articleId)) return;
        AVObject object = AVObject.create("Comment");
        object.put("articleId", articleId);
        object.put("owner", AVUser.getCurrentUser());
        object.put("urlStr", url);
        object.put("content", comment);
        object.setFetchWhenSave(true);
        object.saveEventually(callback);
        latestObj = object;
    }
}
