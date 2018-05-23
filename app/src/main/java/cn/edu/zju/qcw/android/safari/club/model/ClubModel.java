package cn.edu.zju.qcw.android.safari.club.model;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.edu.zju.qcw.android.safari.club.listener.ClubNameListCallback;

/**
 * Created by SQ on 2017/4/8.
 */

public class ClubModel {

    //全局单例缓存：
    //所有社团摘要
    //用户关注列表

    public void getFollowClubNameList(ClubNameListCallback callback) {
        //本地缓存


        //User表查询


        //默认关注列表

    }



    public void getSpecificClubInfo(String clubId) {

    }

    /**
     * 需要从网络获取并更新本地缓存的部分
     */
    public void getAllClubAbstractInfo() {
        //更新本地缓存
    }

    public void unFollowClub(String clubId) {
        //Installation取消订阅，User表删除
    }

    public void followClub(String clubId) {
        //保存到Installation和User表，Installation表用于推送
    }

    public void openPush(String clubId) {

    }

    public void closePush(String clubId) {

    }

    //End

    //返回数据用于TabLayout
    private void getFollowClubFromLeanCloud(final ClubNameListCallback callback) {
        AVInstallation.getCurrentInstallation().getInstallationId();
        AVQuery<AVObject> query = new AVQuery<>("_Installation");
        query.selectKeys(Arrays.asList("channels"));
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                if (e == null) {
                    callback.success((ArrayList<String>) avObject.getList("channels"));
                }else {
                    callback.error();
                }
            }
        });
    }

    //返回数据（带有关注信息）用于社团列表
    public void getUserFollowClubFromLeanCloud(FindCallback callback) {
        AVInstallation.getCurrentInstallation().getInstallationId();
        AVQuery<AVObject> query = new AVQuery<>("_User");
        query.selectKeys(Arrays.asList("followClubs"));
        query.findInBackground(callback);
    }

    private void cacheAllClubInfo() {

    }

    private void cacheFollowList() {

    }
}
