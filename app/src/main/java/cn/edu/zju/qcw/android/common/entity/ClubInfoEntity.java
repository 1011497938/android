package cn.edu.zju.qcw.android.common.entity;

import com.google.gson.annotations.SerializedName;


/**
 * Created by SQ on 2017/4/8.
 */
public class ClubInfoEntity {

    private long id;

    private String shortname;

    private String fullname;

    private String logo;

    private String recommend;

    private String updateAt;

    @SerializedName("new")
    private String showNew;


    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getShortname() {
        return this.shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    public String getLogo() {
        return this.logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getRecommend() {
        return this.recommend;
    }

    public void setRecommend(String recommend) {
        this.recommend = recommend;
    }

    public String getUpdateAt() {
        return this.updateAt;
    }

    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
    }

    public String getShowNew() {
        return this.showNew;
    }

    public void setShowNew(String showNew) {
        this.showNew = showNew;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
}
