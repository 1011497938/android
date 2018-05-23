package cn.edu.zju.qcw.android.activity.list.bean;

import android.support.annotation.Nullable;
import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.List;

/**
 * Created by SQ on 2017/5/20.
 */

public class ActivityListItem implements MultiItemEntity {
    public static final int ACTIVITY = 1;
    public static final int BANNER = 2;
    public static final int WEEK = 3;
    public static final int MONTH = 4;
    public static final int OTHER = 5;
    private int itemType;

    public ActivityListItem(int itemType, @Nullable ActivityBean activityBean) {
        this.itemType = itemType;
        this.activityBean = activityBean;
    }

    public ActivityListItem(int itemType) {
        this.itemType = itemType;
    }

    public ActivityListItem(int itemType, List<ActivityBannerBean> bannerBean) {
        this.itemType = itemType;
        this.activityBannerBean = bannerBean;
    }

    private ActivityBean activityBean;

    public ActivityBean getActivityBean() {
        return activityBean;
    }

    public void setActivityBean(ActivityBean activityBean) {
        this.activityBean = activityBean;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    private List<ActivityBannerBean> activityBannerBean;

    public List<ActivityBannerBean> getActivityBannerBean() {
        return activityBannerBean;
    }
}
