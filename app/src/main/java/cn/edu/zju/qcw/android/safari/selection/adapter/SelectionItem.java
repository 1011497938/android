package cn.edu.zju.qcw.android.safari.selection.adapter;

import android.support.annotation.Nullable;
import cn.edu.zju.qcw.android.safari.selection.bean.SelectionArticleBean;
import cn.edu.zju.qcw.android.safari.selection.bean.SelectionBean;
import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * Created by SQ on 2017/6/13.
 */

public class SelectionItem implements MultiItemEntity {
    public static final int ARTICLE = 1;
    public static final int MUSIC = 2;

    private SelectionArticleBean selectionBean;
    private int itemType;

    public SelectionItem(int itemType, @Nullable SelectionArticleBean selectionBean) {
        this.itemType = itemType;
        this.selectionBean = selectionBean;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public SelectionArticleBean getSelectionBean() {
        return selectionBean;
    }
}
