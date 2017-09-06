package com.cloud.factory.presenter.account;

import com.cloud.factory.presenter.BasePresenter;

/**
 * Created by fmm on 2017/9/4.
 */

public class LoginPresenter extends BasePresenter<LoginContract.View>
        implements LoginContract.Presenter{


    public LoginPresenter(LoginContract.View view) {
        super(view);
    }


    @Override
    public void login(String account, String password) {
        start();

    }
}
