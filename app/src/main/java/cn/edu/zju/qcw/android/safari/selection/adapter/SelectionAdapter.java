package cn.edu.zju.qcw.android.safari.selection.adapter;

import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.edu.zju.qcw.android.R;
import cn.edu.zju.qcw.android.common.audio.AudioPlayer;
import cn.edu.zju.qcw.android.common.club.ClubHelper;
import cn.edu.zju.qcw.android.common.comment.view.CommentActivity;
import cn.edu.zju.qcw.android.common.event.ShowClubEvent;
import cn.edu.zju.qcw.android.safari.selection.bean.SelectionArticleBean;
import cn.edu.zju.qcw.android.safari.selection.view.SelectionFragment;
import cn.edu.zju.qcw.android.util.image.ImageHelper;
import cn.edu.zju.qcw.android.util.network.NetworkHelper;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import jp.co.recruit_lifestyle.android.widget.PlayPauseButton;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by SQ on 2017/5/25.
 */

public class SelectionAdapter extends BaseMultiItemQuickAdapter<SelectionItem, BaseViewHolder> {

    @BindView(R.id.selection_title)
    TextView mTitleView;

    @BindView(R.id.selection_subtitle)
    TextView mSubtitleView;

    @BindView(R.id.selection_club_name)
    TextView mClubName;

    @BindView(R.id.selection_follow)
    TextView mFollow;

    @BindView(R.id.see)
    TextView mSee;

    @BindView(R.id.comment)
    TextView mComment;

    @BindView(R.id.date)
    TextView mDate;

    @BindView(R.id.selection_head_image)
    ImageView mClubImage;

    @BindView(R.id.selection_image)
    ImageView mImage;

    @BindView(R.id.clubSection)
    LinearLayout mClubSection;

    private String clubId;

    public SelectionAdapter() {
        super(null);
        addItemType(SelectionItem.ARTICLE, R.layout.item_selection);
        addItemType(SelectionItem.MUSIC, R.layout.item_music);
    }

    @Override
    protected void convert(final BaseViewHolder helper, final SelectionItem item) {
        clubId = item.getSelectionBean().getClub();

        switch (item.getItemType()) {
            case SelectionItem.ARTICLE:
                ButterKnife.bind(this, helper.itemView);

                mTitleView.setText(item.getSelectionBean().getTitle());
                mSubtitleView.setText(item.getSelectionBean().getSubtitle());
                mClubName.setText(item.getSelectionBean().getShortname());
                mFollow.setText(String.format(mContext.getResources().getString(R.string.follow), item.getSelectionBean().getFollow()));
                mSee.setText(item.getSelectionBean().getVisits());
                mComment.setText(item.getSelectionBean().getComments());
                mDate.setText(item.getSelectionBean().getCreatAt().substring(5, 10));

                ImageHelper.loadImage(mContext, item.getSelectionBean().getCoverUrl(), mImage);
                ImageHelper.loadImage(mContext, item.getSelectionBean().getLogo(), mClubImage);

                helper.addOnClickListener(R.id.clubSection);

                mClubSection.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TextView clubName = (TextView) view.findViewById(R.id.selection_club_name);
                        EventBus.getDefault().post(new ShowClubEvent(Integer.parseInt(ClubHelper.getInstance().getClubIdWithName(clubName.getText().toString()))));
                    }
                });
                break;

            case SelectionItem.MUSIC:
                ImageHelper.loadImage(mContext, item.getSelectionBean().getCoverUrl(), (ImageView) helper.getView(R.id.music_Iv));
                helper.setText(R.id.music_title_Tv, item.getSelectionBean().getTitle());
                helper.setText(R.id.music_tag_Tv, String.format("— 电台 ･ %s  —", item.getSelectionBean().getShortname()));

                PlayPauseButton playPauseButton = helper.getView(R.id.music_play_btn);
                playPauseButton.setColor(Color.WHITE);

                helper.getView(R.id.music_tag_Tv).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TextView clubNameTv = (TextView) view.findViewById(R.id.music_tag_Tv);
                        String clubName = clubNameTv.getText().toString().split(" ")[3];
                        EventBus.getDefault().post(new ShowClubEvent(Integer.parseInt(ClubHelper.getInstance().getClubIdWithName(clubName))));
                    }
                });

                helper.getView(R.id.music_comment_icon).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, CommentActivity.class);
                        intent.putExtra("url", item.getSelectionBean().getUrl());
                        mContext.startActivity(intent);
                    }
                });
                String[] array = item.getSelectionBean().getSubtitle().split("&url=");
                final String url = array.length > 1 ? array[1] : "";
                playPauseButton.setOnControlStatusChangeListener(new PlayPauseButton.OnControlStatusChangeListener() {
                    @Override
                    public void onStatusChange(View view, boolean state) {
                        AudioPlayer.getInstance().setButton((PlayPauseButton) helper.getView(R.id.music_play_btn));
                        if (state){
                            if (!url.equals(AudioPlayer.getInstance().getUrl())) {
                                try {
                                    NetworkHelper.requestByGet("https://papiccms.applinzi.com/article/id/" + item.getSelectionBean().getArticleId());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            AudioPlayer.getInstance().playUrl(url);
                        }else{
                            AudioPlayer.getInstance().pause();
                        }
                    }
                });

                break;
        }
    }
}