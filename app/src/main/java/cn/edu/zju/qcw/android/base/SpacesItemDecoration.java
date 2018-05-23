package cn.edu.zju.qcw.android.base;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by SQ on 2017/4/8.
 */

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

    private int space;

    private int left;

    private int right;
    private int top;
    private int bottom;

    public SpacesItemDecoration(int space) {
        this.space = space;
        left = space;
        right = space;
        top = space;
        bottom = space;
    }

    public SpacesItemDecoration(int left, int right, int top, int bottom) {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = left;
        outRect.right = right;
        outRect.bottom = bottom;
        outRect.top = top;
    }
}
