package cn.edu.zju.qcw.android.common.comment.presenter;

import android.content.Intent;
import android.text.TextUtils;
import cn.edu.zju.qcw.android.base.BaseObserver;
import cn.edu.zju.qcw.android.base.mvp_activity.BaseMvpPresenter;
import cn.edu.zju.qcw.android.common.comment.model.CommentModel;
import cn.edu.zju.qcw.android.common.comment.view.CommentActivity;
import cn.edu.zju.qcw.android.user.signin.view.SigninActivity;
import cn.edu.zju.qcw.android.util.String.StringUtil;
import cn.edu.zju.qcw.android.util.toast.ToastHelper;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SQ on 2017/5/17.
 */

public class CommentPresenter extends BaseMvpPresenter<CommentActivity, CommentModel> {

    public CommentPresenter(CommentActivity view) {
        super(view);
    }

    @Override
    public CommentModel initModel() {
        return new CommentModel();
    }

    public void getData(String url) {
        getModel().getComment(url, new BaseObserver<List<AVObject>>() {
            @Override
            public void onNext(List<AVObject> value) {
                getView().changeEmptyView();
                getView().getAdapter().setNewData(value);
                getView().refreshTitle();
            }

            @Override
            public void onError(Throwable e) {
                ToastHelper.showShortToast("哎呀出错了，检查下网络吧~");
                getView().onBackPressed();
            }
        });
    }

    public void saveComment(String url, String comment) {
        if (TextUtils.isEmpty(comment)) {
            return;
        }
        if (AVUser.getCurrentUser() == null) {
            Intent intent = new Intent(getView(), SigninActivity.class);
            getView().startActivity(intent);
            return;
        }
        getModel().saveComment(url, comment, new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null && null != getModel().latestObj) {
                    if (getView().getAdapter().getData().size() == 0) {
                        List<AVObject> list = new ArrayList<AVObject>();
                        list.add(getModel().latestObj);
                        getView().getAdapter().setNewData(list);
                    }else{
                        getView().getAdapter().getData().add(0, getModel().latestObj);
                        getView().getAdapter().notifyItemInserted(0);
                    }
                    getView().getRecyclerView().scrollToPosition(0);
                    getView().refreshTitle();
                }else{
                    ToastHelper.showShortToast(StringUtil.genUnhappyFace() + " 评论失败");
                }
            }
        });

    }
}
