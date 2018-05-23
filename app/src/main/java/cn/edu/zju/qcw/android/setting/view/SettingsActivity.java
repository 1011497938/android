package cn.edu.zju.qcw.android.setting.view;

import android.Manifest;
import android.content.*;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.PermissionChecker;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import cn.edu.zju.qcw.android.R;
import cn.edu.zju.qcw.android.base.mvp_activity.BaseMvpActivty;
import cn.edu.zju.qcw.android.base.widget.TitleBar;
import cn.edu.zju.qcw.android.browser.BrowserActivity;
import cn.edu.zju.qcw.android.setting.about.AboutActivity;
import cn.edu.zju.qcw.android.setting.presenter.SettingsPresenter;
import cn.edu.zju.qcw.android.util.String.StringUtil;
import cn.edu.zju.qcw.android.util.dialog.DialogUtil;
import cn.edu.zju.qcw.android.util.image.GlideCacheUtil;
import cn.edu.zju.qcw.android.util.toast.ToastHelper;
import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.avos.avoscloud.*;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;

import static android.R.attr.targetSdkVersion;
import static cn.edu.zju.qcw.android.util.ticket.SoundUtil.context;


/**
 * Created by SQ on 2017/5/21.
 */

public class SettingsActivity extends BaseMvpActivty<SettingsPresenter> {

    public static final String HOMEPAGE = "http://qcw.zju.edu.cn";

    @BindView(R.id.settings_titlebar)
    TitleBar titlebar;
    @BindView(R.id.pushSwitch)
    Switch pushSwitch;
    @BindView(R.id.cacheSize)
    TextView cacheSize;
    @BindView(R.id.clearCache)
    RelativeLayout clearCache;
    @BindView(R.id.feedback)
    RelativeLayout feedback;
    @BindView(R.id.wechat)
    RelativeLayout wechat;
    @BindView(R.id.homePage)
    RelativeLayout homePage;
    @BindView(R.id.about)
    RelativeLayout about;

    @Override
    protected int initLayout() {
        return R.layout.activity_settings;
    }

    @Override
    protected boolean isThemeStyle() {
        return true;
    }

    @Override
    protected SettingsPresenter initPresenter() {
        return new SettingsPresenter(this);
    }

    @Override
    protected void init() {
        cacheSize.setText(GlideCacheUtil.getInstance().getCacheSize(this));
        PushServiceFactory.getCloudPushService().checkPushChannelStatus(new CommonCallback() {
            @Override
            public void onSuccess(String s) {
                pushSwitch.setChecked("on".equals(s));
            }

            @Override
            public void onFailed(String s, String s1) {
                pushSwitch.setEnabled(false);
            }
        });
    }

    @Override
    protected void initListeners() {
        pushSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pushSwitch.isChecked()) {
                    PushServiceFactory.getCloudPushService().turnOnPushChannel(null);
                    pushSwitch.setChecked(true);
                } else {
                    DialogUtil.showNormalDialog(
                            SettingsActivity.this,
                            "关闭推送后将不在接收订阅社团的推送，确定要关闭推送吗？",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    PushServiceFactory.getCloudPushService().turnOffPushChannel(null);
                                    pushSwitch.setChecked(false);
                                }
                            },
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    pushSwitch.setChecked(true);
                                }
                            }
                    );
                }
            }
        });
    }


    @OnClick({R.id.clearCache, R.id.feedback, R.id.wechat, R.id.homePage, R.id.about})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.clearCache:
                GlideCacheUtil.getInstance().clearImageDiskCache(this);
                AVCacheManager.clearCacheMoreThanOneDay();
                cacheSize.setText(GlideCacheUtil.getInstance().getCacheSize(this));
                if ("0MB".equals(cacheSize.getText().toString())) {
                    ToastHelper.showShortToast(StringUtil.genHappyFace() + " 缓存清除成功");
                }
                break;
            case R.id.feedback:
                AVQuery<AVUser> query = AVQuery.getQuery("_User");
                query.whereEqualTo("mobilePhoneNumber", "15869000608");
                query.getFirstInBackground(new GetCallback<AVUser>() {
                    @Override
                    public void done(AVUser avUser, AVException e) {
                        if (e == null) {
                            RongIM.getInstance().startPrivateChat(getContext(), avUser.getObjectId(), "意见反馈");
                        } else {
                            ToastHelper.showShortToast("哎呀出错了，检查下网络吧~");
                        }
                    }
                });
                break;
            case R.id.wechat:
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData myClip = ClipData.newPlainText("text", "zjupapic");
                cm.setPrimaryClip(myClip);
                ToastHelper.showShortToast(StringUtil.genHappyFace() + " 公众号已复制到剪切板");
                break;
            case R.id.homePage:
                Intent intent = new Intent(this, BrowserActivity.class);
                intent.putExtra(BrowserActivity.URL, HOMEPAGE);
                intent.putExtra(BrowserActivity.SHOW_ICON, false);
                startActivity(intent);
                break;
            case R.id.about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
        }
    }

    public boolean selfPermissionGranted(String permission) {
        // For Android < Android M, self permissions are always granted.
        boolean result = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (targetSdkVersion >= Build.VERSION_CODES.M) {
                // targetSdkVersion >= Android M, we can
                // use Context#checkSelfPermission
                int i = context.checkSelfPermission(permission);
                result = context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
            } else {
                // targetSdkVersion < Android M, we have to use PermissionChecker
                result = PermissionChecker.checkSelfPermission(context, permission)
                        == PermissionChecker.PERMISSION_GRANTED;
            }
        }

        return result;
    }

    private Context getContext() {
        return this;
    }
}
