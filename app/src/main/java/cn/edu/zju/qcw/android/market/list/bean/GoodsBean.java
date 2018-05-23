package cn.edu.zju.qcw.android.market.list.bean;

import android.app.Fragment;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVObject;

/**
 * Created by SQ on 2017/3/26.
 */
@AVClassName("Goods")
public class GoodsBean extends AVObject{

    private String title;

    private String price;

    private String headUrl;

    private int like;

    private int see;

    private int spanSize;

    public GoodsBean() {

    }

    public String getTitle() {
        return getString("title");
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return "￥"+getString("price");
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getHeadUrl() {
        return getAVFile("firstImg").getUrl();
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public int getLike() {
        return getInt("like");
    }

    public void setLike(int like) {
        this.like = like;
    }

    public int getSee() {
        return getInt("see");
    }

    public void setSee(int see) {
        this.see = see;
    }

    public boolean getValid() {
        return getBoolean("valid");
    }


}
