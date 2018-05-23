package cn.edu.zju.qcw.android.safari.selection.bean;

import cn.edu.zju.qcw.android.base.BaseBean;

/**
 * Created by SQ on 2017/3/20.
 */

public class SelectionArticleBean extends BaseBean {

    private String club;

    private String shortname;

    private String follow;

    private String logo;

    private String articleId;

    private String title;

    private String kind;

    private String subtitle;

    private String coverUrl;

    private String visits;

    private String comments;

    private String creatAt;

    public String getClub() {
        return club;
    }

    public void setClub(String club) {
        this.club = club;
    }

    public String getShortname() {
        return shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    public String getFollow() {
        return follow;
    }

    public void setFollow(String follow) {
        this.follow = follow;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getVisits() {
        return visits;
    }

    public void setVisits(String visits) {
        this.visits = visits;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getCreatAt() {
        return creatAt;
    }

    public void setCreatAt(String creatAt) {
        this.creatAt = creatAt;
    }

    @Override
    protected String setApi() {
        return "article/id/";
    }

    @Override
    protected String setUrlId() {
        return articleId;
    }

    @Override
    protected String setTitle() {
        return title;
    }
}
