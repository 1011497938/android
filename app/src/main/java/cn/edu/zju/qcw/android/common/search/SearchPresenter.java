package cn.edu.zju.qcw.android.common.search;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import cn.edu.zju.qcw.android.base.BaseObserver;
import cn.edu.zju.qcw.android.base.mvp_activity.BaseMvpActivty;
import cn.edu.zju.qcw.android.base.mvp_activity.BaseMvpModel;
import cn.edu.zju.qcw.android.base.mvp_activity.BaseMvpPresenter;
import cn.edu.zju.qcw.android.browser.BrowserActivity;
import cn.edu.zju.qcw.android.market.detail.view.GoodsDetailActivity;
import cn.edu.zju.qcw.android.market.list.adapter.GridAdapter;
import cn.edu.zju.qcw.android.market.list.bean.GoodsBean;
import cn.edu.zju.qcw.android.safari.selection.adapter.SelectionAdapter;
import cn.edu.zju.qcw.android.safari.selection.adapter.SelectionItem;
import cn.edu.zju.qcw.android.safari.selection.bean.SelectionArticleBean;
import cn.edu.zju.qcw.android.safari.selection.bean.SelectionBean;
import cn.edu.zju.qcw.android.util.dialog.DialogUtil;
import cn.edu.zju.qcw.android.util.network.NetworkHelper;
import cn.edu.zju.qcw.android.util.toast.ToastHelper;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.chad.library.adapter.base.BaseQuickAdapter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.icu.util.HebrewCalendar.AV;
import static cn.edu.zju.qcw.android.common.search.SearchActivity.SEARCH_ARTICLE;
import static cn.edu.zju.qcw.android.common.search.SearchActivity.SEARCH_MARKET;
import static cn.edu.zju.qcw.android.market.detail.view.GoodsDetailActivity.GOODS_ID;

/**
 * Created by SQ on 2017/5/25.
 */

public class SearchPresenter extends BaseMvpPresenter<SearchActivity,SearchModel>{

    public SearchPresenter(SearchActivity view) {
        super(view);
    }

    @Override
    public SearchModel initModel() {
        return new SearchModel();
    }

    public void searchQuery(String query) {
        getView().setEmptyView();
        DialogUtil.getInstance().showLoading(getView());
        if (getView().getAdapter() == null) {
            if (getView().getIntent().getBooleanExtra(SEARCH_ARTICLE, false)){
                getView().setAdapter(new SelectionAdapter());
                getView().getRecyclerView().setAdapter(getView().getAdapter());

                getView().getAdapter().setSpanSizeLookup(new BaseQuickAdapter.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(GridLayoutManager gridLayoutManager, int position) {
                        return 2;
                    }
                });
                getView().getAdapter().setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                        Intent intent = new Intent(getView(), BrowserActivity.class);
                        intent.putExtra(BrowserActivity.URL, ((SelectionItem)getView().getAdapter().getData().get(position)).getSelectionBean().getUrl());
                        getView().startActivity(intent);
                    }
                });
            }else if (getView().getIntent().getBooleanExtra(SEARCH_MARKET, false)){
                getView().setAdapter(new GridAdapter());
                getView().getRecyclerView().setAdapter(getView().getAdapter());

                getView().getAdapter().setSpanSizeLookup(new BaseQuickAdapter.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(GridLayoutManager gridLayoutManager, int position) {
                        return 1;
                    }
                });
                getView().getAdapter().setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                        Intent intent = new Intent(getView(), GoodsDetailActivity.class);
                        intent.putExtra(GOODS_ID, ((GoodsBean)getView().getAdapter().getData().get(position)).getObjectId());
                        getView().startActivity(intent);
                    }
                });

            }else{
                DialogUtil.getInstance().closeLoading();
                ToastHelper.showShortToast("哎呀出错了，稍后重试吧~");
                getView().onBackPressed();
                return;
            }
        }
        if (getView().getIntent().getBooleanExtra(SEARCH_ARTICLE, false)) {
            NetworkHelper.requestBuilder(ArticleSearchApi.class)
                    .find(query)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new BaseObserver<List<SelectionArticleBean>>(){
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
                            ((SelectionAdapter)getView().getAdapter()).setNewData(list);
                        }

                        @Override
                        public void onError(Throwable e) {
                            ToastHelper.showShortToast("哎呀出错了，稍后重试吧~");
                            getView().onBackPressed();
                        }

                        @Override
                        public void onComplete() {
                            DialogUtil.getInstance().closeLoading();
                            getView().setEmptyText();
                            getView().changeEmptyView();
                        }
                    });
        }
        if (getView().getIntent().getBooleanExtra(SEARCH_MARKET, false)){
            AVQuery<GoodsBean> searchQuery = AVQuery.getQuery("Goods");
            searchQuery.whereContains("title", query);

            AVQuery<GoodsBean> orQuery = AVQuery.getQuery("Goods");
            orQuery.whereContains("description", query);

            AVQuery<GoodsBean> avQuery = AVQuery.or(Arrays.asList(searchQuery, orQuery));
            avQuery.findInBackground(new FindCallback<GoodsBean>() {
                @Override
                public void done(List<GoodsBean> list, AVException e) {
                    if (e == null) {
                        ((GridAdapter)getView().getAdapter()).setNewData(list);
                    }else{
                        ToastHelper.showShortToast("哎呀出错了，稍后重试吧~");
                    }
                    DialogUtil.getInstance().closeLoading();
                    getView().setEmptyText();
                    getView().changeEmptyView();
                }
            });
        }
    }

    interface ArticleSearchApi {
        @GET("q/{query}")
        io.reactivex.Observable<List<SelectionArticleBean>> find(@Path("query") String query);
    }
}
