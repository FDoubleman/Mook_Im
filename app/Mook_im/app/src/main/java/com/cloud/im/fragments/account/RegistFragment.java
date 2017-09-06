package com.cloud.im.fragments.account;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.EditText;

import com.cloud.common.base.BasePresenterFragment;
import com.cloud.factory.presenter.account.RegisterContract;
import com.cloud.factory.presenter.account.RegisterPresenter;
import com.cloud.im.R;
import com.cloud.im.activity.MainActivity;

import net.qiujuer.genius.ui.widget.Loading;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegistFragment extends BasePresenterFragment<RegisterContract.Presenter>
       implements RegisterContract.View{

    @BindView(R.id.edit_phone)
    EditText editPhone;
    @BindView(R.id.edit_password)
    EditText editPassword;
    @BindView(R.id.edit_name)
    EditText editName;

    @BindView(R.id.btn_submit)
    Button btnSubmit;

    @BindView(R.id.loading)
    Loading loading;
    private AccountTrigger mTrigger;

    public RegistFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mTrigger =(AccountTrigger)context;
    }

    @Override
    public int getContentLayoutId() {
        return R.layout.fragment_register;
    }


    @Override
    protected RegisterContract.Presenter initPresenter() {
        return new RegisterPresenter(this);
    }


    @OnClick(R.id.btn_submit)
    public void onSubmitRegister(){
        String phone =editPhone.getText().toString();
        String name =editName.getText().toString();
        String password =editPassword.getText().toString();
        mPresenter.reginster(phone,name,password);
    }

    @OnClick(R.id.txt_go_login)
    void trigger(){
        //切换登录页面
        mTrigger.trigger();
    }


    @Override
    public void showError(int msg) {
        super.showError(msg);
        // 当需要显示错误的时候触发，一定是结束了
        // 停止Loading
        loading.stop();
        // 让控件可以输入
        editPhone.setEnabled(true);
        editName.setEnabled(true);
        editPassword.setEnabled(true);
        // 提交按钮可以继续点击
        btnSubmit.setEnabled(true);

    }

    @Override
    public void onLoading() {
        super.onLoading();
        loading.start();
        editPhone.setEnabled(false);
        editName.setEnabled(false);
        editPassword.setEnabled(false);
        btnSubmit.setEnabled(false);
    }

    @Override
    public void reginsterSuccess() {
        // 注册成功，这个时候账户已经登录
        // 我们需要进行跳转到MainActivity界面
        MainActivity.show(getContext());
        // 关闭当前界面
        getActivity().finish();
    }

}
