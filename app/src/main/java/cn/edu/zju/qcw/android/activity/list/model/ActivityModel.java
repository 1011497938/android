package cn.edu.zju.qcw.android.activity.list.model;

import cn.edu.zju.qcw.android.activity.list.bean.ActivityApiBean;
import cn.edu.zju.qcw.android.activity.list.bean.ActivityBean;
import cn.edu.zju.qcw.android.activity.list.bean.ActivityListItem;
import cn.edu.zju.qcw.android.activity.list.presenter.ActivityInterface;
import cn.edu.zju.qcw.android.util.network.NetworkHelper;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.http.GET;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by SQ on 2017/3/27.
 */

public class ActivityModel implements ActivityInterface.IModel {
    interface ActivityListApi {
        @GET("activityList2")
        Observable<ActivityApiBean> getActivityList();
    }

    @Override
    public void loadData(Observer<List<ActivityListItem>> observer) {
        NetworkHelper.requestBuilder(ActivityListApi.class)
                .getActivityList()
                .map(new Function<ActivityApiBean, List<ActivityListItem>>() {
                    @Override
                    public List<ActivityListItem> apply(ActivityApiBean activityApiBean) throws Exception {
                        List<ActivityListItem> list = new ArrayList<>();
                        boolean week = false;
                        boolean month = false;
                        boolean other = false;
                        if (activityApiBean.getActivityBanner().size() > 0) {
                            list.add(new ActivityListItem(ActivityListItem.BANNER, activityApiBean.getActivityBanner()));
                        }

                        for (ActivityBean activityBean : activityApiBean.getActivityList()) {
                            if (!week && inWeek(activityBean.getTime())) {
                                list.add(new ActivityListItem(ActivityListItem.WEEK));
                                week = true;
                            }else if (!month && inMonth(activityBean.getTime())) {
                                list.add(new ActivityListItem(ActivityListItem.MONTH));
                                month = true;
                            }else if (!other) {
                                list.add(new ActivityListItem(ActivityListItem.OTHER));
                                other = true;
                            }
                            list.add(new ActivityListItem(ActivityListItem.ACTIVITY, activityBean));
                        }
                        return list;
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    private boolean inWeek(String dateString) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateString).getTime() / 1000 < new Date().getTime() / 1000 + 60 * 60 * 24 * 7;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean inMonth(String dateString) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateString).getTime() / 1000 < new Date().getTime() / 1000 + 60 * 60 * 24 * 7 * 30;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }
}
