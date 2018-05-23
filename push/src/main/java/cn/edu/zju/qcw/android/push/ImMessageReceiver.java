package cn.edu.zju.qcw.android.push;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.alibaba.fastjson.JSONObject;
import io.rong.push.notification.PushMessageReceiver;
import io.rong.push.notification.PushNotificationMessage;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by SQ on 2017/6/11.
 */

public class ImMessageReceiver extends PushMessageReceiver {
    @Override
    public boolean onNotificationMessageArrived(Context context, PushNotificationMessage message) {
        Log.d("receive IM message", message.getPushContent());
        return false;
    }

    @Override
    public boolean onNotificationMessageClicked(Context context, PushNotificationMessage message) {
        Log.d("click IM message", message.getPushContent());
//        Intent intent = new Intent(this, ChildActivity.class);

        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage("cn.edu.zju.qcw.android");
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        launchIntent.putExtra("show_im", true);
        context.startActivity(launchIntent);
//        EventBus.getDefault().post(new OpenImPushEvent());

        return true;
    }
}
