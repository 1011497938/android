package cn.edu.zju.qcw.android.user.parttime_apply;

import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.View;
import butterknife.BindView;
import cn.edu.zju.qcw.android.R;
import cn.edu.zju.qcw.android.base.mvp_activity.BaseMvpPresenter;
import cn.edu.zju.qcw.android.base.mvp_activity.BaseMvpRecyclerViewActivtiy;
import cn.edu.zju.qcw.android.browser.BrowserActivity;
import com.chad.library.adapter.base.BaseQuickAdapter;

/**
 * Created by SQ on 2017/5/23.
 */

public class ApplyActivity extends BaseMvpRecyclerViewActivtiy<ApplyAdapter, ApplyPresenter> {

    @Override
    protected int initLayout() {
        return R.layout.activity_user_parttime;
    }

    @Override
    protected BaseMvpPresenter initPresenter() {
        return new ApplyPresenter(this);
    }

    @Override
    protected void init() {
        getPresenter().getData();
    }

    @Override
    protected boolean isThemeStyle() {
        return true;
    }

    @Override
    protected void initListeners() {
        if (getAdapter() == null) return;
        getAdapter().setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(ApplyActivity.this, BrowserActivity.class);
                intent.putExtra(BrowserActivity.URL, getAdapter().getData().get(position).getUrl());
                startActivity(intent);
            }
        });

//        final Paint paint = new Paint();
//        paint.setAntiAlias(true);
//        paint.setTextSize(20);
//        paint.setColor(Color.WHITE);
//        OnItemSwipeListener onItemSwipeListener = new OnItemSwipeListener() {
//            @Override
//            public void onItemSwipeStart(RecyclerView.ViewHolder viewHolder, int pos) {
//            }
//
//            @Override
//            public void clearView(RecyclerView.ViewHolder viewHolder, int pos) {
//            }
//
//            @Override
//            public void onItemSwiped(RecyclerView.ViewHolder viewHolder, int pos) {
//            }
//
//            @Override
//            public void onItemSwipeMoving(Canvas canvas, RecyclerView.ViewHolder viewHolder, float dX, float dY, boolean isCurrentlyActive) {
//                String str = "删除";
//
//                canvas.drawColor(Color.RED);
//                canvas.drawText("Just some text", 0, 40, paint);
//
//                Rect bounds = new Rect();
//                paint.getTextBounds(testString, 0, testString.length(), bounds);
//                Paint.FontMetricsInt fontMetrics = mPaint.getFontMetricsInt();
//                int baseline = (getMeasuredHeight() - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
//                canvas.drawText(testString,getMeasuredWidth() / 2 - bounds.width() / 2, baseline, mPaint);
//            }
//        };
//
//        ItemDragAndSwipeCallback callback = new ItemDragAndSwipeCallback(getAdapter());
//        ItemTouchHelper helper = new ItemTouchHelper(callback);
//        helper.attachToRecyclerView(getRecyclerView());
//
//        //mItemDragAndSwipeCallback.setDragMoveFlags(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.UP | ItemTouchHelper.DOWN);
//        callback.setSwipeMoveFlags(ItemTouchHelper.END);
//        mAdapter.enableSwipeItem();
//        mAdapter.setOnItemSwipeListener(onItemSwipeListener);
//        mAdapter.enableDragItem(helper);
    }

    @Override
    protected ApplyAdapter initAdapter() {
        return new ApplyAdapter();
    }

    @Override
    public String setEmptyText() {
        return "还没有报名过兼职哦";
    }
}
