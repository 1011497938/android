package cn.edu.zju.qcw.android.market.list.view;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;

import cn.edu.zju.qcw.android.market.detail.view.GoodsDetailActivity;
import com.chad.library.adapter.base.BaseQuickAdapter;
import cn.edu.zju.qcw.android.base.mvp_recyclerview_fragment.BaseRecyclerViewFragment;
import cn.edu.zju.qcw.android.base.SpacesItemDecoration;
import cn.edu.zju.qcw.android.market.list.bean.GoodsBean;
import cn.edu.zju.qcw.android.market.list.GoodsListInterface;
import cn.edu.zju.qcw.android.market.list.presenter.GoodsListPresenter;
import cn.edu.zju.qcw.android.market.list.adapter.GridAdapter;

import static cn.edu.zju.qcw.android.market.detail.view.GoodsDetailActivity.GOODS_ID;

public class GoodsGridFragment extends BaseRecyclerViewFragment<GoodsBean, GridAdapter> implements GoodsListInterface.IView {

    private static final String TAG = GoodsGridFragment.class.getSimpleName();

    private GoodsListInterface.IPresenter mPresenter = new GoodsListPresenter(this);

    public GoodsGridFragment() {

    }

    /**
     * Super class abstract method implement
     */

    @Override
    public void onRefresh() {
        super.onRefresh();
        mPresenter.getDataWithKind(getArguments().getString("kind"));
    }

    @Override
    protected void loadMore() {
        mPresenter.getMoreData(getArguments().getString("kind"));
    }

    @Override
    protected GridAdapter initRecycleViewAdapter() {
        return new GridAdapter();
    }

    @Override
    protected boolean customInit() {
        return true;
    }

    @Override
    protected void init() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position > getAdapter().getData().size() - 1) {
                    return 2;
                }else return 1;
            }
        });
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(10));
        mRecyclerView.setBackgroundColor(Color.parseColor("#F5F5F5"));

        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(getActivity(), GoodsDetailActivity.class);
                intent.putExtra(GOODS_ID, mAdapter.getData().get(position).getObjectId());
                startActivity(intent);
            }
        });
    }

}

