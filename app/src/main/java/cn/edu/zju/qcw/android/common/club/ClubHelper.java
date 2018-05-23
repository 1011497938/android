package cn.edu.zju.qcw.android.common.club;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import cn.edu.zju.qcw.android.PapicApp;
import cn.edu.zju.qcw.android.base.BaseObserver;
import cn.edu.zju.qcw.android.common.IM.ImHelper;
import cn.edu.zju.qcw.android.common.entity.ClubInfoEntity;
import cn.edu.zju.qcw.android.common.entity.UserFollowEntity;
import cn.edu.zju.qcw.android.common.event.MoveClubEvent;
import cn.edu.zju.qcw.android.common.event.UpdateClubListEvent;
import cn.edu.zju.qcw.android.common.event.UpdateSafariTabEvent;
import cn.edu.zju.qcw.android.push.AliNullCallback;
import cn.edu.zju.qcw.android.safari.SafariFragment;
import cn.edu.zju.qcw.android.util.Logger;
import cn.edu.zju.qcw.android.util.network.NetworkHelper;
import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.avos.avoscloud.*;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.schedulers.Schedulers;
import org.greenrobot.eventbus.EventBus;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.*;

/**
 * Created by SQ on 2017/4/8.
 */

public class ClubHelper {

    private volatile static ClubHelper instance;

    private List<ClubInfoEntity> userFollowList;

    private List<ClubInfoEntity> userUnFollowList;

    private Map<String, Boolean> userPushSetting = new HashMap<>();

    private boolean isLogin = ImHelper.getInstance().isImOnline();

    public static ClubHelper getInstance() {
        if (instance == null) {
            synchronized (ClubHelper.class) {
                if (instance == null) {
                    instance = new ClubHelper();
                }
            }
        }
        return instance;
    }

    public List<ClubInfoEntity> getUserFollowList() {
        return userFollowList;
    }

    public List<ClubInfoEntity> getUserUnFollowList() {
        return userUnFollowList;
    }

    //整个应用生命周期只调用一次
    public void initFollowClubList() {
        Observable.zip(clubInfoObservable(), followIdObservable(),
                new BiFunction<List<ClubInfoEntity>, List<String>, List<List<ClubInfoEntity>>>() {
                    //获取关注列表
                    @Override
                    public List<List<ClubInfoEntity>> apply(List<ClubInfoEntity> allClubInfo, List<String> clubId) throws Exception {
                        //TODO:将allClubinfo缓存
                        List<ClubInfoEntity> followList = new ArrayList<ClubInfoEntity>();
                        List<List<ClubInfoEntity>> list = new ArrayList<List<ClubInfoEntity>>();
                        for (String id : clubId) {
                            for (ClubInfoEntity clubAbstract : allClubInfo) {
                                if (Long.valueOf(id) == clubAbstract.getId()) {
                                    followList.add(clubAbstract);
                                    allClubInfo.remove(clubAbstract);
                                    break;
                                }
                            }
                        }
                        list.add(followList);//关注列表
                        list.add(allClubInfo);//非关注
                        userFollowList = followList;
                        userUnFollowList = allClubInfo;
                        //TODO:将FollowIDlist缓存
                        return list;
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<List<List<ClubInfoEntity>>>() {
                    @Override
                    public void onNext(List<List<ClubInfoEntity>> value) {
                        EventBus.getDefault().post(new UpdateClubListEvent());
                        updateSafariTab();
                    }

                    @Override
                    public void onError(Throwable e) {
                        SafariFragment.getInstance().showError();
                    }
                });
        //TODO:错误处理
    }

    @Nullable
    public String getClubIdWithName(String clubName) {
        if (userFollowList == null || userUnFollowList == null) return null;
        for (ClubInfoEntity clubInfoEntity : userFollowList) {
            if (clubInfoEntity.getShortname().equals(clubName)) {
                return String.valueOf(clubInfoEntity.getId());
            }
        }
        for (ClubInfoEntity clubInfoEntity : userUnFollowList) {
            if (clubInfoEntity.getShortname().equals(clubName)) {
                return String.valueOf(clubInfoEntity.getId());
            }
        }
        return null;
    }

    /**
     * 检查本地缓存是否需要更新
     */

    private Observable<List<ClubInfoEntity>> clubInfoObservable() {
        //先网络获取，如果是最新的数据或网络错误，加载缓存
        //TODO:如果没有本地缓存且网络错误？能否显示错误提示？

        return NetworkHelper.requestBuilder(ClubAbstractInfoApi.class)
                .getClubAbstract("0");//1491645637
    }

    /**
     * 关注Id Observable
     *
     * @return 任何情况都会返回一个List<String>
     */
    private Observable<List<String>> followIdObservable() {
        //TODO:磁盘缓存
//        Observable<List<String>> db_local = Observable.create(new ObservableOnSubscribe<List<String>>() {
//            @Override
//            public void subscribe(ObservableEmitter<List<String>> e) throws Exception {
//                if () {
//
//                }else {
//                    subscriber.onComplete();
//                }
//            }
//        });
        //从Installation表获取关注数据
        Observable<List<String>> follow_leancloud = Observable.create(new ObservableOnSubscribe<List<String>>() {
            @Override
            public void subscribe(ObservableEmitter<List<String>> e) throws Exception {
                if (AVUser.getCurrentUser() != null) {
                    AVQuery<AVObject> query = new AVQuery<>("_User");
                    query.whereEqualTo("objectId", AVUser.getCurrentUser().getObjectId());
                    query.selectKeys(Arrays.asList("followClubs"));
                    try {
                        AVObject avObject = query.getFirst();
                        if (avObject == null) {
                            e.onComplete();
                            return;
                        }
                        List<List> followClubs = new ArrayList<List>();
                        if (avObject.getList("followClubs") != null) {
                            followClubs = avObject.getList("followClubs");
                        }
                        if (followClubs.isEmpty()) {
                            e.onComplete();
                        } else {
                            List<UserFollowEntity> followData = new ArrayList<>();
                            List<String> followIds = new ArrayList<>();
                            Map<String, Boolean> pushMap = new HashMap<>();
                            for (List followClub : followClubs) {
                                String id = (String) followClub.get(0);
                                followIds.add(id);

                                UserFollowEntity entity = new UserFollowEntity();
                                entity.setClubId(id);
                                entity.setPush((boolean) followClub.get(1));
                                followData.add(entity);

                                pushMap.put(id, (Boolean) followClub.get(1));
                            }
                            userPushSetting = pushMap;
                            e.onNext(followIds);
                        }
                    } catch (AVException avError) {
                        e.onComplete();
                    }
                } else {
                    e.onComplete();
                }
            }
        });
        //Recommend默认列表
        Observable<List<String>> default_follow = Observable.create(new ObservableOnSubscribe<List<String>>() {
            @Override
            public void subscribe(ObservableEmitter<List<String>> e) throws Exception {
                List<String> channels = new ArrayList<String>();
                channels.add("2");//勤创
                //TODO:从所有社团表中查询recommend
                //TODO:缓存到本地
                e.onNext(channels);
            }
        });

        return Observable.concat(follow_leancloud, default_follow);
    }

    /**
     * 用户行为接口
     */

    public void syncData() {
        //Installation
        AVInstallation.getCurrentInstallation().saveInBackground();
        //User->follow,从userFollowList拿到关注列表，然后通过userFollowSetting拿到推送开关
        if (userPushSetting != null && userPushSetting.size() > 0) {
            List userFollow = new ArrayList<>();
            for (ClubInfoEntity clubInfoEntity : userFollowList) {//以userFollowList的顺序为准
                List temp = new ArrayList<>();
                if (userPushSetting.containsKey(String.valueOf(clubInfoEntity.getId()))) {
                    temp.add(String.valueOf(clubInfoEntity.getId()));
                    temp.add(userPushSetting.get(String.valueOf(clubInfoEntity.getId())));
                } else {
                    temp.add(String.valueOf(clubInfoEntity.getId()));
                    temp.add(true);
                }
                userFollow.add(temp);
            }
            AVUser.getCurrentUser().put("followClubs", userFollow);
            AVUser.getCurrentUser().saveInBackground();

            //更新onesdk tag
            oneSdkRegisterTags();
        }
        //TODO:本地缓存
        //刷新Tab
        updateSafariTab();
    }

    /**
     * 无论是关注、取消关注，都会调用这个方法
     */
    public void userFollowAction(String clubId) {
        Logger.d("user follow/unfollow action, club id:", clubId);
        if (isFollow(clubId)) {
            unFollowClub(clubId);
            //TODO:更新userFollowList
        } else {
            followClub(clubId);
            //TODO:更新userFollowList
        }
    }

    /**
     * 无论是打开、关闭推送，都会调用这个方法
     */
    public void userPushAction(String clubId, boolean openPush) {
        if (openPush)
            openPush(clubId);
        else
            closePush(clubId);
    }

    public boolean isFollow(String clubId) {
        for (ClubInfoEntity entity : userFollowList) {
            if (clubId.equals(String.valueOf(entity.getId()))) {
                return true;
            }
        }
        return false;
    }

    public boolean isPushOpened(String clubId) {
        if (isFollow(clubId)) {
            if (!userPushSetting.containsKey(clubId)) {
                return true;
            }
            return userPushSetting.get(clubId);
        } else {
            return false;
        }
    }

    private void openPush(String clubId) {
        //TODO:更新缓存
        userPushSetting.put(clubId, true);
        //leancloud更新订阅信息
        AVInstallation.getCurrentInstallation().addUnique("channels", clubId);
    }

    private void closePush(String clubId) {
        //TODO:更新缓存
        userPushSetting.put(clubId, false);
        //leancloud更新订阅信息
        PushService.unsubscribe(PapicApp.getAppContext(), clubId);
    }

    private void unFollowClub(String clubId) {
        Logger.d("unfollow club:", clubId);
        //TODO:更新缓存
        //更新userFollowList
        for (ClubInfoEntity clubInfoEntity : userFollowList) {
            if (clubInfoEntity.getId() == Long.valueOf(clubId)) {
                userUnFollowList.add(
                        0,
                        userFollowList.remove(userFollowList.indexOf(clubInfoEntity))
                );
                break;
            }
        }
        //从User表删除
        if (userPushSetting.containsKey(clubId)) {
            userPushSetting.remove(clubId);
        }
        //后台管理系统
        NetworkHelper.requestBuilder(ClubUnFollowApi.class)
                .updateClubUnFollowData(clubId)
                .subscribeOn(Schedulers.newThread())
                .subscribe(new BaseObserver<List>());
        //更新UI
        EventBus.getDefault().post(new MoveClubEvent(MoveClubEvent.REMOVE, Long.valueOf(clubId)));
    }

    private void followClub(String clubId) {
        Logger.d("follow club:", clubId);
        //TODO:更新缓存
        //TODO:这里可能需要触发openPush
        //更新userFollowList
        for (ClubInfoEntity clubInfoEntity : userUnFollowList) {
            if (clubInfoEntity.getId() == Long.valueOf(clubId)) {
                userFollowList.add(
                        userUnFollowList.remove(
                                userUnFollowList.indexOf(clubInfoEntity)
                        )
                );
                break;
            }
        }
        //从User表添加
        if (!userPushSetting.containsKey(clubId)) {
            userPushSetting.put(clubId, true);
        }
        //后台管理系统
        NetworkHelper.requestBuilder(ClubFollowApi.class)
                .updateClubFollowData(clubId)
                .subscribeOn(Schedulers.newThread())
                .subscribe(new BaseObserver<List>());
        //更新UI
        EventBus.getDefault().post(new MoveClubEvent(MoveClubEvent.ADD, Long.valueOf(clubId)));
    }

    private void updateSafariTab() {
        if (userFollowList == null) return;
        List<String> nameList = new ArrayList<String>();
        for (ClubInfoEntity clubInfoEntity : userFollowList) {
            nameList.add(clubInfoEntity.getShortname());
        }
        EventBus.getDefault().post(new UpdateSafariTabEvent(nameList));
    }

    private void oneSdkRegisterTags() {
        PushServiceFactory.getCloudPushService().listTags(CloudPushService.DEVICE_TARGET, new CommonCallback() {
            @Override
            public void onSuccess(String s) {
                if (TextUtils.isEmpty(s)) return;
                Logger.d("list devices banded tags:", s);

                if (s.contains(",") && s.split(",").length > 0) {
                    PushServiceFactory.getCloudPushService().unbindTag(
                            CloudPushService.DEVICE_TARGET,
                            s.split(","),
                            "",
                            new CommonCallback() {
                                @Override
                                public void onSuccess(String s) {
                                    if (TextUtils.isEmpty(s)) return;

                                    Logger.d("unbind all tags succeed:", s);
                                    List<String> clubArray = new ArrayList<>();
                                    for (Map.Entry<String, Boolean> entry : userPushSetting.entrySet()) {
                                        if (entry.getValue()) {
                                            clubArray.add(entry.getKey());
                                        }
                                    }
                                    Logger.d("register push tags: ", clubArray.toString());
                                    PushServiceFactory.getCloudPushService().bindTag(
                                            CloudPushService.DEVICE_TARGET,
                                            clubArray.toArray(new String[clubArray.size()]),
                                            "",
                                            new AliNullCallback()
                                    );

                                }

                                @Override
                                public void onFailed(String s, String s1) {

                                }
                            }
                    );
                    return;
                }
                List<String> clubArray = new ArrayList<>();
                for (Map.Entry<String, Boolean> entry : userPushSetting.entrySet()) {
                    if (entry.getValue()) {
                        clubArray.add(entry.getKey());
                    }
                }
                Logger.d("register push tags: ", clubArray.toString());
                PushServiceFactory.getCloudPushService().bindTag(
                        CloudPushService.DEVICE_TARGET,
                        clubArray.toArray(new String[clubArray.size()]),
                        "",
                        new AliNullCallback()
                );
            }

            @Override
            public void onFailed(String s, String s1) {

            }
        });

    }

    interface ClubAbstractInfoApi {
        @GET("club/{updateDate}")
        Observable<List<ClubInfoEntity>> getClubAbstract(@Path("updateDate") String updateAt);
    }

    interface ClubFollowApi {
        @GET("follow/{clubId}")
        Observable<List> updateClubFollowData(@Path("clubId") String clubId);
    }

    interface ClubUnFollowApi {
        @GET("unfollow/{clubId}")
        Observable<List> updateClubUnFollowData(@Path("clubId") String clubId);
    }
}
