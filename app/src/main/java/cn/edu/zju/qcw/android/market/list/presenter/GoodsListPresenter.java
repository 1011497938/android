package cn.edu.zju.qcw.android.market.list.presenter;

import cn.edu.zju.qcw.android.market.list.view.GoodsGridFragment;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.FindCallback;

import java.util.List;

import cn.edu.zju.qcw.android.market.list.GoodsListInterface;
import cn.edu.zju.qcw.android.market.list.bean.GoodsBean;
import cn.edu.zju.qcw.android.market.list.model.GoodsListModel;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by SQ on 2017/3/27.
 */

public class GoodsListPresenter implements GoodsListInterface.IPresenter{

    private GoodsGridFragment mView;

    private GoodsListInterface.IModel mModel;

    public GoodsListPresenter(GoodsGridFragment view) {
        mView = view;
        mModel = new GoodsListModel();
    }

    @Override
    public void getDataWithKind(String kind) {
        mModel.loadData(kind, new FindCallback<GoodsBean>() {
            @Override
            public void done(List<GoodsBean> list, AVException e) {
                if (e == null) {
                    mView.reloadData(list);
                }else {
                    mView.onError();
                }
            }
        });
    }

    @Override
    public void getMoreData(String kind) {
        mModel.loadMoreData(mView.getAdapter().getData().size(), kind, new FindCallback<GoodsBean>() {
            @Override
            public void done(List<GoodsBean> list, AVException e) {
                if (e == null) {
                    mView.insertData(list);
                }else{
                    mView.getAdapter().loadMoreFail();
                }
            }
        });
    }

}