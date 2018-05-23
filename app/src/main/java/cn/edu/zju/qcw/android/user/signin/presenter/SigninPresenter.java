package cn.edu.zju.qcw.android.user.signin.presenter;

import cn.edu.zju.qcw.android.base.BaseObserver;
import cn.edu.zju.qcw.android.common.IM.ImHelper;
import cn.edu.zju.qcw.android.common.bean.AVExceptionBean;
import cn.edu.zju.qcw.android.common.club.ClubHelper;
import cn.edu.zju.qcw.android.common.event.LoginEvent;
import cn.edu.zju.qcw.android.push.AliNullCallback;
import cn.edu.zju.qcw.android.user.signin.view.SigninActivity;
import cn.edu.zju.qcw.android.util.dialog.DialogUtil;
import cn.edu.zju.qcw.android.util.image.ImageHelper;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.avos.avoscloud.*;

import cn.edu.zju.qcw.android.user.signin.SigninInterface;
import cn.edu.zju.qcw.android.user.signin.model.SigninModel;
import com.google.gson.Gson;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.rong.eventbus.EventBus;

/**
 * Created by SQ on 2017/4/4.
 */

public class SigninPresenter implements SigninInterface.IPresenter {

    private SigninActivity mView;

    private SigninInterface.IModel mModel = new SigninModel();

    public SigninPresenter(SigninActivity view) {
        mView = view;
    }

    @Override
    public void signIn(String phoneNum, String verifyNum) {
        DialogUtil.getInstance().showLoading(mView);

        mModel.signIn(phoneNum, verifyNum, new LogInCallback<AVUser>() {
            @Override
            public void done(AVUser avUser, AVException e) {
                if (e != null) {
                    Gson gson = new Gson();
                    try {
                        mView.showError(gson.fromJson(e.getLocalizedMessage(), AVExceptionBean.class).getError());
                    }catch (Exception error) {
                        mView.showError("登录失败");
                    }
                    DialogUtil.getInstance().closeLoading();
                    return;
                }

                Observable.create(new ObservableOnSubscribe<AVObject>() {
                    @Override
                    public void subscribe(ObservableEmitter<AVObject> e) throws Exception {
                        AVObject object = AVUser.getCurrentUser().fetch();
                        e.onNext(object);
                    }
                })
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new BaseObserver<AVObject>() {
                            @Override
                            public void onNext(AVObject value) {
                                ImHelper.getInstance().loginIm();
                                ClubHelper.getInstance().initFollowClubList();
                                org.greenrobot.eventbus.EventBus.getDefault().post(new LoginEvent(value));
                                if (AVUser.getCurrentUser() != null) {
                                    PushServiceFactory.getCloudPushService().bindAccount(AVUser.getCurrentUser().getObjectId(), new AliNullCallback());
                                }
                                mView.signinSuccess();
                            }

                            @Override
                            public void onComplete() {
                                DialogUtil.getInstance().closeLoading();
                            }
                        });

            }
        });
    }

    @Override
    public void getVerifyNum(String phoneNum) {
        mModel.getVerifyNum(phoneNum, new RequestMobileCodeCallback() {
            @Override
            public void done(AVException e) {
                if (e != null) {
                    if (e.getLocalizedMessage().contains("601")) {
                        mView.showError("发送频率过快，稍后重试吧~");
                    }else{
                        mView.showError("获取验证码失败");
                    }
                    return;
                }
                mView.sendSuccess();
            }
        });
    }
}
