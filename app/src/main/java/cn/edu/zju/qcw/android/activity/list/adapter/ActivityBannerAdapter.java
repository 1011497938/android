package cn.edu.zju.qcw.android.activity.list.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.edu.zju.qcw.android.R;
import cn.edu.zju.qcw.android.activity.list.bean.ActivityBannerBean;
import cn.edu.zju.qcw.android.util.image.ImageHelper;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

/**
 * Created by SQ on 2017/5/17.
 */

public class ActivityBannerAdapter extends BaseQuickAdapter<ActivityBannerBean, BaseViewHolder> {

    @BindView(R.id.bannerImg)
    ImageView bannerImg;
    @BindView(R.id.bannerTitle)
    TextView bannerTitle;
    @BindView(R.id.foot)
    View foot;


    public ActivityBannerAdapter() {
        super(R.layout.item_activity_banner_cell, null);
    }

    @Override
    protected void convert(BaseViewHolder helper, ActivityBannerBean item) {
        ButterKnife.bind(this, helper.itemView);

        bannerTitle.setText(item.getTitle());
        ImageHelper.loadRoundImage(mContext, item.getCoverUrl(), bannerImg, 5);
        if (getData().indexOf(item) == getData().size() - 1) {
            foot.setVisibility(View.VISIBLE);
        }
    }
}
