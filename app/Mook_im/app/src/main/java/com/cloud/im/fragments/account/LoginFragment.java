package com.cloud.im.fragments.account;

import android.content.Context;
import android.widget.TextView;

import com.cloud.common.base.BasePresenterFragment;
import com.cloud.factory.presenter.account.LoginContract;
import com.cloud.factory.presenter.account.LoginPresenter;
import com.cloud.im.R;
import com.cloud.im.activity.AccountActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by fmm on 2017/9/4.
 */

public class LoginFragment extends BasePresenterFragment<LoginContract.Presenter>
        implements LoginContract.View{
    public static String tag = AccountActivity.class.getName();
    private AccountTrigger mTrigger;

    @BindView(R.id.txt_go_register)
    TextView tvGoRegist;

    @Override
    public int getContentLayoutId() {
        return R.layout.fragment_login;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mTrigger =(AccountTrigger)context;
    }

    @Override
    protected LoginContract.Presenter initPresenter() {
        return new LoginPresenter(this);
    }


    @OnClick(R.id.txt_go_register)
    public void regist(){
        mTrigger.trigger();
    }



    @Override
    public void loginSuccess() {

    }
}
