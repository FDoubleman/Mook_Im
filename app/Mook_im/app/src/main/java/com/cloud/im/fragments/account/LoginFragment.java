package com.cloud.im.fragments.account;

import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cloud.common.base.BasePresenterFragment;
import com.cloud.factory.presenter.account.LoginContract;
import com.cloud.factory.presenter.account.LoginPresenter;
import com.cloud.im.R;
import com.cloud.im.activity.AccountActivity;
import com.cloud.im.activity.MainActivity;

import net.qiujuer.genius.ui.widget.Loading;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by fmm on 2017/9/4.
 */

public class LoginFragment extends BasePresenterFragment<LoginContract.Presenter>
        implements LoginContract.View {
    public static String tag = AccountActivity.class.getName();
    private AccountTrigger mTrigger;

    @BindView(R.id.txt_go_register)
    TextView tvGoRegist;

    @BindView(R.id.edit_phone)
    EditText mPhone;
    @BindView(R.id.edit_password)
    EditText mPassword;

    @BindView(R.id.loading)
    Loading mLoading;

    @BindView(R.id.btn_submit)
    Button mSubmit;

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

    /**
     * 登录功能
     */
    @OnClick(R.id.btn_submit)
    void setSubmit(){
        String phone = mPhone.getText().toString();
        String password = mPassword.getText().toString();
        // 调用P层进行注册
        mPresenter.login(phone, password);
    }

    @Override
    public void loginSuccess() {
        MainActivity.show(getContext());
        getActivity().finish();
    }

    @Override
    public void showError(int msg) {
        super.showError(msg);
        // 停止Loading
        mLoading.stop();
        // 让控件可以输入
        mPhone.setEnabled(true);
        mPassword.setEnabled(true);
        // 提交按钮可以继续点击
        mSubmit.setEnabled(true);
    }

    @Override
    public void onLoading() {
        super.onLoading();
        mLoading.start();
        // 让控件可以输入
        mPhone.setEnabled(false);
        mPassword.setEnabled(false);
        // 提交按钮可以继续点击
        mSubmit.setEnabled(false);

    }
}
