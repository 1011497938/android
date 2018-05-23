package cn.edu.zju.qcw.android.safari.club.bean;

import com.google.gson.annotations.SerializedName;

public class ClubBean {
    private String id;//

    private String fullname;

    private String shortname;//

    private int follow;

    @SerializedName("abstract")
    private String clubAbstract;

    private String contact;

    private String wx;

    private String im;

    private String logo;//

    private String updateAt;

    private boolean recommend;

    @SerializedName("new")
    private boolean isNew;

    private boolean openPush;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getShortname() {
        return shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    public int getFollow() {
        return follow;
    }

    public void setFollow(int follow) {
        this.follow = follow;
    }

    public String getClubAbstract() {
        return clubAbstract;
    }

    public void setClubAbstract(String clubAbstract) {
        this.clubAbstract = clubAbstract;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getWx() {
        return wx;
    }

    public void setWx(String wx) {
        this.wx = wx;
    }

    public String getIm() {
        return im;
    }

    public void setIm(String im) {
        this.im = im;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
    }

    public boolean isRecommend() {
        return recommend;
    }

    public void setRecommend(boolean recommend) {
        this.recommend = recommend;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public boolean isOpenPush() {
        return openPush;
    }

    public void setOpenPush(boolean openPush) {
        this.openPush = openPush;
    }
}
