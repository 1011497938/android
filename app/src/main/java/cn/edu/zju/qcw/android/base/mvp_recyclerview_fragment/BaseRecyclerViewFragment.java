package cn.edu.zju.qcw.android.base.mvp_recyclerview_fragment;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import cn.edu.zju.qcw.android.base.BaseBean;
import cn.edu.zju.qcw.android.util.String.StringUtil;
import cn.edu.zju.qcw.android.util.toast.ToastHelper;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.List;

import cn.edu.zju.qcw.android.R;
import cn.edu.zju.qcw.android.browser.BrowserActivity;
import cn.edu.zju.qcw.android.safari.selection.bean.SelectionArticleBean;

public abstract class BaseRecyclerViewFragment<T, A extends BaseQuickAdapter> extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private final String TAG = this.getClass().getSimpleName();

    protected String mTag;

    protected RecyclerView mRecyclerView;

    protected A mAdapter;

    protected SwipeRefreshLayout mSwipeRefreshLayout;

    private View rootView;

    private View notDataView;

    private View errorView;

    private View loadingView;

    public BaseRecyclerViewFragment() {

    }

    protected abstract A initRecycleViewAdapter();

    protected abstract void init();

    /**
     * 下拉刷新、上拉刷新方法
     */
    protected abstract void loadMore();

    @Override
    public void onRefresh() {
        setLoading();
    }//onRefresh直接重写SwipeRefreshLayout接口

    public void reloadData(List<T> list) {
        if (list.size() == 0) {
            setEmpty();
        }
        mAdapter.setNewData(list);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    public void insertData(List<T> list) {
        mAdapter.addData(list);
        if (list.size() > 0) {
            mAdapter.loadMoreComplete();
        } else {
            mAdapter.loadMoreEnd();
        }
    }

    public void onError() {
        setError();
        if (mAdapter != null && mAdapter.getData().size() > 0) {
            ToastHelper.showShortToast(StringUtil.genUnhappyFace() + " 哎呀出错了，检查下网络吧~");
        }
        if (null != mSwipeRefreshLayout && mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    protected boolean customInit() {
        return false;
    }

    /**
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (null != rootView) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (null != parent) {
                parent.removeView(rootView);
            }
            return rootView;
        } else {
            rootView = inflater.inflate(R.layout.base_recyclerview, container, false);
        }
        //初始化顺序不能错
        mAdapter = initRecycleViewAdapter();

        setRecyclerView();

        if (customInit()) init();

        setSwipeRefresh();

        setRecyclerViewAdapter(inflater);
        //lazyLoad
        setUserVisibleHint(getUserVisibleHint());
        //
        if (!customInit()) init();
        //
        return rootView;
    }


    /**
     * 用于lazyload，对于RecycleView来说，只需要LazyLoad onRefresh方法
     *
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            if (mAdapter != null && !mAdapter.isLoading() && (mAdapter.getData() == null || mAdapter.getData().size() == 0)) {
                onRefresh();
            }
        }
    }

    /**
     * @param inflater
     */
    private void setRecyclerViewAdapter(LayoutInflater inflater) {
        mAdapter.openLoadAnimation();
        mAdapter.setNotDoAnimationCount(10);
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadMore();
            }
        }, mRecyclerView);
        if (mAdapter.getOnItemClickListener() == null) {
            mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    BaseBean baseBean = (BaseBean) adapter.getData().get(position);
                    Intent intent = new Intent(getActivity(), BrowserActivity.class);
                    intent.putExtra("url", baseBean.getUrl());
                    if (baseBean.getTitle() != null) {
                        intent.putExtra("title", baseBean.getTitle());
                    }
                    startActivity(intent);
                }
            });
        }

        //
        notDataView = inflater.inflate(R.layout.recyclerview_empty, (ViewGroup) mRecyclerView.getParent(), false);
        notDataView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRefresh();
            }
        });
        errorView = inflater.inflate(R.layout.recyclerview_error, (ViewGroup) mRecyclerView.getParent(), false);
        errorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRefresh();
            }
        });
        loadingView = inflater.inflate(R.layout.recyclerview_loading, (ViewGroup) mRecyclerView.getParent(), false);
    }

    private void setSwipeRefresh() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeLayout);
        mSwipeRefreshLayout.setColorSchemeColors(Color.rgb(47, 223, 189));
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    private void setRecyclerView() {
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.base_recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
    }

    protected void refreshEmptyView() {
        ((TextView) notDataView.findViewById(R.id.emptyText)).setText(StringUtil.genHappyFace() + "\n" + setEmptyText());
        ((TextView) errorView.findViewById(R.id.netErrorText)).setText(StringUtil.genUnhappyFace() + "\n啊哦出错了，检查下网络吧~");
    }

    protected void setEmpty() {
        refreshEmptyView();
        mAdapter.setEmptyView(notDataView);
    }

    protected void setError() {
        refreshEmptyView();
        mAdapter.setEmptyView(errorView);
    }

    protected void setLoading() {
        mAdapter.setEmptyView(loadingView);
    }

    protected String setEmptyText() {
        return "暂时还没有内容哦";
    }

    public A getAdapter() {
        return mAdapter;
    }
}
