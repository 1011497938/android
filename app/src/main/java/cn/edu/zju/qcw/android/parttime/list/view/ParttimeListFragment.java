package cn.edu.zju.qcw.android.parttime.list.view;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import cn.edu.zju.qcw.android.base.BaseBean;
import cn.edu.zju.qcw.android.browser.BrowserActivity;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.edu.zju.qcw.android.R;
import cn.edu.zju.qcw.android.base.mvp_recyclerview_fragment.BaseRecyclerViewFragment;
import cn.edu.zju.qcw.android.parttime.ParttimeFragment;
import cn.edu.zju.qcw.android.parttime.list.bean.ParttimeBean;
import cn.edu.zju.qcw.android.parttime.list.presenter.ParttimeListInterface;
import cn.edu.zju.qcw.android.parttime.list.presenter.ParttimeListPresenter;

public class ParttimeListFragment extends BaseRecyclerViewFragment<ParttimeBean, ParttimeListFragment.ParttimeListAdapter> implements ParttimeListInterface.IView {

    private static final String TAG = ParttimeFragment.class.getSimpleName();

    private ParttimeListInterface.IPresenter mPresenter = new ParttimeListPresenter(this);

    public ParttimeListFragment() {

    }

    /**
     * Super Class Implement
     */

    @Override
    protected void init() {
        mTag = getArguments().getString("kind");
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                BaseBean baseBean = (BaseBean) adapter.getData().get(position);
                Intent intent = new Intent(getActivity(), BrowserActivity.class);
                intent.putExtra("url", baseBean.getUrl());
                intent.putExtra(BrowserActivity.SHOW_PARTTIME_BTN, true);
                intent.putExtra(BrowserActivity.PARTTIME_NUMBER, ((ParttimeBean)adapter.getData().get(position)).getNo());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onRefresh() {
        super.onRefresh();
        mPresenter.refreshWithKind(mTag);
    }

    @Override
    protected void loadMore() {
        mAdapter.setEnableLoadMore(false);
        mAdapter.loadMoreEnd();
    }

    @Override
    protected ParttimeListAdapter initRecycleViewAdapter() {
        return new ParttimeListAdapter();
    }


    class ParttimeListAdapter extends BaseQuickAdapter<ParttimeBean, BaseViewHolder> {

        @BindView(R.id.title)
        TextView mTitleView;

        @BindView(R.id.register)
        TextView mRegister;

        @BindView(R.id.visit)
        TextView mVisit;

        @BindView(R.id.date)
        TextView mDate;

        @BindView(R.id.price)
        TextView mPrice;

        @BindView(R.id.unit)
        TextView mUnit;

        @BindView(R.id.tag)
        TextView mTag;

        ParttimeListAdapter() {
            super(R.layout.item_parttime, null);
        }

        @Override
        protected void convert(BaseViewHolder helper, ParttimeBean bean) {
            ButterKnife.bind(this, helper.convertView);

            mTitleView.setText(bean.getTitle());
            mRegister.setText(String.format(getString(R.string.register), bean.getRegisterNum()));
            mVisit.setText(String.format(getString(R.string.visit), bean.getVisits()));
            mDate.setText(bean.getCreatAt().substring(5, 10));

            if (bean.getPrice().contains("/")) {
                mPrice.setVisibility(View.VISIBLE);
                mUnit.setVisibility(View.VISIBLE);
                mTag.setVisibility(View.INVISIBLE);

                mPrice.setText(bean.getPrice().split("/")[0]);
                mUnit.setText(" /" + bean.getPrice().split("/")[1]);
            } else {
                mPrice.setVisibility(View.INVISIBLE);
                mUnit.setVisibility(View.INVISIBLE);
                mTag.setVisibility(View.VISIBLE);

                mTag.setText(bean.getPrice());

                String type = bean.getType();
                if (type.equals("家教")) {
                    mTag.setBackgroundResource(R.drawable.gradient_blue_green);
                } else if (type.equals("校内")) {
                    mTag.setBackgroundResource(R.drawable.gradient_red_orange);
                } else if (type.equals("校外")) {
                    mTag.setBackgroundResource(R.drawable.gradient_blue_blue);
                } else {
                    mTag.setBackgroundResource(R.color.primary);
                    mTag.setText("来自勤创");
                }
            }
        }
    }
}

