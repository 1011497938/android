package cn.edu.zju.qcw.android.activity.liveRoom.bean;

import com.avos.avoscloud.AVObject;

/**
 * Created by SQ on 2017/4/5.
 */

public class LiveBean extends AVObject {

    private String coverUrl;

    private String streamUrl;

    private String headImgUrl;

    private String username;

    public String getCoverUrl() {
        return getString("coverUrl");
    }

    public String getStreamUrl() {
        return getString("streamUrl");
    }

    public String getHeadImgUrl() {
        return getAVUser("user").getAVFile("head").getUrl();
    }

    public String getUsername() {
        return getAVUser("user").getUsername();
    }
}
