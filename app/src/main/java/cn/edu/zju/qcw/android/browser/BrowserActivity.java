package cn.edu.zju.qcw.android.browser;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.*;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import butterknife.BindView;
import cn.edu.zju.qcw.android.R;
import cn.edu.zju.qcw.android.base.mvp_activity.BaseMvpActivty;
import cn.edu.zju.qcw.android.base.widget.IconTextView;
import cn.edu.zju.qcw.android.common.comment.view.CommentActivity;
import cn.edu.zju.qcw.android.common.menu.BadgeActionProvider;
import cn.edu.zju.qcw.android.util.Animation.AnimUtil;
import com.avos.avoscloud.AVUser;
import com.saltedfishcaptain.library.ShadowLayout;

import java.util.HashMap;

public class BrowserActivity extends BaseMvpActivty<BrowserPresenter> {

    public static final String URL = "url";

    public static final String SHOW_ICON = "showActionIcon";

    public static final String SHOW_PARTTIME_BTN = "showParttimeBtn";

    public static final String PARTTIME_NUMBER = "parttimenumber";

    public static final int SHOW_REGISTER_BTN = 0;

    public static final int SHOW_TICKET_BTN = 1;

    public static final int SHOW_ACT_BTN = 3;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.browser_back_icon)
    IconTextView backIcon;
    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.snackBar)
    FrameLayout snackBarLayout;
    @BindView(R.id.registerBtn)
    ShadowLayout registerBtn;
    @BindView(R.id.ticketBtn)
    ShadowLayout ticketBtn;
    @BindView(R.id.ticketText)
    IconTextView ticketText;
    @BindView(R.id.activityBtnContainer)
    LinearLayout activityBtnContainer;
    @BindView(R.id.parttimeBtn)
    IconTextView parttimeBtn;
    @BindView(R.id.parttimeBtnContainer)
    ShadowLayout parttimeBtnContainer;

    private BadgeActionProvider mLikeActionProvider;
    private BadgeActionProvider mCommentActionProvider;

    private String url;

    private boolean notShowExtraIcon;

    @Override
    protected int initLayout() {
        return R.layout.activity_browser;
    }

    @Override
    protected boolean isThemeStyle() {
        return false;
    }

    @Override
    protected BrowserPresenter initPresenter() {
        return new BrowserPresenter(this);
    }

    @Override
    protected void init() {
        url = getIntent().getStringExtra(URL);
        if (TextUtils.isEmpty(url)) return;
        notShowExtraIcon = url.contains("parttime") || !getIntent().getBooleanExtra(SHOW_ICON, true);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        WebSettings settings = webView.getSettings();

        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        settings.setJavaScriptEnabled(true);
        // 开始 css 适配
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        // 开启AppCache
        settings.setAppCacheEnabled(true);
        // 开启本地存储
        settings.setDomStorageEnabled(true);


        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                HashMap<String, String> map = new HashMap<>();
                map.put("Referer", view.getUrl());
                view.loadUrl(url, map);
                return true;
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                        }
                    }, 500);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        });

        webView.loadUrl(url);

        getPresenter().initNaviBadge();
        if (getIntent().getBooleanExtra(SHOW_PARTTIME_BTN, false)) {
            showParttimeBtn();
        } else {
            getPresenter().initBtnData(url);
        }
    }

    @Override
    protected void initListeners() {
        ticketBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPresenter().selectTicket();
            }
        });
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPresenter().selectRegister();
            }
        });
        parttimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPresenter().selectParttime();
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void showActivityBtn(int i, String ticketString) {
        //TODO:ICON
        switch (i) {
            case SHOW_REGISTER_BTN:
                ticketBtn.setVisibility(View.GONE);
                break;
            case SHOW_TICKET_BTN:
                ticketText.setText(ticketString);
                registerBtn.setVisibility(View.GONE);
                break;
            case SHOW_ACT_BTN:
                ticketText.setText(ticketString);
                break;
        }
        AnimUtil.alphaAnimation(activityBtnContainer, 0, 1, 500, null);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getPresenter().refreshComment();
    }

    public void showParttimeBtn() {
        AnimUtil.alphaAnimation(parttimeBtnContainer, 0, 1, 500, null);
    }

    public void loadNewUrl(String url) {
        AnimUtil.alphaAnimation(activityBtnContainer, 1, 0, 500, null);
        webView.loadUrl(url);
        getPresenter().initBtnData(url);
    }

    /**
     * Menu
     *
     * @return
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.share) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("text/plain");

            intent.putExtra(Intent.EXTRA_SUBJECT, "来自勤创的分享");
            intent.putExtra(Intent.EXTRA_TEXT, url);

            startActivity(Intent.createChooser(intent, "分享"));
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (notShowExtraIcon) {
            getMenuInflater().inflate(R.menu.menu_browser_only_share, menu);
            return true;
        }
        getMenuInflater().inflate(R.menu.menu_browser, menu);
        MenuItem likeItem = menu.findItem(R.id.like);
        MenuItem commentItem = menu.findItem(R.id.comment);
        mLikeActionProvider = (BadgeActionProvider) MenuItemCompat.getActionProvider(likeItem);
        mCommentActionProvider = (BadgeActionProvider) MenuItemCompat.getActionProvider(commentItem);
//        mActionProvider.setOnClickListener(0, onClickListener);// 设置点击监听。
        mLikeActionProvider.setOnClickListener(0, new BadgeActionProvider.OnClickListener() {
            @Override
            public void onClick(int what) {
                getPresenter().likeAction();
                if (AVUser.getCurrentUser() == null) {
                    return;
                }
                boolean liked = mLikeActionProvider.getIcon() == R.drawable.browser_like_24dp;
                mLikeActionProvider.setIcon(liked ? R.drawable.browser_liked_24dp : R.drawable.browser_like_24dp);
                if (liked) {
                    setLikeNumber(mLikeActionProvider.getBadge() + 1);
                } else {
                    setLikeNumber(mLikeActionProvider.getBadge() - 1);
                }
            }
        });
        mCommentActionProvider.setOnClickListener(0, new BadgeActionProvider.OnClickListener() {
            @Override
            public void onClick(int what) {
                Intent intent = new Intent(BrowserActivity.this, CommentActivity.class);
                intent.putExtra("url", url);
                startActivity(intent);
            }
        });
        return true;
    }

    public void setLikeNumber(int count) {
        if (null == mLikeActionProvider) return;
        mLikeActionProvider.setBadge(count);
    }

    public void setCommentNumber(int count) {
        if (null == mCommentActionProvider) return;
        mCommentActionProvider.setBadge(count);
    }

    public void setLikeOrNot(boolean liked) {
        if (null != mLikeActionProvider) {
            mLikeActionProvider.setIcon(liked ? R.drawable.browser_liked_24dp : R.drawable.browser_like_24dp);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (notShowExtraIcon) return;
        if (null == mLikeActionProvider || null == mCommentActionProvider) return;
        if (0 == mLikeActionProvider.getIcon()) {
            mLikeActionProvider.setIcon(R.drawable.browser_like_24dp);
        }
        if (0 == mCommentActionProvider.getIcon()) {
            mCommentActionProvider.setIcon(R.drawable.browser_comment_24dp);
        }
    }

    /**
     * getter & setter
     */
    public String getUrl() {
        return url;
    }

    public boolean isParttime() {
        return notShowExtraIcon;
    }


    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
            return;
        }
        super.onBackPressed();
    }
}
