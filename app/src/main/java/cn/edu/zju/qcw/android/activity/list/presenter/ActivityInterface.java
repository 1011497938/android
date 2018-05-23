package cn.edu.zju.qcw.android.activity.list.presenter;

import java.util.List;

import cn.edu.zju.qcw.android.activity.list.bean.ActivityApiBean;
import cn.edu.zju.qcw.android.activity.list.bean.ActivityBean;
import cn.edu.zju.qcw.android.activity.list.bean.ActivityListItem;
import cn.edu.zju.qcw.android.base.mvp_recyclerview_fragment.BaseRecyclerViewPresenter;
import io.reactivex.Observer;

/**
 * Created by SQ on 2017/3/26.
 */

public interface ActivityInterface {
    interface IModel { //网络请求,数据处理
        void loadData(Observer<List<ActivityListItem>> observer);
    }
    interface IView {
        void reloadData(List<ActivityListItem> beanList);
    }
    interface IPresenter extends BaseRecyclerViewPresenter{

    }
}
