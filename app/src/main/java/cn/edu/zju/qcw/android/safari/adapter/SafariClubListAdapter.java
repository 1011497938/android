package cn.edu.zju.qcw.android.safari.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.edu.zju.qcw.android.R;
import cn.edu.zju.qcw.android.common.entity.ClubInfoEntity;
import cn.edu.zju.qcw.android.common.entity.ClubSectionEntity;

/**
 * Created by SQ on 2017/4/15.
 */

public class SafariClubListAdapter extends BaseSectionQuickAdapter<ClubSectionEntity, BaseViewHolder>{

    @BindView(R.id.club_name)
    TextView clubName;
    @BindView(R.id.club_new)
    LinearLayout club_new;

    public List<ClubSectionEntity> initialData;

    public boolean onFilter = false;

    public boolean firstLoad = true;

    @Override
    public void setNewData(List<ClubSectionEntity> data) {
        super.setNewData(data);
        if (firstLoad){
            initialData = data;
            firstLoad = false;
        }
    }

    public SafariClubListAdapter() {
        super(R.layout.item_club, R.layout.item_club_sectionhead, null);
    }

    public void filterClub(String query) {
        if (initialData != null && TextUtils.isEmpty(query)){
            onFilter = false;
            setNewData(initialData);
            return;
        }
        if (initialData == null) return;
        onFilter = true;
        List<ClubSectionEntity> filterData = new ArrayList<>();
        for (ClubSectionEntity clubSectionEntity : initialData) {
            if (clubSectionEntity.isHeader || clubSectionEntity.getClubInfoEntity().getShortname().contains(query) || clubSectionEntity.getClubInfoEntity().getFullname().contains(query) ) {
                filterData.add(clubSectionEntity);
            }
        }
        setNewData(filterData);
    }

    @Override
    protected void convertHead(BaseViewHolder helper, ClubSectionEntity item) {
        if (onFilter) {
            if (item.header.equals("section1")) {
                helper.setText(R.id.clublist_header_text, "已关注");
                return;
            }
            helper.setText(R.id.clublist_header_text, "未关注");
        }else {
            if (item.header.equals("section1")) {
                helper.setText(R.id.clublist_header_text, "点击查看详情、设置推送，长按拖动排序");
                return;
            }
            helper.setText(R.id.clublist_header_text, "关注更多组织、社团");
        }
    }

    @Override
    protected void convert(BaseViewHolder helper, ClubSectionEntity item) {
        ButterKnife.bind(this, helper.itemView);
        //TODO:new样式
        clubName.setText(item.getClubInfoEntity().getShortname());
        if ("1".equals(item.getClubInfoEntity().getShowNew())){
            club_new.setVisibility(View.VISIBLE);
        }else {
            club_new.setVisibility(View.GONE);
        }
    }
}
