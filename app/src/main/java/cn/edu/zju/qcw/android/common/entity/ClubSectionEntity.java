package cn.edu.zju.qcw.android.common.entity;

import com.chad.library.adapter.base.entity.SectionEntity;

import cn.edu.zju.qcw.android.common.entity.ClubInfoEntity;

/**
 * Created by SQ on 2017/4/16.
 */

public class ClubSectionEntity extends SectionEntity<ClubInfoEntity> {

    private ClubInfoEntity clubInfoEntity;

    public ClubInfoEntity getClubInfoEntity() {
        return clubInfoEntity;
    }

    public ClubSectionEntity(boolean isHeader, String header) {
        super(isHeader, header);
    }

    public ClubSectionEntity(ClubInfoEntity clubInfoEntity) {
        super(clubInfoEntity);
        this.clubInfoEntity = clubInfoEntity;
    }

}