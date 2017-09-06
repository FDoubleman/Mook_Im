package com.cloud.factory.presenter.account;

import com.cloud.factory.presenter.BaseContract;

/**
 * Created by fmm on 2017/9/4.
 *
 */

public interface RegisterContract {

    interface View extends BaseContract.View<RegisterContract.Presenter>{
        //注册成功
        void reginsterSuccess();
    }

    interface Presenter extends BaseContract.Presenter{

        //发起注册
        void reginster(String phone, String name, String password );

        // 检查手机号是否正确
        boolean checkMobile(String phone);
    }
}
