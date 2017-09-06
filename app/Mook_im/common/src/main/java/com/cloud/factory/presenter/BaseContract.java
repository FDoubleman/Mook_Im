package com.cloud.factory.presenter;

import android.support.annotation.StringRes;

/**
 * Created by fmm on 2017/9/4.
 */

public interface BaseContract {

     interface View< T extends Presenter>{

        //公共 显示错误提示
        void showError(@StringRes int msg);

        //公共 显示加载中
        void onLoading();

        //公共 设置presenter
        void setPresenter(T presenter);
    }

     interface Presenter{
         // 共用的开始触发
         void start();

         // 共用的销毁触发
         void destroy();
    }
}
