package cn.edu.zju.qcw.android.browser;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import cn.edu.zju.qcw.android.base.BaseObserver;
import cn.edu.zju.qcw.android.base.mvp_activity.BaseMvpPresenter;
import cn.edu.zju.qcw.android.common.bean.AVExceptionBean;
import cn.edu.zju.qcw.android.parttime.form.ParttimeFormActivity;
import cn.edu.zju.qcw.android.safari.article.bean.ArticleBean;
import cn.edu.zju.qcw.android.user.signin.view.SigninActivity;
import cn.edu.zju.qcw.android.util.String.StringUtil;
import cn.edu.zju.qcw.android.util.dialog.DialogUtil;
import cn.edu.zju.qcw.android.util.toast.ToastHelper;
import com.avos.avoscloud.*;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by SQ on 2017/5/17.
 */

public class BrowserPresenter extends BaseMvpPresenter<BrowserActivity, BrowserModel> {

    String articleId;

    ArticleBean articleData;

    public BrowserPresenter(BrowserActivity view) {
        super(view);
    }

    @Override
    public BrowserModel initModel() {
        return new BrowserModel();
    }

    public void initNaviBadge() {
        articleId = Arrays.asList(getView().getUrl().split("/")).get(getView().getUrl().split("/").length - 1);
        if (null != getView().getUrl() && !getView().isParttime()) {
            getModel().getCommentCount(articleId, new CountCallback() {
                @Override
                public void done(int i, AVException e) {
                    if (null == e && getView() != null) {
                        getView().setCommentNumber(i);
                    }
                }
            });
            getModel().getLikeCount(articleId, new CountCallback() {
                @Override
                public void done(int i, AVException e) {
                    if (null == e && getView() != null) {
                        getView().setLikeNumber(i);
                    }
                }
            });
            getModel().likeOrNot(articleId, new GetCallback<AVObject>() {
                @Override
                public void done(AVObject avObject, AVException e) {
                    if (e == null && getView() != null) {
                        getView().setLikeOrNot(null != avObject);
                    }
                }
            });
        }
    }


    public void refreshComment(){
        articleId = Arrays.asList(getView().getUrl().split("/")).get(getView().getUrl().split("/").length - 1);
        if (null != getView().getUrl() && !getView().isParttime()) {
            getModel().getCommentCount(articleId, new CountCallback() {
                @Override
                public void done(int i, AVException e) {
                    if (null == e && getView() != null) {
                        getView().setCommentNumber(i);
                    }
                }
            });
        }
    }

    public void likeAction() {
        if (AVUser.getCurrentUser() == null) {
            Snackbar.make(getView().snackBarLayout, StringUtil.genHappyFace() + " 登录后才可以收藏哦", Snackbar.LENGTH_LONG).setAction("现在登录", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastHelper.showShortToast("123");
                }
            }).show();
            return;
        }

        getModel().likeOrNot(articleId, new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                if (null == e && getView() != null) {
                    if (null == avObject) {
                        getModel().likeAction(articleId);
                        getView().setLikeOrNot(true);
                        ToastHelper.showShortToast(StringUtil.genHappyFace() + " 收藏成功");
                    } else {
                        avObject.deleteInBackground();
                        getView().setLikeOrNot(false);
                        ToastHelper.showShortToast(StringUtil.genHappyFace() + " 已取消收藏");
                    }
                } else {
                    ToastHelper.showShortToast(StringUtil.genUnhappyFace() + " 操作失败");
                }
            }
        });
    }

    public void initBtnData(String url) {
        getModel().getBtnData(url, new BaseObserver<ArticleBean>() {
            @Override
            public void onNext(ArticleBean value) {
                articleData = value;
                if (!TextUtils.isEmpty(articleData.getRegisterUrl()) && !TextUtils.isEmpty(articleData.getTicketId())) {
                    getView().showActivityBtn(BrowserActivity.SHOW_ACT_BTN, value.getBeginTicketDate());
                    return;
                }
                if (!TextUtils.isEmpty(articleData.getRegisterUrl())) {
                    getView().showActivityBtn(BrowserActivity.SHOW_REGISTER_BTN, value.getBeginTicketDate());
                    return;
                }
                if (!TextUtils.isEmpty(articleData.getTicketId())) {
                    if (TextUtils.isEmpty(value.getBeginTicketDate())) return;
                    getView().showActivityBtn(BrowserActivity.SHOW_TICKET_BTN, value.getBeginTicketDate());
                }
            }
        });
    }

    public void selectParttime() {
        //TODO:
        Intent intent = new Intent(getView(), ParttimeFormActivity.class);
        intent.putExtra(ParttimeFormActivity.PARTTIME_ID, getView().getIntent().getStringExtra(BrowserActivity.PARTTIME_NUMBER));
        getView().startActivity(intent);
    }

    public void selectRegister() {
        if (articleData != null && !TextUtils.isEmpty(articleData.getRegisterUrl()))
            getView().loadNewUrl(articleData.getRegisterUrl());
    }

    public void selectTicket() {
        if (AVUser.getCurrentUser() == null) {
            getView().startActivity(new Intent(getView(), SigninActivity.class));
            return;
        }
        if (!getView().ticketText.getText().toString().contains("线上领票")) {
            return;
        }

        DialogUtil.getInstance().showLoading(getView());

        if (articleData == null || TextUtils.isEmpty(articleData.getTicketId())) return;
        Map<String, String> dicParameters = new HashMap<String, String>();
        dicParameters.put("targetTicketId", articleData.getTicketId());
        dicParameters.put("userId", AVUser.getCurrentUser().getObjectId());
        AVCloud.callFunctionInBackground("getTicket", dicParameters, new FunctionCallback<Object>() {
            @Override
            public void done(Object avObject, AVException e) {

                DialogUtil.getInstance().closeLoading();

                if (e == null) {
                    //TODO:加入提醒事项
                    ToastHelper.showShortToast("领票成功,可在个人中心“门票”中查看");
                } else {
                    Gson gson = new Gson();
                    ToastHelper.showShortToast(StringUtil.genUnhappyFace() + gson.fromJson(e.getLocalizedMessage().toString(), AVExceptionBean.class).getError());
                }
            }
        });
    }
}
