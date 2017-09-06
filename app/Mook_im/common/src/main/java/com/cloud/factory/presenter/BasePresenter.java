package com.cloud.factory.presenter;

/**
 * Created by fmm on 2017/9/4.
 */

public class BasePresenter <T extends BaseContract.View> implements BaseContract.Presenter {
    private T mView;

    public BasePresenter (T view){
        setView(view);
    }
    /**
     * 设置一个View，子类可以复写
     */
    protected void setView(T view){
        mView =view;

        mView.setPresenter(this);
    }

    /**
     * 给子类获得view 的一个方法
     * 不允许复写
     * @return
     */
    protected final T getView(){
        return mView;
    }

    @Override
    public void start() {
        T View =mView;
        if(mView !=null){
            //开始时 走加载方法
            View.onLoading();
        }
    }

    @Override
    public void destroy() {
        T view = mView;
        mView = null;
        if (view != null) {
            // 把Presenter设置为NULL
            view.setPresenter(null);
        }
    }
}
