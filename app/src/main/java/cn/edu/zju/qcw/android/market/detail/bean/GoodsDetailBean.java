package cn.edu.zju.qcw.android.market.detail.bean;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Created by SQ on 2017/5/14.
 */

public class GoodsDetailBean {
    private AVObject goodsObj;

    private AVObject likeObj;

    private AVObject ownerObj;

    public GoodsDetailBean(AVObject goodsObj, AVObject likeObj, AVObject ownerObj) {
        this.goodsObj = goodsObj;
        this.likeObj = likeObj;
        this.ownerObj = ownerObj;
    }

    public AVObject getGoodsObj() {
        return goodsObj;
    }

    public void setGoodsObj(AVObject goodsObj) {
        this.goodsObj = goodsObj;
    }

    public AVObject getLikeObj() {
        return likeObj;
    }

    public void setLikeObj(AVObject likeObj) {
        this.likeObj = likeObj;
    }

    public AVObject getOwnerObj() {
        return ownerObj;
    }

    public void setOwnerObj(AVObject ownerObj) {
        this.ownerObj = ownerObj;
    }

    public String getUsername() {
        return ownerObj.getString("username");
    }

    public String getUserHead() {
        if (ownerObj.getAVFile("head") == null) {
            return null;
        }
        return ownerObj.getAVFile("head").getUrl();
    }

    public String getMobile() {
        return ownerObj.getString("mobilePhoneNumber");
    }

    public String getTitle() {
        return goodsObj.getString("title");
    }

    public String getDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS", Locale.CHINA);
        sdf.applyPattern("yyyy/MM/dd HH:mm");
        return sdf.format(goodsObj.getCreatedAt());
    }

    public String getDetail() {
        return goodsObj.getString("description");
    }

    public String getLikeCount() {
        return String.valueOf(goodsObj.getNumber("like"));
    }

    public String getPrice() {
        return "￥"+ goodsObj.getString("price");
    }

    public String getImToken() {
        return ownerObj.getString("ImToken");
    }

    public List<String> getImages() {
        List<String> images = new ArrayList<>();
        List<AVFile> list = goodsObj.getList("image");
        for (AVFile avFile : list) {
            images.add(avFile.getUrl());
        }
        return images;
    }

}
