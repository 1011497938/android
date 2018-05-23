package cn.edu.zju.qcw.android.activity.wall.view;

import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import butterknife.BindView;
import cn.edu.zju.qcw.android.R;
import cn.edu.zju.qcw.android.activity.wall.adapter.WallAdapter;
import cn.edu.zju.qcw.android.activity.wall.presenter.WallPresenter;
import cn.edu.zju.qcw.android.base.mvp_activity.BaseMvpRecyclerViewActivtiy;
import cn.edu.zju.qcw.android.base.widget.TitleBar;
import cn.edu.zju.qcw.android.util.String.StringUtil;
import cn.edu.zju.qcw.android.util.toast.ToastHelper;

/**
 * Created by SQ on 2017/5/17.
 */

public class WallActivity extends BaseMvpRecyclerViewActivtiy<WallAdapter, WallPresenter> {

    public static final String ROOM_ID = "roomId";

    @BindView(R.id.activity_wall_titlebar)
    TitleBar titleBar;
    @BindView(R.id.commentText)
    public TextInputEditText commentText;

    @Override
    protected WallAdapter initAdapter() {
        return new WallAdapter();
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_wall;
    }

    @Override
    protected WallPresenter initPresenter() {
        return new WallPresenter(this);
    }

    @Override
    protected void init() {
        titleBar.hideStatusOffset();
        getPresenter().initIm();
    }

    @Override
    protected boolean isImmersion() {
        return false;
    }

    @Override
    protected boolean isThemeStyle() {
        return false;
    }

    @Override
    protected void initListeners() {
        commentText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (TextUtils.isEmpty(commentText.getText())) {
                        ToastHelper.showShortToast(StringUtil.genUnhappyFace() + " 内容不能为空哦");
                        return false;
                    }
                    getPresenter().sendMessage(commentText.getText().toString());
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (inputMethodManager.isActive()) {
                        inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                    }
                    return true;
                }
                return false;
            }
        });
    }

}
