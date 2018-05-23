package cn.edu.zju.qcw.android;

import android.content.Context;

import android.util.Log;
import butterknife.ButterKnife;
import cn.edu.zju.qcw.android.common.IM.ImHelper;
import cn.edu.zju.qcw.android.util.Logger;
import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.alibaba.sdk.android.push.register.HuaWeiRegister;
import com.alibaba.sdk.android.push.register.MiPushRegister;
import com.avos.avoscloud.*;

import cn.edu.zju.qcw.android.market.list.bean.GoodsBean;
import android.support.multidex.MultiDexApplication;
import io.rong.imkit.RongIM;
import io.rong.push.RongPushClient;

/**
 * Created by SQ on 2017/3/24.
 */

public class PapicApp extends MultiDexApplication  {

    private static Context context;

    private static boolean debug;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        //LeanCloud
        AVObject.registerSubclass(GoodsBean.class);
        AVOSCloud.initialize(this,"2CNicwtrpRYuVrd1c4wAFTwI","NsiOe95SjEJEbg28pir8H1lP");
        AVInstallation.getCurrentInstallation().saveInBackground();
        AVAnalytics.enableCrashReport(this, true);
        PushService.setDefaultPushCallback(this, MainActivity.class);//LeanCloud的PushService仅用于活动大屏幕
        //RongCloud
        RongPushClient.registerMiPush(this, "2882303761517588022", "5791758824022");
//        RongPushClient.registerHWPush(this);
        // TODO: 03/08/2017 huaweiPush
        RongIM.init(this);
        ImHelper.getInstance().setUserInfoProvider();
        ImHelper.getInstance().loginIm();
        //OneSDK
        initCloudChannel(this);
        MiPushRegister.register(this, "2882303761517588022", "5791758824022");
        HuaWeiRegister.register(this);// TODO: 03/08/2017 验证
        //
        canDebug();
    }

    public static Context getAppContext() {
        return context;
    }

    public static boolean isDebug() {
        return debug;
    }

    private void initCloudChannel(Context applicationContext) {
        PushServiceFactory.init(applicationContext);
        CloudPushService pushService = PushServiceFactory.getCloudPushService();
        pushService.register(applicationContext, new CommonCallback() {
            @Override
            public void onSuccess(String response) {
                Logger.d("init cloudchannel success");
            }
            @Override
            public void onFailed(String errorCode, String errorMessage) {
                Logger.d("init cloudchannel failed -- errorcode:" + errorCode + " -- errorMessage:" + errorMessage);
            }
        });
    }

    private void canDebug() {
        if (!"dev".equals(BuildConfig.FLAVOR) && !BuildConfig.DEBUG) {
            Logger.i("------ Don't open the debug mode ------");
            debug = false;
            return;
        }

        Logger.d("------ Open Debug Model ------");

        debug = true;

        AVOSCloud.setDebugLogEnabled(true);
        ButterKnife.setDebug(true);
    }
}
