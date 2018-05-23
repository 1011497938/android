package cn.edu.zju.qcw.android.parttime;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.TextView;
import cn.edu.zju.qcw.android.base.widget.TitleBar;
import cn.edu.zju.qcw.android.util.Logger;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.edu.zju.qcw.android.R;
import cn.edu.zju.qcw.android.base.BaseFragment;
import cn.edu.zju.qcw.android.parttime.list.view.ParttimeListFragment;
import cn.edu.zju.qcw.android.parttime.form.ParttimeFormActivity;

public class ParttimeFragment extends BaseFragment {

    private static final String TAG = ParttimeFragment.class.getSimpleName();

    private volatile static ParttimeFragment instance;

    List<Fragment> mFragmentList;

    @BindView(R.id.parttime_tab_layout)
    SmartTabLayout mTabLayout;
    @BindView(R.id.parttime_viewPager)
    ViewPager mViewPager;
    @BindView(R.id.parttime_titlebar)
    TitleBar titleBar;

    public ParttimeFragment() {

    }

    public static ParttimeFragment getInstance() {
        if (instance == null) {
            synchronized (ParttimeFragment.class) {
                if (instance == null) {
                    instance = new ParttimeFragment();
                }
            }
        }
        return instance;
    }

    @Override
    protected int setFragmentLayoutID() {
        return R.layout.fragment_parttime;
    }

    @Override
    protected void initListeners() {
        titleBar.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ParttimeFormActivity.class));
            }
        });
    }

    @Override
    protected void initView() {
        mFragmentList = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            Bundle bundle = new Bundle();
            bundle.putString("kind", String.valueOf(i));
            ParttimeListFragment fragment = new ParttimeListFragment();
            fragment.setArguments(bundle);
            mFragmentList.add(fragment);
        }

        ParttimeAdapter adapter = new ParttimeAdapter(getActivity().getSupportFragmentManager(), mFragmentList);
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(3);
        mTabLayout.setViewPager(mViewPager);
    }

    private class ParttimeAdapter extends FragmentPagerAdapter {

        private List<Fragment> mList;

        private String mTabTitle[] = new String[]{"家教", "校内", "校外"};

        ParttimeAdapter(FragmentManager fm, List<Fragment> list) {
            super(fm);
            mList = list;
        }

        @Override
        public Fragment getItem(int position) {
            return mList.get(position);
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabTitle[position];
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            try{
                super.finishUpdate(container);
            } catch (NullPointerException nullPointerException){
                Logger.d("Catch the NullPointerException in FragmentPagerAdapter.finishUpdate");
            }
        }
    }
}
