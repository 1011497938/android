package cn.edu.zju.qcw.android.user.profile;

import android.text.TextUtils;
import cn.edu.zju.qcw.android.base.mvp_activity.BaseMvpPresenter;
import cn.edu.zju.qcw.android.common.IM.ImHelper;
import cn.edu.zju.qcw.android.common.bean.AVExceptionBean;
import cn.edu.zju.qcw.android.common.event.LoginEvent;
import cn.edu.zju.qcw.android.util.dialog.DialogUtil;
import cn.edu.zju.qcw.android.util.toast.ToastHelper;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.google.gson.Gson;
import org.greenrobot.eventbus.EventBus;

import java.io.FileNotFoundException;
import java.util.Date;

/**
 * Created by SQ on 2017/5/22.
 */

public class ProfilePresenter extends BaseMvpPresenter<ProfileActivity, ProfileModel> {
    public ProfilePresenter(ProfileActivity view) {
        super(view);
    }

    @Override
    public ProfileModel initModel() {
        return new ProfileModel();
    }

    public void getData() {
        if (AVUser.getCurrentUser() == null) {
            ToastHelper.showShortToast("还没有登录哦");
            getView().onBackPressed();
            return;
        }
        getView().setViewWithData(AVUser.getCurrentUser());
    }

    public void logout() {
        AVUser.logOut();
        ImHelper.getInstance().logout();
        EventBus.getDefault().post(new LoginEvent());
        getView().onBackPressed();
    }

    public void save(String username, final String filePath) {
        if (AVUser.getCurrentUser() == null) {
            ToastHelper.showShortToast("还没有登录哦");
            return;
        }
        DialogUtil.getInstance().showLoading(getView());
        if (!TextUtils.isEmpty(username)) {
            final String oldUsername = AVUser.getCurrentUser().getUsername();
            AVUser.getCurrentUser().setUsername(username);
            AVUser.getCurrentUser().saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    if (!isActivityBind()) return;
                    if (e != null) {
                        AVUser.getCurrentUser().setUsername(oldUsername);
                        ToastHelper.showShortToast(new AVExceptionBean(e).getErrorMessage());
                    } else {
                        if (!TextUtils.isEmpty(filePath)) {
                            try {
                                AVFile file = AVFile.withAbsoluteLocalPath(String.valueOf(new Date().getTime() / 1000), filePath);
                                AVUser.getCurrentUser().put("head", file);
                                AVUser.getCurrentUser().saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(AVException e) {
                                        if (e != null) {
                                            AVUser.getCurrentUser().setUsername(oldUsername);
                                            ToastHelper.showShortToast(new AVExceptionBean(e).getErrorMessage());
                                        } else {
                                            //更新Im
                                            ImHelper.getInstance().updateUserInfo(AVUser.getCurrentUser().getObjectId());
                                            //
                                            ToastHelper.showShortToast("更新成功");
                                            EventBus.getDefault().post(new LoginEvent());
                                        }
                                    }
                                });
                            } catch (FileNotFoundException e1) {
                                AVUser.getCurrentUser().setUsername(oldUsername);
                                ToastHelper.showShortToast("更新失败");
                                e.printStackTrace();
                            }
                        } else {
                            ToastHelper.showShortToast("更新成功");
                            EventBus.getDefault().post(new LoginEvent());
                        }
                    }
                    DialogUtil.getInstance().closeLoading();
                }
            });
        }
    }
}
