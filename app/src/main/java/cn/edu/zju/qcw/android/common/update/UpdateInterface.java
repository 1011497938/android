package cn.edu.zju.qcw.android.common.update;

import io.reactivex.Observable;
import retrofit2.http.GET;

/**
 * Created by SQ on 2017/6/13.
 */

public interface UpdateInterface {
    @GET("checkVersion")
    Observable<UpdateBean> checkUpdate();
}