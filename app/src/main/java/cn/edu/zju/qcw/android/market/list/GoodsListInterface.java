package cn.edu.zju.qcw.android.market.list;

import com.avos.avoscloud.FindCallback;

import java.util.List;

import cn.edu.zju.qcw.android.market.list.bean.GoodsBean;
import io.reactivex.Observer;

/**
 * Created by SQ on 2017/3/27.
 */

public class GoodsListInterface {
    public interface IModel {
        void loadData(String kind, FindCallback<GoodsBean> callback);

        void loadMoreData(int skip, String kind, FindCallback<GoodsBean> callback);
    }

    public interface IView {
        void reloadData(List<GoodsBean> beanList);
    }

    public interface IPresenter {
        void getDataWithKind(String kind);

        void getMoreData(String kind);
    }
}
