package cn.edu.zju.qcw.android.activity.history.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.edu.zju.qcw.android.R;
import cn.edu.zju.qcw.android.activity.list.bean.ActivityBean;
import cn.edu.zju.qcw.android.util.date.DateUtil;
import cn.edu.zju.qcw.android.util.image.ImageHelper;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

/**
 * Created by SQ on 2017/5/17.
 */

public class HistoryAdapter extends BaseQuickAdapter<ActivityBean, BaseViewHolder> {

    @BindView(R.id.titleImage)
    ImageView titleImage;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.date)
    TextView date;
    @BindView(R.id.location)
    TextView location;
    @BindView(R.id.onlineRegister)
    TextView onlineRegister;
    @BindView(R.id.onlineTicket)
    TextView onlineTicket;
    @BindView(R.id.club)
    TextView club;

    public HistoryAdapter() {
        super(R.layout.item_activity, null);
    }

    @Override
    protected void convert(BaseViewHolder helper, ActivityBean item) {
        ButterKnife.bind(this, helper.convertView);

        title.setText(item.getFullname());
        location.setText(item.getLocation());
        club.setText(item.getClubName());
        date.setText(DateUtil.formatDateString("MM-dd HH:mm", item.getTime()));
        ImageHelper.loadRoundImage(mContext, item.getPosterUrl(), titleImage, 5);
        
        if (TextUtils.isEmpty(item.getTicketId())){
            onlineTicket.setVisibility(View.GONE);
        }else{
            onlineTicket.setVisibility(View.VISIBLE);
        }
        if (TextUtils.isEmpty(item.getRegisterUrl())){
            onlineRegister.setVisibility(View.GONE);
        }else{
            onlineRegister.setVisibility(View.VISIBLE);
        }

    }


}
