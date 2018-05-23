package cn.edu.zju.qcw.android.user.parttime_apply;

import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.edu.zju.qcw.android.R;
import cn.edu.zju.qcw.android.parttime.list.bean.ParttimeBean;
import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

/**
 * Created by SQ on 2017/5/23.
 */

public class ApplyAdapter extends BaseItemDraggableAdapter<ParttimeBean, BaseViewHolder> {

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

    public ApplyAdapter() {
        super(R.layout.item_parttime, null);
    }

    @Override
    protected void convert(BaseViewHolder helper, ParttimeBean bean) {
        ButterKnife.bind(this, helper.itemView);

        mTitleView.setText(bean.getTitle());
        mRegister.setText(String.format(mContext.getString(R.string.register), bean.getRegisterNum()));
        mVisit.setText(String.format(mContext.getString(R.string.register), bean.getVisits()));
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
