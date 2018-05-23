package cn.edu.zju.qcw.android.activity.wall.presenter;

import android.text.TextUtils;
import cn.edu.zju.qcw.android.activity.wall.bean.WallBean;
import cn.edu.zju.qcw.android.activity.wall.model.WallModel;
import cn.edu.zju.qcw.android.activity.wall.view.WallActivity;
import cn.edu.zju.qcw.android.base.mvp_activity.BaseMvpPresenter;
import cn.edu.zju.qcw.android.util.String.StringUtil;
import cn.edu.zju.qcw.android.util.toast.ToastHelper;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.im.v2.*;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * 这个模块业务逻辑比较深，所以不再把model单独拆分出来
 * Created by SQ on 2017/5/17.
 */

public class WallPresenter extends BaseMvpPresenter<WallActivity, WallModel> {

    private String roomId;

    private AVIMConversation conversation;

    public WallPresenter(WallActivity view) {
        super(view);
    }

    private AVIMMessageHandler handler = new AVIMMessageHandler() {
        @Override
        public void onMessage(AVIMMessage message, AVIMConversation conversation, AVIMClient client) {
            if (!isActivityBind()) return;
            AVIMTextMessage message1 = (AVIMTextMessage) message;
            if (getView().getAdapter().getData().isEmpty()) {
                List<WallBean> list = new ArrayList<>();
                list.add(new WallBean(message1.getText(), (String) message1.getAttrs().get("username"), (String) message1.getAttrs().get("headUrl")));
                getView().getAdapter().setNewData(list);
                return;
            }
            getView().getAdapter().getData().add(new WallBean(message1.getText(), (String) message1.getAttrs().get("username"), (String) message1.getAttrs().get("headUrl")));
            getView().getAdapter().notifyItemInserted(getView().getAdapter().getData().size() - 1);
            getView().getRecyclerView().scrollToPosition(getView().getAdapter().getData().size() - 1);
        }
    };

    @Override
    public WallModel initModel() {
        return new WallModel();
    }

    public void initIm() {
        if (AVUser.getCurrentUser() == null) {
            ToastHelper.showShortToast(StringUtil.genHappyFace() + " 还没有登录哦");
            getView().onBackPressed();
            return;
        }
        roomId = getView().getIntent().getStringExtra(WallActivity.ROOM_ID);
        if (TextUtils.isEmpty(roomId)) {
            joinFailed();
            return;
        }
        final AVIMClient client = AVIMClient.getInstance(AVUser.getCurrentUser().getObjectId());
        AVIMClient.setMessageQueryCacheEnable(false);
        AVIMClient.setOfflineMessagePush(false);
        client.open(new AVIMClientCallback() {
            @Override
            public void done(AVIMClient avimClient, AVIMException e) {
                AVIMConversationQuery query = client.getQuery();
                query.whereEqualTo("objectId", roomId);
                query.findInBackground(new AVIMConversationQueryCallback() {
                    @Override
                    public void done(List<AVIMConversation> list, AVIMException e) {
                        if (e != null || list.isEmpty()) {
                            joinFailed();
                        } else {
                            conversation = list.get(0);
                            conversation.join(new AVIMConversationCallback() {
                                @Override
                                public void done(AVIMException e) {
                                    if (e != null) {
                                        joinFailed();
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });

        AVIMMessageManager.setConversationEventHandler(new AVIMConversationEventHandler() {
            @Override
            public void onMemberLeft(AVIMClient avimClient, AVIMConversation avimConversation, List<String> list, String s) {

            }

            @Override
            public void onMemberJoined(AVIMClient avimClient, AVIMConversation avimConversation, List<String> list, String s) {

            }

            @Override
            public void onKicked(AVIMClient avimClient, AVIMConversation avimConversation, String s) {
                if (!isActivityBind()) return;
                ToastHelper.showShortToast("已被踢出大屏幕");
                getView().onBackPressed();
            }

            @Override
            public void onInvited(AVIMClient avimClient, AVIMConversation avimConversation, String s) {

            }
        });

        AVIMMessageManager.registerMessageHandler(AVIMMessage.class, handler);
    }

    public void sendMessage(String message) {
        if (AVUser.getCurrentUser() == null) {
            ToastHelper.showShortToast(StringUtil.genHappyFace() + " 还没有登录哦");
            getView().onBackPressed();
            return;
        }
        AVObject object = AVObject.create("ActivityWall");
        object.put("content", message);
        object.put("user", AVUser.getCurrentUser());
        object.put("roomID", roomId);
        object.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    getView().commentText.setText("");
                }
            }
        });
    }

    private void joinFailed() {
        ToastHelper.showShortToast(StringUtil.genUnhappyFace() + " 加入失败");
        getView().onBackPressed();
    }

    @Override
    public void onDestroy() {
        AVIMMessageManager.unregisterMessageHandler(AVIMMessage.class, handler);
        AVIMMessageManager.setConversationEventHandler(null);
        super.onDestroy();
    }
}
