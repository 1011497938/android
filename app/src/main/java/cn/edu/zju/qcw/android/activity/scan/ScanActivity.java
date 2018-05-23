package cn.edu.zju.qcw.android.activity.scan;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import cn.edu.zju.qcw.android.R;
import cn.edu.zju.qcw.android.activity.scan.view.CustomViewFinderView;
import cn.edu.zju.qcw.android.activity.wall.view.WallActivity;
import cn.edu.zju.qcw.android.base.mvp_activity.BaseMvpActivty;
import cn.edu.zju.qcw.android.base.mvp_activity.BaseMvpPresenter;
import cn.edu.zju.qcw.android.base.widget.LoadingDialog;
import cn.edu.zju.qcw.android.base.widget.TitleBar;
import cn.edu.zju.qcw.android.browser.BrowserActivity;
import cn.edu.zju.qcw.android.common.bean.AVExceptionBean;
import cn.edu.zju.qcw.android.user.signin.view.SigninActivity;
import cn.edu.zju.qcw.android.util.String.StringUtil;
import cn.edu.zju.qcw.android.util.ticket.SoundUtil;
import cn.edu.zju.qcw.android.util.ticket.UriUtils;
import cn.edu.zju.qcw.android.util.toast.ToastHelper;
import com.avos.avoscloud.AVCloud;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FunctionCallback;
import com.google.gson.Gson;
import com.google.zxing.Result;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import scanqrcodelib.core.IViewFinder;
import scanqrcodelib.util.QRCodeDecoder;
import scanqrcodelib.view.ZXingScannerView;

import java.util.HashMap;
import java.util.Map;

public class ScanActivity extends BaseMvpActivty implements ZXingScannerView.ResultHandler {

    private final int INTENT_GET_PHOTO = 100;

    private ZXingScannerView mScannerView;
    //重置扫描时间
    private final int SCAN_TIME = 500;
    //是否播放声音
    private boolean isSound = true;
    //是否震动
    private boolean isVibrator = true;

    @BindView(R.id.activity_scan_titlebar)
    TitleBar titlebar;

    @Override
    protected void init() {
        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(this) {
            @Override
            protected IViewFinder createViewFinderView(Context context) {
                return new CustomViewFinderView(context);
            }
        };
        contentFrame.addView(mScannerView);
        if (isSound) {
            SoundUtil.initSoundPool(this);
        }

        titlebar.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doTakePhoto();
            }
        });
    }

    @Override
    protected void initListeners() {

    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_scan;
    }

    @Override
    protected BaseMvpPresenter initPresenter() {
        return null;
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        //播放声音
        if (isSound) SoundUtil.play(1, 0);

        //震动
        if (isVibrator) showVibrator();

        handResultStr(rawResult.getText());
    }


    /**
     * 重新启动扫描
     */
    public void resumeCameraPreview() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(ScanActivity.this);
            }
        }, SCAN_TIME);
    }

    /**
     * 震动效果
     */
    private void showVibrator() {
        Vibrator vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        long[] pattern = {30, 400};
        vibrator.vibrate(pattern, -1);
    }

    /**
     * 从相册选择
     */
    public void doTakePhoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, INTENT_GET_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case INTENT_GET_PHOTO://相册选择回来的回调
                if (resultCode == Activity.RESULT_OK) {
                    if (null != data) {
                        Uri mImageCaptureUri = data.getData();
                        String imgagePath = UriUtils.getImageAbsolutePath(this, mImageCaptureUri);
                        if (TextUtils.isEmpty(imgagePath)) return;
                        String result = QRCodeDecoder.syncDecodeQRCode(imgagePath);
                        handResultStr(result);
                    }
                }
                break;
        }
    }

    private void handResultStr(String result) {
        if (result.contains("ticket/")) {
            if (AVUser.getCurrentUser() == null) {
                startActivity(new Intent(this, SigninActivity.class));
                return;
            }
            result = result.split("/")[1];

            Map<String, String> dicParameters = new HashMap<String, String>();
            dicParameters.put("targetTicketId", result);
            dicParameters.put("userId", AVUser.getCurrentUser().getObjectId());
            final LoadingDialog loadingDialog = new LoadingDialog(this, null);
            loadingDialog.show();
            AVCloud.callFunctionInBackground("getTicket", dicParameters, new FunctionCallback<Object>() {
                @Override
                public void done(Object avObject, AVException e) {
                    loadingDialog.close();
                    if (e == null) {
                        //TODO:加入提醒事项
                        ToastHelper.showShortToast("领票成功,可在个人中心“门票”中查看");
                        onBackPressed();
                    } else {
                        Gson gson = new Gson();
                        ToastHelper.showShortToast(StringUtil.genUnhappyFace() + gson.fromJson(e.getLocalizedMessage().toString(), AVExceptionBean.class).getError());
                        resumeCameraPreview();
                    }
                }
            });
        } else if (result.contains("ActivityWall/")) {
            if (AVUser.getCurrentUser() == null) {
                startActivity(new Intent(this, SigninActivity.class));
                return;
            }
            result = result.split("/")[1];
            Intent intent = new Intent(this, WallActivity.class);
            intent.putExtra(WallActivity.ROOM_ID, result);
            onBackPressed();
            startActivity(intent);
        } else if (result.contains("http")) {
            Intent intent = new Intent(this, BrowserActivity.class);
            intent.putExtra("showActionIcon", true);
            intent.putExtra(BrowserActivity.URL, result);
            startActivity(intent);
            onBackPressed();
        } else {
            ToastHelper.showShortToast("无法识别二维码");
            resumeCameraPreview();
        }
    }
}
