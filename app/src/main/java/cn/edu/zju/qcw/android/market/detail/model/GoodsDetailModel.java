package cn.edu.zju.qcw.android.market.detail.model;

import cn.edu.zju.qcw.android.base.mvp_activity.BaseMvpModel;
import cn.edu.zju.qcw.android.base.BaseNullAvObject;
import cn.edu.zju.qcw.android.base.BaseObserver;
import cn.edu.zju.qcw.android.market.detail.bean.GoodsDetailBean;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.schedulers.Schedulers;

import java.util.Arrays;

/**
 * Created by SQ on 2017/5/14.
 */

public class GoodsDetailModel extends BaseMvpModel {
    public void getData(final String goodsId, BaseObserver<GoodsDetailBean> baseObserver){
        Observable<AVObject> likeObj = Observable.create(new ObservableOnSubscribe<AVObject>() {
            @Override
            public void subscribe(ObservableEmitter<AVObject> e) throws Exception {
                if (AVUser.getCurrentUser() == null) {
                    e.onNext(new BaseNullAvObject());
                }else{
                    AVQuery<AVObject> query = new AVQuery<>("Like");
                    query.whereEqualTo("GoodId", AVObject.createWithoutData("Goods", goodsId));
                    query.whereEqualTo("UserId", AVUser.getCurrentUser());
                    AVObject obj = query.getFirst();
                    if (obj == null){
                        e.onNext(new BaseNullAvObject());
                    }else{
                        e.onNext(obj);
                    }
                }
            }
        }).subscribeOn(Schedulers.newThread());

        Observable<AVObject> goodsObj = Observable.create(new ObservableOnSubscribe<AVObject>() {
            @Override
            public void subscribe(ObservableEmitter<AVObject> e) throws Exception {
                AVQuery<AVObject> query = new AVQuery<>("Goods");
                query.whereEqualTo("objectId", goodsId);
                query.include("image");
                e.onNext(query.getFirst());
            }
        }).subscribeOn(Schedulers.newThread());

        Observable.zip(likeObj, goodsObj, new BiFunction<AVObject, AVObject, GoodsDetailBean>() {
            @Override
            public GoodsDetailBean apply(AVObject likeObj, AVObject goodsObj) throws Exception {
                AVQuery<AVUser> query = new AVQuery<>("_User");
                query.whereEqualTo("objectId", goodsObj.getAVObject("creatBy").getObjectId());
                query.selectKeys(Arrays.asList("mobilePhoneNumber", "username", "head"));
                return new GoodsDetailBean(goodsObj, likeObj, query.getFirst());
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(baseObserver);
    }
}
