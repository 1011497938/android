package cn.edu.zju.qcw.android.safari.article.bean;

import cn.edu.zju.qcw.android.base.BaseBean;

/**
 * Created by SQ on 2017/3/20.
 */

public class ArticleBean extends BaseBean{

    private String id;

    private String owner;

    private String kind;

    private String form;

    private String registerUrl;

    private String ticketId;

    private String title;

    private String subtitle;

    private String coverUrl;

    private String visits;

    private String comments;

    private String creatAt;

    private String beginTicketDate;

    public String getBeginTicketDate() {
        return beginTicketDate;
    }

    public void setBeginTicketDate(String beginTicketDate) {
        this.beginTicketDate = beginTicketDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public String getRegisterUrl() {
        return registerUrl;
    }

    public void setRegisterUrl(String registerUrl) {
        this.registerUrl = registerUrl;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
        return null;
    }

    @Override
    public String getUrl() {
        return "https://papiccms.applinzi.com/article/id/" + getId();
    }

    @Override
    protected String setUrlId() {
        return null;
    }
}