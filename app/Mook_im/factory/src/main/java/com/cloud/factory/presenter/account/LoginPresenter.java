package com.cloud.factory.presenter.account;

import android.text.TextUtils;

import com.cloud.factory.R;
import com.cloud.factory.data.DataSource;
import com.cloud.factory.data.helper.AccountHelper;
import com.cloud.factory.model.api.account.LoginModel;
import com.cloud.factory.model.db.User;
import com.cloud.factory.persistence.Account;
import com.cloud.factory.presenter.BasePresenter;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

/**
 * Created by fmm on 2017/9/4.
 */

public class LoginPresenter extends BasePresenter<LoginContract.View>
        implements LoginContract.Presenter ,DataSource.Callback<User>{


    public LoginPresenter(LoginContract.View view) {
        super(view);
    }


    @Override
    public void login(String account, String password) {
        start();
        //判断是否为空
        LoginContract.View view = getView();
        if(TextUtils.isEmpty(account)||TextUtils.isEmpty(password)){
            view.showError(R.string.data_account_login_invalid_parameter);
        }else{
            //获得登录model
            LoginModel loginModel = new LoginModel(account,password, Account.getPushId());

            AccountHelper.login(loginModel,this);
        }


    }


    @Override
    public void onDataLoaded(User user) {
        //请求数据成功
        final LoginContract.View view = getView();
        if(view==null){
            return;
        }
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                view.loginSuccess();
            }
        });
    }

    @Override
    public void onDataNotAvailable(final int strRes) {
    //请求数据失败
        final LoginContract.View view = getView();
        if(view==null){
            return;
        }

        Run.onUiSync(new Action() {
            @Override
            public void call() {
                view.showError(strRes);
            }
        });
    }
}
