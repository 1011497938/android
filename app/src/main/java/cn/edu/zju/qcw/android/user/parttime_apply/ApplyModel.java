package cn.edu.zju.qcw.android.user.parttime_apply;

import android.text.TextUtils;
import cn.edu.zju.qcw.android.base.BaseObserver;
import cn.edu.zju.qcw.android.base.mvp_activity.BaseMvpModel;
import cn.edu.zju.qcw.android.parttime.list.bean.ParttimeBean;
import cn.edu.zju.qcw.android.safari.selection.model.SelectionModel;
import cn.edu.zju.qcw.android.util.network.NetworkHelper;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by SQ on 2017/5/23.
 */

public class ApplyModel extends BaseMvpModel {

    interface ParttimeInfoApi {
        @GET("parttime")
        Observable<List<ParttimeBean>> getParttime();
    }

    public void getUserParttime(final BaseObserver<List<ParttimeBean>> aObserver) {
        Observable.create(new ObservableOnSubscribe<List<AVObject>>() {
            @Override
            public void subscribe(ObservableEmitter<List<AVObject>> e) throws Exception {
                AVQuery<AVObject> query = AVQuery.getQuery("Parttime");
                query.whereEqualTo("owner", AVUser.getCurrentUser());
                query.addDescendingOrder("createdAt");
                e.onNext(query.find());
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<List<AVObject>>() {
                    @Override
                    public void onNext(List<AVObject> value) {
                        String Ids = "";
                        for (AVObject object : value) {
                            Ids = Ids + "/" + object.getString("NO");
                        }
                        Ids = Ids + "/";
                        OkHttpClient client = new OkHttpClient();
                        client.newBuilder().connectTimeout(NetworkHelper.TIME_OUT, TimeUnit.SECONDS);
                        new Retrofit.Builder()
                                .addConverterFactory(GsonConverterFactory.create())
                                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                                .baseUrl(NetworkHelper.SERVER_URL + "parttime/" + Ids)
                                .client(client)
                                .build()
                                .create(ParttimeInfoApi.class)
                                .getParttime()
                                .map(new Function<List<ParttimeBean>, List<ParttimeBean>>() {
                                    @Override
                                    public List<ParttimeBean> apply(List<ParttimeBean> parttimeBeen) throws Exception {
                                        List<ParttimeBean> list = new ArrayList<ParttimeBean>();
                                        for (ParttimeBean parttimeBean : parttimeBeen) {
                                            if (parttimeBean != null && !TextUtils.isEmpty(parttimeBean.getTitle())) {
                                                list.add(parttimeBean);
                                            }
                                        }
                                        return list;
                                    }
                                })
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(aObserver);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                });
    }
}
