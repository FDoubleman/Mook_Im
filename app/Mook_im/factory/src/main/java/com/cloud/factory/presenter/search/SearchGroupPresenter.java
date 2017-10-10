package com.cloud.factory.presenter.search;

import com.cloud.factory.presenter.BasePresenter;

/**
 * Created by fmm on 2017/10/5.
 */

public class SearchGroupPresenter extends BasePresenter<SearchContract.GroupView>
        implements SearchContract.SearchPresenter {
    public SearchGroupPresenter(SearchContract.GroupView view) {
        super(view);
    }

    @Override
    public void search(String content) {

    }
}
