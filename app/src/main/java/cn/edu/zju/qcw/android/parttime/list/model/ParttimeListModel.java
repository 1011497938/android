package cn.edu.zju.qcw.android.parttime.list.model;

import java.util.List;

import cn.edu.zju.qcw.android.parttime.list.bean.ParttimeBean;
import cn.edu.zju.qcw.android.parttime.list.presenter.ParttimeListInterface;
import cn.edu.zju.qcw.android.util.network.NetworkHelper;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by SQ on 2017/3/27.
 */

public class ParttimeListModel implements ParttimeListInterface.IModel {
    interface ParttimeApi {
        @GET("parttime/valid/{kind}")
        Observable<List<ParttimeBean>> getParttime(@Path("kind") String kind);
    }

    @Override
    public void loadData(String kind, Observer<List<ParttimeBean>> observer) {
        NetworkHelper.requestBuilder(ParttimeApi.class)
                .getParttime(kind)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
}
