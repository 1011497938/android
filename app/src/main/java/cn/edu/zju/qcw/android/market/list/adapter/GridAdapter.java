package cn.edu.zju.qcw.android.market.list.adapter;

import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.edu.zju.qcw.android.R;
import cn.edu.zju.qcw.android.market.list.bean.GoodsBean;
import cn.edu.zju.qcw.android.market.list.view.GoodsGridFragment;
import cn.edu.zju.qcw.android.util.image.ImageHelper;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

/**
 * Created by SQ on 2017/5/21.
 */

public class GridAdapter extends BaseQuickAdapter<GoodsBean, BaseViewHolder> {

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

    public GridAdapter() {
        super(R.layout.item_goods_grid, null);
    }

    @Override
    protected void convert(BaseViewHolder helper, GoodsBean item) {
        ButterKnife.bind(this, helper.itemView);

        mTitle.setText(item.getTitle());
        mLike.setText(String.valueOf(item.getLike()));
        mSee.setText(String.valueOf(item.getSee()));
        mPrice.setText(item.getPrice());

        ImageHelper.loadImage(mContext, item.getHeadUrl(), mImage);
    }
}