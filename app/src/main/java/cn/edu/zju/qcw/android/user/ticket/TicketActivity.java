package cn.edu.zju.qcw.android.user.ticket;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import cn.edu.zju.qcw.android.base.mvp_activity.BaseMvpActivty;
import cn.edu.zju.qcw.android.base.mvp_activity.BaseMvpPresenter;
import cn.edu.zju.qcw.android.base.widget.TitleBar;
import cn.edu.zju.qcw.android.util.dialog.DialogUtil;
import com.avos.avoscloud.*;
import cn.edu.zju.qcw.android.R;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

import java.util.List;

/**
 * Example of using Folding Cell with ListView and ListAdapter
 */
public class TicketActivity extends BaseMvpActivty {

    TitleBar titleBar;

    @Override
    protected int initLayout() {
        return R.layout.activity_ticket;
    }

    @Override
    protected BaseMvpPresenter initPresenter() {
        return null;
    }

    @Override
    protected void init() {
        final ListView theListView = (ListView) findViewById(R.id.mainListView);
        titleBar = (TitleBar) findViewById(R.id.user_ticket_titlebar);

        DialogUtil.getInstance().showLoading(this);

        AVQuery<AVObject> query = AVQuery.getQuery("AllTickets");
        query.whereEqualTo("owner", AVUser.getCurrentUser());
        query.addAscendingOrder("valid");
        query.addDescendingOrder("createdAt");
        query.include("ticket");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                DialogUtil.getInstance().closeLoading();
                if (e == null) {
                    final FoldingCellListAdapter adapter = new FoldingCellListAdapter(TicketActivity.this, list);
                    theListView.setAdapter(adapter);

                    theListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                            // toggle clicked cell state
                            ((FoldingCell) view).toggle(false);
                            // register in adapter that state for selected cell is toggled
                            adapter.registerToggle(pos);
                        }
                    });
                } else {
                    Toast.makeText(TicketActivity.this, "哎呀出错了，检查下网络吧~", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    @Override
    protected void initListeners() {

    }

    @Override
    protected boolean isThemeStyle() {
        return true;
    }
}
