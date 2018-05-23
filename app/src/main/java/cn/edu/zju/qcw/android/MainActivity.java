package cn.edu.zju.qcw.android;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import cn.edu.zju.qcw.android.activity.ActivityFragment;
import cn.edu.zju.qcw.android.base.BaseObserver;
import cn.edu.zju.qcw.android.base.mvp_activity.BaseMvpActivty;
import cn.edu.zju.qcw.android.base.mvp_activity.BaseMvpPresenter;
import cn.edu.zju.qcw.android.base.widget.IconTextView;
import cn.edu.zju.qcw.android.browser.BrowserActivity;
import cn.edu.zju.qcw.android.common.club.BlurPopupWindow;
import cn.edu.zju.qcw.android.common.event.*;
import cn.edu.zju.qcw.android.common.update.UpdateBean;
import cn.edu.zju.qcw.android.common.update.UpdateInterface;
import cn.edu.zju.qcw.android.market.MarketFragment;
import cn.edu.zju.qcw.android.message.MessageFragment;
import cn.edu.zju.qcw.android.parttime.ParttimeFragment;
import cn.edu.zju.qcw.android.push.BrowserEvent;
import cn.edu.zju.qcw.android.safari.SafariFragment;
import cn.edu.zju.qcw.android.setting.view.SettingsActivity;
import cn.edu.zju.qcw.android.user.parttime_apply.ApplyActivity;
import cn.edu.zju.qcw.android.user.profile.ProfileActivity;
import cn.edu.zju.qcw.android.user.signin.view.SigninActivity;
import cn.edu.zju.qcw.android.user.ticket.TicketActivity;
import cn.edu.zju.qcw.android.user.ticket.animations.AnimationEndListener;
import cn.edu.zju.qcw.android.user.user_goods.UserGoodsActivity;
import cn.edu.zju.qcw.android.util.Logger;
import cn.edu.zju.qcw.android.util.String.StringUtil;
import cn.edu.zju.qcw.android.util.image.ImageHelper;
import cn.edu.zju.qcw.android.util.network.NetworkHelper;
import cn.edu.zju.qcw.android.util.toast.ToastHelper;
import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVUser;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.rong.imkit.RongIM;
import io.rong.imkit.manager.IUnReadMessageObserver;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;


public class MainActivity extends BaseMvpActivty {

    public static final String EXTRA_URL = "extra_url";

    @BindView(R.id.navigation)
    BottomNavigationViewEx navigation;

    @BindView(R.id.main_container)
    LinearLayout mainContainer;

    @BindView(R.id.main_drawer)
    RelativeLayout mainDrawer;

    @BindView(R.id.main_root_view)
    DrawerLayout mainRootView;

    @BindView(R.id.userHead)
    ImageView userHead;
    @BindView(R.id.userName)
    TextView userName;
    @BindView(R.id.userHeadIcon)
    IconTextView userHeadIcon;

    @BindView(R.id.snackBar_container)
    CoordinatorLayout snackBarContainer;

    private Fragment currentFragment;

    private SafariFragment safariFragment;

    private ActivityFragment activityFragment;

    private MarketFragment marketFragment;

    private ParttimeFragment parttimeFragment;

    private MessageFragment messageFragment;

    private Badge badge;

    private long exitTime;

    @Override
    protected boolean isThemeStyle() {
        return true;
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected BaseMvpPresenter initPresenter() {
        return null;
    }

    @Override
    protected void init() {
        EventBus.getDefault().register(this);
        setSwipeBackEnable(false);

        safariFragment = SafariFragment.getInstance();
        currentFragment = safariFragment;
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.tab_content, safariFragment).commit();

        navigation.enableShiftingMode(false);
        navigation.enableItemShiftingMode(false);
        navigation.enableAnimation(false);
        navigation.setTextSize(10);
        navigation.setIconSize(22, 22);
        navigation.setIconsMarginTop(35);

        updateUserHead(new LoginEvent());

        checkUpdate();

        handleNotify();

    }

    @Override
    protected void initListeners() {
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_safari:
                        if (safariFragment == null) {
                            safariFragment = SafariFragment.getInstance();
                        }
                        setStatusBarStyle(safariFragment.isCollapsed);
                        showFragment(safariFragment);
                        AVAnalytics.onFragmentStart("safariFragment");
                        return true;
                    case R.id.navigation_market:
                        if (marketFragment == null) {
                            marketFragment = MarketFragment.getInstance();
                        }
                        setStatusBarStyle(true);
                        showFragment(marketFragment);
                        AVAnalytics.onFragmentStart("marketFragment");
                        return true;
                    case R.id.navigation_activity:
                        if (activityFragment == null) {
                            activityFragment = ActivityFragment.getInstance();
                        }
                        showFragment(activityFragment);
                        setStatusBarStyle(false);
                        AVAnalytics.onFragmentStart("activityFragment");
                        return true;
                    case R.id.navigation_parttime:
                        if (parttimeFragment == null) {
                            parttimeFragment = ParttimeFragment.getInstance();
                        }
                        showFragment(parttimeFragment);
                        setStatusBarStyle(false);
                        AVAnalytics.onFragmentStart("parttimeFragment");
                        return true;
                    case R.id.navigation_message:
                        if (messageFragment == null) {
                            messageFragment = MessageFragment.getInstance();
                        }
                        showFragment(messageFragment);
                        setStatusBarStyle(false);
                        AVAnalytics.onFragmentStart("messageFragment");
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showClubView(ShowClubEvent event) {
//        ClubPopupWindow clubPopupWindow = new ClubPopupWindow(this);
//        clubPopupWindow.showWithId(this, event.getClubId());
        new BlurPopupWindow(this).showWithId(event.getClubId());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateUserHead(LoginEvent event) {
        if (AVUser.getCurrentUser() != null) {
            if (AVUser.getCurrentUser().getAVFile("head") != null) {
                ImageHelper.loadCircleImage(this, AVUser.getCurrentUser().getAVFile("head").getUrl(), userHead);
                userHeadIcon.setVisibility(View.GONE);
                userHead.setVisibility(View.VISIBLE);
            } else {
                userHead.setVisibility(View.GONE);
                userHeadIcon.setVisibility(View.VISIBLE);
            }
            if (!TextUtils.isEmpty(AVUser.getCurrentUser().getUsername())) {
                userName.setText(AVUser.getCurrentUser().getUsername());
            }
        } else {
            userName.setText("登录");
            userHead.setVisibility(View.GONE);
            userHeadIcon.setVisibility(View.VISIBLE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void toggleBottom(ToggleBottomEvent event) {
        if (navigation.getVisibility() == View.VISIBLE) {
            navigation.setVisibility(View.GONE);
            TranslateAnimation animation = new TranslateAnimation(0, 0, 0, navigation.getHeight());
            animation.setDuration(300);
            animation.setFillBefore(true);
            animation.setFillAfter(true);
            animation.setStartOffset(200);
            animation.setAnimationListener(new AnimationEndListener() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    navigation.clearAnimation();
                }
            });
            navigation.startAnimation(animation);
        } else {
            navigation.setVisibility(View.VISIBLE);
            TranslateAnimation animation = new TranslateAnimation(0, 0, navigation.getHeight(), 0);
            animation.setDuration(300);
            animation.setFillBefore(true);
            animation.setFillAfter(true);
            animation.setAnimationListener(new AnimationEndListener() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    navigation.clearAnimation();
                }
            });
            navigation.startAnimation(animation);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showDrawer(ShowDrawerEvent event) {
        mainRootView.openDrawer(mainDrawer);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void changeStatusStyle(ChangeStatusStyleEvent event) {
        setStatusBarStyle(event.getStyle() == ChangeStatusStyleEvent.STYLE_DARK);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateBadge(UpdateBadgeEvent event) {
        RongIMClient.getInstance().getTotalUnreadCount(new RongIMClient.ResultCallback<Integer>() {
            @Override
            public void onSuccess(Integer count) {
                if (null == count) return;
                if (null == badge) {
                    badge = new QBadgeView(MainActivity.this)
                            .setShowShadow(false)
                            .setGravityOffset(15, 2, true)
                            .bindTarget(navigation.getBottomNavigationItemView(4));
                }
                badge.setBadgeNumber(count);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (safariFragment.clubListShown) {
            safariFragment.showClubList(safariFragment.safariShowclubIcon);
            return;
        }
        if (mainRootView.isDrawerOpen(mainDrawer)) {
            mainRootView.closeDrawer(mainDrawer);
            return;
        }
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            ToastHelper.showShortToast("再按一次退出程序");
            exitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }

    @OnClick(R.id.ticket)
    public void goTicket(View view) {
        mainRootView.closeDrawer(mainDrawer);
        startActivity(new Intent(this, TicketActivity.class));
    }

    @OnClick(R.id.user)
    public void goUser(View view) {
        mainRootView.closeDrawer(mainDrawer);
        if (AVUser.getCurrentUser() == null) {
            startActivity(new Intent(this, SigninActivity.class));
        } else {
            startActivity(new Intent(this, ProfileActivity.class));
        }
    }

    @OnClick(R.id.market)
    public void goMarket(View view) {
        mainRootView.closeDrawer(mainDrawer);
        if (AVUser.getCurrentUser() == null) {
            startActivity(new Intent(this, SigninActivity.class));
        } else {
            startActivity(new Intent(this, UserGoodsActivity.class));
        }
    }

    @OnClick(R.id.settings)
    public void goSettings() {
        mainRootView.closeDrawer(mainDrawer);
        startActivity(new Intent(this, SettingsActivity.class));
    }

    @OnClick(R.id.parttime)
    public void goUserParttime() {
        mainRootView.closeDrawer(mainDrawer);
        if (AVUser.getCurrentUser() == null) {
            startActivity(new Intent(this, SigninActivity.class));
        } else {
            startActivity(new Intent(this, ApplyActivity.class));
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (safariFragment == null && fragment instanceof SafariFragment) {
            safariFragment = (SafariFragment) fragment;
        }
        if (activityFragment == null && fragment instanceof ActivityFragment) {
            activityFragment = (ActivityFragment) fragment;
        }
        if (marketFragment == null && fragment instanceof MarketFragment) {
            marketFragment = (MarketFragment) fragment;
        }
        if (parttimeFragment == null && fragment instanceof ParttimeFragment) {
            parttimeFragment = (ParttimeFragment) fragment;
        }
        if (messageFragment == null && fragment instanceof MessageFragment) {
            messageFragment = (MessageFragment) fragment;
        }
    }

    private void showFragment(Fragment fragment) {
        if (fragment == currentFragment) {
            return;
        }
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        if (!fragment.isAdded()) {
            transaction.hide(currentFragment).add(R.id.tab_content, fragment).commit();
        } else {
            transaction.hide(currentFragment).show(fragment).commit();
        }

        currentFragment.setUserVisibleHint(false);
        currentFragment = fragment;
        currentFragment.setUserVisibleHint(true);
    }

    /**
     * 检查更新
     */
    private void checkUpdate() {
        NetworkHelper.requestBuilder(UpdateInterface.class)
                .checkUpdate()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<UpdateBean>() {
                    @Override
                    public void onNext(final UpdateBean updateBean) {
                        if (!updateBean.getVersion().equals(BuildConfig.VERSION_NAME)) {
                            Snackbar.make(snackBarContainer, StringUtil.genHappyFace() + " 检测到新版本", Snackbar.LENGTH_LONG)
                                    .setAction("升级", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent();
                                            intent.setAction("android.intent.action.VIEW");
                                            Uri content_url = Uri.parse(updateBean.getDownloadUrl());
                                            intent.setData(content_url);
                                            startActivity(intent);
                                        }
                                    })
                                    .setActionTextColor(ContextCompat.getColor(MainActivity.this, R.color.primary))
                                    .setDuration(10000)
                                    .show();
                        }
                    }
                });
    }

    /**
     * 处理推送
     */
    private void handleNotify() {
        if (getIntent().hasExtra(EXTRA_URL) && !TextUtils.isEmpty(getIntent().getStringExtra(EXTRA_URL))) {
            Logger.d("receive notification, open url:", getIntent().getStringExtra(EXTRA_URL));
            Intent intent = new Intent(this, BrowserActivity.class);
            intent.putExtra(BrowserActivity.URL, getIntent().getStringExtra(EXTRA_URL));
            startActivity(intent);
        }
    }


    /**
     * 处理应用内打开、除小米华为外打开推送的情况
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleNotifyEvent(BrowserEvent event) {
        if (!TextUtils.isEmpty(event.getUrl())) {
            Logger.d("receive notification, open url:", event.getUrl());
            Intent intent = new Intent(this, BrowserActivity.class);
            intent.putExtra(BrowserActivity.URL, event.getUrl());
            startActivity(intent);
        }
    }

}


