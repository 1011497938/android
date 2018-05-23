package cn.edu.zju.qcw.android.market.list.model;

import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;

import java.util.Arrays;
import java.util.List;

import cn.edu.zju.qcw.android.market.list.bean.GoodsBean;
import cn.edu.zju.qcw.android.market.list.GoodsListInterface;
import cn.edu.zju.qcw.android.parttime.list.bean.ParttimeBean;
import cn.edu.zju.qcw.android.parttime.list.model.ParttimeListModel;
import cn.edu.zju.qcw.android.util.network.NetworkHelper;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by SQ on 2017/3/27.
 */

public class GoodsListModel implements GoodsListInterface.IModel {
    @Override
    public void loadData(String kind, FindCallback<GoodsBean> callback) {
        AVQuery<GoodsBean> query = AVObject.getQuery(GoodsBean.class);
        query.orderByDescending("createdAt")
                .limit(16)
                .whereEqualTo("valid", true)
                .selectKeys(Arrays.asList("title", "see", "price", "firstImg", "like"));

        if (!kind.equals("全部")) {
            query.whereEqualTo("kind", kind);
        }
        query.findInBackground(callback);
    }

    @Override
    public void loadMoreData(int skip,String kind, FindCallback<GoodsBean> callback) {
        AVQuery<GoodsBean> query = AVObject.getQuery(GoodsBean.class);
        query.orderByDescending("createdAt")
                .skip(skip)
                .limit(16)
                .whereEqualTo("valid", true)
                .selectKeys(Arrays.asList("title", "see", "price", "firstImg", "like"));
        if (!kind.equals("全部")) {
            query.whereEqualTo("kind", kind);
        }
        query.findInBackground(callback);
    }
}

