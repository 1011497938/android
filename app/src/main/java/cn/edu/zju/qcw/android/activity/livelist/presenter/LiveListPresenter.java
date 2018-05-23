package cn.edu.zju.qcw.android.activity.livelist.presenter;

import android.content.Intent;
import android.net.Uri;
import cn.edu.zju.qcw.android.activity.liveRoom.LiveKit;
import cn.edu.zju.qcw.android.activity.liveRoom.ui.LiveShowActivity;
import cn.edu.zju.qcw.android.activity.livelist.model.LiveListModel;
import cn.edu.zju.qcw.android.activity.livelist.view.LiveListActivity;
import cn.edu.zju.qcw.android.base.mvp_activity.BaseMvpPresenter;
import cn.edu.zju.qcw.android.user.signin.view.SigninActivity;
import cn.edu.zju.qcw.android.util.toast.ToastHelper;
import com.avos.avoscloud.*;
import io.rong.imlib.model.UserInfo;

import java.util.List;

/**
 * Created by SQ on 2017/5/17.
 */

public class LiveListPresenter extends BaseMvpPresenter<LiveListActivity, LiveListModel> {

    public LiveListPresenter(LiveListActivity view) {
        super(view);

    }

    @Override
    public LiveListModel initModel() {
        return new LiveListModel();
    }

    public void getData() {
        getModel().getLiveList(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (!isActivityBind()) return;
                if (e == null) {
                    getView().changeEmptyView();
                    getView().getAdapter().setNewData(list);
                    getView().getSwipeRefreshLayout().setRefreshing(false);
                } else {
                    ToastHelper.showShortToast("啊哦出错了，检查下网络吧~");
                    getView().onBackPressed();
                }
            }
        });
    }

    public void didSelectLiveRoom(int position) {
        if (!isActivityBind()) return;
        if (AVUser.getCurrentUser() == null) {
            getView().startActivity(new Intent(getView(), SigninActivity.class));
            return;
        }

        if (getView().getAdapter().getData().size() > 0) {
            UserInfo userInfo = new UserInfo(AVUser.getCurrentUser().getObjectId(), AVUser.getCurrentUser().getUsername(), null);
            LiveKit.setCurrentUser(userInfo);
            Intent intent = new Intent(getView(), LiveShowActivity.class);
            intent.putExtra(LiveShowActivity.LIVE_URL, getView().getAdapter().getData().get(position).getString("streamUrl"));
            intent.putExtra(LiveShowActivity.ROOM_ID, getView().getAdapter().getData().get(position).getObjectId());
            getView().startActivity(intent);
        }
    }
}
