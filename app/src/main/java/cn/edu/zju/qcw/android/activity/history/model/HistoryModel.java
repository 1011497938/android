package cn.edu.zju.qcw.android.activity.history.model;

import cn.edu.zju.qcw.android.activity.list.bean.ActivityBean;
import cn.edu.zju.qcw.android.base.mvp_activity.BaseMvpModel;
import cn.edu.zju.qcw.android.util.network.NetworkHelper;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.Date;
import java.util.List;

/**
 * Created by SQ on 2017/5/17.
 */

public class HistoryModel extends BaseMvpModel {
    interface ActivityHistoryApi {
        @GET("pastActivity/{lastTimeStamp}")
        Observable<List<ActivityBean>> getActivityList(@Path("lastTimeStamp") String lastTimeStamp);
    }

    public void getActivityHistory(Observer<List<ActivityBean>> observer) {
        NetworkHelper.requestBuilder(ActivityHistoryApi.class)
                .getActivityList(String.valueOf(new Date().getTime() / 1000))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void getMoreData(Long timeStamp, Observer<List<ActivityBean>> observer) {
        NetworkHelper.requestBuilder(ActivityHistoryApi.class)
                .getActivityList(String.valueOf(timeStamp))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
}
