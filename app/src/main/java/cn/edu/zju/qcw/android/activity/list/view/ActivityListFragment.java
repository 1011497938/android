package cn.edu.zju.qcw.android.activity.list.view;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import cn.edu.zju.qcw.android.activity.list.adapter.ActivityListAdapter;
import cn.edu.zju.qcw.android.activity.list.bean.ActivityBean;
import cn.edu.zju.qcw.android.activity.list.bean.ActivityListItem;
import cn.edu.zju.qcw.android.activity.list.presenter.ActivityInterface;
import cn.edu.zju.qcw.android.activity.list.presenter.ActivityPresenter;
import cn.edu.zju.qcw.android.base.mvp_recyclerview_fragment.BaseRecyclerViewFragment;
import cn.edu.zju.qcw.android.browser.BrowserActivity;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.List;

public class ActivityListFragment extends BaseRecyclerViewFragment<ActivityListItem, ActivityListAdapter> implements ActivityInterface.IView {

    private static final String TAG = ActivityListFragment.class.getSimpleName();

    private ActivityInterface.IPresenter mPresenter = new ActivityPresenter(this);

    public ActivityListFragment() {

    }

    /**
     * Super Class Abstract Method Implement
     */

    @Override
    public void onRefresh() {
        super.onRefresh();
        mPresenter.getNewData();
    }

    @Override
    protected void init() {
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ActivityListItem item = getAdapter().getData().get(position);
                if (ActivityListItem.ACTIVITY == item.getItemType() && !TextUtils.isEmpty(item.getActivityBean().getUrl())) {
                    Intent intent = new Intent(getActivity(), BrowserActivity.class);
                    intent.putExtra(BrowserActivity.URL, item.getActivityBean().getUrl());
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void loadMore() {
        mAdapter.setEnableLoadMore(false);
        mAdapter.loadMoreEnd();
    }

    @Override
    protected ActivityListAdapter initRecycleViewAdapter() {
        return new ActivityListAdapter(getActivity());
    }

    @Override
    protected String setEmptyText() {
        return "目前没有活动哦~";
    }

}

