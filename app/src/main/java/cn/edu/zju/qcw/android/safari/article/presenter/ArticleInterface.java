package cn.edu.zju.qcw.android.safari.article.presenter;

import java.util.List;

import cn.edu.zju.qcw.android.base.BaseObserver;
import cn.edu.zju.qcw.android.safari.article.bean.ArticleBean;

/**
 * Created by SQ on 2017/3/26.
 */

public interface ArticleInterface {
    interface IModel { //网络请求,数据处理
        public void loadData(String clubId, String lastId, BaseObserver<List<ArticleBean>> observer);
    }

    interface IView {

    }

    interface IPresenter {
        void getNewData(String clubName);

        void getMoreData(String clubName, String lastId);
    }
}
