package com.cloud.im.activity;

import android.content.Intent;

import com.cloud.common.base.BaseActivity;
import com.cloud.common.base.BaseFragment;
import com.cloud.im.R;
import com.cloud.im.fragments.user.UpdateInfoFragment;

/**
 * Created by fmm on 2017/9/4.
 *
 */

public class UserActivity extends BaseActivity {

    private BaseFragment mCurFragment;

    @Override
    public int getContentLayoutId() {
        return R.layout.activity_user;

    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mCurFragment = new UpdateInfoFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fl_container,
                        mCurFragment,UpdateInfoFragment.Tag)
                .commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCurFragment.onActivityResult(requestCode, resultCode, data);
    }
}
