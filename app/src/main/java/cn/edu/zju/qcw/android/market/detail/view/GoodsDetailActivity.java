package cn.edu.zju.qcw.android.market.detail.view;


import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import cn.edu.zju.qcw.android.R;
import cn.edu.zju.qcw.android.base.mvp_activity.BaseMvpActivty;
import cn.edu.zju.qcw.android.base.widget.TitleBar;
import cn.edu.zju.qcw.android.market.detail.bean.GoodsDetailBean;
import cn.edu.zju.qcw.android.market.detail.presenter.GoodsDetailPresenter;
import cn.edu.zju.qcw.android.util.Animation.AnimUtil;
import cn.edu.zju.qcw.android.util.String.StringUtil;
import cn.edu.zju.qcw.android.util.image.ImageHelper;
import cn.edu.zju.qcw.android.util.toast.ToastHelper;
import com.avos.avoscloud.AVUser;
import com.stx.xhb.xbanner.XBanner;

public class GoodsDetailActivity extends BaseMvpActivty<GoodsDetailPresenter> implements XBanner.XBannerAdapter {

    public static final String GOODS_ID = "goodsId";

    @BindView(R.id.goods_detail_titlebar)
    TitleBar titleBar;
    @BindView(R.id.imageScrollView)
    XBanner imageScrollView;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.userHead)
    ImageView userHead;
    @BindView(R.id.username)
    TextView username;
    @BindView(R.id.date)
    TextView date;
    @BindView(R.id.price)
    TextView price;
    @BindView(R.id.detail)
    TextView detail;
    @BindView(R.id.favorite_icon)
    ImageView favorite_icon;
    @BindView(R.id.actionBar)
    LinearLayout actionBar;
    @BindView(R.id.container)
    LinearLayout container;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @Override
    protected GoodsDetailPresenter initPresenter() {
        return new GoodsDetailPresenter(this);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_goods_detail;
    }

    @Override
    protected void init() {
        getPresenter().loadData(getIntent().getStringExtra(GOODS_ID));
        actionBar.setVisibility(View.GONE);
        container.setVisibility(View.GONE);
    }

    @Override
    protected void initListeners() {

    }

    @Override
    protected boolean isThemeStyle() {
        return true;
    }

    public void setViewWithData(GoodsDetailBean bean) {
        if (bean.getOwnerObj() == null) {
            ToastHelper.showShortToast(StringUtil.genUnhappyFace() + " 哎呀出错了，检查下网络吧~");
            onBackPressed();
        }

        title.setText(bean.getTitle());
        date.setText(bean.getDate());
        price.setText(bean.getPrice());
        detail.setText(bean.getDetail());
        username.setText(bean.getUsername());

        ImageHelper.loadCircleImage(this, bean.getUserHead(), userHead);

        imageScrollView.setData(bean.getImages(), null);
        imageScrollView.setmAdapter(this);

        updateFavoriteIcon();

        AnimUtil.alphaAnimation(container, 0, 1, 300, null);
        AnimUtil.alphaAnimation(progressBar, 1, 0, 300, null);
        if (AVUser.getCurrentUser() != null && AVUser.getCurrentUser().getObjectId().equals(bean.getOwnerObj().getObjectId())) {
            actionBar.setVisibility(View.GONE);
            return;
        }
        AnimUtil.alphaAnimation(actionBar, 0, 1, 300, null);
    }

    public void updateFavoriteIcon() {
        if (getPresenter().getData().getLikeObj().getAVObject("UserId") != null) {//已赞
            favorite_icon.setImageResource(R.drawable.goods_detail_favorite_18dp);
        } else {//未赞
            favorite_icon.setImageResource(R.drawable.goods_detail_favorite_border_white_18dp);
        }
    }

    @OnClick(R.id.like)
    public void like(View view) {
        getPresenter().likeAction();
    }

    @OnClick(R.id.call)
    public void call(View view) {
        getPresenter().callAction();
    }

    @OnClick(R.id.message)
    public void message(View view) {
        getPresenter().messageAction();
    }

    @Override
    protected void onResume() {
        super.onResume();
        imageScrollView.startAutoPlay();
    }

    @Override
    protected void onStop() {
        super.onStop();
        imageScrollView.stopAutoPlay();
    }

    @Override
    public void loadBanner(XBanner banner, Object model, View view, int position) {
        if (getPresenter().getData() == null) return;
        ImageHelper.loadImage(this, getPresenter().getData().getImages().get(position), (ImageView) view);
    }
}
