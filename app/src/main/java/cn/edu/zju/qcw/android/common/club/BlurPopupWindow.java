package cn.edu.zju.qcw.android.common.club;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.*;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import cn.edu.zju.qcw.android.R;
import cn.edu.zju.qcw.android.base.BaseObserver;
import cn.edu.zju.qcw.android.common.IM.ImHelper;
import cn.edu.zju.qcw.android.safari.club.bean.ClubBean;
import cn.edu.zju.qcw.android.user.signin.view.SigninActivity;
import cn.edu.zju.qcw.android.util.Animation.AnimUtil;
import cn.edu.zju.qcw.android.util.image.BlurKit;
import cn.edu.zju.qcw.android.util.image.ImageHelper;
import cn.edu.zju.qcw.android.util.network.NetworkHelper;
import cn.edu.zju.qcw.android.util.toast.ToastHelper;
import com.avos.avoscloud.AVUser;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SQ on 2017/5/23.
 */

public class BlurPopupWindow {
    Button cancelBtn;
    Switch pushSwitch;
    TextView pushText;
    ImageView headImg;
    TextView clubName;
    Button followBtn;
    TextView describe;
    TextView follow;
    Button profile_text;
    FrameLayout container;
    ProgressBar progressBar;

    TextView wx;
    TextView phone;
    TextView chat;


    private PopupWindow popupWindow;

    private Context context;

    private ClubBean clubBean;

    private String clubId;

    private TextView selectedBtn;

    private List<TextView> btn_array;



    PopupWindow mPopupWindow;
    View mRootView;
    FrameLayout mContentView;
    ImageView mBlurImageView;
    Activity mActivity;
    private int mDelayAnim=150;

    public BlurPopupWindow(Context context) {
        mActivity = (Activity) context;
        this.context = context;
        if (mActivity.getParent() != null) {
            ViewGroup contentView= (ViewGroup)  mActivity.getParent().getWindow().findViewById(Window.ID_ANDROID_CONTENT);
            mRootView = (ViewGroup) contentView.getChildAt(0);
            mActivity = mActivity.getParent();
        }
        if(mRootView==null){
            ViewGroup contentView= (ViewGroup) mActivity.getWindow().findViewById(Window.ID_ANDROID_CONTENT);
            mRootView = (ViewGroup) contentView.getChildAt(0);
        }
        generateContentView();
        BlurKit.init(mActivity.getApplicationContext());

    }
    public boolean isShowing(){
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            return true ;
        }
        return false;
    }
    boolean isAnimRuning ;
    public boolean onBackpressed(){
        if (mPopupWindow == null || !mPopupWindow.isShowing()) {
            return true ;
        }
        if(isAnimRuning){
            return true;
        }
        ValueAnimator valueAnimator = ValueAnimator.ofInt(255,0).setDuration(400);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {


            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (int) valueAnimator.getAnimatedValue();
                mBlurImageView.setImageAlpha(value);
            }
        });
        valueAnimator.setStartDelay(mDelayAnim);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setRepeatCount(0);
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mPopupWindow != null && mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                }
                isAnimRuning = false;

            }

            @Override
            public void onAnimationCancel(Animator animation) {
                isAnimRuning = false;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator.start();
        isAnimRuning = true;
        return false;
    }
    public void onShowAnimStart(){
    }
    public void onShowAnimEnd(){

    }
    public final void dismiss(){
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
        isAnimRuning = false;
    }
    private void generateContentView() {
        mContentView = new FrameLayout(mActivity);
        mPopupWindow = new PopupWindow(mContentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mBlurImageView = new ImageView(mActivity);
        mBlurImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mContentView.addView(mBlurImageView, params);
        initViews();
        mPopupWindow.setContentView(mContentView);
        mPopupWindow.setAnimationStyle(0);

    }

    public void showWithId(int clubId) {

        View view = mActivity.getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        if(!isViewCreated){
            isViewCreated =true;
            onCreatView(mContentView);
        }
        Bitmap bitmap = BlurKit.getInstance().fastBlur(mRootView,10,0.12f);
//        Bitmap bitmap = BlurKit.getInstance().fastBlur(mRootView,5,0.5f);
        mBlurImageView.setImageBitmap(bitmap);
        mBlurImageView.setImageAlpha(0);
        mBlurImageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {

                ValueAnimator valueAnimator = ValueAnimator.ofInt(0,255).setDuration(400);
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {


                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        int value = (int) valueAnimator.getAnimatedValue();
                        mBlurImageView.setImageAlpha(value);
                    }
                });
                valueAnimator.setInterpolator(new DecelerateInterpolator());
                valueAnimator.setRepeatCount(0);
                valueAnimator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        onShowAnimStart();
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        isAnimRuning= false;
                        onShowAnimEnd();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        isAnimRuning= false;
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                valueAnimator.start();
                mBlurImageView.getViewTreeObserver().removeOnPreDrawListener(this);
                isAnimRuning =true;
                return true;
            }
        });

        mBlurImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.dismiss();
            }
        });

        mPopupWindow.showAtLocation(mRootView, Gravity.CENTER , 0, 0);

        container.setVisibility(View.INVISIBLE);

        this.clubId = String.valueOf(clubId);

        initListeners();

        getDataWithClubId(clubId);
    }
    boolean isViewCreated;
    public void onCreatView(FrameLayout container) {


    }
    @SuppressWarnings("unchecked")
    final public <E extends View> E findView(int id) {
        try {
            return (E)mContentView.findViewById(id);
        } catch (ClassCastException e) {

            throw e;
        }
    }

    /* Click listener that avoid double click event in short time*/
    public NoDoubleClickListener mNoDoubleClickListener = new NoDoubleClickListener(){

        @Override
        public void onClickInternal(View v) {
            BlurPopupWindow.this.onClick(v);
        }
    };

    public interface NoDoubleClickListener{
        public void onClickInternal(View v);

    }
    public void onClick(View v){

    }


    private void initViews() {
        View contentView = LayoutInflater.from(mActivity).inflate(R.layout.popup_club, null);
        cancelBtn = (Button) contentView.findViewById(R.id.cancelBtn);
        pushSwitch = (Switch) contentView.findViewById(R.id.pushSwitch);
        pushText = (TextView) contentView.findViewById(R.id.pushText);
        headImg = (ImageView) contentView.findViewById(R.id.headImg);
        clubName = (TextView) contentView.findViewById(R.id.clubName);
        describe = (TextView) contentView.findViewById(R.id.describe);
        container = (FrameLayout) contentView.findViewById(R.id.container);
        progressBar = (ProgressBar) contentView.findViewById(R.id.progressBar);
        follow = (TextView) contentView.findViewById(R.id.follow);
        followBtn = (Button) contentView.findViewById(R.id.followBtn);
        profile_text = (Button) contentView.findViewById(R.id.profile_text);
        wx = (TextView) contentView.findViewById(R.id.wx_btn);
        phone = (TextView) contentView.findViewById(R.id.phone_btn);
        chat = (TextView) contentView.findViewById(R.id.chat_btn);

        btn_array = new ArrayList<>();
        btn_array.add(wx);
        btn_array.add(phone);
        btn_array.add(chat);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectProfileBtn(view);
            }
        };
        wx.setOnClickListener(listener);
        phone.setOnClickListener(listener);
        chat.setOnClickListener(listener);

        mContentView.addView(contentView);
    }

    private void setViewWithData(ClubBean clubBean) {
        this.clubBean = clubBean;

        ImageHelper.loadCircleImage(context, clubBean.getLogo(), headImg);
        describe.setText(clubBean.getClubAbstract());
        clubName.setText(clubBean.getFullname());
        pushSwitch.setChecked(clubBean.isOpenPush());
        pushText.setText(pushSwitch.isChecked() ? "推送打开" : "推送关闭");

        follow.setText(String.valueOf(clubBean.getFollow()) + " 人关注");

        refreshBtn();

        progressBar.setVisibility(View.GONE);
        container.setVisibility(View.VISIBLE);

    }

    private void getDataWithClubId(int clubId) {
        NetworkHelper.requestBuilder(clubProfileApi.class)
                .getClubProfile(String.valueOf(clubId))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<ClubBean>() {
                    @Override
                    public void onNext(ClubBean value) {
                        setViewWithData(value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mPopupWindow.dismiss();
                        ToastHelper.showShortToast("啊哦出错了，检查下网络吧~");
                    }
                });
    }

    private void selectProfileBtn(View button) {
        selectedBtn = (TextView) button;
        if (selectedBtn == profile_text) {//显示三个按钮
            for (TextView btn : btn_array) {
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectProfileBtn(view);
                    }
                });
                AnimUtil.alphaAnimation(btn, 0, 1, 300, null);
            }
            profile_text.setOnClickListener(null);
            AnimUtil.alphaAnimation(profile_text, 1, 0, 300, null);
        } else {//隐藏三个按钮
            if (button == wx) {
                profile_text.setText(TextUtils.isEmpty(clubBean.getWx()) ? "暂未提供" : clubBean.getWx());
            } else if (button == phone) {
                profile_text.setText(TextUtils.isEmpty(clubBean.getContact()) ? "暂未提供" : clubBean.getContact());
            } else if (button == chat) {
                if (TextUtils.isEmpty(clubBean.getIm())) {
                    profile_text.setText("暂时无法通过APP与社团联系");
                } else {
                    ImHelper.startPrivateConversation(context, clubBean.getIm(), clubBean.getShortname());
                    return;
                }
            }
            for (TextView btn : btn_array) {
                btn.setOnClickListener(null);
                AnimUtil.alphaAnimation(btn, 1, 0, 300, null);
            }
            profile_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectProfileBtn(view);
                }
            });
            AnimUtil.alphaAnimation(profile_text, 0, 1, 300, null);
        }
    }

    private void refreshBtn() {
        followBtn.setText(ClubHelper.getInstance().isFollow(clubId) ? "已关注" : "未关注");
        pushSwitch.setChecked(ClubHelper.getInstance().isPushOpened(clubId));
        pushSwitch.setEnabled(ClubHelper.getInstance().isFollow(clubId));
    }

    private void initListeners() {
        followBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AVUser.getCurrentUser() == null) {
                    context.startActivity(new Intent(context, SigninActivity.class));
                    return;
                }
                ClubHelper.getInstance().userFollowAction(clubId);
                refreshBtn();
            }
        });

        pushSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean b = pushSwitch.isChecked();
                if (AVUser.getCurrentUser() == null) {
                    pushSwitch.setChecked(!b);
                    context.startActivity(new Intent(context, SigninActivity.class));
                    return;
                }
                ClubHelper.getInstance().userPushAction(clubId, b);
            }
        });

        pushSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                pushText.setText(isChecked ? "推送打开" : "推送关闭");
            }
        });

        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ClubHelper.getInstance().syncData();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.dismiss();
            }
        });
    }

    private interface clubProfileApi {
        @GET("clubDetail/{clubId}")
        Observable<ClubBean> getClubProfile(@Path("clubId") String clubId);
    }
}