package cn.edu.zju.qcw.android.activity.history.view;

import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import butterknife.BindView;
import cn.edu.zju.qcw.android.R;
import cn.edu.zju.qcw.android.activity.history.adapter.HistoryAdapter;
import cn.edu.zju.qcw.android.activity.history.presenter.HistoryPresenter;
import cn.edu.zju.qcw.android.activity.list.bean.ActivityBean;
import cn.edu.zju.qcw.android.activity.list.bean.ActivityListItem;
import cn.edu.zju.qcw.android.base.mvp_activity.BaseMvpRecyclerViewActivtiy;
import cn.edu.zju.qcw.android.browser.BrowserActivity;
import com.chad.library.adapter.base.BaseQuickAdapter;

/**
 * Created by SQ on 2017/5/17.
 */

public class HistoryActivity extends BaseMvpRecyclerViewActivtiy<HistoryAdapter, HistoryPresenter> {

    @Override
    protected HistoryAdapter initAdapter() {
        return new HistoryAdapter();
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_history;
    }

    @Override
    protected HistoryPresenter initPresenter() {
        return new HistoryPresenter(this);
    }

    @Override
    protected boolean isThemeStyle() {
        return true;
    }

    @Override
    protected void init() {
        getPresenter().getData();
    }

    @Override
    protected void initListeners() {
        if(mAdapter == null) return;
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ActivityBean activity = mAdapter.getData().get(position);
                if (!TextUtils.isEmpty(activity.getUrl())) {
                    Intent intent = new Intent(HistoryActivity.this, BrowserActivity.class);
                    intent.putExtra(BrowserActivity.URL, activity.getUrl());
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public boolean enableLoadMore() {
        return true;
    }

    @Override
    public void loadMore() {
        getPresenter().getMoreData();
    }
}
