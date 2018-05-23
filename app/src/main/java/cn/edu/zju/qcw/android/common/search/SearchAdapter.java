package cn.edu.zju.qcw.android.common.search;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import cn.edu.zju.qcw.android.base.BaseBean;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * Created by SQ on 2017/5/25.
 */

public class SearchAdapter extends BaseQuickAdapter<Object, BaseViewHolder> {
    public SearchAdapter(@LayoutRes int layoutResId, @Nullable List<Object> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Object item) {

    }
}
