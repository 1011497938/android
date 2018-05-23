package cn.edu.zju.qcw.android.activity;

import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import butterknife.BindView;
import cn.edu.zju.qcw.android.R;
import cn.edu.zju.qcw.android.activity.list.view.ActivityListFragment;
import cn.edu.zju.qcw.android.activity.livelist.view.LiveListActivity;
import cn.edu.zju.qcw.android.activity.scan.ScanActivity;
import cn.edu.zju.qcw.android.base.BaseFragment;
import cn.edu.zju.qcw.android.base.widget.TitleBar;

public class ActivityFragment extends BaseFragment {

    private static final String TAG = ActivityFragment.class.getSimpleName();

    private volatile static ActivityFragment instance;

    private ActivityListFragment mActivityList;

    @BindView(R.id.activity_titlebar)
    TitleBar titleBar;

    public ActivityFragment() {

    }

    public static ActivityFragment getInstance() {
        if (instance == null) {
            synchronized (ActivityFragment.class) {
                if (instance == null) {
                    instance = new ActivityFragment();
                }
            }
        }
        return instance;
    }

    @Override
    protected int setFragmentLayoutID() {
        return R.layout.fragment_activity;
    }

    @Override
    protected void initView() {

        mActivityList = new ActivityListFragment();

        getChildFragmentManager().beginTransaction()
                .add(R.id.container, mActivityList)
                .commit();
    }

    @Override
    protected void initListeners() {
        titleBar.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ScanActivity.class));
            }
        });
        titleBar.setLeftOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), LiveListActivity.class));
            }
        });
    }
}
