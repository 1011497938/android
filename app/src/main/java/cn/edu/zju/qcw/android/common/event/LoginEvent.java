package cn.edu.zju.qcw.android.common.event;

import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;

/**
 * Created by SQ on 2017/5/18.
 */

public class LoginEvent {

    AVObject user;

    public LoginEvent() {
    }

    public LoginEvent(AVObject user) {
        this.user = user;
    }

    public AVObject getUser() {
        return user;
    }
}
