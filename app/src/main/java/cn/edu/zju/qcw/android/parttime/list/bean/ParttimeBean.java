package cn.edu.zju.qcw.android.parttime.list.bean;

import cn.edu.zju.qcw.android.base.BaseBean;

/**
 * Created by SQ on 2017/3/21.
 */

public class ParttimeBean extends BaseBean{
    private int id;

    private String no;

    private String valid;

    private String type;

    private String title;

    private String price;

    private String creatAt;

    private String visits;

    private String registerNum;

    @Override
    protected String setApi() {
        return "parttime/id/";
    }

    @Override
    protected String setUrlId() {
        return String.valueOf(id);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getValid() {
        return valid;
    }

    public void setValid(String valid) {
        this.valid = valid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCreatAt() {
        return creatAt;
    }

    public void setCreatAt(String creatAt) {
        this.creatAt = creatAt;
    }

    public String getVisits() {
        return visits;
    }

    public void setVisits(String visits) {
        this.visits = visits;
    }

    public String getRegisterNum() {
        return registerNum;
    }

    public void setRegisterNum(String registerNum) {
        this.registerNum = registerNum;
    }
}
