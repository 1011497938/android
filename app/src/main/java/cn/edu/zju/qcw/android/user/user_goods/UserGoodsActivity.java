package cn.edu.zju.qcw.android.user.user_goods;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.accessibility.CaptioningManager;
import butterknife.BindView;
import cn.edu.zju.qcw.android.R;
import cn.edu.zju.qcw.android.base.mvp_activity.BaseMvpPresenter;
import cn.edu.zju.qcw.android.base.mvp_activity.BaseMvpRecyclerViewActivtiy;
import cn.edu.zju.qcw.android.market.detail.view.GoodsDetailActivity;
import cn.edu.zju.qcw.android.util.dialog.DialogUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;

/**
 * Created by SQ on 2017/5/24.
 */

public class UserGoodsActivity extends BaseMvpRecyclerViewActivtiy<UserGoodsAdapter, UserGoodsPresenter> {

    @Override
    protected int initLayout() {
        return R.layout.activity_user_goods;
    }

    @Override
    protected BaseMvpPresenter initPresenter() {
        return new UserGoodsPresenter(this);
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
        if (mAdapter == null) return;

        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(UserGoodsActivity.this, GoodsDetailActivity.class);
                intent.putExtra(GoodsDetailActivity.GOODS_ID, mAdapter.getData().get(position).getObjectId());
                startActivity(intent);
            }
        });
        mAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, final int position) {
                final String sale = getAdapter().getData().get(position).getValid() ? "下架" : "上架";
                DialogUtil.showListDialog(UserGoodsActivity.this, null, new String[]{sale, "删除"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                if ("上架".equals(sale)) {
                                    getPresenter().setGoodsStatus(getAdapter().getData().get(position), true);
                                }
                                if ("下架".equals(sale)){
                                    getPresenter().setGoodsStatus(getAdapter().getData().get(position), false);
                                }
                                break;
                            case 1:
                                DialogUtil.showNormalDialog(UserGoodsActivity.this, "确定要删除吗，删除后不可恢复", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        getPresenter().deleteGoods(getAdapter().getData().get(position));
                                    }
                                });
                                break;
                        }
                    }
                });
                return true;
            }
        });
    }

    @Override
    protected UserGoodsAdapter initAdapter() {
        return new UserGoodsAdapter();
    }

    @Override
    public String setEmptyText() {
        return "还没有发布过物品，去跳蚤市场看看吧~";
    }
}
