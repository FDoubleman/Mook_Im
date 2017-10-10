package com.cloud.im.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.cloud.common.base.BaseFragment;
import com.cloud.common.base.ToolbarActivity;
import com.cloud.im.R;
import com.cloud.im.fragments.search.SearchGroupFragment;
import com.cloud.im.fragments.search.SearchUserFragment;

public class SearchActivity extends ToolbarActivity {

    private static final String EXTRA_TYPE = "EXTRA_TYPE";
    public static final int TYPE_USER = 1; // 搜索人
    public static final int TYPE_GROUP = 2; // 搜索群
    private int mType;
    public SearchListener mSearchListener;

    /**
     * 开启搜索activity
     *
     * @param context 上下文
     * @param type    打开的是搜索群组 还是 搜索人
     */
    public static void show(Context context, int type) {
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra(EXTRA_TYPE, type);
        context.startActivity(intent);
    }

    @Override
    public int getContentLayoutId() {
        return R.layout.activity_search;
    }


    @Override
    protected boolean initArgs(Bundle bundle) {
        mType = bundle.getInt(EXTRA_TYPE);
        return mType == TYPE_GROUP || mType == TYPE_USER;
    }


    @Override
    protected void initWidget() {
        super.initWidget();
        BaseFragment baseFragment;
        if (mType == TYPE_USER) {
            SearchUserFragment userFragment = new SearchUserFragment();
            baseFragment = userFragment;
            mSearchListener = userFragment;
        } else {
            SearchGroupFragment groupFragment = new SearchGroupFragment();
            baseFragment = groupFragment;
            mSearchListener = groupFragment;
        }

        getSupportFragmentManager().beginTransaction()
                .add(R.id.lay_container, baseFragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        if (searchView != null) {
            // 拿到一个搜索管理器
            SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            //设置搜索监听
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    //提交按钮
                    search(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    //文字变化 // 当文字改变的时候，咱们不会及时搜索，只在为null的情况下进行搜索
                    if (TextUtils.isEmpty(newText)) {
                        search("");
                        return true;
                    }
                    return false;
                }
            });
        }
        return super.onCreateOptionsMenu(menu);
    }

    private void search(String query) {
        if (mSearchListener == null) {
            return;
        }
        mSearchListener.search(query);
    }

    public interface SearchListener {
        void search(String content);
    }
}
