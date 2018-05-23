package cn.edu.zju.qcw.android.splash;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;
import cn.edu.zju.qcw.android.MainActivity;
import cn.edu.zju.qcw.android.R;
import cn.edu.zju.qcw.android.base.BaseObserver;
import cn.edu.zju.qcw.android.browser.BrowserActivity;
import cn.edu.zju.qcw.android.common.bean.AdBean;
import cn.edu.zju.qcw.android.util.Logger;
import cn.edu.zju.qcw.android.util.network.DownLoadManager;
import cn.edu.zju.qcw.android.util.network.NetworkHelper;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.sdk.android.push.AndroidPopupActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static cn.edu.zju.qcw.android.MainActivity.EXTRA_URL;
import static com.avos.avoscloud.AVOSCloud.applicationContext;

public class SplashActivity extends AndroidPopupActivity {

    public static final String AD_PREFERENCE = "ad";

    public static final String AD_SOURCE_URL = "ad_source_url";

    public static final String AD_FILE_PATH = "ad_file_path";

    public static final int DEFAULT_DELAY = 1500;

    public String extraUrl;

    ImageView imageView;

    VideoView videoView;

    TextView skipView;

    Handler handler = new Handler();

    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        ViewGroup mContentView = (ViewGroup) this.findViewById(Window.ID_ANDROID_CONTENT);
        ViewGroup rootView = (ViewGroup) mContentView.getChildAt(0);
        ViewCompat.setFitsSystemWindows(rootView, false);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        handleNotification();

        EventBus.getDefault().register(this);

        imageView = (ImageView) findViewById(R.id.imageView);
        videoView = (VideoView) findViewById(R.id.videoView);
        skipView = (TextView) findViewById(R.id.skip);

        runnable = new Runnable() {
            @Override
            public void run() {
                startMainActivity();
                finish();
            }
        };

        handler.postDelayed(runnable, DEFAULT_DELAY);

        NetworkHelper.requestBuilder(adApi.class)
                .getAd()
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe(new BaseObserver<List<AdBean>>() {
                    @Override
                    public void onNext(List<AdBean> value) {
                        if (!value.isEmpty() && !TextUtils.isEmpty(value.get(0).getUrl())) {
                            AdBean adBean = value.get(0);
                            String url = adBean.getUrl();
                            SharedPreferences preferences = getSharedPreferences(AD_PREFERENCE, MODE_PRIVATE);

                            if (url.contains("mp4") || url.contains("avi") || url.contains("MP4") || url.contains("AVI") || url.contains("mov") || url.contains("MOV")) {
                                if (!isWifi(SplashActivity.this)) return;
                                if (url.equals(preferences.getString(AD_SOURCE_URL, null))
                                        && !TextUtils.isEmpty(preferences.getString(AD_FILE_PATH, null))) {
                                    //说明有缓存
                                    //重新设置延时
//                                    resetDelay(adBean);
                                    EventBus.getDefault().post(adBean);
                                    //播放
                                    return;
                                }
                                downloadFile(url);
                                preferences.edit().putString(AD_SOURCE_URL, url).apply();

                            } else if (url.contains("jpg") || url.contains("JPG") || url.contains("png") || url.contains("PNG")) {
                                //重新设置延时
//                                resetDelay(adBean);
                                EventBus.getDefault().post(adBean);
//                                Glide.with(SplashActivity.this).load(url).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imageView);
                            } else if (url.contains("gif") || url.contains("GIF")) {
                                if (!url.equals(preferences.getString(AD_SOURCE_URL, null))) {
                                    //WIFI下载
                                    if (!isWifi(SplashActivity.this)) return;
                                    DisplayMetrics dm = new DisplayMetrics();
                                    try {
                                        Glide.with(applicationContext)
                                                .load(url).downloadOnly(dm.widthPixels, dm.heightPixels).get();
                                        preferences.edit().putString(AD_SOURCE_URL, url).apply();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    } catch (ExecutionException e) {
                                        e.printStackTrace();
                                    }
                                    //下载完才会保存source_url
                                } else {
                                    //重新设置延时
//                                    resetDelay(adBean);
                                    EventBus.getDefault().post(adBean);
//                                    Glide.with(SplashActivity.this).load(url).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imageView);
                                }
                            }
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void downloadFile(String url) {

        NetworkHelper.requestBuilder(adApi.class)
                .downloadVideo(url)
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        String path = DownLoadManager.writeResponseBodyToDisk(SplashActivity.this, responseBody);
                        if (!TextUtils.isEmpty(path)) {
                            SharedPreferences preferences = getSharedPreferences(AD_PREFERENCE, MODE_PRIVATE);
                            preferences.edit().putString(AD_FILE_PATH, path).apply();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void resetDelay(final AdBean adBean) {
        //调用这个方法时说明有广告显示了
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, Integer.valueOf(adBean.getDuration()).intValue() * 1000);

        String url = adBean.getUrl();

        if (url.contains("mp4") || url.contains("avi") || url.contains("MP4") || url.contains("AVI") || url.contains("mov") || url.contains("MOV")) {
            SharedPreferences preferences = getSharedPreferences(AD_PREFERENCE, MODE_PRIVATE);
            if (adBean.getUrl().equals(preferences.getString(AD_SOURCE_URL, null))
                    && !TextUtils.isEmpty(preferences.getString(AD_FILE_PATH, null))) {
                Uri uri = Uri.parse(preferences.getString(AD_FILE_PATH, null));
                videoView.setVisibility(View.VISIBLE);
                videoView.setVideoURI(uri);
                videoView.start();
                videoView.requestFocus();
                videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                        return true;
                    }
                });
            }
        } else if (url.contains("jpg") || url.contains("JPG") || url.contains("png") || url.contains("PNG")) {
            Glide.with(SplashActivity.this).load(url).crossFade().centerCrop().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imageView);
        } else if (url.contains("gif") || url.contains("GIF")) {
            Glide.with(SplashActivity.this).load(url).asGif().crossFade().centerCrop().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imageView);
        }

        skipView.setVisibility(View.VISIBLE);
        final android.os.Handler aHandler = new android.os.Handler();
        final Runnable aRunnable = new Runnable() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(skipView.getText())) {
                    skipView.setText(String.valueOf(Integer.valueOf(adBean.getDuration())) + " 跳过");
                } else if ("0".equals(skipView.getText().toString())) {
                    aHandler.removeCallbacks(this);
                    return;
                } else {
                    skipView.setText(String.valueOf(Integer.valueOf(skipView.getText().toString().split(" ")[0]) - 1) + " 跳过");
                }
                aHandler.postDelayed(this, 1000);
            }
        };
        aHandler.post(aRunnable);

        skipView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aHandler.removeCallbacks(aRunnable);
                handler.removeCallbacks(runnable);
                startMainActivity();
                finish();
            }
        });

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(adBean.getOpenUrl())) {
                    aHandler.removeCallbacks(aRunnable);
                    handler.removeCallbacks(runnable);

                    startMainActivity();

                    Intent adIntent = new Intent(SplashActivity.this, BrowserActivity.class);
                    adIntent.putExtra(BrowserActivity.URL, adBean.getOpenUrl());
                    startActivity(adIntent);

                    finish();
                }
            }
        };

        videoView.setOnClickListener(listener);
        imageView.setOnClickListener(listener);
    }

    interface adApi {
        @GET("adpage")
        Observable<List<AdBean>> getAd();

        @Streaming
        @GET
        Observable<ResponseBody> downloadVideo(@Url String fileUrl);
    }

    private static boolean isWifi(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        if (!TextUtils.isEmpty(extraUrl)) {
            intent.putExtra(EXTRA_URL, extraUrl);
        }
        startActivity(intent);
    }

    @Override
    protected void onSysNoticeOpened(String s, String s1, Map<String, String> map) {
        Logger.d("辅助弹窗 onSysNoticeOpened：", map.toString());
        if (null != map && map.containsKey("url")) {
            extraUrl = map.get("url");
        }
    }

    private void handleNotification() {
        if (getIntent().hasExtra("extraMap")) {
            JSONObject extraMap = JSONObject.parseObject(getIntent().getStringExtra("extraMap"));
            if (extraMap.containsKey("url")) {
                Logger.d("receive extra url:", extraMap.getString("url"));
                extraUrl = extraMap.getString("url");
            }
        }
    }


}
