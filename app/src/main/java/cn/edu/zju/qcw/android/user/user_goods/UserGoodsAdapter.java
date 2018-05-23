package cn.edu.zju.qcw.android.user.user_goods;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.edu.zju.qcw.android.R;
import cn.edu.zju.qcw.android.market.list.bean.GoodsBean;
import cn.edu.zju.qcw.android.util.image.ImageHelper;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * Created by SQ on 2017/5/24.
 */

public class UserGoodsAdapter extends BaseQuickAdapter<GoodsBean, BaseViewHolder> {

    @BindView(R.id.title)
    TextView mTitle;

    @BindView(R.id.like)
    TextView mLike;

    @BindView(R.id.see)
    TextView mSee;

    @BindView(R.id.price)
    TextView mPrice;

    @BindView(R.id.image)
    ImageView mImage;

    public UserGoodsAdapter() {
        super(R.layout.item_user_goods, null);
    }

    @Override
    protected void convert(BaseViewHolder helper, GoodsBean item) {
        ButterKnife.bind(this, helper.itemView);

        mTitle.setText(item.getTitle());
        mLike.setText(String.valueOf(item.getLike()));
        mSee.setText(String.valueOf(item.getSee()));
        mPrice.setText(item.getPrice());

        ImageHelper.loadRoundImage(mContext, item.getHeadUrl(), mImage, 3);
    }
}
