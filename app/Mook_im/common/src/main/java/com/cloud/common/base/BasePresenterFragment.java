package com.cloud.common.base;

import android.content.Context;

import com.cloud.factory.presenter.BaseContract;

/**
 * Created by fmm on 2017/9/4.
 */

public abstract class BasePresenterFragment<Presenter extends BaseContract.Presenter>
        extends BaseFragment implements BaseContract.View<Presenter>{

    public Presenter mPresenter;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // 在界面onAttach之后就触发初始化Presenter
        initPresenter();
    }

    protected abstract Presenter initPresenter();

    @Override
    public void showError(int msg) {
        //弹出错误提示
        BaseApplication.showToast(msg);
    }

    @Override
    public void onLoading() {
        // TODO: 2017/9/4  正在加载

    }

    @Override
    public void setPresenter(Presenter presenter) {
        mPresenter =presenter;
    }
}
