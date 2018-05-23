package cn.edu.zju.qcw.android.activity.livelist.model;

import cn.edu.zju.qcw.android.base.BaseObserver;
import cn.edu.zju.qcw.android.base.mvp_activity.BaseMvpModel;
import com.avos.avoscloud.*;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by SQ on 2017/5/17.
 */

public class LiveListModel extends BaseMvpModel {
    public void getLiveList(FindCallback<AVObject> callback) {
        AVQuery<AVObject> query = AVQuery.getQuery("LiveRoom");
        query.include("user");
        query.include("user.head");
        query.include("user.head.url");
        query.addDescendingOrder("count");
        query.addDescendingOrder("createdAt");
        query.findInBackground(callback);
    }
}
