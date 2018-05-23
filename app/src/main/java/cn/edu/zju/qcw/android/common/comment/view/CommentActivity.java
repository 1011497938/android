package cn.edu.zju.qcw.android.common.comment.view;

import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import butterknife.BindView;
import cn.edu.zju.qcw.android.R;
import cn.edu.zju.qcw.android.base.mvp_activity.BaseMvpRecyclerViewActivtiy;
import cn.edu.zju.qcw.android.base.widget.TitleBar;
import cn.edu.zju.qcw.android.common.comment.adapter.CommentAdapter;
import cn.edu.zju.qcw.android.common.comment.presenter.CommentPresenter;
import cn.edu.zju.qcw.android.util.String.StringUtil;
import cn.edu.zju.qcw.android.util.toast.ToastHelper;

/**
 * Created by SQ on 2017/5/17.
 */

public class CommentActivity extends BaseMvpRecyclerViewActivtiy<CommentAdapter, CommentPresenter> {

    @BindView(R.id.comment_titlebar)
    TitleBar titleBar;
    @BindView(R.id.commentText)
    TextInputEditText commentText;

    @Override
    protected CommentAdapter initAdapter() {
        return new CommentAdapter();
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_comment;
    }

    @Override
    protected CommentPresenter initPresenter() {
        return new CommentPresenter(this);
    }

    @Override
    protected void init() {
        getPresenter().getData(getIntent().getStringExtra("url"));
        titleBar.hideStatusOffset();
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
                        ToastHelper.showShortToast(StringUtil.genUnhappyFace() + " 评论不能为空哦");
                        return false;
                    }
                    getPresenter().saveComment(getIntent().getStringExtra("url"), commentText.getText().toString());
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

    @Override
    public String setEmptyText() {
        return "沙发还没有被抢走";
    }

    public void refreshTitle() {
        titleBar.setTitle("评论(" + String.valueOf(getAdapter().getData().size()) + ")");
    }
}
