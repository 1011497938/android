package cn.edu.zju.qcw.android.safari.selection.bean;

import java.util.List;

import cn.edu.zju.qcw.android.base.BaseBean;

/**
 * Created by SQ on 2017/3/20.
 */

public class SelectionBean extends BaseBean {

    private List<SelectionBannerBean> bannerList;

    private List<SelectionArticleBean> selectionList;

    public List<SelectionBannerBean> getBannerList() {
        return bannerList;
    }

    public void setBannerList(List<SelectionBannerBean> bannerList) {
        this.bannerList = bannerList;
    }

    public List<SelectionArticleBean> getSelectionList() {
        return selectionList;
    }

    public void setSelectionList(List<SelectionArticleBean> selectionList) {
        this.selectionList = selectionList;
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
