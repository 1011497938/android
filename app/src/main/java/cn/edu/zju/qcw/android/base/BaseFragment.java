package cn.edu.zju.qcw.android.base;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import cn.edu.zju.qcw.android.util.Logger;
import com.avos.avoscloud.AVAnalytics;
import org.greenrobot.eventbus.EventBus;

public abstract class BaseFragment extends Fragment {

    private final String TAG = this.getClass().getSimpleName();

    private static final String STATE_SAVE_IS_HIDDEN = "STATE_SAVE_IS_HIDDEN";

    public BaseFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (useEventBus()) {
            EventBus.getDefault().register(this);
        }
        if (savedInstanceState != null) {
            Logger.d("Fragment has savedInstanceState");
            boolean isSupportHidden = savedInstanceState.getBoolean(STATE_SAVE_IS_HIDDEN);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            if (isSupportHidden) {
                ft.hide(this);
            } else {
                ft.show(this);
            }
            ft.commit();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(STATE_SAVE_IS_HIDDEN, isHidden());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (useEventBus()) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(setFragmentLayoutID(), container, false);

        ButterKnife.bind(this, rootView);

        initView();

        initListeners();

        return rootView;
    }

//    protected abstract void initData();

    protected abstract void initView();

    protected abstract int setFragmentLayoutID();

    protected abstract void initListeners();

    protected boolean useEventBus() {
        return false;
    }

    @Override
    public void onPause() {
        super.onPause();
        AVAnalytics.onFragmentEnd(this.getClass().getSimpleName());

    }

    @Override
    public void onResume() {
        super.onResume();
        AVAnalytics.onFragmentStart(this.getClass().getSimpleName());
    }
}
