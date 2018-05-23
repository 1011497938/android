package cn.edu.zju.qcw.android.activity.wall.bean;

import cn.edu.zju.qcw.android.base.BaseBean;
import com.google.gson.annotations.SerializedName;

/**
 * Created by SQ on 2017/5/22.
 */

public class WallBean extends BaseBean {

    private String content;

    private String username;

    private String headUrl;

    public WallBean(String content, String username, String headUrl) {
        this.content = content;
        this.username = username;
        this.headUrl = headUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    @Override
    protected String setApi() {
        return null;
    }

    @Override
    protected String setUrlId() {
        return null;
    }
}
