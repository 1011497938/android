package cn.edu.zju.qcw.android.activity.livelist.view;

import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import butterknife.BindView;
import cn.edu.zju.qcw.android.R;
import cn.edu.zju.qcw.android.activity.liveRoom.LiveKit;
import cn.edu.zju.qcw.android.activity.livelist.adapter.LiveListAdapter;
import cn.edu.zju.qcw.android.activity.livelist.presenter.LiveListPresenter;
import cn.edu.zju.qcw.android.base.mvp_activity.BaseMvpRecyclerViewActivtiy;
import cn.edu.zju.qcw.android.base.widget.TitleBar;
import com.chad.library.adapter.base.BaseQuickAdapter;

/**
 * Created by SQ on 2017/5/17.
 */

public class LiveListActivity extends BaseMvpRecyclerViewActivtiy<LiveListAdapter, LiveListPresenter>{

    @BindView(R.id.swipeLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected LiveListAdapter initAdapter() {
        return new LiveListAdapter();
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_live_list;
    }

    @Override
    protected LiveListPresenter initPresenter() {
        return new LiveListPresenter(this);
    }

    @Override
    protected boolean isThemeStyle() {
        return true;
    }

    @Override
    protected void init() {
        if (getPresenter() != null){
            getPresenter().getData();
        }
        LiveKit.init(this);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPresenter().getData();
            }
        });
        swipeRefreshLayout.setColorSchemeColors(Color.rgb(47, 223, 189));
    }

    @Override
    protected void initListeners() {
        if (getAdapter() == null) return;
        getAdapter().setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                getPresenter().didSelectLiveRoom(position);
            }
        });
    }

    @Override
    public String setEmptyText() {
        return "目前没有直播哦";
    }

    @Override
    protected void onResume() {
        getPresenter().getData();
        super.onResume();
    }

    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return swipeRefreshLayout;
    }
}

