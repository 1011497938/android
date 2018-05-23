package cn.edu.zju.qcw.android.user.signin;

import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.RequestMobileCodeCallback;

/**
 * Created by SQ on 2017/4/4.
 */

public interface SigninInterface {
    interface IModel {
        void signIn(String phoneNum, String verifyNum, LogInCallback<AVUser> callback);
        void getVerifyNum(String phoneNum, RequestMobileCodeCallback callback);
    }
    interface IView {
        void showError(String errorMessage);
        void sendSuccess();
        void signinSuccess();
    }
    interface IPresenter {
        void signIn(String phoneNum, String verifyNum);
        void getVerifyNum(String phoneNum);
    }
}
