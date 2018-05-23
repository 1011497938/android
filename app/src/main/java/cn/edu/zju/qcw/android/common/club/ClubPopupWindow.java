package cn.edu.zju.qcw.android.common.club;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import cn.edu.zju.qcw.android.common.IM.ImHelper;
import cn.edu.zju.qcw.android.user.signin.view.SigninActivity;
import cn.edu.zju.qcw.android.util.image.BlurKit;
import cn.edu.zju.qcw.android.util.toast.ToastHelper;
import com.avos.avoscloud.AVUser;

import java.util.ArrayList;
import java.util.List;

import cn.edu.zju.qcw.android.R;
import cn.edu.zju.qcw.android.base.BaseObserver;
import cn.edu.zju.qcw.android.safari.club.bean.ClubBean;
import cn.edu.zju.qcw.android.util.Animation.AnimUtil;
import cn.edu.zju.qcw.android.util.image.ImageHelper;
import cn.edu.zju.qcw.android.util.network.NetworkHelper;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.http.GET;
import retrofit2.http.Path;


/**
 * Created by SQ on 2017/5/6.
 */

public class ClubPopupWindow {

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

    public ClubPopupWindow(Context context) {

        this.context = context;

        initViews();
    }

    public void showWithId(Context context, int clubId) {

        View view = ((Activity) context).getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        popupWindow.showAtLocation(LayoutInflater.from(context).inflate(R.layout.activity_main, null), Gravity.CENTER, 0, 0);

        container.setVisibility(View.INVISIBLE);

        this.clubId = String.valueOf(clubId);

        initListeners();

        getDataWithClubId(clubId);

    }

    private void setViewWithData(ClubBean clubBean) {
        this.clubBean = clubBean;

        ImageHelper.loadCircleImage(context, clubBean.getLogo(), headImg);
        describe.setText(clubBean.getClubAbstract());
        clubName.setText(clubBean.getFullname());
        pushSwitch.setChecked(clubBean.isOpenPush());
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
                        popupWindow.dismiss();
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

        pushSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (AVUser.getCurrentUser() == null) {
                    pushSwitch.setChecked(!b);
                    context.startActivity(new Intent(context, SigninActivity.class));
                    return;
                }
                ClubHelper.getInstance().userPushAction(clubId, b);
                pushText.setText(b ? "推送打开" : "推送关闭");
            }
        });

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ClubHelper.getInstance().syncData();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
    }

    private void initViews() {
        View contentView = LayoutInflater.from(context).inflate(R.layout.popup_club, null);
        popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
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
    }

    private interface clubProfileApi {
        @GET("clubDetail/{clubId}")
        Observable<ClubBean> getClubProfile(@Path("clubId") String clubId);
    }

}
