package com.cloud.im.activity;

import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;

import com.cloud.common.base.BaseActivity;
import com.cloud.common.base.BaseFragment;
import com.cloud.common.utils.ImageUtil;
import com.cloud.im.R;
import com.cloud.im.fragments.account.AccountTrigger;
import com.cloud.im.fragments.account.LoginFragment;
import com.cloud.im.fragments.account.RegistFragment;

import butterknife.BindView;

public class AccountActivity extends BaseActivity implements AccountTrigger {
    public static String tag = AccountActivity.class.getName();
    private BaseFragment mCurFragment;
    private BaseFragment mLoginFragment;
    private BaseFragment mRegisterFragment;

    @BindView(R.id.im_bg)
    ImageView ivBg;
    /**
     * 账户Activity显示的入口
     *
     * @param context Context
     */
    public static void show(Context context) {
        context.startActivity(new Intent(context, AccountActivity.class));
    }

    @Override
    public int getContentLayoutId() {
        return R.layout.activity_account;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mCurFragment = mLoginFragment = new LoginFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fl_container,
                        mCurFragment, LoginFragment.tag)
                .commit();
        ImageUtil.loadBgView(this.getApplicationContext(),R.drawable.bg_src_tianjin
                ,ivBg,R.color.colorAccent);
    }

    @Override
    protected void initData() {
        super.initData();
    }


    /**
     * 切换 登录和注册fragment
     */
    @Override
    public void trigger() {
        BaseFragment fragment;
        if (mCurFragment == mLoginFragment) {
            //当前是 注册 切换成 登录
            if (mRegisterFragment == null) {
                mRegisterFragment = new RegistFragment();
            }
            fragment = mRegisterFragment;
        } else {
            //当前是 注册 切换成 登录
            fragment = mLoginFragment;
        }
        mCurFragment = fragment;

        //切换fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_container, fragment)
                .commit();
    }
}
