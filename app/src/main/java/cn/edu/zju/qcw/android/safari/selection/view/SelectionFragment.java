package cn.edu.zju.qcw.android.safari.selection.view;

import android.content.Intent;
import android.view.View;
import cn.edu.zju.qcw.android.R;
import cn.edu.zju.qcw.android.base.BaseBean;
import cn.edu.zju.qcw.android.base.Constant;
import cn.edu.zju.qcw.android.base.mvp_recyclerview_fragment.BaseRecyclerViewFragment;
import cn.edu.zju.qcw.android.browser.BrowserActivity;
import cn.edu.zju.qcw.android.common.audio.AudioPlayer;
import cn.edu.zju.qcw.android.safari.selection.adapter.SelectionAdapter;
import cn.edu.zju.qcw.android.safari.selection.adapter.SelectionItem;
import cn.edu.zju.qcw.android.safari.selection.bean.SelectionArticleBean;
import cn.edu.zju.qcw.android.safari.selection.SelectionInterface;
import cn.edu.zju.qcw.android.safari.selection.presenter.SelectionPresenter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import jp.co.recruit_lifestyle.android.widget.PlayPauseButton;

public class SelectionFragment extends BaseRecyclerViewFragment<SelectionItem, SelectionAdapter> {

    private static final String TAG = SelectionFragment.class.getSimpleName();

    public static final String FOLLOW_LIST = "isSubscribeList";

    private SelectionInterface.IPresenter mPresenter = new SelectionPresenter(this);

    /**
     * Super class abstract method implement
     */

    @Override
    public void onRefresh() {
        super.onRefresh();
        mPresenter.getNewData();
    }

    @Override
    protected void loadMore() {
        //注意最后会自动加一个viewholder作为“下拉加载”指示，所以RECYLER_VIEW_DATA_OFFSET=2
        mPresenter.getMoreData(mAdapter.getItem(mAdapter.getItemCount() - Constant.RECYLER_VIEW_DATA_OFFSET).getSelectionBean().getArticleId());
    }

    @Override
    protected void init() {
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                SelectionItem item = (SelectionItem) adapter.getData().get(position);
                if (!"music".equals(item.getSelectionBean().getKind())) {
                    Intent intent = new Intent(getActivity(), BrowserActivity.class);
                    intent.putExtra("url", item.getSelectionBean().getUrl());
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected String setEmptyText() {
        return "还没有关注社团哦";
    }

    @Override
    protected SelectionAdapter initRecycleViewAdapter() {
        return new SelectionAdapter();
    }

    /**
     * Interface Implement
     */



}

