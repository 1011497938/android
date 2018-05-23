package cn.edu.zju.qcw.android.safari;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import cn.edu.zju.qcw.android.common.club.ClubHelper;
import cn.edu.zju.qcw.android.common.entity.ClubInfoEntity;
import cn.edu.zju.qcw.android.common.entity.ClubSectionEntity;
import cn.edu.zju.qcw.android.common.event.MoveClubEvent;
import cn.edu.zju.qcw.android.common.event.ShowClubEvent;

/**
 * Created by SQ on 2017/4/29.
 */

public class SafariFragmentPresenter {

    private final String SECTION_1 = "section1";

    private final String SECTION_2 = "section2";

    private SafariFragment mView;

    public SafariFragmentPresenter(SafariFragment view) {
        mView = view;
    }

    public void showClubInfo() {

    }

    public void clickClubAtIndex(int position) {
        if (mView.getmClubListAdapter().getData().get(position).isHeader) {
            return;
        }
        EventBus.getDefault().post(new ShowClubEvent((int) mView.getmClubListAdapter().getData().get(position).getClubInfoEntity().getId()));
    }

    public List<ClubSectionEntity> getNewClubData() {
        List<ClubSectionEntity> clubData = new ArrayList<>();
        clubData.add(new ClubSectionEntity(true, SECTION_1));
        for (ClubInfoEntity clubInfoEntity : ClubHelper.getInstance().getUserFollowList()) {
            clubData.add(new ClubSectionEntity(clubInfoEntity));
        }
        clubData.add(new ClubSectionEntity(true, SECTION_2));
        for (ClubInfoEntity clubInfoEntity : ClubHelper.getInstance().getUserUnFollowList()) {
            clubData.add(new ClubSectionEntity(clubInfoEntity));
        }
        return clubData;
    }

    public void updateClubList(MoveClubEvent event) {
        //RecyclerView改变顺序
        boolean flag = false;
        int index = 0;
        int fromIndex = -1;
        int toIndex = -1;
        for (ClubSectionEntity clubSectionEntity : mView.getmClubListAdapter().getData()) {
            if (!clubSectionEntity.isHeader && clubSectionEntity.getClubInfoEntity().getId() == event.getId()) {
                fromIndex = index;
                flag = true;
            }
            if (SECTION_2.equals(clubSectionEntity.header)) {
                toIndex = index;
                if (flag) break;
            }
            index++;
        }
        if (!flag) return;
        if (fromIndex == -1) return;
        if (event.addOrRemove() == MoveClubEvent.ADD) {
            if (toIndex == -1) return;
        }
        if (event.addOrRemove() == MoveClubEvent.REMOVE) {
            toIndex = mView.getmClubListAdapter().getData().size() - 1;
        }
        //由于执行Follow/UnFollow动作时ClubHelper会改变userFollowList的顺序、元素，所以这里不用管userFollowList
        mView.getmClubListAdapter().getData().add(toIndex, mView.getmClubListAdapter().getData().remove(fromIndex));
        mView.getmClubListAdapter().notifyItemMoved(fromIndex, toIndex);
    }
}
