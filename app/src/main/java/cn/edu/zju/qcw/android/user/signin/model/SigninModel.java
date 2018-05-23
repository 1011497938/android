package cn.edu.zju.qcw.android.user.signin.model;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.RequestMobileCodeCallback;

import cn.edu.zju.qcw.android.user.signin.SigninInterface;

/**
 * Created by SQ on 2017/4/4.
 */

public class SigninModel implements SigninInterface.IModel {

    @Override
    public void getVerifyNum(String phoneNum, RequestMobileCodeCallback callback) {
        AVOSCloud.requestSMSCodeInBackground(phoneNum, callback);
    }

    @Override
    public void signIn(String phoneNum, String verifyNum, LogInCallback<AVUser> callback) {
        AVUser.signUpOrLoginByMobilePhoneInBackground(phoneNum, verifyNum, callback);
    }


}
