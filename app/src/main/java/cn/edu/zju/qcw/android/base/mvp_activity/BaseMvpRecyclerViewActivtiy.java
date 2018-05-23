package cn.edu.zju.qcw.android.base.mvp_activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import cn.edu.zju.qcw.android.R;
import cn.edu.zju.qcw.android.util.String.StringUtil;
import com.avos.avoscloud.AVAnalytics;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.List;

/**
 * Created by SQ on 2017/5/14.
 */

public abstract class BaseMvpRecyclerViewActivtiy<A extends BaseQuickAdapter, P extends BaseMvpPresenter> extends BaseMvpActivty {

    @BindView(R.id.base_recyclerView)
    RecyclerView recyclerView;

    protected A mAdapter;

    protected abstract A initAdapter();

    private View notDataView;

    private View loadingView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = initAdapter();

        notDataView = getLayoutInflater().inflate(R.layout.recyclerview_empty, (ViewGroup) recyclerView.getParent(), false);
        notDataView.findViewById(R.id.emptyBtn).setVisibility(View.GONE);
        if (!TextUtils.isEmpty(setEmptyText())) {
            ((TextView) notDataView.findViewById(R.id.emptyText)).setText(StringUtil.genHappyFace() + "\n" + setEmptyText());
        }
        loadingView = getLayoutInflater().inflate(R.layout.recyclerview_loading, (ViewGroup) recyclerView.getParent(), false);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (mAdapter != null) {
            recyclerView.setAdapter(mAdapter);
            mAdapter.setEmptyView(loadingView);
        }

        init();

        initListeners();

        if (enableLoadMore()) {
            getAdapter().setEnableLoadMore(true);
            getAdapter().setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
                @Override
                public void onLoadMoreRequested() {
                    loadMore();
                }
            }, getRecyclerView());
            getAdapter().disableLoadMoreIfNotFullPage();
        }
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public void setAdapter(A mAdapter) {
        this.mAdapter = mAdapter;
    }

    public A getAdapter() {
        return mAdapter;
    }

    @Override
    public P getPresenter() {
        return (P) super.getPresenter();
    }

    public String setEmptyText() {
        return "";
    }

    public void changeEmptyView() {
        mAdapter.setEmptyView(notDataView);
    }

    public void setEmptyView() {
        if (mAdapter != null) {
            mAdapter.setEmptyView(loadingView);
        }
    }
    /**
     * LoadMore
     *
     * @return
     */

    public boolean enableLoadMore() {
        return false;
    }

    public void loadMore() {

    }

    public void loadMoreFinish(List value) {
        if (value.size() > 0) {
            getAdapter().addData(value);
            getAdapter().loadMoreComplete();
        } else {
            getAdapter().loadMoreEnd();
        }
    }

    public void loadMoreFailed() {
        getAdapter().loadMoreFail();
    }

    @Override
    protected void onPause() {
        super.onPause();
        AVAnalytics.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AVAnalytics.onResume(this);
    }
}
