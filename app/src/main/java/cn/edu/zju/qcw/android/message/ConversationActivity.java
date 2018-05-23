package cn.edu.zju.qcw.android.message;

import cn.edu.zju.qcw.android.R;
import cn.edu.zju.qcw.android.base.mvp_activity.BaseMvpActivty;
import cn.edu.zju.qcw.android.base.mvp_activity.BaseMvpPresenter;
import cn.edu.zju.qcw.android.base.widget.TitleBar;

/**
 * Created by SQ on 2017/5/21.
 */

public class ConversationActivity extends BaseMvpActivty {

    @Override
    protected boolean isThemeStyle() {
        return true;
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_conversation;
    }

    @Override
    protected BaseMvpPresenter initPresenter() {
        return null;
    }

    @Override
    protected void init() {
        TitleBar titleBar = (TitleBar) findViewById(R.id.conversation_titlebar);
        titleBar.setTitle(getIntent().getData().getQueryParameter("title"));
    }

    @Override
    protected void initListeners() {

    }
}
