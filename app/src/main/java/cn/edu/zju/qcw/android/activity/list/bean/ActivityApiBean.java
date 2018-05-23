package cn.edu.zju.qcw.android.activity.list.bean;

import cn.edu.zju.qcw.android.base.BaseBean;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by SQ on 2017/3/27.
 */

public class ActivityApiBean extends BaseBean{
    @SerializedName("activity")
    private List<ActivityBean> activityList;

    @SerializedName("banner")
    private List<ActivityBannerBean> activityBanner;

    public List<ActivityBean> getActivityList() {
        return activityList;
    }

    public void setActivityList(List<ActivityBean> activityList) {
        this.activityList = activityList;
    }

    public List<ActivityBannerBean> getActivityBanner() {
        return activityBanner;
    }

    public void setActivityBanner(List<ActivityBannerBean> activityBanner) {
        this.activityBanner = activityBanner;
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
