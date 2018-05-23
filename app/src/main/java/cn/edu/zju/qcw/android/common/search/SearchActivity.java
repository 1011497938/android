package cn.edu.zju.qcw.android.common.search;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.SearchView;
import butterknife.BindView;
import cn.edu.zju.qcw.android.R;
import cn.edu.zju.qcw.android.base.SpacesItemDecoration;
import cn.edu.zju.qcw.android.base.mvp_activity.BaseMvpPresenter;
import cn.edu.zju.qcw.android.base.mvp_activity.BaseMvpRecyclerViewActivtiy;
import cn.edu.zju.qcw.android.util.String.StringUtil;
import cn.edu.zju.qcw.android.util.toast.ToastHelper;
import com.chad.library.adapter.base.BaseQuickAdapter;

/**
 * Created by SQ on 2017/5/25.
 */

public class SearchActivity extends BaseMvpRecyclerViewActivtiy<BaseQuickAdapter, SearchPresenter> {

    public static final String SEARCH_ARTICLE = "search_article";

    public static final String SEARCH_MARKET = "search_market";

    @BindView(R.id.searchView)
    android.support.v7.widget.SearchView searchView;

    @Override
    protected int initLayout() {
        return R.layout.activity_search;
    }

    @Override
    protected BaseMvpPresenter initPresenter() {
        return new SearchPresenter(this);
    }

    @Override
    protected boolean isThemeStyle() {
        return true;
    }

    @Override
    protected void init() {
        SpannableString spanText = new SpannableString("搜索");
        spanText.setSpan(new AbsoluteSizeSpan(20, true), 0, spanText.length(),
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        spanText.setSpan(new ForegroundColorSpan(Color.WHITE), 0,
                spanText.length(),Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        EditText searchEditText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(Color.WHITE);
        searchEditText.setHintTextColor(Color.WHITE);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position > getAdapter().getData().size() - 1) {
                    return 2;
                }else return 1;
            }
        });
        getRecyclerView().setLayoutManager(gridLayoutManager);
        getRecyclerView().addItemDecoration(new SpacesItemDecoration(10));
    }

    @Override
    protected void initListeners() {
        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (TextUtils.isEmpty(searchView.getQuery().toString())){
                    return false;
                }
                if (TextUtils.isEmpty(searchView.getQuery().toString())) {
                    ToastHelper.showShortToast(StringUtil.genUnhappyFace() + " 内容不能为空哦");
                    return false;
                }
                getPresenter().searchQuery(searchView.getQuery().toString());
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputMethodManager.isActive()) {
                    searchView.clearFocus();
                    inputMethodManager.hideSoftInputFromWindow(searchView.getApplicationWindowToken(), 0);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    protected BaseQuickAdapter initAdapter() {
        return null;
    }

    @Override
    public String setEmptyText() {
        return "没有找到你想要的内容";
    }
}