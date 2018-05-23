package cn.edu.zju.qcw.android.safari.selection;

import java.util.List;

import android.support.annotation.Nullable;
import cn.edu.zju.qcw.android.base.mvp_recyclerview_fragment.BaseRecyclerViewPresenter;
import cn.edu.zju.qcw.android.safari.selection.bean.SelectionArticleBean;
import cn.edu.zju.qcw.android.safari.selection.bean.SelectionBean;
import io.reactivex.Observer;

/**
 * Created by SQ on 2017/3/26.
 */

public interface SelectionInterface {
    interface IModel { //网络请求,数据处理
        void loadData(String lastItemId, Observer<SelectionBean> observer);
        void loadSubscribeData(String lastItemId, Observer<List<SelectionArticleBean>> observer);
    }
    interface IView {
        void reloadData(List<SelectionArticleBean> beanList);
    }
    interface IPresenter extends BaseRecyclerViewPresenter{
        void getSubscribeData(@Nullable String lastItemId);
    }
}
