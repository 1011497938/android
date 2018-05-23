package cn.edu.zju.qcw.android.message;

import android.net.Uri;
import android.support.v7.widget.Toolbar;
import butterknife.BindView;
import cn.edu.zju.qcw.android.R;
import cn.edu.zju.qcw.android.base.BaseFragment;
import cn.edu.zju.qcw.android.base.widget.TitleBar;
import cn.edu.zju.qcw.android.common.IM.ImHelper;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.model.Conversation;

public class MessageFragment extends BaseFragment {


    @BindView(R.id.message_titlebar)
    TitleBar titleBar;

    ConversationListFragment mConversationList;

    private volatile static MessageFragment instance;

    public MessageFragment() {

    }

    public static MessageFragment getInstance() {
        if (instance == null) {
            synchronized (MessageFragment.class) {
                if (instance == null) {
                    instance = new MessageFragment();
                }
            }
        }
        return instance;
    }

    @Override
    protected int setFragmentLayoutID() {
        return R.layout.fragment_message;
    }

    @Override
    protected void initView() {
        if (mConversationList == null){
            mConversationList = new ConversationListFragment();
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.conversation_list_container, mConversationList)
                    .commit();
        }
        Uri uri = Uri.parse("rong://" + getContext().getApplicationInfo().packageName).buildUpon()
                .appendPath("conversationlist")
                .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话，该会话聚合显示
                .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "false")//设置群组会话，该会话非聚合显示
                .build();
        mConversationList.setUri(uri);
    }

    @Override
    protected void initListeners() {

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }
}