package com.cloud.common.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by fmm on 2017/7/9.
 */

public abstract class BaseFragment extends Fragment {

    protected View mRootView;
    private Unbinder mRootUnBinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if(mRootView ==null){
            View view = inflater.inflate(getContentLayoutId(),container,false);
            mRootView =view;
            initWidget(mRootView);
        }else {
            if(mRootView.getParent()!=null){
               ViewGroup vg = (ViewGroup) mRootView.getParent();
                vg.removeView(mRootView);
            }
        }

        return mRootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initArgs(getArguments());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
    }

    /**
     * 初始化控件
     * @return
     */
    public abstract int getContentLayoutId();


    /**
     * 初始化相关参数
     *
     * @param bundle 参数Bundle
     * @return 如果参数正确返回True，错误返回False
     */
    protected void initArgs(Bundle bundle) {
    }
    /**
     * 初始化 控件
     */
    protected void initWidget(View root){
        mRootUnBinder = ButterKnife.bind(this, root);
    }

    /**
     * 初始化 数据
     */
    protected void initData(){

    }

    /**
     * 返回按键触发时调用
     *
     * @return 返回True代表我已处理返回逻辑，Activity不用自己finish。
     * 返回False代表我没有处理逻辑，Activity自己走自己的逻辑
     */
    public boolean onBackPressed() {
        return false;
    }
}