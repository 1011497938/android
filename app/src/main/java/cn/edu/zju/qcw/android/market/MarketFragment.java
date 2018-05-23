package cn.edu.zju.qcw.android.market;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.OnClick;
import cn.edu.zju.qcw.android.R;
import cn.edu.zju.qcw.android.base.BaseFragment;
import cn.edu.zju.qcw.android.base.BaseObserver;
import cn.edu.zju.qcw.android.base.widget.PapicTabLayout;
import cn.edu.zju.qcw.android.browser.BrowserActivity;
import cn.edu.zju.qcw.android.common.club.ClubHelper;
import cn.edu.zju.qcw.android.common.entity.ClubInfoEntity;
import cn.edu.zju.qcw.android.common.search.SearchActivity;
import cn.edu.zju.qcw.android.market.compose.view.ComposeActivity;
import cn.edu.zju.qcw.android.market.list.view.GoodsGridFragment;
import cn.edu.zju.qcw.android.parttime.list.view.ParttimeListFragment;
import cn.edu.zju.qcw.android.safari.adapter.SafariTabAdapter;
import cn.edu.zju.qcw.android.user.signin.view.SigninActivity;
import cn.edu.zju.qcw.android.util.Animation.AnimUtil;
import cn.edu.zju.qcw.android.util.image.ImageHelper;
import cn.edu.zju.qcw.android.util.network.NetworkHelper;
import com.avos.avoscloud.AVUser;
import com.stx.xhb.xbanner.XBanner;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.http.GET;
import retrofit2.http.Path;


/**
 * 懒得MVP了……
 */
public class MarketFragment extends BaseFragment implements XBanner.XBannerAdapter {

    private static final String TAG = MarketFragment.class.getSimpleName();

    private static final String mTabTitle[] = new String[]{"全部", "衣", "食", "住", "行", "学习", "运动", "电子", "虚拟", "娱乐", "出租", "赠送", "其他"};

    public static final int BANNER_LINK = 0;

    public static final int BANNER_IMG = 1;

    private volatile static MarketFragment instance;

    public boolean isCollapsed = false;

    private List<Fragment> mFragmentList;

    private List<List<String>> bannerData; //[0]是跳转链接 [1]是图片地址

    @BindView(R.id.market_tab_layout)
    PapicTabLayout mTabLayout;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.banner)
    XBanner banner;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout toolbarLayout;
    @BindView(R.id.app_bar)
    AppBarLayout appBarLayout;
    @BindView(R.id.compose)
    FloatingActionButton compose;

    public MarketFragment() {

    }

    public static MarketFragment getInstance() {
        if (instance == null) {
            synchronized (MarketFragment.class) {
                if (instance == null) {
                    instance = new MarketFragment();
                }
            }
        }
        return instance;
    }

    @Override
    protected int setFragmentLayoutID() {
        return R.layout.fragment_market;
    }

    @Override
    protected void initView() {

        toolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
        toolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);

        mFragmentList = new ArrayList<>();

        for (int i = 0; i < mTabTitle.length; i++) {
            Bundle bundle = new Bundle();
            bundle.putString("kind", Arrays.asList(mTabTitle).get(i));
            GoodsGridFragment fragment = new GoodsGridFragment();
            fragment.setArguments(bundle);
            mFragmentList.add(fragment);
        }
        MarketAdapter adapter = new MarketAdapter(getActivity().getSupportFragmentManager(), mFragmentList);
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(3);
        mTabLayout.setViewPager(mViewPager);

        banner.setmAdapter(this);
        //加载banner数据
        NetworkHelper.requestBuilder(MarketBannerApi.class)
                .getMarketBanner()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<List<List<String>>>() {
                    @Override
                    public void onNext(List<List<String>> lists) {
                        bannerData = lists;
                        banner.setData(lists.get(BANNER_IMG), null);
                    }
                });
    }

    @Override
    protected void initListeners() {
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset == 0) {
                    if (state != CollapsingToolbarLayoutState.EXPANDED) {
                        state = CollapsingToolbarLayoutState.EXPANDED;//修改状态标记为展开
                        toolbarLayout.setTitle("");//设置title为EXPANDED
                        isCollapsed = true;
                        AnimUtil.alphaAnimation(compose, 0, 1, 200, null);
                    }
                } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                    if (state != CollapsingToolbarLayoutState.COLLAPSED) {
                        toolbarLayout.setTitle("跳蚤市场");//设置title不显示
                        state = CollapsingToolbarLayoutState.COLLAPSED;//修改状态标记为折叠
                        isCollapsed = false;
                        AnimUtil.alphaAnimation(compose, 1, 0, 200, null);
                    }
                } else {
                    if (state != CollapsingToolbarLayoutState.INTERNEDIATE) {
                        if (state == CollapsingToolbarLayoutState.COLLAPSED) {
                            //由折叠变为中间状态时隐藏播放按钮
                        }
                        toolbarLayout.setTitle("");//设置title为INTERNEDIATE
                        state = CollapsingToolbarLayoutState.INTERNEDIATE;//修改状态标记为中间
                    }
                }
            }
        });

        banner.setOnItemClickListener(new XBanner.OnItemClickListener() {
            @Override
            public void onItemClick(XBanner banner, int position) {
                Intent intent = new Intent(getActivity(), BrowserActivity.class);
                intent.putExtra(BrowserActivity.URL, bannerData.get(BANNER_LINK).get(position));
                startActivity(intent);
            }
        });
        compose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AVUser.getCurrentUser() == null) {
                    startActivity(new Intent(getContext(), SigninActivity.class));
                    return;
                }
                startActivity(new Intent(getContext(), ComposeActivity.class));
            }
        });

    }

    @OnClick(R.id.market_search_icon)
    public void goSearch() {
        Intent intent = new Intent(getActivity(), SearchActivity.class);
        intent.putExtra(SearchActivity.SEARCH_MARKET, true);
        startActivity(intent);
    }


    @Override
    public void loadBanner(XBanner banner, Object model, View view, int position) {
        if (bannerData == null) return;
        ImageHelper.loadImage(this, bannerData.get(BANNER_IMG).get(position), (ImageView) view);
    }


    private class MarketAdapter extends FragmentStatePagerAdapter {

        private List<Fragment> mList;

        MarketAdapter(FragmentManager fm, List<Fragment> list) {
            super(fm);
            mList = list;
        }

        @Override
        public Fragment getItem(int position) {
            return mList.get(position);
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabTitle[position];
        }

    }

    interface MarketBannerApi {
        @GET("marketbanner")
        Observable<List<List<String>>> getMarketBanner();
    }

    private CollapsingToolbarLayoutState state;

    private enum CollapsingToolbarLayoutState {
        EXPANDED,
        COLLAPSED,
        INTERNEDIATE
    }
}
