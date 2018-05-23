package cn.edu.zju.qcw.android.push;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.sdk.android.push.MessageReceiver;
import com.alibaba.sdk.android.push.notification.CPushMessage;
import org.greenrobot.eventbus.EventBus;

import java.util.Map;

/**
 * Created by SQ on 2017/6/16.
 */

public class OneSDKMessageReceiver extends MessageReceiver {

    public static final String TAG = OneSDKMessageReceiver.class.getSimpleName();

    public static final String REC_TAG = "receiver";

    @Override
    public void onNotification(Context context, String title, String summary, Map<String, String> extraMap) {
        Log.d("MyMessageReceiver", "Receive notification, title: " + title + ", summary: " + summary + ", extraMap: " + extraMap);
    }

    @Override
    public void onMessage(Context context, CPushMessage cPushMessage) {
        Log.d("MyMessageReceiver", "onMessage, messageId: " + cPushMessage.getMessageId() + ", title: " + cPushMessage.getTitle() + ", content:" + cPushMessage.getContent());
    }

    @Override
    public void onNotificationOpened(Context context, String title, String summary, String extraMap) {
        Log.d("MyMessageReceiver", "onNotificationOpened, title: " + title + ", summary: " + summary + ", extraMap:" + extraMap);
//        Intent launchIntent = new Intent(context, SplashActivity.class);
//        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        if (JSONObject.parseObject(extraMap).containsKey("url")) {
            Log.d(TAG, "receive url :" + JSONObject.parseObject(extraMap).getString("url"));
//            launchIntent.putExtra("extra_url", JSONObject.parseObject(extraMap).getString("url"));
            EventBus.getDefault().post(new BrowserEvent(JSONObject.parseObject(extraMap).getString("url")));
        }
//        context.startActivity(launchIntent);
    }

    @Override
    protected void onNotificationClickedWithNoAction(Context context, String title, String summary, String extraMap) {
        Log.d("MyMessageReceiver", "onNotificationClickedWithNoAction, title: " + title + ", summary: " + summary + ", extraMap:" + extraMap);
    }

    @Override
    protected void onNotificationReceivedInApp(Context context, String title, String summary, Map<String, String> extraMap, int openType, String openActivity, String openUrl) {
        Log.d("MyMessageReceiver", "onNotificationReceivedInApp, title: " + title + ", summary: " + summary + ", extraMap:" + extraMap + ", openType:" + openType + ", openActivity:" + openActivity + ", openUrl:" + openUrl);
    }

    @Override
    protected void onNotificationRemoved(Context context, String messageId) {
        Log.d("MyMessageReceiver", "onNotificationRemoved");
    }
}
