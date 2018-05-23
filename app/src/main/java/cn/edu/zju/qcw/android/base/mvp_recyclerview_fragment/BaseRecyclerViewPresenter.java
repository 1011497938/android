package cn.edu.zju.qcw.android.base.mvp_recyclerview_fragment;

import java.util.List;

/**
 * Created by SQ on 2017/3/27.
 */

public interface BaseRecyclerViewPresenter {

    void getNewData();

    void getMoreData(String lastItemId);
}
