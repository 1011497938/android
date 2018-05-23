package cn.edu.zju.qcw.android.safari.article.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cn.edu.zju.qcw.android.base.widget.IconmoonTextView;
import cn.edu.zju.qcw.android.browser.BrowserActivity;
import cn.edu.zju.qcw.android.common.audio.AudioPlayer;
import cn.edu.zju.qcw.android.common.comment.view.CommentActivity;
import cn.edu.zju.qcw.android.util.network.NetworkHelper;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.edu.zju.qcw.android.R;
import cn.edu.zju.qcw.android.base.mvp_recyclerview_fragment.BaseRecyclerViewFragment;
import cn.edu.zju.qcw.android.base.Constant;
import cn.edu.zju.qcw.android.safari.article.bean.ArticleBean;
import cn.edu.zju.qcw.android.safari.article.presenter.ArticleInterface;
import cn.edu.zju.qcw.android.safari.article.presenter.ArticlePresenter;
import cn.edu.zju.qcw.android.util.image.ImageHelper;
import jp.co.recruit_lifestyle.android.widget.PlayPauseButton;

public class ArticleFragment extends BaseRecyclerViewFragment<ArticleBean, ArticleFragment.ArticleListAdapter> implements ArticleInterface.IView {

    private static final String TAG = ArticleFragment.class.getSimpleName();

    private ArticleInterface.IPresenter mPresenter = new ArticlePresenter(this);

    public String clubName;

    public ArticleFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        clubName = getArguments().getString("clubName");
    }

    /**
     * Super Class Abstract Method Implement
     */
    @Override
    public void onRefresh() {
        super.onRefresh();
        mPresenter.getNewData(clubName);
    }

    @Override
    protected void loadMore() {
        mPresenter.getMoreData(clubName, mAdapter.getItem(mAdapter.getItemCount() - Constant.RECYLER_VIEW_DATA_OFFSET).getId());
    }

    @Override
    protected void init() {
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ArticleBean baseBean = (ArticleBean) adapter.getData().get(position);
                if (!"music".equals(baseBean.getKind())) {
                    Intent intent = new Intent(getActivity(), BrowserActivity.class);
                    intent.putExtra("url", baseBean.getUrl());
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), CommentActivity.class);
                    intent.putExtra("url", baseBean.getUrl());
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected ArticleListAdapter initRecycleViewAdapter() {
        return new ArticleListAdapter();
    }


    class ArticleListAdapter extends BaseQuickAdapter<ArticleBean, BaseViewHolder> {

        @BindView(R.id.title)
        TextView mTitleView;

        @BindView(R.id.subtitle)
        TextView mSubtitle;

        @BindView(R.id.titleImage)
        ImageView mImage;

        @BindView(R.id.see)
        TextView mSee;

        @BindView(R.id.comment)
        TextView mComment;

        @BindView(R.id.date)
        TextView mDate;

        @BindView(R.id.music)
        TextView musicTag;

        @BindView(R.id.activity)
        TextView activityTag;

        @BindView(R.id.musicMask)
        View musicMask;

        @BindView(R.id.playBtn)
        PlayPauseButton playPauseButton;

        @BindView(R.id.article_see_icon)
        IconmoonTextView seeIcon;

        ArticleListAdapter() {
            super(R.layout.item_article, null);
        }

        @Override
        protected void convert(final BaseViewHolder helper, final ArticleBean bean) {
            ButterKnife.bind(this, helper.itemView);

            mSubtitle.setText(bean.getSubtitle());
            mSee.setText(bean.getVisits());
            mComment.setText(bean.getComments());
            mDate.setText(bean.getCreatAt().substring(5, 10));

            ImageHelper.loadRoundImage(mContext, bean.getCoverUrl(), mImage, 5);

            if ("music".equals(bean.getKind())) {
                musicTag.setVisibility(View.VISIBLE);
                mTitleView.setText(bean.getTitle());
                musicMask.setVisibility(View.VISIBLE);
                playPauseButton.setVisibility(View.VISIBLE);
                playPauseButton.setColor(Color.WHITE);
                seeIcon.setText(getString(R.string.article_music));

                String[] array = bean.getSubtitle().split("&url=");
                final String url = array.length > 1 ? array[1] : "";
                playPauseButton.setOnControlStatusChangeListener(new PlayPauseButton.OnControlStatusChangeListener() {
                    @Override
                    public void onStatusChange(View view, boolean state) {
                        AudioPlayer.getInstance().setButton((PlayPauseButton) helper.getView(R.id.music_play_btn));
                        if (state) {
                            if (!url.equals(AudioPlayer.getInstance().getUrl())) {
                                try {
                                    NetworkHelper.requestByGet("https://papiccms.applinzi.com/article/id/" + bean.getId());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            AudioPlayer.getInstance().playUrl(url);
                        } else {
                            AudioPlayer.getInstance().pause();
                        }
                    }
                });
                musicMask.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        playPauseButton.callOnClick();
                    }
                });
                mSubtitle.setText(bean.getSubtitle().split("&url=")[0]);
            } else if ("activity".equals(bean.getKind())) {
                activityTag.setVisibility(View.VISIBLE);
                mTitleView.setText(bean.getTitle());
            } else {
                mTitleView.setText(bean.getTitle());
            }
        }
    }
}

