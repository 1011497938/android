package cn.edu.zju.qcw.android.common.IM;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import cn.edu.zju.qcw.android.base.BaseObserver;
import cn.edu.zju.qcw.android.common.event.UpdateBadgeEvent;
import cn.edu.zju.qcw.android.util.network.NetworkHelper;
import com.avos.avoscloud.*;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.rong.imkit.RongIM;
import io.rong.imkit.manager.IUnReadMessageObserver;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import org.greenrobot.eventbus.EventBus;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by SQ on 2017/5/18.
 */

public class ImHelper {

    private volatile static ImHelper instance;

    private boolean imOnline = false;

    public boolean isImOnline() {
        return imOnline;
    }


    public static ImHelper getInstance() {
        if (instance == null) {
            synchronized (ImHelper.class) {
                if (instance == null) {
                    instance = new ImHelper();
                }
            }
        }
        return instance;
    }

    public ImHelper() {
        final Conversation.ConversationType[] conversationTypes = {

                Conversation.ConversationType.PRIVATE,

                Conversation.ConversationType.GROUP, Conversation.ConversationType.SYSTEM,

                Conversation.ConversationType.PUBLIC_SERVICE, Conversation.ConversationType.APP_PUBLIC_SERVICE

        };
        RongIM.getInstance().addUnReadMessageCountChangedObserver(new IUnReadMessageObserver() {
            @Override
            public void onCountChanged(int i) {
                EventBus.getDefault().post(new UpdateBadgeEvent());
            }
        }, conversationTypes);
    }

    public void setUserInfoProvider() {
        RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {
            @Override
            public UserInfo getUserInfo(final String userId) {
                UserInfo userInfo = new UserInfo(userId, "", Uri.EMPTY);
                updateUserInfo(userId);
                return userInfo;
            }
        }, true);
    }

    public static void startPrivateConversation(Context context, String targetId, String title) {
        RongIM.getInstance().startConversation(context, Conversation.ConversationType.PRIVATE, targetId, TextUtils.isEmpty(title) ? "消息" : title);
    }

    public void loginIm() {
        if (AVUser.getCurrentUser() != null && !isImOnline()) {
            AVUser.getCurrentUser().fetchInBackground(new GetCallback<AVObject>() {
                @Override
                public void done(AVObject avObject, AVException e) {
                    if (avObject == null) return;
                    if (TextUtils.isEmpty(avObject.getString("ImToken")) || "null".equals(avObject.getString("ImToken"))) {
                        genImToken();
                    } else {
                        connect(avObject.getString("ImToken"));
                    }
                }
            });
        }
    }

    public void logout() {
        RongIM.getInstance().logout();
        imOnline = false;
    }

    public void updateUserInfo(final String userId) {
        AVQuery<AVUser> query = AVQuery.getQuery("_User");
        query.whereEqualTo("objectId", userId);
        query.setCachePolicy(AVQuery.CachePolicy.CACHE_ELSE_NETWORK);
        query.getFirstInBackground(new GetCallback<AVUser>() {
            @Override
            public void done(AVUser avObject, AVException e) {
                if (e == null) {
                    Uri headUri = Uri.EMPTY;
                    if (avObject.getAVFile("head") != null) {
                        headUri = Uri.parse(avObject.getAVFile("head").getUrl());
                    }
                    UserInfo userInfo = new UserInfo(userId, avObject.getUsername(), headUri);
                    RongIM.getInstance().refreshUserInfoCache(userInfo);
                }
            }
        });
    }

    private void connect(String token) {
        RongIM.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {
                imOnline = false;
            }

            @Override
            public void onSuccess(String userid) {
                imOnline = true;
//                if (AVUser.getCurrentUser() == null || AVUser.getCurrentUser().getAVFile("head") == null) return;
//                UserInfo userInfo = new UserInfo(AVUser.getCurrentUser().getObjectId(), AVUser.getCurrentUser().getUsername(), Uri.parse(AVUser.getCurrentUser().getAVFile("head").getUrl()));
//                RongIM.getInstance().setCurrentUserInfo(userInfo);
                EventBus.getDefault().post(new UpdateBadgeEvent());
            }

            /**
             * 连接融云失败
             */
            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                imOnline = false;
            }
        });
    }

    private void genImToken() {
        String headUrl = null;
        try {
            if (AVUser.getCurrentUser().getAVFile("head") != null) {
                headUrl = URLEncoder.encode(AVUser.getCurrentUser().getAVFile("head").getUrl(), "UTF-8");
            }
            NetworkHelper.requestBuilder(genImTokenApi.class)
                    .genImToken(AVUser.getCurrentUser().getObjectId(), AVUser.getCurrentUser().getUsername(), headUrl == null ? "1" : headUrl)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(Schedulers.newThread())
                    .subscribe(new BaseObserver<String>() {
                        @Override
                        public void onNext(String value) {
                            loginIm();
                        }

                        @Override
                        public void onError(Throwable e) {
                            super.onError(e);
                        }
                    });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    interface genImTokenApi {
        @GET("genImToken/{objectId}/{username}")
        Observable<String> genImToken(
                @Path("objectId") String objectId,
                @Path("username") String username,
                @Query("head") String headUrl
        );
    }
}
