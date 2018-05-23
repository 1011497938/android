package cn.edu.zju.qcw.android.parttime.list.presenter;

import java.util.List;

import cn.edu.zju.qcw.android.parttime.list.bean.ParttimeBean;
import io.reactivex.Observable;
import io.reactivex.Observer;

/**
 * Created by SQ on 2017/3/27.
 */

public interface ParttimeListInterface {
    interface IModel {
        void loadData(String kind, Observer<List<ParttimeBean>> observer);
    }
    interface IView {
        void reloadData(List<ParttimeBean> beanList);
    }
    interface IPresenter {
        void refreshWithKind(String kind);
    }
}
