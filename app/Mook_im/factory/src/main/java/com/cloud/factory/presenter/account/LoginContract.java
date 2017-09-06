package com.cloud.factory.presenter.account;

import com.cloud.factory.presenter.BaseContract;

/**
 * Created by fmm on 2017/9/4.
 *
 */

public interface LoginContract {

     interface View extends BaseContract.View<LoginContract.Presenter>{
         // 登录成功
         void loginSuccess();
    }

     interface Presenter extends BaseContract.Presenter{

         //发起一个登录请求
         void login(String account,String password);
    }
}
