package cn.edu.zju.qcw.android.common.entity;

/**
 * Created by SQ on 2017/4/8.
 *
 * 虽然是个Entity，但数据库中始终应该只保存一个
 */
public class UserFollowEntity {
    private long id;

    private String clubId;

    private boolean push;

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getClubId() {
        return this.clubId;
    }

    public void setClubId(String clubId) {
        this.clubId = clubId;
    }

    public boolean getPush() {
        return this.push;
    }

    public void setPush(boolean push) {
        this.push = push;
    }
}
