package com.cloud.factory.presenter.account;

import android.support.annotation.StringRes;
import android.text.TextUtils;

import com.cloud.common.Common;
import com.cloud.factory.R;
import com.cloud.factory.data.DataSource;
import com.cloud.factory.data.helper.AccountHelper;
import com.cloud.factory.model.api.account.RegisterModel;
import com.cloud.factory.model.db.User;
import com.cloud.factory.presenter.BasePresenter;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import java.util.regex.Pattern;

/**
 * Created by fmm on 2017/9/4.
 */

public class RegisterPresenter extends BasePresenter<RegisterContract.View>
        implements RegisterContract.Presenter, DataSource.CallBack<User> {

    public RegisterPresenter(RegisterContract.View view) {
        super(view);
    }

    @Override
    public void reginster(String phone, String name, String password) {
        // 调用开始方法，在start中默认启动了Loading
        start();
        RegisterContract.View view =  getView();
        //验证
        if(!checkMobile(phone)){
            // 提示
            view.showError(R.string.data_account_register_invalid_parameter_mobile);
        }else if(name.length()<2){
            // 姓名需要大于2位
            view.showError(R.string.data_account_register_invalid_parameter_name);
        }else if(password.length()<6){
            // 密码需要大于6位
            view.showError(R.string.data_account_register_invalid_parameter_password);
        }else{
            //进行网络请求
            RegisterModel model =  new RegisterModel(phone,password, name);
            AccountHelper.register(model,this);
        }
    }

    @Override
    public boolean checkMobile(String phone) {
        return !TextUtils.isEmpty(phone)
                && Pattern.matches(Common.Constance.REGEX_MOBILE, phone);
    }

    @Override
    public void onDataLoad(User user) {
        //数据请求成功
        final RegisterContract.View view =getView();
        if(view!=null){
            Run.onUiAsync(new Action() {
                @Override
                public void call() {
                    view.reginsterSuccess();
                }
            });
        }

    }

    @Override
    public void onDataNotLoad(@StringRes final int strRes) {
        //数据请求失败
        final RegisterContract.View view =getView();
        if(view!=null){
            Run.onUiAsync(new Action() {
                @Override
                public void call() {
                    view.showError(strRes);
                }
            });
        }
    }
}
