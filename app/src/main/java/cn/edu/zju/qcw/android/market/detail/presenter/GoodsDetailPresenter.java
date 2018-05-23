package cn.edu.zju.qcw.android.market.detail.presenter;

import android.content.Intent;
import android.net.Uri;
import cn.edu.zju.qcw.android.base.mvp_activity.BaseMvpPresenter;
import cn.edu.zju.qcw.android.base.BaseNullAvObject;
import cn.edu.zju.qcw.android.base.BaseObserver;
import cn.edu.zju.qcw.android.common.IM.ImHelper;
import cn.edu.zju.qcw.android.market.detail.bean.GoodsDetailBean;
import cn.edu.zju.qcw.android.market.detail.model.GoodsDetailModel;
import cn.edu.zju.qcw.android.market.detail.view.GoodsDetailActivity;
import cn.edu.zju.qcw.android.user.signin.view.SigninActivity;
import cn.edu.zju.qcw.android.util.toast.ToastHelper;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;

/**
 * Created by SQ on 2017/5/14.
 */

public class GoodsDetailPresenter extends BaseMvpPresenter<GoodsDetailActivity, GoodsDetailModel> {

    public void setData(GoodsDetailBean data) {
        this.data = data;
    }

    public GoodsDetailBean getData() {
        return data;
    }

    private GoodsDetailBean data;

    public GoodsDetailPresenter(GoodsDetailActivity view) {
        super(view);
    }

    @Override
    public GoodsDetailModel initModel() {
        return new GoodsDetailModel();
    }


    public void loadData(final String goodsId) {
        getModel().getData(goodsId, new BaseObserver<GoodsDetailBean>() {
            @Override
            public void onNext(GoodsDetailBean value) {
                if (!isActivityBind()) return;
                data = value;
                getView().setViewWithData(value);
            }

            @Override
            public void onError(Throwable e) {
                if (!isActivityBind()) return;
                ToastHelper.showShortToast("哎呀出错了，检查下网络吧~");
                getView().onBackPressed();
            }
        });
    }

    public void likeAction() {
        if (AVUser.getCurrentUser() == null) {
            getView().startActivity(new Intent(getView(), SigninActivity.class));
            return;
        }

        if (getData().getLikeObj().getAVObject("UserId") != null) {//取消赞
            getData().getLikeObj().deleteInBackground();
            getData().setLikeObj(new BaseNullAvObject());
            //Goods表like - 1
            getData().getGoodsObj().increment("like", -1);
        } else {
            AVObject likeObj = AVObject.create("Like");
            likeObj.put("GoodId", getData().getGoodsObj());
            likeObj.put("UserId", AVUser.getCurrentUser());
            likeObj.saveInBackground();
            getData().setLikeObj(likeObj);
            //Goods表like + 1
            getData().getGoodsObj().increment("like");
        }
        getView().updateFavoriteIcon();
    }

    public void callAction() {
        if (AVUser.getCurrentUser() == null) {
            getView().startActivity(new Intent(getView(), SigninActivity.class));
            return;
        }
        Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
        sendIntent.setData(Uri.parse("smsto:" + getData().getMobile()));
        sendIntent.putExtra("sms_body", "hello~我在勤创APP上看到了你发布的物品");
        getView().startActivity(sendIntent);
    }

    public void messageAction() {
        if (AVUser.getCurrentUser() == null) {
            getView().startActivity(new Intent(getView(), SigninActivity.class));
            return;
        }
        ImHelper.startPrivateConversation(getView(), data.getOwnerObj().getObjectId(), data.getOwnerObj().getString("username"));
    }
}
