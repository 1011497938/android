package cn.edu.zju.qcw.android.common.event;

import java.util.List;

/**
 * Created by SQ on 2017/5/13.
 */

public class UpdateSafariTabEvent {

    List<String> clubNameList;

    public UpdateSafariTabEvent(List<String> clubNameList) {
        this.clubNameList = clubNameList;
    }

    public List<String> getClubNameList() {
        return clubNameList;
    }
}
