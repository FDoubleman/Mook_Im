package com.cloud.im.activity;

import android.content.Context;
import android.content.Intent;

import com.cloud.common.base.BaseActivity;
import com.cloud.im.R;
import com.cloud.im.fragments.account.UpdateInfoFragment;

public class AccountActivity extends BaseActivity {



    private UpdateInfoFragment mUpdateInfoFragment;
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
        mUpdateInfoFragment = new UpdateInfoFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fl_container,
                mUpdateInfoFragment,UpdateInfoFragment.Tag)
                .commit();
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mUpdateInfoFragment.onActivityResult(requestCode, resultCode, data);
    }
}
