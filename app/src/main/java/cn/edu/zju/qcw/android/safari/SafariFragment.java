package cn.edu.zju.qcw.android.safari;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import butterknife.BindView;
import butterknife.OnClick;
import cn.edu.zju.qcw.android.R;
import cn.edu.zju.qcw.android.base.BaseAnimationListener;
import cn.edu.zju.qcw.android.base.BaseFragment;
import cn.edu.zju.qcw.android.base.widget.IconTextView;
import cn.edu.zju.qcw.android.base.widget.PapicTabLayout;
import cn.edu.zju.qcw.android.base.widget.TitleBar;
import cn.edu.zju.qcw.android.browser.BrowserActivity;
import cn.edu.zju.qcw.android.common.club.ClubHelper;
import cn.edu.zju.qcw.android.common.event.*;
import cn.edu.zju.qcw.android.common.search.SearchActivity;
import cn.edu.zju.qcw.android.safari.adapter.SafariClubListAdapter;
import cn.edu.zju.qcw.android.safari.adapter.SafariTabAdapter;
import cn.edu.zju.qcw.android.util.Animation.AnimUtil;
import cn.edu.zju.qcw.android.util.Logger;
import cn.edu.zju.qcw.android.util.SizeUtils;
import cn.edu.zju.qcw.android.util.String.StringUtil;
import cn.edu.zju.qcw.android.util.image.ImageHelper;
import com.avos.avoscloud.AVUser;
import com.chad.library.adapter.base.BaseQuickAdapter;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collections;
import java.util.List;

import static cn.edu.zju.qcw.android.MainActivity.EXTRA_URL;
import static java.lang.Math.abs;

public class SafariFragment extends BaseFragment {

    private static final String TAG = SafariFragment.class.getSimpleName();

    private static final int CLUB_LIST_ANIMATION_DURATION = 300;

    private volatile static SafariFragment instance;

    private SafariTabAdapter mAdapter;

    private SafariClubListAdapter mClubListAdapter;

    private SafariFragmentPresenter mPresenter;

    private List<String> mNameList;

    private boolean forceRefreshClubList = false;

    public boolean clubListShown = false;

    public boolean isCollapsed = false;

    @BindView(R.id.safari_tab_layout)
    PapicTabLayout tabLayout;
    @BindView(R.id.safari_viewPager)
    ViewPager viewPager;
    @BindView(R.id.club_list_container)
    FrameLayout clubListContainer;
    @BindView(R.id.safari_showclub_icon)
    public IconTextView safariShowclubIcon;
    @BindView(R.id.club_recycler_view)
    RecyclerView clubRecyclerView;
    @BindView(R.id.safari_search_container)
    LinearLayout searchContainer;
    @BindView(R.id.safari_search_edit)
    EditText searchEdit;
    @BindView(R.id.clubBar)
    RelativeLayout clubBar;
    @BindView(R.id.safari_titlebar)
    TitleBar titleBar;
    @BindView(R.id.safari_appbar_layout)
    AppBarLayout appBarLayout;

    @BindView(R.id.safari_error)
    LinearLayout errorView;
    @BindView(R.id.safari_loading)
    LinearLayout loadingView;

    private int height;

    public static SafariFragment getInstance() {
        if (instance == null) {
            synchronized (SafariFragment.class) {
                if (instance == null) {
                    instance = new SafariFragment();
                    instance.mPresenter = new SafariFragmentPresenter(instance);
                }
            }
        }
        if (instance.mPresenter == null) {
            instance.mPresenter = new SafariFragmentPresenter(instance);
        }
        return instance;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateViewPager(UpdateSafariTabEvent event) {

        mNameList = event.getClubNameList();

        errorView.setVisibility(View.GONE);
        viewPager.setVisibility(View.VISIBLE);
        loadingView.setVisibility(View.GONE);
        clubBar.setVisibility(View.VISIBLE);

        if (mAdapter == null) {
            mAdapter = new SafariTabAdapter(getActivity().getSupportFragmentManager());
        }
        viewPager.setCurrentItem(0, true);
        viewPager.setOffscreenPageLimit(5);
        mAdapter.updateAdapter(mNameList);

        viewPager.setAdapter(mAdapter);
        tabLayout.setViewPager(viewPager);

        if (null == mClubListAdapter.getData() || mClubListAdapter.getData().size() == 0 || forceRefreshClubList) {
            mClubListAdapter.setNewData(getInstance().mPresenter.getNewClubData());
            forceRefreshClubList = false;
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateItem(MoveClubEvent event) {
        mPresenter.updateClubList(event);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateClubList(UpdateClubListEvent event) {
        forceRefreshClubList = true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateUserHead(LoginEvent event) {
        if (AVUser.getCurrentUser() != null) {
            if (AVUser.getCurrentUser().getAVFile("head") != null) {
                titleBar.showLeftImage(AVUser.getCurrentUser().getAVFile("head").getUrl());
            }
        } else {
            titleBar.leftView.setVisibility(View.VISIBLE);
            titleBar.leftImageContainer.setVisibility(View.GONE);
        }
    }

    @Override
    protected int setFragmentLayoutID() {
        return R.layout.fragment_safari;
    }

    @Override
    protected boolean useEventBus() {
        return true;
    }

    @Override
    protected void initListeners() {
        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mClubListAdapter.filterClub(s.toString());
            }
        });

        titleBar.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                intent.putExtra(SearchActivity.SEARCH_ARTICLE, true);
                startActivity(intent);
            }
        });

        titleBar.setLeftOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new ShowDrawerEvent());
            }
        });

        mClubListAdapter.setOnItemClickListener(
                new BaseQuickAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                        mPresenter.clickClubAtIndex(position);
                    }
                });

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                float i = (float) abs(verticalOffset) / (float) SizeUtils.dp2px(35);
                titleBar.refreshMask(i, Color.WHITE);
                if (i > 0.95) {
                    EventBus.getDefault().post(new ChangeStatusStyleEvent(ChangeStatusStyleEvent.STYLE_DARK));
                    isCollapsed = true;
                    return;
                }
                if (i < 0.01) {
                    EventBus.getDefault().post(new ChangeStatusStyleEvent(ChangeStatusStyleEvent.STYLE_LIGHT));
                    isCollapsed = false;
                }
            }
        });
    }

    public SafariClubListAdapter getmClubListAdapter() {
        return mClubListAdapter;
    }

    @Override
    public void onDestroy() {
        mPresenter = null;
        super.onDestroy();
    }

    @Override
    protected void initView() {
        Logger.d("init safariFragment");

        ClubHelper.getInstance().initFollowClubList();

        mClubListAdapter = new SafariClubListAdapter();
        clubRecyclerView.setAdapter(mClubListAdapter);
        clubRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));


        height = SizeUtils.getHeight() - getResources().getDimensionPixelSize(R.dimen.titlbar_height) - SizeUtils.getStatusBarPxHeight();
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
        lp.setMargins(0, -height, 0, 0);
        clubListContainer.setLayoutParams(lp);

        if (AVUser.getCurrentUser() != null) {
            if (AVUser.getCurrentUser().getAVFile("head") != null) {
                titleBar.showLeftImage(AVUser.getCurrentUser().getAVFile("head").getUrl());
            }
        }

        helper.attachToRecyclerView(clubRecyclerView);
    }

    /**
     * ClubList动画
     */

    @OnClick(R.id.safari_showclub_icon)
    public void showClubList(final IconTextView icon) {
        icon.setClickable(false);
        if (!clubListShown) {//下来
            AnimUtil.moveDistanceAnimation(clubListContainer, AnimUtil.MOVE_DOWN, height, CLUB_LIST_ANIMATION_DURATION, new BaseAnimationListener() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    clubListContainer.clearAnimation();
                    FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
                    lp.setMargins(0, 0, 0, 0);
                    clubListContainer.setLayoutParams(lp);

                    AnimUtil.alphaAnimation(searchContainer, 0, 1, 200, null);
                    icon.setText(getResources().getString(R.string.icon_up_arrow));
                    icon.setClickable(true);
                }
            });
        } else {//上去
            AnimUtil.moveDistanceAnimation(clubListContainer, AnimUtil.MOVE_UP, height, CLUB_LIST_ANIMATION_DURATION, new BaseAnimationListener() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
                    lp.setMargins(0, -height, 0, 0);
                    clubListContainer.setLayoutParams(lp);

                    AnimUtil.alphaAnimation(searchContainer, 1, 0, 200, null);
                    icon.setText(getResources().getString(R.string.icon_manage));
                    icon.setClickable(true);

                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(icon.getWindowToken(), 0);
                }
            });
            //同步关注列表顺序
            ClubHelper.getInstance().syncData();
        }
        clubListShown = !clubListShown;
        //EventBus
        EventBus.getDefault().post(new ToggleBottomEvent());
    }

    public void refresh() {
        errorView.setVisibility(View.GONE);
        viewPager.setVisibility(View.GONE);
        loadingView.setVisibility(View.VISIBLE);
        clubBar.setVisibility(View.GONE);
        ClubHelper.getInstance().initFollowClubList();
    }

    public void showError() {
        errorView.setVisibility(View.VISIBLE);
        viewPager.setVisibility(View.GONE);
        loadingView.setVisibility(View.GONE);
        clubBar.setVisibility(View.GONE);
        ((TextView) errorView.findViewById(R.id.netErrorText)).setText(StringUtil.genUnhappyFace() + "\n啊哦出错了，检查下网络吧~");
        errorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });
    }

    /**
     * 对item拖动的设置
     */
    private ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (viewHolder.getLayoutPosition() == 0 || viewHolder.getAdapterPosition() > ClubHelper.getInstance().getUserFollowList().size() || mClubListAdapter.onFilter) {
                    return makeMovementFlags(0, 0);
                }
                return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, 0);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                if (target.getLayoutPosition() == 0 || target.getAdapterPosition() > ClubHelper.getInstance().getUserFollowList().size()) {
                    return false;
                }
                Logger.d("viewHolder.getAdapterPosition():" + viewHolder.getAdapterPosition() + " target.getAdapterPosition:" + target.getAdapterPosition());
                //注意要-1去掉header
                Collections.swap(ClubHelper.getInstance().getUserFollowList(), target.getAdapterPosition() - 1, target.getAdapterPosition() - 1);
                Collections.swap(mClubListAdapter.getData(), target.getAdapterPosition(), target.getAdapterPosition());
                mClubListAdapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

            }

            @Override
            public boolean isLongPressDragEnabled() {
                return true;
            }
        });
}
