package cn.edu.zju.qcw.android.activity.liveRoom.ui;

import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.edu.zju.qcw.android.R;
import cn.edu.zju.qcw.android.activity.liveRoom.LiveKit;
import cn.edu.zju.qcw.android.activity.liveRoom.controller.ChatListAdapter;
import cn.edu.zju.qcw.android.activity.liveRoom.ui.animation.HeartLayout;
import cn.edu.zju.qcw.android.activity.liveRoom.ui.fragment.BottomPanelFragment;
import cn.edu.zju.qcw.android.activity.liveRoom.ui.widget.ChatListView;
import cn.edu.zju.qcw.android.activity.liveRoom.ui.widget.InputPanel;
import cn.edu.zju.qcw.android.base.BaseObserver;
import cn.edu.zju.qcw.android.base.mvp_activity.BaseMvpActivty;
import cn.edu.zju.qcw.android.base.mvp_activity.BaseMvpPresenter;
import cn.edu.zju.qcw.android.util.image.ImageHelper;
import cn.edu.zju.qcw.android.util.network.NetworkHelper;
import com.avos.avoscloud.*;
import com.ksyun.media.player.IMediaPlayer;
import com.ksyun.media.player.KSYMediaPlayer;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.MessageContent;
import io.rong.message.InformationNotificationMessage;
import io.rong.message.TextMessage;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class LiveShowActivity extends BaseMvpActivty implements View.OnClickListener, Handler.Callback {

    public static final String LIVE_URL = "live_url";
    public static final String ROOM_ID = "room_id";

    private ViewGroup background;
    private ChatListView chatListView;
    private BottomPanelFragment bottomPanel;
    //    private ImageView btnGift;
//    private ImageView btnHeart;
    private HeartLayout heartLayout;
    private SurfaceView surfaceView;

    private ImageView go_back;
    private ImageView owner_head;
    private TextView ownername;
    private TextView count;

    private Handler countHandler;
    private Runnable countRunnable;

    private Random random = new Random();
    private Handler handler = new Handler(this);
    private ChatListAdapter chatListAdapter;
    private String roomId;
    private KSYMediaPlayer ksyMediaPlayer;
    private SurfaceHolder surfaceHolder;

    private IMediaPlayer.OnPreparedListener onPreparedListener = new IMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(IMediaPlayer mp) {
            ksyMediaPlayer.setVideoScalingMode(KSYMediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
            ksyMediaPlayer.start();
        }
    };

    private final SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            if (ksyMediaPlayer != null && ksyMediaPlayer.isPlaying())
                ksyMediaPlayer.setVideoScalingMode(KSYMediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if (ksyMediaPlayer != null)
                ksyMediaPlayer.setDisplay(holder);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (ksyMediaPlayer != null) {
                ksyMediaPlayer.setDisplay(null);
            }
        }
    };

    @Override
    protected boolean isImmersion() {
        return false;
    }

    private void initView() {
        background = (ViewGroup) findViewById(R.id.background);
        chatListView = (ChatListView) findViewById(R.id.chat_listview);
        bottomPanel = (BottomPanelFragment) getSupportFragmentManager().findFragmentById(R.id.bottom_bar);
//        btnGift = (ImageView) bottomPanel.getView().findViewById(R.id.btn_gift);
//        btnHeart = (ImageView) bottomPanel.getView().findViewById(R.id.btn_heart);
        heartLayout = (HeartLayout) findViewById(R.id.heart_layout);
        surfaceView = (SurfaceView) findViewById(R.id.player_surface);
        go_back = (ImageView) findViewById(R.id.go_back);
        owner_head = (ImageView) findViewById(R.id.ownerHead);
        ownername = (TextView) findViewById(R.id.ownerName);
        count = (TextView) findViewById(R.id.countText);


        chatListAdapter = new ChatListAdapter();
        chatListView.setAdapter(chatListAdapter);
        background.setOnClickListener(this);
//        btnGift.setOnClickListener(this);
//        btnHeart.setOnClickListener(this);
        bottomPanel.setInputPanelListener(new InputPanel.InputPanelListener() {
            @Override
            public void onSendClick(String text) {
                final TextMessage content = TextMessage.obtain(text);
                LiveKit.sendMessage(content);
            }
        });

        go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LiveShowActivity.this.onBackPressed();
            }
        });

        ksyMediaPlayer = new KSYMediaPlayer.Builder(this).build();
        ksyMediaPlayer.setOnPreparedListener(onPreparedListener);
        ksyMediaPlayer.setScreenOnWhilePlaying(true);
        ksyMediaPlayer.setBufferTimeMax(5);
        ksyMediaPlayer.setTimeout(20, 100);
    }

    private void startLiveShow() {
        roomId = getIntent().getStringExtra(LiveShowActivity.ROOM_ID);
        String liveUrl = getIntent().getStringExtra(LiveShowActivity.LIVE_URL);
        joinChatRoom(roomId);
        playShow(liveUrl);
    }

    private void joinChatRoom(final String roomId) {
        LiveKit.joinChatRoom(roomId, 2, new RongIMClient.OperationCallback() {
            @Override
            public void onSuccess() {
                final InformationNotificationMessage content = InformationNotificationMessage.obtain("进入了直播间");
                LiveKit.sendMessage(content);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                Toast.makeText(LiveShowActivity.this, "加入直播间失败", Toast.LENGTH_SHORT).show();
                LiveShowActivity.this.onBackPressed();
            }
        });
    }

    private void playShow(String liveUrl) {
        try {
            ksyMediaPlayer.setDataSource(liveUrl);
            ksyMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }

        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(surfaceCallback);
    }

    @Override
    public void onBackPressed() {
        if (!bottomPanel.onBackAction()) {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.equals(background)) {
            bottomPanel.onBackAction();
        }
//        } else if (v.equals(btnGift)) {
//            GiftMessage msg = new GiftMessage("2", "送您一个礼物");
//            LiveKit.sendMessage(msg);
//        } else if (v.equals(btnHeart)) {
//            heartLayout.post(new Runnable() {
//                @Override
//                public void run() {
//                    int rgb = Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));
//                    heartLayout.addHeart(rgb);
//                }
//            });
//            GiftMessage msg = new GiftMessage("1", "为您点赞");
//            LiveKit.sendMessage(msg);
//        }
    }

    @Override
    public boolean handleMessage(android.os.Message msg) {
        switch (msg.what) {
            case LiveKit.MESSAGE_ARRIVED: {
                MessageContent content = (MessageContent) msg.obj;
                chatListAdapter.addMessage(content);
                break;
            }
            case LiveKit.MESSAGE_SENT: {
                MessageContent content = (MessageContent) msg.obj;
                chatListAdapter.addMessage(content);
                break;
            }
            case LiveKit.MESSAGE_SEND_ERROR: {
                break;
            }
            default:
        }
        chatListAdapter.notifyDataSetChanged();
        return false;
    }

    @Override
    protected void onDestroy() {
        LiveKit.quitChatRoom(new RongIMClient.OperationCallback() {
            @Override
            public void onSuccess() {
                LiveKit.removeEventHandler(handler);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                LiveKit.removeEventHandler(handler);
            }
        });
        ksyMediaPlayer.stop();

        countHandler.removeCallbacks(countRunnable);
        countHandler = null;
        countRunnable = null;
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        ksyMediaPlayer.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        ksyMediaPlayer.start();
        super.onResume();
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_liveshow;
    }

    @Override
    protected BaseMvpPresenter initPresenter() {
        return null;
    }

    @Override
    protected void init() {
        LiveKit.addEventHandler(handler);
        initView();
        startLiveShow();
        initData();
    }

    @Override
    protected void initListeners() {

    }

    @Override
    protected boolean isThemeStyle() {
        return true;
    }

    private void initData() {
        if (TextUtils.isEmpty(roomId)) return;

        AVQuery<AVObject> query = AVQuery.getQuery("LiveRoom");
        query.include("user");
        query.include("user.head");
        query.include("user.head.url");
        query.getInBackground(roomId, new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                if (e == null) {
                    if (avObject.getAVObject("user") != null
                            && avObject.getAVObject("user").get("head") != null
                            && avObject.getAVObject("user").get("head") instanceof AVFile) {
                        ImageHelper.loadCircleImage(LiveShowActivity.this, avObject.getAVUser("user").getAVFile("head").getUrl(), owner_head);
                    } else {
                        ImageHelper.loadCircleImage(LiveShowActivity.this, null, owner_head);
                    }
                    ownername.setText(avObject.getAVUser("user").getUsername());
                }
            }
        });

        countHandler = new Handler();
        countRunnable = new Runnable() {
            @Override
            public void run() {
                NetworkHelper.requestBuilder(roomCountApi.class)
                        .getCount(roomId)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new BaseObserver<List<String>>() {
                            @Override
                            public void onNext(List<String> strings) {
                                if (strings.size() > 0) {
                                    count.setText(strings.get(0) + "人");
                                }
                            }
                        });
                handler.postDelayed(this, 10000);
            }
        };
        countHandler.post(countRunnable);
    }


    interface roomCountApi {
        @GET("liveRoom/{roomId}")
        Observable<List<String>> getCount(@Path("roomId") String roomId);
    }
}
