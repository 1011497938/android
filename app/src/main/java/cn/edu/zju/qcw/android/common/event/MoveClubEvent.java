package cn.edu.zju.qcw.android.common.event;

import cn.edu.zju.qcw.android.common.entity.ClubInfoEntity;

/**
 * Created by SQ on 2017/5/11.
 */

public class MoveClubEvent {

    public static final boolean ADD = true;

    public static final boolean REMOVE = false;

    private boolean flag;

    private long id;

    public MoveClubEvent(boolean addOrRemove, long id) {
        this.flag = addOrRemove;
        this.id = id;
    }

    public boolean addOrRemove() {
        return flag;
    }

    public long getId() {
        return id;
    }
}
