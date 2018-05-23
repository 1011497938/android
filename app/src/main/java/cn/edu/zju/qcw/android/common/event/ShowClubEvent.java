package cn.edu.zju.qcw.android.common.event;

/**
 * Created by SQ on 2017/4/30.
 */

public class ShowClubEvent {

    private int clubId;

    public ShowClubEvent(int clubId) {
        this.clubId = clubId;
    }

    public int getClubId() {
        return clubId;
    }

    public void setClubId(int clubId) {
        this.clubId = clubId;
    }
}
