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
        //优先显示错误占位 ，其次 弹出错误提示
        if(mPlaceHolderView!=null){
            mPlaceHolderView.triggerError(msg);
        }else{
            BaseApplication.showToast(msg);
        }
    }

    @Override
    public void onLoading() {

        if(mPlaceHolderView!=null){
            mPlaceHolderView.triggerLoading();
        }
    }

    @Override
    public void setPresenter(Presenter presenter) {
        mPresenter =presenter;
    }
}
