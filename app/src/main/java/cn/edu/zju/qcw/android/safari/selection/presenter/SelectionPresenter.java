package cn.edu.zju.qcw.android.safari.selection.presenter;

import android.support.annotation.Nullable;
import cn.edu.zju.qcw.android.base.BaseObserver;
import cn.edu.zju.qcw.android.common.club.ClubHelper;
import cn.edu.zju.qcw.android.safari.selection.SelectionInterface;
import cn.edu.zju.qcw.android.safari.selection.adapter.SelectionItem;
import cn.edu.zju.qcw.android.safari.selection.bean.SelectionArticleBean;
import cn.edu.zju.qcw.android.safari.selection.bean.SelectionBean;
import cn.edu.zju.qcw.android.safari.selection.model.SelectionModel;
import cn.edu.zju.qcw.android.safari.selection.view.SelectionFragment;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SQ on 2017/3/27.
 */

public class SelectionPresenter implements SelectionInterface.IPresenter {

    private SelectionFragment mView;

    private SelectionInterface.IModel mModel;

    private final String DEFAULT_ALL_DATA = "0";

    public SelectionPresenter(SelectionFragment view) {
        mView = view;
        mModel = new SelectionModel();
    }

    @Override
    public void getNewData() {
        if (null != mView.getArguments() && mView.getArguments().getBoolean("isSubscribeList", false)) {
            if (null == ClubHelper.getInstance().getUserFollowList() || ClubHelper.getInstance().getUserFollowList().size() == 0) {
                mView.reloadData(new ArrayList<SelectionItem>());
                return;
            }
            getSubscribeData(DEFAULT_ALL_DATA);
            return;
        }

        mModel.loadData(DEFAULT_ALL_DATA, new BaseObserver<SelectionBean>() {
            @Override
            public void onNext(SelectionBean value) {
                List<SelectionItem> list = new ArrayList<SelectionItem>();
                for (SelectionArticleBean selectionArticleBean : value.getSelectionList()) {
                    if ("music".equals(selectionArticleBean.getKind())){
                        list.add(new SelectionItem(SelectionItem.MUSIC, selectionArticleBean));
                    }else{
                        list.add(new SelectionItem(SelectionItem.ARTICLE, selectionArticleBean));
                    }
                }
                mView.reloadData(list);
            }

            @Override
            public void onError(Throwable e) {
                mView.onError();
            }
        });
    }

    @Override
    public void getSubscribeData(@Nullable String lastItemId) {
        if (null == lastItemId) lastItemId = DEFAULT_ALL_DATA;

        final String finalLastItemId = lastItemId;
        mModel.loadSubscribeData(lastItemId, new BaseObserver<List<SelectionArticleBean>>() {
            @Override
            public void onNext(List<SelectionArticleBean> value) {
                List<SelectionItem> list = new ArrayList<SelectionItem>();
                for (SelectionArticleBean selectionArticleBean : value) {
                    if ("music".equals(selectionArticleBean.getKind())){
                        list.add(new SelectionItem(SelectionItem.MUSIC, selectionArticleBean));
                    }else{
                        list.add(new SelectionItem(SelectionItem.ARTICLE, selectionArticleBean));
                    }
                }
                if (DEFAULT_ALL_DATA.equals(finalLastItemId)) {
                    mView.reloadData(list);
                }else{
                    mView.insertData(list);
                }
            }

            @Override
            public void onError(Throwable e) {
                mView.onError();
            }
        });
    }

    @Override
    public void getMoreData(String lastItemId) {
        if (null != mView.getArguments() && mView.getArguments().getBoolean("isSubscribeList", false)) {
            getSubscribeData(lastItemId);
            return;
        }

        mModel.loadData(lastItemId, new BaseObserver<SelectionBean>() {
            @Override
            public void onNext(SelectionBean value) {
                List<SelectionItem> list = new ArrayList<SelectionItem>();
                for (SelectionArticleBean selectionArticleBean : value.getSelectionList()) {
                    if ("music".equals(selectionArticleBean.getKind())){
                        list.add(new SelectionItem(SelectionItem.MUSIC, selectionArticleBean));
                    }else{
                        list.add(new SelectionItem(SelectionItem.ARTICLE, selectionArticleBean));
                    }
                }
                mView.insertData(list);
            }

            @Override
            public void onError(Throwable e) {
                mView.onError();
            }
        });
    }
}
