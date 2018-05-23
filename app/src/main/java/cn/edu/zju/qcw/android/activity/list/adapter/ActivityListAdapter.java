package cn.edu.zju.qcw.android.activity.list.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.edu.zju.qcw.android.R;
import cn.edu.zju.qcw.android.activity.history.view.HistoryActivity;
import cn.edu.zju.qcw.android.activity.list.bean.ActivityBannerBean;
import cn.edu.zju.qcw.android.activity.list.bean.ActivityListItem;
import cn.edu.zju.qcw.android.base.SpacesItemDecoration;
import cn.edu.zju.qcw.android.browser.BrowserActivity;
import cn.edu.zju.qcw.android.common.club.ClubHelper;
import cn.edu.zju.qcw.android.common.event.ShowClubEvent;
import cn.edu.zju.qcw.android.util.date.DateUtil;
import cn.edu.zju.qcw.android.util.image.ImageHelper;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by SQ on 2017/5/20.
 */

public class ActivityListAdapter extends BaseMultiItemQuickAdapter<ActivityListItem, BaseViewHolder> {


    @BindView(R.id.title)
    TextView mTitleView;

    @BindView(R.id.date)
    TextView mDate;

    @BindView(R.id.location)
    TextView mLocation;

    @BindView(R.id.club)
    TextView mClub;

    @BindView(R.id.titleImage)
    ImageView mImage;

    @BindView(R.id.onlineRegister)
    TextView onlineRegister;

    @BindView(R.id.onlineTicket)
    TextView onlineTicket;

    public ActivityListAdapter(Context context) {
        super(null);
        addItemType(ActivityListItem.BANNER, R.layout.item_activity_banner);
        addItemType(ActivityListItem.ACTIVITY, R.layout.item_activity);
        addItemType(ActivityListItem.WEEK, R.layout.item_activity_header);
        addItemType(ActivityListItem.MONTH, R.layout.item_activity_header);
        addItemType(ActivityListItem.OTHER, R.layout.item_activity_header);
    }

    @Override
    protected void convert(BaseViewHolder helper, ActivityListItem item) {
        switch (helper.getItemViewType()) {
            case ActivityListItem.BANNER:
                RecyclerView recyclerView = (RecyclerView) helper.convertView.findViewById(R.id.banner);
                ActivityBannerAdapter adapter = new ActivityBannerAdapter();
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
                linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(adapter);
                adapter.setNewData(item.getActivityBannerBean());
                adapter.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                        Intent intent = new Intent(mContext, BrowserActivity.class);
                        intent.putExtra(BrowserActivity.URL, ((ActivityBannerBean) adapter.getData().get(position)).getUrl());
                        mContext.startActivity(intent);
                    }
                });
                break;
            case ActivityListItem.ACTIVITY:
                ButterKnife.bind(this, helper.itemView);
                mTitleView.setText(item.getActivityBean().getFullname());
                mLocation.setText(item.getActivityBean().getLocation());
                mClub.setText(item.getActivityBean().getClubName());
                mDate.setText(DateUtil.formatDateString("MM-dd HH:mm", item.getActivityBean().getTime()));
                ImageHelper.loadRoundImage(mContext, item.getActivityBean().getPosterUrl(), mImage, 5);

                mClub.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String clubIdStr = ClubHelper.getInstance().getClubIdWithName(mClub.getText().toString());
                        if (clubIdStr != null){
                            Integer clubId = Integer.valueOf(clubIdStr);
                            if (clubId != null) {
                                EventBus.getDefault().post(new ShowClubEvent(clubId));
                            }
                        }
                    }
                });
                if (TextUtils.isEmpty(item.getActivityBean().getTicketId())){
                    onlineTicket.setVisibility(View.GONE);
                }else{
                    onlineTicket.setVisibility(View.VISIBLE);
                }
                if (TextUtils.isEmpty(item.getActivityBean().getRegisterUrl())){
                    onlineRegister.setVisibility(View.GONE);
                }else{
                    onlineRegister.setVisibility(View.VISIBLE);
                }

                break;
            case ActivityListItem.WEEK:
                helper.setText(R.id.date, "一周内");
                setMoreOnclick(helper);
                break;
            case ActivityListItem.MONTH:
                helper.setText(R.id.date, "一月内");
                setMoreOnclick(helper);
                break;
            case ActivityListItem.OTHER:
                helper.setText(R.id.date, "其他");
                setMoreOnclick(helper);
                break;
        }
    }

    public void setMoreOnclick(BaseViewHolder helper) {
        TextView more = (TextView) helper.convertView.findViewById(R.id.more);
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.startActivity(new Intent(mContext, HistoryActivity.class));
            }
        });
    }


}

