package com.cloud.common.base;

import android.support.annotation.StringRes;

import com.cloud.factory.presenter.BaseContract;

/**
 * Created by fmm on 2017/10/12.
 */

public abstract class PresenterToolbarActivity<Presenter extends BaseContract.Presenter> extends ToolbarActivity
        implements BaseContract.View<Presenter> {
    public Presenter mPresenter;

    @Override
    protected void initBefore() {
        super.initBefore();
        // 初始化Presenter
        initPresenter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.destroy();
        }
    }

    //初始化 presenter
    protected abstract Presenter initPresenter();

    @Override
    public void showError(@StringRes int msg) {
        // 显示错误, 优先使用占位布局
        if (mPlaceHolderView != null) {
            mPlaceHolderView.triggerError(msg);
        } else {
            BaseApplication.showToast(msg);
        }
    }

    @Override
    public void onLoading() {
        if (mPlaceHolderView != null) {
            mPlaceHolderView.triggerLoading();
        }
    }

    protected void hideLoading() {
        if (mPlaceHolderView != null) {
            mPlaceHolderView.triggerOk();
        }
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.mPresenter = presenter;
    }
}
