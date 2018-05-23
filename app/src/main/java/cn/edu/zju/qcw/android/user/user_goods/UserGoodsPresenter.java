package cn.edu.zju.qcw.android.user.user_goods;

import cn.edu.zju.qcw.android.base.mvp_activity.BaseMvpPresenter;
import cn.edu.zju.qcw.android.common.bean.AVExceptionBean;
import cn.edu.zju.qcw.android.market.list.bean.GoodsBean;
import cn.edu.zju.qcw.android.util.String.StringUtil;
import cn.edu.zju.qcw.android.util.dialog.DialogUtil;
import cn.edu.zju.qcw.android.util.toast.ToastHelper;
import com.avos.avoscloud.*;

import java.util.Arrays;
import java.util.List;

/**
 * Created by SQ on 2017/5/24.
 */

public class UserGoodsPresenter extends BaseMvpPresenter<UserGoodsActivity, UserGoodsModel> {
    public UserGoodsPresenter(UserGoodsActivity view) {
        super(view);
    }

    @Override
    public UserGoodsModel initModel() {
        return new UserGoodsModel();
    }

    public void getData() {
        final AVQuery<GoodsBean> query = AVQuery.getQuery("Goods");
        query.whereEqualTo("creatBy", AVUser.getCurrentUser());
        query.addDescendingOrder("createdAt");
        query.selectKeys(Arrays.asList("firstImg", "like", "see", "title", "price", "valid", "image"));
        query.findInBackground(new FindCallback<GoodsBean>() {
            @Override
            public void done(List<GoodsBean> list, AVException e) {
                if (!isActivityBind()) return;
                if (e != null) {
                    ToastHelper.showShortToast("哎呀出错了，检查下网络吧~");
                    getView().onBackPressed();
                } else {
                    getView().changeEmptyView();
                    getView().getAdapter().setNewData(list);
                }
            }
        });
    }

    public void setGoodsStatus(GoodsBean goodsBean, final boolean isOnSale) {
        goodsBean.put("valid", isOnSale);
        goodsBean.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    ToastHelper.showShortToast(StringUtil.genHappyFace() + " 已" + (isOnSale ? "上架" : "下架"));
                }else{
                    ToastHelper.showShortToast(new AVExceptionBean(e).getErrorMessage());
                }
            }
        });
    }

    public void deleteGoods(final GoodsBean goodsBean) {
        DialogUtil.getInstance().showLoading(getView());
        for (AVObject object : goodsBean.getList("image", AVObject.class)) {
            object.deleteInBackground();
        }
        goodsBean.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(AVException e) {
                if (!isActivityBind()) return;
                if (e == null) {
                    int index = getView().getAdapter().getData().indexOf(goodsBean);
                    getView().getAdapter().remove(index);
                    getView().getAdapter().notifyItemRemoved(index);
                    ToastHelper.showShortToast("已删除");
                }else{
                    ToastHelper.showShortToast(new AVExceptionBean(e).getErrorMessage());
                }
                DialogUtil.getInstance().closeLoading();
            }
        });
    }
}
