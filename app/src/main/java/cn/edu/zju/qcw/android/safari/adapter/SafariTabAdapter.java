package cn.edu.zju.qcw.android.safari.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;
import cn.edu.zju.qcw.android.safari.article.view.ArticleFragment;
import cn.edu.zju.qcw.android.safari.selection.view.SelectionFragment;
import cn.edu.zju.qcw.android.util.Logger;


public class SafariTabAdapter extends FragmentPagerAdapter {

    private List<Fragment> mFragmentList;

    private FragmentManager fm;

    private SelectionFragment mSelectionFragment;

    private SelectionFragment mSubscribeFragment;

    private List<String> mTabTitle = new ArrayList<>();

    private List<String> nameList = new ArrayList<>();

    public SafariTabAdapter(FragmentManager fm) {
        super(fm);
        this.fm = fm;
        mTabTitle.add("精选");
        mTabTitle.add("关注");

        mSelectionFragment = new SelectionFragment();

        mSubscribeFragment = new SelectionFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(SelectionFragment.FOLLOW_LIST, true);
        mSubscribeFragment.setArguments(bundle);

        mFragmentList = new ArrayList<>();
        mFragmentList.add(mSelectionFragment);
        mFragmentList.add(mSubscribeFragment);
    }

    public void updateAdapter(List<String> nameList) {
        //判断是否需要更新
        if (nameList.size() == this.nameList.size()) {
            boolean flag = false;
            for (int index = 0; index < nameList.size(); index++) {
                if (!nameList.get(index).equals(this.nameList.get(index))) {
                    flag = true;
                    break;
                }
            }
            if (!flag) return;
        }
        this.nameList = nameList;
        //
        while (mTabTitle.size() > 2) {
            mTabTitle.remove(mTabTitle.size() - 1);
        }
        mTabTitle.addAll(nameList);
        while (mFragmentList.size() > 2) {
            Fragment fragment = mFragmentList.remove(mFragmentList.size() - 1);
            //由于FragmentPagerAdapter会判断FragmentManager中是否存在fragment的缓存，如果不存在才会重新调用getItem生成，所以要这样处理
            //http://blog.csdn.net/jiang547860818/article/details/54380055
            FragmentTransaction ft = fm.beginTransaction();
            ft.remove(fragment);
            ft.commit();
            ft = null;
            fm.executePendingTransactions();
        }
        for (String name : nameList) {
            ArticleFragment articleFragment = new ArticleFragment();
            Bundle bundle = new Bundle();
            bundle.putString("clubName", name);
            articleFragment.setArguments(bundle);
            mFragmentList.add(articleFragment);
        }
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabTitle.get(position);
    }

    @Override
    public int getItemPosition(Object object) {
        if (mFragmentList.indexOf(object) < 2) {
            return POSITION_UNCHANGED;
        }
        return POSITION_NONE;
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
