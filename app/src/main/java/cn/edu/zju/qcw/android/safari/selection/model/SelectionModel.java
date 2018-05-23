package cn.edu.zju.qcw.android.safari.selection.model;

import cn.edu.zju.qcw.android.common.club.ClubHelper;
import cn.edu.zju.qcw.android.common.entity.ClubInfoEntity;
import cn.edu.zju.qcw.android.safari.selection.bean.SelectionArticleBean;
import cn.edu.zju.qcw.android.safari.selection.bean.SelectionBean;
import cn.edu.zju.qcw.android.safari.selection.SelectionInterface;
import cn.edu.zju.qcw.android.util.network.NetworkHelper;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.net.sip.SipErrorCode.TIME_OUT;

/**
 * Created by SQ on 2017/3/26.
 */

public class SelectionModel implements SelectionInterface.IModel {

    //接口实现
    interface SelectionApi {
        @GET("selectionlist/{lastId}")
        Observable<SelectionBean> getSelection(@Path("lastId") String lastId);
    }

    //接口实现
    interface SubscribeApi {
        @GET("{lastItemId}")
        Observable<List<SelectionArticleBean>> getSubscribe(@Path("lastItemId") String lastItemId);
    }

    @Override
    public void loadData(String lastItemId, Observer<SelectionBean> observer) {
        NetworkHelper.requestBuilder(SelectionApi.class)
                .getSelection(lastItemId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    @Override
    public void loadSubscribeData(String lastItemId, Observer<List<SelectionArticleBean>> observer) {

        String clubIds = "";
        for (ClubInfoEntity entity : ClubHelper.getInstance().getUserFollowList()) {
            clubIds = clubIds + String.valueOf(entity.getId()) + "/";
        }

        OkHttpClient client = new OkHttpClient();
        client.newBuilder().connectTimeout(NetworkHelper.TIME_OUT, TimeUnit.SECONDS);

        new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(NetworkHelper.SERVER_URL + "subscribe/" + clubIds)
                .client(client)
                .build()
                .create(SubscribeApi.class)
                .getSubscribe(lastItemId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }


}
