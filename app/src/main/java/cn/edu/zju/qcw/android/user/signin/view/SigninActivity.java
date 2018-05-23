package cn.edu.zju.qcw.android.user.signin.view;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.regex.Pattern;

import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import butterknife.OnFocusChange;
import cn.edu.zju.qcw.android.util.Animation.AnimUtil;
import cn.edu.zju.qcw.android.util.toast.ToastHelper;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.edu.zju.qcw.android.R;
import cn.edu.zju.qcw.android.user.signin.SigninInterface;
import cn.edu.zju.qcw.android.user.signin.presenter.SigninPresenter;
import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AnalyticsUtils;

public class SigninActivity extends AppCompatActivity implements SigninInterface.IView {

    private static final String TAG = SigninActivity.class.getSimpleName();

    private SigninPresenter mPresenter = new SigninPresenter(this);

    @BindView(R.id.phoneNum)
    EditText phoneNum;

    @BindView(R.id.verifyNum)
    EditText verifyNum;

    @BindView(R.id.verifyNumBtn)
    TextView verifyBtn;

    @BindView(R.id.verifyBg)
    View verifyBg;

    @BindView(R.id.phoneBg)
    View phoneBg;

    @BindView(R.id.imgScrollView)
    ScrollView scrollView;

    @BindView(R.id.signinBtn)
    RelativeLayout signin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signin);
        ButterKnife.bind(this);
        scrollView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                clear(v);
                return true;
            }
        });

        View.OnKeyListener listener = new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    String phone = phoneNum.getText().toString();
                    String verify = verifyNum.getText().toString();

                    if (phone.length() == 0) {
                        showError("请填写正确的手机号");
                        return false;
                    }
                    if (verify.length() == 0) {
                        showError("请填写正确的验证码");
                        return false;
                    }
//                    boolean match = Pattern.compile("[1][358]\\d{9}")
//                            .matcher(phone)
//                            .matches();
//                    if (!match) {
//                        showError("请填写正确的手机号");
//                        return false;
//                    }
                    clear(v);
                    mPresenter.signIn(phone, verify);


                    return true;
                }
                return false;
            }
        };
        verifyNum.setOnKeyListener(listener);
        phoneNum.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    verifyNum.requestFocus();
                    InputMethodManager imm = (InputMethodManager)SigninActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    return true;
                }
                return false;
            }
        });
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = phoneNum.getText().toString();
                String verify = verifyNum.getText().toString();

                if (phone.length() == 0) {
                    showError("请填写正确的手机号");
                    return;
                }
                if (verify.length() == 0) {
                    showError("请填写正确的验证码");
                    return;
                }
//                    boolean match = Pattern.compile("[1][358]\\d{9}")
//                            .matcher(phone)
//                            .matches();
//                    if (!match) {
//                        showError("请填写正确的手机号");
//                        return false;
//                    }
                clear(signin);
                mPresenter.signIn(phone, verify);
            }
        });
    }

    /**
     * Interface Implement
     */

    @Override
    public void showError(String errorMessage) {
        ToastHelper.showShortToast(errorMessage);
    }

    @Override
    public void signinSuccess() {
        ToastHelper.showShortToast("登录成功");
        //TODO:修改用户名提示
        onBackPressed();
    }

    @Override
    public void sendSuccess() {
        ToastHelper.showShortToast("验证码发送成功");
        if ("获取".equals(verifyBtn.getText().toString())) {
            final android.os.Handler handler = new android.os.Handler();
            final Runnable aRunnable = new Runnable() {
                @Override
                public void run() {
                    if ("获取".equals(verifyBtn.getText().toString())) {
                        verifyBtn.setText("60");
                    } else if ("0".equals(verifyBtn.getText().toString())) {
                        verifyBtn.setText("获取");
                        handler.removeCallbacks(this);
                        return;
                    } else {
                        verifyBtn.setText(String.valueOf(Integer.valueOf(verifyBtn.getText().toString()) - 1));
                    }
                    handler.postDelayed(this, 1000);
                }
            };
            handler.postDelayed(aRunnable, 1000);

        }
    }


    /**
     * View Actions
     */

    @OnClick(R.id.verifyNumBtn)
    public void getVerifyNum() {
        if (!("获取".equals(verifyBtn.getText().toString()))) return;
        String phone = phoneNum.getText().toString();
        if (phone.length() == 0) {
            showError("请填写正确的手机号");
            return;
        }
        mPresenter.getVerifyNum(phone);
    }

    @OnFocusChange({R.id.verifyNum, R.id.phoneNum})
    public void focusTextEdit(View view, boolean isFocus) {
        if (isFocus) {//激活
            if (phoneBg.getAlpha() * verifyBg.getAlpha() == 0) {
                AnimUtil.alphaAnimation(phoneBg, 0, 1, 500, null);
                AnimUtil.alphaAnimation(verifyBg, 0, 1, 500, null);
                phoneNum.setCompoundDrawablesWithIntrinsicBounds(R.drawable.sign_in_mobile_gray, 0, 0, 0);
                phoneNum.setHintTextColor(Color.parseColor("#707070"));
                phoneNum.setTextColor(Color.parseColor("#707070"));

                verifyNum.setCompoundDrawablesWithIntrinsicBounds(R.drawable.sign_in_verify_gray, 0, 0, 0);
                verifyNum.setHintTextColor(Color.parseColor("#707070"));
                verifyNum.setTextColor(Color.parseColor("#707070"));

                verifyBtn.setTextColor(Color.parseColor("#707070"));
            }
        } else if (!phoneNum.isFocused() && !verifyNum.isFocused()) {
            if (phoneBg.getAlpha() * verifyBg.getAlpha() != 0) {
                AnimUtil.alphaAnimation(phoneBg, 1, 0, 700, null);
                AnimUtil.alphaAnimation(verifyBg, 1, 0, 700, null);
                phoneNum.setCompoundDrawablesWithIntrinsicBounds(R.drawable.sign_in_mobile_white, 0, 0, 0);
                phoneNum.setHintTextColor(Color.WHITE);
                phoneNum.setTextColor(Color.WHITE);

                verifyNum.setCompoundDrawablesWithIntrinsicBounds(R.drawable.sign_in_verify_white, 0, 0, 0);
                verifyNum.setHintTextColor(Color.WHITE);
                verifyNum.setTextColor(Color.WHITE);

                verifyBtn.setTextColor(Color.WHITE);
            }
        }
    }

    @OnClick(R.id.imgScrollView)
    public void clear(View view) {

        phoneNum.clearFocus();
        verifyNum.clearFocus();

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isActive()) {
            inputMethodManager.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        AVAnalytics.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AVAnalytics.onPause(this);
    }
}
